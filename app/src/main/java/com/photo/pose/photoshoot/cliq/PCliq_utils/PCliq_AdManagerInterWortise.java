package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.content.Context;

import com.wortise.ads.interstitial.InterstitialAd;

public class PCliq_AdManagerInterWortise {
    static InterstitialAd interAd;
    private final Context ctx;

    public PCliq_AdManagerInterWortise(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        interAd = new InterstitialAd((ctx), PCliq_Constant.interstitialAdID);
        interAd.loadAd();
    }

    public InterstitialAd getAd() {
        return interAd;
    }

    public static void setAd(InterstitialAd interstitialAd) {
        interAd = interstitialAd;
    }
}