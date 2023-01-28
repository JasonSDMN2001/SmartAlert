package com.unipi.boidanis.smartalert;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Restarter extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "This works unfortunately", Toast.LENGTH_SHORT).show();

    }

}