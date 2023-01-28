package com.unipi.boidanis.smartalert;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {
    EditText email,password;
    FirebaseAuth mAuth;
    private String role;
    FirebaseDatabase database;
    DatabaseReference myRef,myRef2;
    FirebaseUser user;
    LocationManager locationManager;
    SharedPreferences sharedPreferences;
    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        email= findViewById(R.id.editTextTextPersonName);
        password=findViewById(R.id.editTextTextPersonName2);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        database = FirebaseDatabase.getInstance();
        permissions();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkBox=findViewById(R.id.checkBox);
    }

    private void permissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    124);
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
    public void signup(View view){
        mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showMessage("Success!","User authenticated");
                            showMessage("Success!",mAuth.getUid());
                            if(!role.matches("")){
                                myRef=database.getReference("Users");
                                myRef.child(mAuth.getUid()).child("Role").setValue(role);
                            }else{
                                showMessage("Error","Choose a Role");
                            }

                        }else {
                            showMessage("Error",task.getException().getLocalizedMessage());
                        }
                    }
                });
    }
    public void signin(View view){
        mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener((task)->{
                    if(task.isSuccessful()){
                        myRef2 = database.getReference().child("Users").child(mAuth.getUid()).child("Role");
                        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getValue().toString().equals("Employee")){
                                    showMessage("Success!","Ok");
                                    Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }else if(snapshot.getValue().toString().equals("Alerting User")) {
                                    showMessage("Success!","Ok");
                                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }else if(snapshot.getValue().toString().equals("Alerted User")) {
                                    if(checkBox.isChecked()){
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("User", mAuth.getUid());
                                        editor.apply();
                                    }

                                    showMessage("Success!","Ok");
                                    Intent intent = new Intent(getApplicationContext(), MainActivity4.class);
                                    startActivity(intent);

                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }else {
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        role = (String)adapterView.getItemAtPosition(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void buildAlertMessageNoGps() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}