package com.example.strangertalk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.strangertalk.R;
import com.example.strangertalk.databinding.ActivityCallBinding;
import com.example.strangertalk.models.InterfaceJava;
import com.example.strangertalk.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.DatabaseMetaData;
import java.util.UUID;

public class CallActivity extends AppCompatActivity {

    ActivityCallBinding binding ;
    String uniqueId = "";
    FirebaseAuth mAuth ;
    String userName="";
    String incoming = "" ;
    String friendUsername = "";

    boolean isPeerConnected = false;

    DatabaseReference reference ;

    boolean isAudio = true ;
    boolean isVideo = true ;

    String createdBy ;
    boolean pageExit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("users");

        userName = getIntent().getStringExtra("userName");
        incoming = getIntent().getStringExtra("incoming");
        createdBy = getIntent().getStringExtra("createdBy");

       /* friendUsername = "";

        if(incoming.equalsIgnoreCase(friendUsername))*/
            friendUsername = incoming ;

        setWebView();


        binding.callAudio.setOnClickListener(v ->{
            isAudio = !isAudio;
            callJavascriptFunction("javascript:toggleAudio(\""+isAudio+"\")");
            if(isAudio){
                binding.callAudio.setImageResource(R.drawable.btn_unmute_normal);
            }else{
                binding.callAudio.setImageResource(R.drawable.btn_mute_normal);
            }
        });

        binding.callVideo.setOnClickListener(v->{
            isVideo = !isVideo;
            callJavascriptFunction("javascript:toggleVideo(\""+isVideo+"\")");
            if(isVideo){
                binding.callVideo.setImageResource(R.drawable.btn_video_normal);
            }else{
                binding.callVideo.setImageResource(R.drawable.btn_video_muted);
            }
        });
        binding.callEnd.setOnClickListener(v->{
            startActivity(new Intent(CallActivity.this,MainActivity.class));
            finish();
        });
    }
    void setWebView(){
        binding.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }
        });
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webView.addJavascriptInterface(new InterfaceJava(this),"Android");

        //load Video call
        loadVideoCall();
    }

    void loadVideoCall(){
        String file = "file:android_asset/call.html";
        binding.webView.loadUrl(file);

        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //peer connected
                initializePeer();
            }
        });
    }
    void initializePeer(){
        uniqueId = getUniqueId();

        callJavascriptFunction("javascript:init(\""+uniqueId+"\")");
        if(createdBy.equalsIgnoreCase(userName)){
            if(pageExit)
                return;
            reference.child(userName).child("connId").setValue(uniqueId);
            reference.child(userName).child("isAvailable").setValue(true);

            FirebaseDatabase.getInstance().getReference()
                            .child("Profiles")
                                    .child(friendUsername)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Users users = snapshot.getValue(Users.class);
                                                    Glide.with(CallActivity.this)
                                                            .load(users.getProfile())
                                                            .into(binding.callProfileImage);
                                                    binding.callProfileName.setText(users.getName());
                                                    binding.callPlaceName.setText(users.getCity());
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

            binding.loadingGroup.setVisibility(View.GONE);
            binding.controls.setVisibility(View.VISIBLE);

        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    friendUsername = createdBy;
                    FirebaseDatabase.getInstance().getReference()
                            .child("Profiles")
                            .child(friendUsername)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Users users = snapshot.getValue(Users.class);
                                    Glide.with(CallActivity.this)
                                            .load(users.getProfile())
                                            .into(binding.callProfileImage);
                                    binding.callProfileName.setText(users.getName());
                                    binding.callPlaceName.setText(users.getCity());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(friendUsername)
                            .child("connId")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getValue() != null){
                                        // send call request
                                        sendCallRequest();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            },2000);
        }
    }
   public  void onPeerConnected(){
        isPeerConnected = true ;
    }
    void sendCallRequest(){
        if(!isPeerConnected) {
            Toast.makeText(this, "You are not connected please check your internet.", Toast.LENGTH_SHORT).show();
            return;
        }
        //listenConnId;
        listenConnId();
    }
    void listenConnId(){
        reference.child(friendUsername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() == null)
                    return;
                binding.loadingGroup.setVisibility(View.GONE);
                binding.controls.setVisibility(View.VISIBLE);
                String connId = snapshot.getValue(String.class);
                callJavascriptFunction("javascript:startCall(\""+connId+"\")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void callJavascriptFunction(String function){
        // whenever in webView if we want to call a function on a button click so we can achieve by doing this line of codes
        binding.webView.post(new Runnable() {
            @Override
            public void run() {
                binding.webView.evaluateJavascript(function,null);
            }
        });
    }

    String getUniqueId(){
        // using this we can create the unique id  in java
        return UUID.randomUUID().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageExit = true ;
        reference.child(createdBy).setValue(null);
        binding.webView.loadUrl(null);
        finish();
    }
}