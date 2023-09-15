package com.example.strangertalk.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.strangertalk.databinding.ActivityFullScreenImageBinding;

public class FullScreenImage extends AppCompatActivity {

    ActivityFullScreenImageBinding binding;
    public static final int GALLERY_INTENT_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullScreenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.location.setOnClickListener(v ->{
            startActivity(new Intent(FullScreenImage.this,MapsActivity.class));
        });
    }

}