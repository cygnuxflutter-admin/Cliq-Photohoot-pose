package com.photo.pose.photoshoot.cliq.PCliq_adManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
//import com.vehicle.information.trending.rtoexam.rto.Task_utils.Task_PreferenceClass;

public class PCliq_InterstitialAdManager {

    private final String admobInterstitialAdId, fbInterstitialAdId;
    private final Context context;
    private final PCliq_PreferenceClass taskPreferenceClass;
    private final String adXInterstitialAdId;
    private InterstitialAd admobInterstitialAd;
    private com.facebook.ads.InterstitialAd fbInterstitialAd;
    private OnAdLoadInterface onAdLoadInterface;
    private boolean isFailed = false;

    private com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog progressDialog;

    public PCliq_InterstitialAdManager(Context context) {
        this.context = context;
        taskPreferenceClass = new PCliq_PreferenceClass(this.context);
        admobInterstitialAdId = taskPreferenceClass.getAdsId("GoogleInterstitialAd");
        adXInterstitialAdId = taskPreferenceClass.getAdsId("AdxInterstitalAdunitID");
        fbInterstitialAdId = taskPreferenceClass.getAdsId("FbInterstitialAd");

        Log.e("TAG%%", "admobInterstitialAdId: " + taskPreferenceClass.getAdsId("GoogleInterstitialAd"));
        Log.e("TAG%%", "adXInterstitialAdId: " + taskPreferenceClass.getAdsId("AdxInterstitalAdunitID"));
        Log.e("TAG%%", "fbInterstitialAdId: " + taskPreferenceClass.getAdsId("FbInterstitialAd"));
        fetchAdMobAd();
//        fetchFbAd();
    }

    private void fetchFbAd() {

        fbInterstitialAd = new com.facebook.ads.InterstitialAd(context, fbInterstitialAdId);

        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // fetchAdMobAd();
//                fetchFbAd();
                if (onAdLoadInterface != null) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    onAdLoadInterface.onAdClose();
                }
            }
            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                isFailed = true;
            }

            @Override
            public void onAdLoaded(Ad ad) {
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        fbInterstitialAd.loadAd(fbInterstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());

    }

    public void fetchAdMobAd() {

        if (isAdmobAdAvailable()) {
            return;
        }


        InterstitialAdLoadCallback loadCallback = new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {

                admobInterstitialAd = ad;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                fetchAdXAd();
            }
        };
        AdRequest request = getAdRequest();
        InterstitialAd.load(context, admobInterstitialAdId, request, loadCallback);
    }

    public void fetchAdXAd() {
        if (isAdmobAdAvailable()) {
            return;
        }

        InterstitialAdLoadCallback loadCallback = new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                admobInterstitialAd = ad;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                fetchFbAd();
            }
        };

        AdRequest request = getAdRequest();
        InterstitialAd.load(context, adXInterstitialAdId, request, loadCallback);
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public boolean isAdmobAdAvailable() {
        return admobInterstitialAd != null;
    }

    public boolean isFbAdAvailable() {
        return fbInterstitialAd != null && fbInterstitialAd.isAdLoaded() && !fbInterstitialAd.isAdInvalidated();
    }

    public void showAdIfAvailable(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        this.onAdLoadInterface = onAdLoadInterface;



        if (isFailed) {
            isFailed = false;
            fetchAdMobAd();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            onAdLoadInterface.onAdClose();
            return;
        }


        int interstitalAdStatus = taskPreferenceClass.getAdsStatus("InerstialClickCount");

        int getClickCount = taskPreferenceClass.getInt("getClickCount");
        if (getClickCount < interstitalAdStatus) {
            taskPreferenceClass.setInt("getClickCount", getClickCount + 1);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            onAdLoadInterface.onAdClose();
            return;
        }

        progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(activity);
        progressDialog.setMessage("Ad Showing...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                taskPreferenceClass.setInt("getClickCount", 0);

                if (isAdmobAdAvailable()) {
                    FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            admobInterstitialAd = null;
                            isFailed = true;
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            onAdLoadInterface.onAdClose();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            admobInterstitialAd = null;
                            fetchAdMobAd();
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            onAdLoadInterface.onAdClose();
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                        }
                    };
                    admobInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        admobInterstitialAd.show(activity);
                    } catch (Exception e) {
                        onAdLoadInterface.onAdClose();
                    }
                } else if (isFbAdAvailable()) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        fbInterstitialAd.show();
                    } catch (Exception e) {
                        onAdLoadInterface.onAdClose();
                    }
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    onAdLoadInterface.onAdClose();
                }
            }
        }, 2000);


    }
//    public void showAdIfAvailable(Activity activity, OnAdLoadInterface onAdLoadInterface) { // if google sec code implement open this method and comment above method
//        this.onAdLoadInterface = onAdLoadInterface;
//
//        if (isFailed) {
//            isFailed = false;
//            fetchAdMobAd();
//            fetchFbAd();
//            if (progressDialog.isShowing()) {
//        progressDialog.dismiss();
//    }
//            onAdLoadInterface.onAdClose();
//            return;
//        }
//
//        int interstitalAdStatus = taskPreferenceClass.getAdsStatus("InerstialClickCount");
//        int googleAdsTime =  taskPreferenceClass.getAdsStatus("GoogleAdsTime");//this flag for Google ads stop 20 sec after ones show
//
//        int getClickCount = taskPreferenceClass.getInt("getClickCount");
//        if (getClickCount < interstitalAdStatus) {
//            taskPreferenceClass.setInt("getClickCount", getClickCount + 1);
//            if (progressDialog.isShowing()) {
//        progressDialog.dismiss();
//    }
//            onAdLoadInterface.onAdClose();
//            return;
//        }
//
//        taskPreferenceClass.setInt("getClickCount", 0);
//
//        long currentTime = System.currentTimeMillis();
//        long lastGoogleAdShownTime = taskPreferenceClass.getLong("lastGoogleAdShownTime");
//        long timeDifference = currentTime - lastGoogleAdShownTime;
//
//        if (timeDifference < googleAdsTime * 1000) {
//            if (isFbAdAvailable()) {
//                fbInterstitialAd.show();
//            } else {
//            if (progressDialog.isShowing()) {
//        progressDialog.dismiss();
//    }
//                onAdLoadInterface.onAdClose();
//            }
//        } else {
//            taskPreferenceClass.setLong("lastGoogleAdShownTime", currentTime);
//
//            if (isAdmobAdAvailable()) {
//                FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
//                    @Override
//                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
//                        super.onAdFailedToShowFullScreenContent(adError);
//                        admobInterstitialAd = null;
//                        isFailed = true;
//            if (progressDialog.isShowing()) {
//        progressDialog.dismiss();
//    }
//                        onAdLoadInterface.onAdClose();
//                    }
//
//                    @Override
//                    public void onAdShowedFullScreenContent() {
//                        super.onAdShowedFullScreenContent();
//                    }
//
//                    @Override
//                    public void onAdDismissedFullScreenContent() {
//                        super.onAdDismissedFullScreenContent();
//                        admobInterstitialAd = null;
//                        fetchAdMobAd();
//            if (progressDialog.isShowing()) {
//        progressDialog.dismiss();
//    }
//                        onAdLoadInterface.onAdClose();
//                    }
//
//                    @Override
//                    public void onAdImpression() {
//                        super.onAdImpression();
//                    }
//                };
//                admobInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
//                admobInterstitialAd.show(activity);
//            } else if (isFbAdAvailable()) {
//                fbInterstitialAd.show();
//            } else {
//            if (progressDialog.isShowing()) {
//        progressDialog.dismiss();
//    }
//                onAdLoadInterface.onAdClose();
//            }
//        }
//    }

    public void showInterstitialAd(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        this.onAdLoadInterface = onAdLoadInterface;

        if (isFailed) {
            isFailed = false;
            fetchAdMobAd();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            onAdLoadInterface.onAdClose();
            return;
        }
        int interstitalAdStatus = taskPreferenceClass.getAdsStatus("InerstialClickCount");

        int getClickCount = taskPreferenceClass.getInt("getClickCount");
        if (getClickCount < interstitalAdStatus) {
            taskPreferenceClass.setInt("getClickCount", getClickCount + 1);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            onAdLoadInterface.onAdClose();
            return;
        }

        taskPreferenceClass.setInt("getClickCount", 0);

        if (isAdmobAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    admobInterstitialAd = null;
                    isFailed = true;
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    onAdLoadInterface.onAdClose();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    admobInterstitialAd = null;
                    fetchAdMobAd();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    onAdLoadInterface.onAdClose();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            };
            admobInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        admobInterstitialAd.show(activity);
                    } catch (Exception e) {
                        onAdLoadInterface.onAdClose();
                    }
        } else if (isFbAdAvailable()) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        fbInterstitialAd.show();
                    } catch (Exception e) {
                        onAdLoadInterface.onAdClose();
                    }
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            onAdLoadInterface.onAdClose();
        }

    }

    public void showFaceBookInterstitial(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        this.onAdLoadInterface = onAdLoadInterface;
        if (isFbAdAvailable()) {
            fbInterstitialAd.show();
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            onAdLoadInterface.onAdClose();
        }

    }

    public void showEDitAdIfAvailable(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        this.onAdLoadInterface = onAdLoadInterface;

        if (isFailed) {
            isFailed = false;
            fetchAdMobAd();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            onAdLoadInterface.onAdClose();
            return;
        }

        int interstitalAdStatus = taskPreferenceClass.getAdsStatus("EditScreenAdCount");

        int getClickCount = taskPreferenceClass.getInt("getEDitClickCount");
        if (getClickCount < interstitalAdStatus) {
            taskPreferenceClass.setInt("getEDitClickCount", getClickCount + 1);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            onAdLoadInterface.onAdClose();
            return;
        }

        taskPreferenceClass.setInt("getEDitClickCount", 0);

        if (isAdmobAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    admobInterstitialAd = null;
                    isFailed = true;
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    onAdLoadInterface.onAdClose();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    admobInterstitialAd = null;
                    fetchAdMobAd();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    onAdLoadInterface.onAdClose();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            };
            admobInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        admobInterstitialAd.show(activity);
                    } catch (Exception e) {
                        onAdLoadInterface.onAdClose();
                    }
        } else if (isFbAdAvailable()) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        fbInterstitialAd.show();
                    } catch (Exception e) {
                        onAdLoadInterface.onAdClose();
                    }
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            onAdLoadInterface.onAdClose();
        }

    }

    public interface OnAdLoadInterface {
        void onAdClose();
    }

}

