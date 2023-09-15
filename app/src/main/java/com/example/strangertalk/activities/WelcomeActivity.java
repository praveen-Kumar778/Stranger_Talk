package com.example.strangertalk.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.strangertalk.R;
import com.google.firebase.auth.FirebaseAuth;


public class WelcomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            goToNextActivity();
        }

        findViewById(R.id.getStartedBtn).setOnClickListener( v ->{
            goToNextActivity();
        });
    }
    void goToNextActivity(){
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        finish();
    }
}