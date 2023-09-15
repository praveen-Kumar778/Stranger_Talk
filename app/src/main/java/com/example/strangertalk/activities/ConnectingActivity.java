package com.example.strangertalk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.strangertalk.databinding.ActivityConnectingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ConnectingActivity extends AppCompatActivity {

    ActivityConnectingBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    boolean isOkay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnectingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        String profiles = getIntent().getStringExtra("profile");
        Glide.with(ConnectingActivity.this)
                .load(profiles)
                .into(binding.connectingProfileImage);

        String userName = mAuth.getUid();

        database.getReference().child("users")
                // here we are searching on the basis of status order if it is 0 then the room is available and if 1 then it is not available
                .orderByChild("status")
                .equalTo(0).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // here we are checking if one user is available for the call then let other go inside and connect it
                        // with other user
                        if (snapshot.getChildrenCount() > 0) {
                            // Room Available
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                isOkay = true;
                                database.getReference().child("users")
                                        .child(dataSnapshot.getKey())
                                        .child("incoming")
                                        .setValue(userName);
                                database.getReference().child("users")
                                        .child(dataSnapshot.getKey())
                                        .child("status")
                                        .setValue(1);
                                Intent intent = new Intent(ConnectingActivity.this, CallActivity.class);
                                String incoming = dataSnapshot.child("incoming").getValue(String.class);
                                String createdBy = dataSnapshot.child("createdBy").getValue(String.class);
                                boolean isAvailable = dataSnapshot.child("isAvailable").getValue(Boolean.class);
                                intent.putExtra("incoming", incoming);
                                intent.putExtra("createdBy", createdBy);
                                intent.putExtra("userName", userName);
                                intent.putExtra("isAvailable", isAvailable);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // Room not Available
                            HashMap<String, Object> room = new HashMap<>();
                            room.put("incoming", userName);
                            room.put("createdBy", userName);
                            room.put("isAvailable", true);
                            room.put("status", 0);

                            database.getReference().child("users")
                                    .child(userName)
                                    .setValue(room).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            database.getReference().child("users")
                                                    .child(userName).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.child("status").exists()) {
                                                                if (snapshot.child("status").getValue(Integer.class) == 1) {
                                                                    if (isOkay)
                                                                        return;
                                                                    isOkay = true;
                                                                    Intent intent = new Intent(ConnectingActivity.this, CallActivity.class);
                                                                    String incoming = snapshot.child("incoming").getValue(String.class);
                                                                    String createdBy = snapshot.child("createdBy").getValue(String.class);
                                                                    boolean isAvailable = snapshot.child("isAvailable").getValue(Boolean.class);
                                                                    intent.putExtra("incoming", incoming);
                                                                    intent.putExtra("createdBy", createdBy);
                                                                    intent.putExtra("userName", userName);
                                                                    intent.putExtra("isAvailable", isAvailable);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        database.getReference().child("users").setValue(null);
        finish();
    }
}