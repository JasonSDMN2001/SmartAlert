package com.unipi.boidanis.smartalert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    Context context;
    ArrayList<DangerData> list;
    public MyAdapter(Context context, ArrayList<DangerData> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dangerdata,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DangerData dangerData=list.get(position);
        holder.dangerType.setText(dangerData.getDangerType());
        holder.description.setText(dangerData.getDescription());
        holder.gps.setText(dangerData.getLongtitude()+","+dangerData.getLat());
        holder.date.setText(dangerData.getDate());
        holder.key = dangerData.getKey();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView dangerType,date,gps;
        EditText description;
        ImageView image;
        Button button1,button2;
        String key;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dangerType=itemView.findViewById(R.id.textView22);
            description=itemView.findViewById(R.id.editTextTextMultiLine2);
            gps=itemView.findViewById(R.id.textView23);
            date=itemView.findViewById(R.id.textView24);
            image=itemView.findViewById(R.id.imageView3);
            button1=itemView.findViewById(R.id.button7);
            button2=itemView.findViewById(R.id.button9);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference().child("Alerts").child(key);
                    reference.child("approved").setValue(true);
                    DatabaseReference ref2 = database.getReference().child("Alerts").child(key).child("approved");
                    ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue().toString().equals("true")){
                                new AlertDialog.Builder(view.getContext()).setTitle("This Danger").setMessage("is approved").setCancelable(true).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference().child("Alerts").child(key);
                    reference.child("approved").setValue(false);
                    DatabaseReference reference2 = database.getReference().child("Alerts").child(key).child("approved");
                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue().toString().equals("false")){
                                new AlertDialog.Builder(view.getContext()).setTitle("This Danger").setMessage("will not be approved").setCancelable(true).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

        }
    }

}
