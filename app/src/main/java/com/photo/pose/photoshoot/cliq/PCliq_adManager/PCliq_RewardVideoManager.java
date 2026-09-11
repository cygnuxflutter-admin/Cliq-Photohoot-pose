package com.photo.pose.photoshoot.cliq.PCliq_adManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
//import com.vehicle.information.trending.rtoexam.rto.R;
//import com.vehicle.information.trending.rtoexam.rto.Task_utils.Task_PreferenceClass;

public class PCliq_RewardVideoManager {
    private static PCliq_PreferenceClass taskPreferenceClass;
    private static String AD_google_Rw;
    private static AlertDialog alertDialog;
    public static RewardedInterstitialAd mRewardedAd;

    static boolean isUserEarnReward = false;
    public static com.facebook.ads.InterstitialAd interstitialFB;

    public static void showRewardVideoAd(final Activity context, OnRewardAdLoadInterface onAdLoadInterface) {
        isUserEarnReward = false;
        if (taskPreferenceClass == null) {
            taskPreferenceClass = new PCliq_PreferenceClass(context);
        }
        AD_google_Rw = taskPreferenceClass.getAdsId("GoogleInterstialRewardAd");
        if (AD_google_Rw == null || AD_google_Rw.trim().isEmpty()) {
            onAdLoadInterface.onAdClose(true);
            return;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pcliq_lottie_anim_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedInterstitialAd.load(context, AD_google_Rw, adRequest, new RewardedInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedInterstitialAd ad) {
                mRewardedAd = ad;
                if (alertDialog != null && alertDialog.isShowing() && !context.isFinishing()) {
                    try {
                        alertDialog.dismiss();
                    } catch (Exception ignored) {}
                }
                if (mRewardedAd != null) {
                    mRewardedAd.show(context, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            isUserEarnReward = true;
                        }
                    });
                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            onAdLoadInterface.onAdClose(isUserEarnReward);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            onAdLoadInterface.onAdClose(true);
                        }
                    });
                } else {
                    onAdLoadInterface.onAdClose(true);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (alertDialog != null && alertDialog.isShowing() && !context.isFinishing()) {
                    try {
                        alertDialog.dismiss();
                    } catch (Exception ignored) {}
                }
                onAdLoadInterface.onAdClose(true);
            }
        });

    }

    public static void fbInterstitial(Context context, OnRewardAdLoadInterface onAdLoadInterface) {
        interstitialFB = new com.facebook.ads.InterstitialAd(context, taskPreferenceClass.getAdsId("FbInterstitialAd"));
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
//                    Log.e("#1", "" + ad.toString());
                // Interstitial ad displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
//                    Log.e("#2", "" + ad.toString());
                // Interstitial dismissed callback
                if (alertDialog != null) {
                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
                onAdLoadInterface.onAdClose(true);
            }

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                Log.e("#3", "" + adError.getErrorMessage());
                Log.e("#3_1", "" + adError.getErrorCode());
                // Ad error callback
                if (alertDialog != null) {
                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
                onAdLoadInterface.onAdFail();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                // Show the ad
//                    Log.e("#2", "" + ad.toString());
                if (alertDialog != null) {
                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
                interstitialFB.show();

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        };
        com.facebook.ads.InterstitialAd interstitialAd = interstitialFB;
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());
    }

    public interface OnRewardAdLoadInterface {
        void onAdClose(boolean isWithReward);

        void onAdFail();
    }
}