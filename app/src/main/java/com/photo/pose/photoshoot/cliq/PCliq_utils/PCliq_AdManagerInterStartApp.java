package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.content.Context;

import com.startapp.sdk.adsbase.StartAppAd;

public class PCliq_AdManagerInterStartApp {
    static StartAppAd startAppAd;
    private final Context ctx;

    public PCliq_AdManagerInterStartApp(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        startAppAd = new StartAppAd(ctx);
        startAppAd.loadAd();
    }

    public StartAppAd getAd() {
        return startAppAd;
    }

    public static void setAd(StartAppAd startAppAdInter) {
        startAppAd = startAppAdInter;
    }
}