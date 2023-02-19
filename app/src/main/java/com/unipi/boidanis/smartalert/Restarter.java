package com.unipi.boidanis.smartalert;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Restarter extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if ((intent.getAction().equals(Intent.ACTION_SCREEN_ON) ||
                intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION) ||
                intent.getAction().matches(LocationManager.KEY_PROVIDER_ENABLED) || intent.getAction().matches(LocationManager.KEY_LOCATION_CHANGED))&&context!=null) {
            context.startService(new Intent(context,MyService.class));

        }
    }

}