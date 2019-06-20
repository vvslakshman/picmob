package com.picmob.Activity;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.picmob.Networking.BaseApplication;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        BaseApplication.getInstance().getSession().setDeviceToken(refreshedToken);
        Log.e("TAG", "refresh token: "+refreshedToken );
    }

}
