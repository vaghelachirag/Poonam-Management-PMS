package com.pms.rcuapp.room;

import android.app.Application;
import android.util.Log;


import com.google.firebase.FirebaseApp;

import java.util.Objects;

import io.reactivex.plugins.RxJavaPlugins;


public class InitDb extends Application {
    public static AppDatabase appDatabase;

    private long lastLocationUpdateTimeOnServer;
    private Boolean mRequestingLocationUpdates = false;



    public static InitDb instance;


    public static InitDb getInstance() {
        return (InitDb) instance;
    }


    public Boolean getmRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public void setmRequestingLocationUpdates(Boolean mRequestingLocationUpdates) {
        this.mRequestingLocationUpdates = mRequestingLocationUpdates;
    }

    public void printLog(String tag, String response) {
        if (tag != null && response != null) {
            Log.e(tag, response);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        appDatabase = AppDatabase.getDatabase(this);
        RxJavaPlugins.setErrorHandler(throwable -> {
            Log.e("Error", Objects.requireNonNull(throwable.getMessage()));
        }); // nothing or some logging
    }

}
