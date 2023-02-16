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
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

                                                    Notification(dangerData.getDangerType(), dangerData.getDescription(),gps_long2,gps_lat2,3);
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
    protected void Notification(String title,String description,Double long1,Double long2,int i) {
        /*Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(long1, long2, 1);
           // String city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //String city = addresses.get(0).getLocality(); */



        NotificationChannel channel = new NotificationChannel("1245", "location2",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "1245");
        if (title.equals("Πλημμυρα")){
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(description +"\n" + "Μήνυμα για " + title + " στην περιοχή:" + long1 +","+ long2 + "\n" + "Αν βρεθείτε μπροστά σε δρόμο που έχει πλημμυρίσει σταματήστε και αλλάξτε κατεύθυνση. Αποφύγετε τα λιμνάζοντα νερά.Περιορίστε δραστικά τις μετακινήσεις και ακολουθήστε τις οδηγίες των αρχών.Οδηγίες αυτοπροστασίας:https://www.civilprotection.gr/el/entona-kairika-fainomena"))
                    .setAutoCancel(true);
            notificationManager.notify(i, builder.build());
        }
        else if(title.equals("Πυρκαγια")){
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(description +"\n" + "Μήνυμα για " + title + " στην περιοχή:" + long1 +","+ long2 + "\n" + "Αν η πυρκαγιά πλησιάζει στο σπίτι σας Διατηρείστε την ψυχραιμία σας. Μεταφέρετε όλα τα εύφλεκτα υλικά από τον περίγυρο του κτιρίου σε κλειστούς και προφυλαγμένους χώρους. Κλείστε όλες τις διόδους (καμινάδες, παράθυρα, πόρτες κλπ.)Οδηγίες αυτοπροστασίας:https://www.civilprotection.gr/el/entona-kairika-fainomena"))
                    .setAutoCancel(true);
            notificationManager.notify(i, builder.build());
        }
        else if(title.equals("Χιονοθύελα")){
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(description +"\n" + "Μήνυμα για " + title + " στην περιοχή:" + long1 +","+ long2 + "\n" + "Πηγαίνετε σε ασφαλές μέρος χωρίς να εκτεθείτε στην χιονοθύελλα. Ντυθείτε με πολλά στρώματα με ελαφριά και ζεστά ρούχα αντί για ένα βαρύ ρούχο. Προτιμήστε ένα εξωτερικό ρούχο. Φορέστε ζεστές αδιάβροχες μπότες.Περιορίστε δραστικά τις μετακινήσεις και ακολουθήστε τις οδηγίες των αρχών.Οδηγίες αυτοπροστασίας:https://www.civilprotection.gr/el/entona-kairika-fainomena"))
                    .setAutoCancel(true);
            notificationManager.notify(i, builder.build());
        }
        else if(title.equals("Σεισμος")){
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(description +"\n" + "Μήνυμα για " + title + " στην περιοχή:" + long1 +","+ long2 + "\n" + "Σε περίπτωση που βρίσκεστε σε εσωτερικό χώρο,εκκενώστε το κτίριο από το κλιμακοστάσιο.Καταφύγετε σε ανοιχτό και ασφαλή χώρο.Οδηγίες αυτοπροστασίας:https://www.civilprotection.gr/el/entona-kairika-fainomena"))
                    .setAutoCancel(true);
            notificationManager.notify(i, builder.build());
        }
        else if(title.equals("Ανεμοστροβιλος")){
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(description +"\n" + "Μήνυμα για " + title + " στην περιοχή:" + long1 +","+ long2 + "\n" + "Προληπτικά μείνετε μακριά από παράθυρα, γωνίες, πόρτες  και εξωτερικούς τοίχους.Προσέξτε τα συντρίμμια που ίπτανται. Καθίστε στο πάτωμα δίπλα σ΄ έναν εσωτερικό τοίχο ή κάτω από ένα βαρύ έπιπλο, όπως ένα τραπέζι.Περιορίστε δραστικά τις μετακινήσεις και ακολουθήστε τις οδηγίες των αρχών.Οδηγίες αυτοπροστασίας:https://www.civilprotection.gr/el/entona-kairika-fainomena"))
                    .setAutoCancel(true);
            notificationManager.notify(i, builder.build());
        }
        else if(title.equals("Καπνος/Σταχτη")){
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(description +"\n" + "Μήνυμα για " + title + " στην περιοχή:" + long1 +","+ long2 + "\n" + "Σε περίπτωση που δεν είναι εφικτή η απομάκρυνση από τη περιοχή, παραμείνετε όσο το δυνατόν περισσότερο σε κλειστούς χώρους και διατηρείστε τον εσωτερικό αέρα όσο το δυνατόν περισσότερο καθαρό.Περιορίστε δραστικά τις μετακινήσεις και ακολουθήστε τις οδηγίες των αρχών.Οδηγίες αυτοπροστασίας:https://www.civilprotection.gr/el/entona-kairika-fainomena"))
                    .setAutoCancel(true);
            notificationManager.notify(i, builder.build());
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

