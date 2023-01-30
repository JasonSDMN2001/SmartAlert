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
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MyService extends Service implements LocationListener {
    FirebaseDatabase database;
    String user;
    FirebaseAuth mAuth;
    LocationManager locationManager;
    DatabaseReference myRef;
    Boolean b = false,gpsfound=false;
    SharedPreferences sharedPreferences;
    Double gps_long2, gps_lat2;
    DangerData dangerData;
    float[] distance = new float[1];
    ;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        NotificationChannel channel = new NotificationChannel("1234", "starting",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "1234");
        builder.setContentTitle("Now")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText("Running")
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        database = FirebaseDatabase.getInstance();
        user = sharedPreferences.getString("User", "");
        if (database == null) {
            Toast.makeText(getApplicationContext(), "Null objects", Toast.LENGTH_SHORT).show();
        }
        if (Objects.equals(user, "")) {
            Toast.makeText(getApplicationContext(), "failed1", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "failed2", Toast.LENGTH_SHORT).show();
            //onTaskRemoved(new Intent(getApplicationContext(), this.getClass()));
            onDestroy();
            return START_NOT_STICKY;

        }

        //location search

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
        return START_NOT_STICKY;
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(getApplicationContext(), "searching location", Toast.LENGTH_SHORT).show();
        Double gps_long1 = location.getLongitude();
        Double gps_lat1 = location.getLatitude();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref2 = database.getReference().child("Alerts");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dangerData = dataSnapshot.getValue(DangerData.class);
                    gps_long2 = dangerData.getLongtitude();
                    gps_lat2 = dangerData.getLat();
                    Location.distanceBetween(gps_lat1, gps_long1, gps_lat2, gps_long2, distance);
                    if (dangerData.getApproved().toString().equals("true") && distance[0] < 100000.0) {
                        NotificationChannel channel = new NotificationChannel("12345", "location",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.createNotificationChannel(channel);
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(getApplicationContext(), "12345");
                        builder.setContentTitle(dangerData.getDangerType())
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentText(dangerData.getDescription())
                                .setAutoCancel(true);
                        notificationManager.notify(2, builder.build());
                        //gpsfound=true;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        locationManager.removeUpdates(this);
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_SHORT).show();

    }
    /* @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Toast.makeText(getApplicationContext(), "rerunning", Toast.LENGTH_SHORT).show();

        /*Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        if(!gpsfound) {
            Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
            restartServiceIntent.setPackage(getPackageName());

            PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);
        }

    }*/
}

