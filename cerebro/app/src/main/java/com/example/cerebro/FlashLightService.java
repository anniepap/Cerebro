package com.example.cerebro;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.util.Log;

public class FlashLightService extends Service {

    private Camera camera;
    private Parameters params;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("getCamera failed: ", e.getMessage());
            }
        }

        if (camera == null || params == null) {
            Log.e("flashlight service", "camera failed");
            return;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        params.setFlashMode(Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();

        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start)/1000 < 2) {
            // wait for 2 seconds to pass
        }
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();
    }
}
