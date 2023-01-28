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
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MyService extends Service implements LocationListener {
    FirebaseDatabase database;
    String user;
    FirebaseAuth mAuth;
    LocationManager locationManager;
    DatabaseReference myRef;
    Boolean b = false;
    SharedPreferences sharedPreferences;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        Toast.makeText(getApplicationContext(), "Created", Toast.LENGTH_SHORT).show();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        database = FirebaseDatabase.getInstance();
        user = sharedPreferences.getString("User", "");
        if (database == null) {
            Toast.makeText(getApplicationContext(), "Null objects", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), user, Toast.LENGTH_SHORT).show();
        if (Objects.equals(user, "")) {
            Toast.makeText(getApplicationContext(), "failed1", Toast.LENGTH_SHORT).show();
            return START_STICKY;
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
            return START_STICKY;
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Double gps_long1 = location.getLongitude();
        Double gps_lat1 = location.getLatitude();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref2 = database.getReference().child("Alerts");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DangerData dangerData = dataSnapshot.getValue(DangerData.class);
                    Double gps_long2 = dangerData.getLongtitude();
                    Double gps_lat2 = dangerData.getLat();
                    float[] distance = new float[1];

                    Location.distanceBetween(gps_lat1, gps_long1, gps_lat2, gps_long2, distance);

                    /*NotificationChannel channel2 = new NotificationChannel("123","channelUnipi",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager2 =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager2.createNotificationChannel(channel2);
                    NotificationCompat.Builder builder2 =
                            new NotificationCompat.Builder(getApplicationContext(),"123");
                    builder2.setContentTitle(String.valueOf(distance[0]))
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentText(gps_lat1 +"\n" +gps_long1+"\n" +gps_lat2 +"\n" + gps_long2)
                            .setAutoCancel(true);
                    notificationManager2.notify(1,builder2.build());*/

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
                        notificationManager.notify(1, builder.build());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Toast.makeText(getApplicationContext(), "terminated", Toast.LENGTH_SHORT).show();
        /*Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);*/
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 10000,
                restartServicePendingIntent);


    }
}

