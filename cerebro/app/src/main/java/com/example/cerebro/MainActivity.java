package com.example.cerebro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Camera camera;
    private Ringtone r;
    private boolean isFlashOn = false;
    private boolean isSoundOn = false;
    private String IPport = "";
    private int freq = 1;
    private Parameters params;
    private MQTTclient cl;
    private Handler handler;
    private WifiReceiver wifiReceiver;
    private WifiManager wifiManager;
    private String regex = "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(?:\\.|$)){4}";
    final Pattern ptn = Pattern.compile(regex);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        getCamera();

        cl = new MQTTclient();
        handler = new Handler();
        wifiReceiver = new WifiReceiver();

        // Switch button click event to toggle flash on/off
//        final ToggleButton btnToggle = findViewById(R.id.btnToggle);
//        btnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    turnOnFlash();
//                } else {
//                    turnOffFlash();
//                }
//            }
//        });

        final Button btnOn = findViewById(R.id.btnOn);
        final Button btnOff = findViewById(R.id.btnOff);

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnSound();
                turnOnFlash();
            }
        });
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOffSound();
                turnOffFlash();
            }
        });
    }

    private void startMqtt() {
        cl.runClient();
        cl.setListener(new MQTTclient.ChangeListener() {
            @Override
            public void onChange() {
                if (cl.getMessage_string().equals("turn On")) {
                    turnOnSound();
                    turnOnFlash();
                } else if (cl.getMessage_string().equals("turn Off")) {
                    turnOffSound();
                    turnOffFlash();
                }
            }
        });
    }

    // Get the camera
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("getCamera failed: ", e.getMessage());
            }
        }
    }

    // Turning on sound
    private void turnOnSound() {
        turnOffSound();
        if (!isSoundOn) {
            try {
                r.play();
                isSoundOn = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Turning off sound
    private void turnOffSound() {
        if (isSoundOn) {
            try {
                r.stop();
                isSoundOn = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Turning On flash
    private void turnOnFlash() {
        //startService(new Intent(getBaseContext(), FlashLightService.class));
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
        }
    }

    // Turning Off flash
    private void turnOffFlash() {
        //stopService(new Intent(getBaseContext(), FlashLightService.class));
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Do you really want to exit? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // on starting the app get the camera params
        getCamera();

        // on starting the app checks internet connection
        final int delay = 8; // seconds
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        handler.postDelayed(new Runnable(){
            public void run(){
                registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiManager.startScan();
                handler.postDelayed(this, delay * 1000);
            }
        }, delay);
    }

    @Override
    protected void onStop() {
        cl.disconnect();
        super.onStop();
        if (this.isFinishing()) {
            turnOffSound();
        }
        if (this.wifiReceiver != null) {
            unregisterReceiver(wifiReceiver);
            wifiReceiver = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Connection Settings");
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            View view = View.inflate(this, R.layout.settings_dialog, layout);

            TextView IPview = view.findViewById(R.id.ip_input);
            IPview.setText("IP address:", TextView.BufferType.EDITABLE);
            final EditText IP = view.findViewById(R.id.IP);

            TextView portView = view.findViewById(R.id.port_input);
            portView.setText("Port:", TextView.BufferType.EDITABLE);
            final EditText port = view.findViewById(R.id.port);

            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Matcher mtch = ptn.matcher(IP.getText().toString());
                    if (mtch.find()) {
                        IPport = "tcp://" + IP.getText().toString() + ":" + port.getText().toString();
                        Log.i("IPportInput", IPport);
                        cl.setIPport(IPport);
                        startMqtt();
                    } else {
                        Log.e("Input Error", "IP is not in correct form");
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.setView(layout);
            builder.show();
        }

        if (id == R.id.action_frequency) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set Frequency");
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.frequency_dialog, null);
            builder.setView(dialogView);

            final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(10);
            numberPicker.setValue(freq);
            numberPicker.setWrapSelectorWheel(true);

            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int id, int i) {
                    Log.d("numberPicker", "onValueChange: ");
                }
            });
            TextView IPview = dialogView.findViewById(R.id.sec);
            IPview.setText("sec", TextView.BufferType.EDITABLE);

            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.i("frequencyInput", "" + numberPicker.getValue());
                    freq = numberPicker.getValue();
                    cl.sendMessage(String.valueOf(freq));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        if (id == R.id.action_exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
