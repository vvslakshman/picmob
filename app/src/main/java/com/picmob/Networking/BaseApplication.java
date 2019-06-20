package com.picmob.Networking;

import android.app.Application;
import com.picmob.LocalStorage.SessionManager;
import com.splunk.mint.Mint;


public class BaseApplication extends Application {
    private static BaseApplication mInstance;
    private static ApiInterface apiService;
    public static SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        if(mInstance==null){
            mInstance=this;
        }
        if (apiService == null) {
            apiService = ApiClient.getClient().create(ApiInterface.class);
        }

        Mint.initAndStartSession(this, "492f6ba3");
    }
    public static synchronized BaseApplication getInstance(){
        return mInstance;
    }
    public ApiInterface getApiClient() {
        return apiService;
    }
    public SessionManager getSession() {
        if (sessionManager == null) {
            sessionManager = new SessionManager(getApplicationContext());
        }
        return sessionManager;
    }
}
