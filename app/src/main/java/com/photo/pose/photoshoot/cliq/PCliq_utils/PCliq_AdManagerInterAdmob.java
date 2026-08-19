package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.content.Context;
import android.os.Bundle;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookMediationAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import androidx.annotation.NonNull;

public class PCliq_AdManagerInterAdmob {
    static InterstitialAd interAd;
    private final Context ctx;

    public PCliq_AdManagerInterAdmob(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        AdRequest adRequest;
        if (ConsentInformation.getInstance(ctx).getConsentStatus() == ConsentStatus.PERSONALIZED) {
            adRequest = new AdRequest.Builder()
                    .build();
        } else {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            if(PCliq_Constant.interstitialAdType.equals(PCliq_Constant.AD_TYPE_ADMOB)) {
                adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
            } else {
                adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, new Bundle())
                        .addNetworkExtrasBundle(FacebookMediationAdapter.class, extras)
                        .build();
            }
        }
        InterstitialAd.load(ctx, PCliq_Constant.interstitialAdID, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                interAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            }
        });
    }

    public InterstitialAd getAd() {
        return interAd;
    }

    public static void setAd(InterstitialAd interstitialAd) {
        interAd = interstitialAd;
    }
}