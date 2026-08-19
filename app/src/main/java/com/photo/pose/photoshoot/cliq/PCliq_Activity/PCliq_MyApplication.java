package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.facebook.FacebookSdk;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

public class PCliq_MyApplication extends Application {

    PCliq_SharedPref sharedPref;
    public static SharedPreferences preferences;
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        FirebaseAnalytics.getInstance(getApplicationContext());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        try {
            PCliq_DBHelper dbHelper = new PCliq_DBHelper(getApplicationContext());
            dbHelper.onCreate(dbHelper.getWritableDatabase());
            PCliq_Constant.arrayListColors.clear();
            PCliq_Constant.arrayListColors.addAll(dbHelper.getColors());
        } catch (Exception e) {
            e.printStackTrace();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());

        sharedPref = new PCliq_SharedPref(this);

        String mode = sharedPref.getDarkMode();
        switch (mode) {
            case PCliq_Constant.DARK_MODE_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case PCliq_Constant.DARK_MODE_OFF:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case PCliq_Constant.DARK_MODE_ON:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }

        sharedPref.getAdDetails();
        new PCliq_Methods(getApplicationContext()).initializeAds();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}