package com.example.daveexpress;

import static com.example.daveexpress.utils.Constants.ONESIGNAL_APP_ID;

import android.app.Application;

import com.onesignal.OneSignal;


public class ApplicationClass extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }
}
