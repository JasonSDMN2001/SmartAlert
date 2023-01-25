package com.unipi.boidanis.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity3 extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference ref;
    DangerData dangerData;
    RecyclerView recyclerView,recyclerView2;
    MyAdapter  myAdapter,myAdapter2;
    ArrayList<DangerData> list,list2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("Alerts");
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list =new ArrayList<>();
        myAdapter =new MyAdapter(this,list);
        recyclerView.setAdapter(myAdapter);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    dangerData = dataSnapshot.getValue(DangerData.class);
                    if(dangerData.getApproved().toString().equals("false")){
                        list.add(dangerData);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView2 = (RecyclerView)findViewById(R.id.recyclerView2);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        list2 =new ArrayList<>();
        myAdapter2 =new MyAdapter(this,list2);
        recyclerView2.setAdapter(myAdapter2);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list2.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    dangerData = dataSnapshot.getValue(DangerData.class);
                    if(dangerData.getApproved().toString().equals("true")){
                        list2.add(dangerData);
                    }
                }
                myAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}