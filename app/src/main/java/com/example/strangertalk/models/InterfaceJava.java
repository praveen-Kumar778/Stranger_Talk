package com.example.strangertalk.models;

import android.webkit.JavascriptInterface;

import com.example.strangertalk.activities.CallActivity;

public class InterfaceJava {
    CallActivity callActivity;
    public InterfaceJava(CallActivity callActivity){
        this.callActivity = callActivity;
    }
    @JavascriptInterface
    public void onPeerConnected(){
    callActivity.onPeerConnected();
    }
}
