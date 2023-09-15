package com.example.strangertalk.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.strangertalk.R;
import com.example.strangertalk.databinding.ActivityRewardBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RewardActivity extends AppCompatActivity {

    ActivityRewardBinding binding;
    private RewardedAd rewardedAd;
    FirebaseDatabase database;
    String currentUid;
    int coins = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRewardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();
        loadAd();

        database.getReference().child("Profiles")
                .child(currentUid)
                .child("coins").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        coins = snapshot.getValue(Integer.class);
                        binding.coins.setText(String.valueOf(coins));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        loadAds();
    }

    void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-5293738147850604~6513442381",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }
                });
    }
    void loadAds(){
        binding.video1.setOnClickListener(v -> {
            if (rewardedAd != null) {
                Activity activityContext = RewardActivity.this;
                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        loadAd();
                        coins = coins +200;
                        database.getReference().child("Profiles")
                                .child(currentUid)
                                .child("coins")
                                .setValue(coins);
                        binding.video1Icon.setImageResource(R.drawable.check);
                    }
                });
            } else {
                Toast.makeText(this, "Error coming while loading the ads", Toast.LENGTH_SHORT).show();
            }
        });
        binding.video2.setOnClickListener(v -> {
            if (rewardedAd != null) {
                Activity activityContext = RewardActivity.this;
                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        loadAd();
                        coins = coins + 300;
                        database.getReference().child("Profiles")
                                .child(currentUid)
                                .child("coins")
                                .setValue(coins);
                        binding.video2Icon.setImageResource(R.drawable.check);
                    }
                });
            } else {
                Toast.makeText(this, "Error coming while loading the ads", Toast.LENGTH_SHORT).show();
            }
        });
        binding.video3.setOnClickListener(v -> {
            if (rewardedAd != null) {
                Activity activityContext = RewardActivity.this;
                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        loadAd();
                        coins = coins + 400;
                        database.getReference().child("Profiles")
                                .child(currentUid)
                                .child("coins")
                                .setValue(coins);
                        binding.video3Icon.setImageResource(R.drawable.check);
                    }
                });
            } else {
                Toast.makeText(this, "Error coming while loading the ads", Toast.LENGTH_SHORT).show();
            }
        });
        binding.video4.setOnClickListener(v -> {
            if (rewardedAd != null) {
                Activity activityContext = RewardActivity.this;
                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        loadAd();
                        coins = coins + 500;
                        database.getReference().child("Profiles")
                                .child(currentUid)
                                .child("coins")
                                .setValue(coins);
                        binding.video4Icon.setImageResource(R.drawable.check);
                    }
                });
            } else {
                Toast.makeText(this, "Error coming while loading the ads", Toast.LENGTH_SHORT).show();
            }
        });
        binding.video5.setOnClickListener(v -> {
            if (rewardedAd != null) {
                Activity activityContext = RewardActivity.this;
                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        loadAd();
                        coins = coins + 1000;
                        database.getReference().child("Profiles")
                                .child(currentUid)
                                .child("coins")
                                .setValue(coins);
                        binding.video5Icon.setImageResource(R.drawable.check);
                    }
                });
            } else {
                Toast.makeText(this, "Error coming while loading the ads", Toast.LENGTH_SHORT).show();
            }
        });
    }
}