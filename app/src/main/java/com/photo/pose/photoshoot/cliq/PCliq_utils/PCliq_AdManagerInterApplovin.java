package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.app.Activity;
import android.content.Context;

import com.applovin.mediation.ads.MaxInterstitialAd;

public class PCliq_AdManagerInterApplovin {
    static MaxInterstitialAd interstitialAd;
    private final Context ctx;

    public PCliq_AdManagerInterApplovin(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        interstitialAd = new MaxInterstitialAd(PCliq_Constant.interstitialAdID, (Activity) ctx);
        interstitialAd.loadAd();
    }

    public MaxInterstitialAd getAd() {
        return interstitialAd;
    }

    public static void setAd(MaxInterstitialAd appLovinInter) {
        interstitialAd = appLovinInter;
    }
}