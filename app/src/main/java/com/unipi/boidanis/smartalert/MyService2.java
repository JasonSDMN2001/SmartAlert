package com.unipi.boidanis.smartalert;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.IBinder;

import androidx.annotation.NonNull;

public class MyService2 extends Service implements LocationListener {
    public MyService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}