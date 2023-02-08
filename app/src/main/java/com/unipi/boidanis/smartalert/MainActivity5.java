package com.unipi.boidanis.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity5 extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    EditText email,password;
    FirebaseAuth mAuth;
    private String role;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView textView,textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        textView = findViewById(R.id.textView12);
        textView2 = findViewById(R.id.textView13);
        email = findViewById(R.id.editTextTextPersonName4);
        password = findViewById(R.id.editTextTextPersonName5);
        mAuth = FirebaseAuth.getInstance();
        Spinner spinner = (Spinner) findViewById(R.id.spinner3);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        database = FirebaseDatabase.getInstance();
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    public void signup(View view){
        if(!role.matches("")){
            mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                showMessage("Success!","User authenticated");
                                showMessage("Success!",mAuth.getUid());
                                myRef=database.getReference("Users");
                                myRef.child(mAuth.getUid()).child("Role").setValue(role);
                            }else {
                                showMessage("Error",task.getException().getLocalizedMessage());
                            }
                        }
                    });
        }else{
            showMessage("Error","Choose a Role");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        role = (String)adapterView.getItemAtPosition(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}