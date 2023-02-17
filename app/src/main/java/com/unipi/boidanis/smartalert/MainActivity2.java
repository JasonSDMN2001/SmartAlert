package com.unipi.boidanis.smartalert;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {
    EditText data;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    FirebaseUser user;
    String dangerType;
    LocationManager locationManager;
    TextView textView4,textView2;
    Date currentTime;
    Location gps;
    ImageView imageView;
    ProgressBar progressBar;
    private String key;
    Button uploadBtn;
    private Uri imageUri;
    StorageReference storreference = FirebaseStorage.getInstance().getReference();
    //Uri filepath;
    //private final int PICK_IMAGE_REQUEST = 71;
    private final int GALLERY_REQ_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        data = findViewById(R.id.editTextTextPersonName3);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();


        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.danger_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        currentTime = Calendar.getInstance().getTime();
        TextView textView3 = findViewById(R.id.textView3);
        textView3.setText(currentTime.toString());
        textView4 = findViewById(R.id.textView4);
        textView2 = findViewById(R.id.textView2);
       // textView66 = findViewById(R.id.textView14);
        String s = getIntent().getStringExtra("myMessage");
        textView2.setText("Welcome" +" "+ s +","+ "\n" + "You can now create your danger alert:");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            permissions();
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        uploadBtn = findViewById(R.id.button2);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        //showAllBtn = findViewById(R.id.button8);
        progressBar.setVisibility(View.INVISIBLE);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                someActivityResultLauncher.launch(galleryIntent);
                //startActivityForResult(galleryIntent, 2);

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri != null){
                    uploadToFirebase(imageUri);
                }
                else{
                    //Toast.makeText(MainActivity2.this,"Please Select Image",Toast.LENGTH_SHORT).show();
                    if(dangerType!=null&&data!=null&&gps!=null&&currentTime!=null)
                    {
                        DatabaseReference ref2 = database.getReference().child("Alerts");
                        key = ref2.push().getKey();
                        DangerData dangerData = new DangerData(key,dangerType, data.getText().toString(),gps.getLongitude(), gps.getLatitude(), currentTime,null,false,1);

                        ref2.child(key).setValue(dangerData);
                        Toast.makeText(MainActivity2.this, "Your danger alert has been submitted!", Toast.LENGTH_SHORT).show();
                    }else{
                        showMessage("Error", "missing stuff");
                    }
                }
            }
        });
        /*
        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,GALLERY_REQ_CODE);

            }
        }); */





        /*button = findViewById(R.id.button8);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });*/
    }
  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    } */

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        imageUri = data.getData();
                        imageView.setImageURI(imageUri);
                    }
                }
            });

    private void uploadToFirebase(Uri uri){

        StorageReference fileRef = storreference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(dangerType!=null&&data!=null&&gps!=null&&currentTime!=null)
                        {
                            DatabaseReference ref2 = database.getReference().child("Alerts");
                            key = ref2.push().getKey();
                            DangerData dangerData = new DangerData(key,dangerType, data.getText().toString(),gps.getLongitude(), gps.getLatitude(), currentTime,uri.toString(),false,1);

                            ref2.child(key).setValue(dangerData);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity2.this, "Your danger alert has been submitted!", Toast.LENGTH_SHORT).show();
                            imageView.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
                        }else{
                            showMessage("Error", "missing stuff");
                        }

                        /*DatabaseReference ref2 = database.getReference().child("Alerts");
                        key = ref2.push().getKey();
                        DangerData dangerData = new DangerData(key,dangerType, data.getText().toString(),gps.getLongitude(), gps.getLatitude(), currentTime,uri.toString(),false,1);

                        ref2.child(key).setValue(dangerData);
                        Toast.makeText(MainActivity2.this,"Uploaded Successfully",Toast.LENGTH_SHORT).show(); */
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity2.this,"Uploading Failed!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQ_CODE){
                image.setImageURI(data.getData());
                textView66.setText(image.toString());
            }
        }
    } */

    /* private void chooseImage() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && data != null && data.getData() != null )
            {
                filepath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                    image.setImageBitmap(bitmap);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  /*  public void write2(View view){
        if(dangerType!=null&&data!=null&&gps!=null&&currentTime!=null)
        {
            DatabaseReference ref2 = database.getReference().child("Alerts");
            key = ref2.push().getKey();
            DangerData dangerData = new DangerData(key,dangerType, data.getText().toString(),gps.getLongitude(), gps.getLatitude(), currentTime,imageView.toString(),false,1);

            ref2.child(key).setValue(dangerData);
            Toast.makeText(MainActivity2.this, "Your danger alert has been submitted!", Toast.LENGTH_SHORT).show();
        }else{
            showMessage("Error", "missing stuff");
        }
    } */
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        dangerType = (String)adapterView.getItemAtPosition(i);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        gps=location;
        textView4.setText(location.getLatitude()+","+location.getLongitude());
        locationManager.removeUpdates(this);
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

    public void signout(View view){
        if (mAuth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity2.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

}