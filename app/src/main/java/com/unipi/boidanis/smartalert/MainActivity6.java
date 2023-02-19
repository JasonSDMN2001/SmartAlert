package com.unipi.boidanis.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity6 extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    ImageView img;
    int num_fire,num_water,num_tornado,num_eq,num_smoke,num_snow;
    DangerData dangerData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        img = (ImageView) findViewById(R.id.imageView2);
        img.setBackgroundResource(R.mipmap.statistics);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Alerts");
        // myRef=database.getReference();
        TextView nameView1 = this.findViewById(R.id.textView33);
        TextView nameView2 = this.findViewById(R.id.textView35);
        TextView nameView3 = this.findViewById(R.id.textView36);
        TextView nameView4 = this.findViewById(R.id.textView37);
        TextView nameView5 = this.findViewById(R.id.textView38);
        TextView nameView6 = this.findViewById(R.id.textView39);
        TextView nameView7 = this.findViewById(R.id.textView41);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dangerData = dataSnapshot.getValue(DangerData.class);
                    if(dangerData.getDangerType().toString().equals("Πυρκαγια")){
                        num_fire = num_fire + 1;
                    }
                    else if(dangerData.getDangerType().toString().equals("Πλημμυρα")){
                        num_water = num_water + 1;
                    }
                    else if(dangerData.getDangerType().toString().equals("Σεισμος")){
                        num_eq = num_eq + 1;
                    }
                    else if(dangerData.getDangerType().toString().equals("Ανεμοστροβιλος")){
                        num_tornado = num_tornado + 1;
                    }
                    else if(dangerData.getDangerType().toString().equals("Καπνος/Σταχτη")){
                        num_smoke = num_smoke + 1;
                    }
                    else if(dangerData.getDangerType().toString().equals("Χιονοθύελα")){
                        num_snow = num_snow + 1;
                    }
                }

                nameView5.setText(String.valueOf(num_fire));
                nameView2.setText(String.valueOf(num_water));
                nameView3.setText(String.valueOf(num_eq));
                nameView4.setText(String.valueOf(num_tornado));
                nameView1.setText(String.valueOf(num_smoke));
                nameView6.setText(String.valueOf(num_snow));
                int total = num_eq + num_snow + num_smoke + num_tornado + num_water + num_fire;
                nameView7.setText(String.valueOf(total));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}