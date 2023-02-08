package com.unipi.boidanis.smartalert;

import static java.lang.String.valueOf;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MyService extends Service{
    FirebaseDatabase database;
    String user;
    FirebaseAuth mAuth;
    LocationManager locationManager;
    DatabaseReference myRef;
    Boolean b = false;
    SharedPreferences sharedPreferences;
    Double gps_long2, gps_lat2;
    DangerData dangerData;
    float[] distance = new float[1];

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        database = FirebaseDatabase.getInstance();
        user = sharedPreferences.getString("User", "");
        if (database == null) {
            onDestroy();
            return START_NOT_STICKY;
        }
        if (Objects.equals(user, "")) {
            onDestroy();
            return START_NOT_STICKY;
        }
        myRef = database.getReference().child("Users").child(user).child("Role");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals("Alerted User")) {
                    b = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (!b) {
            onDestroy();
            return START_NOT_STICKY;

        }

        //location search

        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }
        Date time = Calendar.getInstance().getTime();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Double gps_long1 = location.getLongitude();
                        Double gps_lat1 = location.getLatitude();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref2 = database.getReference().child("Alerts");
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    dangerData = dataSnapshot.getValue(DangerData.class);
                                    if(dangerData.getApproved().toString().equals("true")) {
                                        if(time.getTime()-dangerData.getDate().getTime()<86400*1000) { //1 day
                                            gps_long2 = dangerData.getLongtitude();
                                            gps_lat2 = dangerData.getLat();
                                            Location.distanceBetween(gps_lat1, gps_long1, gps_lat2, gps_long2, distance);
                                            if (distance[0] < 100000.0) { //10 km
                                                Notification(dangerData.getDangerType(), dangerData.getDescription(),3);
                                            }
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        onDestroy();
                    }
                });
        //also working

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
        return START_NOT_STICKY;
    }
    protected void Notification(String title,String description,int i){
        NotificationChannel channel = new NotificationChannel("1245", "location2",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "1245");
        builder.setContentTitle(title)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText(description)
                .setAutoCancel(true);
        notificationManager.notify(i, builder.build());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

