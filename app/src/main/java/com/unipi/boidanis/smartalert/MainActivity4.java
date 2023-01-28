package com.unipi.boidanis.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity4 extends AppCompatActivity implements LocationListener {
    LocationManager locationManager;
    Double gps_long1,gps_lat1,gps_long2,gps_lat2;
    FirebaseDatabase database;
    FirebaseUser user;
    FirebaseAuth mAuth;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        /*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);*/
        /*database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Intent serviceIntent = new Intent(this,MyService.class);
        serviceIntent.putExtra("User", user);
        startService(serviceIntent);*/
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        /*gps_long1=location.getLongitude();
        gps_lat1=location.getLatitude();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref2 = database.getReference().child("Alerts");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    DangerData dangerData = dataSnapshot.getValue(DangerData.class);
                    gps_long2=dangerData.getLongtitude();
                    gps_lat2=dangerData.getLat();
                    if (dangerData.getApproved().toString().equals("true")) {
                        showMessage("Alert! Theres a "+dangerData.getDangerType()+" going on",dangerData.getDescription());
                        NotificationChannel channel = new NotificationChannel("123","channelUnipi",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.createNotificationChannel(channel);
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(getApplicationContext(),"123");
                        builder.setContentTitle(dangerData.getDangerType())
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentText(dangerData.getDescription())
                                .setAutoCancel(true);
                        notificationManager.notify(1,builder.build());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        locationManager.removeUpdates(this);*/
    }
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
    public void RegisterForAlerts(View view){
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        intent = new Intent(this,MyService.class);
        pendingIntent = PendingIntent.getService(getApplicationContext(),1234,intent,
                PendingIntent.FLAG_IMMUTABLE);
        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()
                //+(5000),pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),5000,pendingIntent);
        showMessage("You will now","receive updates");
        //Intent serviceIntent = new Intent(this,MyService2.class);
        //serviceIntent.putExtra("User", user);
        /*serviceIntent.putExtra("gps_long1",gps_long1 );
        serviceIntent.putExtra("gps_lat1",gps_lat1 );
        serviceIntent.putExtra("gps_long2",gps_long2 );
        serviceIntent.putExtra("gps_lat2",gps_lat2 );*/
        //startService(serviceIntent);
    }
    public void Stop(View view){
        alarmManager.cancel(pendingIntent);
        stopService(intent);
    }
}