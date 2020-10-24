package com.example.agua_planeta_rica.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.example.agua_planeta_rica.view.delivery.MapsActivity;
import com.google.android.gms.location.LocationResult;

public class LocationServiceBcst extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "com.example.agua_planeta_rica.util.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null){
                    Location location = result.getLastLocation();
                    try{
                        MapsActivity.getInstance().updateLastLocation(location);
                    }catch (Exception e){
                        Log.w(this.getClass().getName(), e.getMessage(), e);
                    }
                }
            }
        }
    }
}
