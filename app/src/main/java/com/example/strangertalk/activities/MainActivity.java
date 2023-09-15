package com.example.strangertalk.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.strangertalk.databinding.ActivityMainBinding;
import com.example.strangertalk.models.Users;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding ;
    FirebaseAuth auth;
    FirebaseDatabase database;
    long coins ;
    String[] permissions = new String[] {Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
    private int requestCode =1 ;
    Users user ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.loadingGroup.setVisibility(View.VISIBLE);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });


        database.getReference().child("Profiles")
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        binding.loadingGroup.setVisibility(View.GONE);
                        binding.mainFindBtn.setVisibility(View.VISIBLE);
                        user = snapshot.getValue(Users.class);
                        coins = user.getCoins();

                        binding.coinsText.setText(MessageFormat.format("You have: {0}", coins));
                        Glide.with(MainActivity.this)
                                .load(user.getProfile())
                                .into(binding.mainProfileImage);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        binding.mainFindBtn.setOnClickListener(v -> {
            if(isPermissionGranted()) {
                if (coins > 5) {
                    coins = coins - 5;
                    database.getReference()
                            .child("Profiles")
                            .child(currentUser.getUid())
                            .child("coins")
                            .setValue(coins);
                    Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);
                    intent.putExtra("profile",user.getProfile());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Insufficient Coins", Toast.LENGTH_SHORT).show();
                }
            }else{
                askPermission();
            }
        });
        binding.linearTreasureMain.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,RewardActivity.class));
        });
        binding.mainProfileImage.setOnClickListener(v ->{
            startActivity(new Intent(MainActivity.this,FullScreenImage.class));
        });
    }

    void askPermission(){
        ActivityCompat.requestPermissions(this,permissions,requestCode);
    }
    private boolean isPermissionGranted(){
        for(String permission : permissions){
            if(ActivityCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
}