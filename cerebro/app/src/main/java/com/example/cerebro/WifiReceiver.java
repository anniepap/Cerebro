package com.example.cerebro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver {

    int i = 0;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        ServiceManager serviceManager = new ServiceManager(context);
        if(serviceManager.isNetworkAvailable())
            Toast.makeText(context, "Network Available" + i++,Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "No Internet Connection",Toast.LENGTH_SHORT).show();
    }
}
