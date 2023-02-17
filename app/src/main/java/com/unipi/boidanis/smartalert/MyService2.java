package com.unipi.boidanis.smartalert;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MyService2 extends Service {
    FirebaseDatabase database;
    String user;
    FirebaseAuth mAuth;
    LocationManager locationManager;
    DatabaseReference myRef;
    Boolean b = false;
    SharedPreferences sharedPreferences;

    public MyService2() {
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
        /*if (database == null) {
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
                if (snapshot.getValue().toString().equals("Employee")) {
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

        }*/

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref2 = database.getReference().child("Alerts");
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DangerData dangerData = dataSnapshot.getValue(DangerData.class);
                    for(DataSnapshot dataSnapshot1 :snapshot.getChildren()){
                        DangerData dangerData2 = dataSnapshot1.getValue(DangerData.class);
                        if(dangerData.getDangerType().equals(dangerData2.getDangerType())){
                            if(Math.abs(dangerData.getDate().getTime()-dangerData2.getDate().getTime())<3600*1000) { //1 hour
                                double gps_long1 = dangerData.getLongtitude();
                                double gps_lat1 =dangerData.getLongtitude();
                                float[] distance = new float[1];
                                double gps_long2 = dangerData2.getLongtitude();
                                double gps_lat2 = dangerData2.getLongtitude();
                                Location.distanceBetween(gps_lat1, gps_long1, gps_lat2, gps_long2, distance);
                                if(distance[0]<1000.0){ //1km
                                    if(dangerData.getNumber()>dangerData2.getNumber()) {  //no = cause of double record
                                        dataSnapshot1.getRef().removeValue();
                                        int i = dangerData.getNumber();
                                        dangerData.setNumber(i + 1);
                                        String key2 = dangerData.getKey();
                                        ref2.child(key2).setValue(dangerData);
                                    }else if (dangerData.getNumber()<dangerData2.getNumber()){
                                        dataSnapshot.getRef().removeValue();
                                        int i = dangerData2.getNumber();
                                        dangerData2.setNumber(i + 1);
                                        String key2 = dangerData2.getKey();
                                        ref2.child(key2).setValue(dangerData2);
                                    }else if (dangerData.getNumber()==dangerData2.getNumber()&&
                                            dangerData.getKey()!=dangerData2.getKey()){
                                        dataSnapshot1.getRef().removeValue();
                                        dangerData.setNumber(2);
                                        String key2 = dangerData.getKey();
                                        ref2.child(key2).setValue(dangerData);
                                    }else if (dangerData.getNumber()==dangerData2.getNumber()&&
                                            dangerData.getKey()==dangerData2.getKey()){
                                        //String key2 = dangerData.getKey();
                                        //ref2.child(key2).setValue(dangerData);
                                    }
                                }
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
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}