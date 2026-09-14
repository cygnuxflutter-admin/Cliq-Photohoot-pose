package com.photo.pose.photoshoot.cliq.PCliq_adManager;

import static androidx.lifecycle.Lifecycle.Event.ON_START;
import static com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.photo.pose.photoshoot.cliq.BuildConfig;
import com.photo.pose.photoshoot.cliq.MyApplication;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;

import java.util.Date;

public class PCliq_AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private static final String LOG_TAG = "AppOpenManager";
    private AppOpenAd appOpenAd = null;
    private AppOpenAdLoadCallback loadCallback;
    private final MyApplication myApplication;
    private static boolean isShowingAd = false;
    private Activity currentActivity;
    private long loadTime = 0;
    private static PCliq_PreferenceClass taskPreferenceClass;
    private String AD_UNIT_ID1, AD_UNIT_ID2;

    /**
     * Constructor
     */
    public PCliq_AppOpenManager(MyApplication myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * Request an ad
     */
    public void fetchAd() {
        if (BuildConfig.DEBUG) {
            return;
        }
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return;
        }

        loadCallback = new AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(AppOpenAd ad) {
                PCliq_AppOpenManager.this.appOpenAd = ad;
                PCliq_AppOpenManager.this.loadTime = (new Date()).getTime();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                // Handle the error.
                fetchAdX();
            }
        };

        if (taskPreferenceClass == null) {
            taskPreferenceClass = new PCliq_PreferenceClass(myApplication);
        }
        AD_UNIT_ID1 = taskPreferenceClass.getAdsId("GoogleAppopenAd");
        AD_UNIT_ID2 = taskPreferenceClass.getAdsId("AdxAppOpenID");
        Log.e("TAG%%OpenAds", "GoogleAppopenAd: " + taskPreferenceClass.getAdsId("GoogleAppopenAd"));
        Log.e("TAG%%OpenAds", "AdxAppOpenID: " + taskPreferenceClass.getAdsId("AdxAppOpenID"));
        AdRequest request = getAdRequest();
        AppOpenAd.load(myApplication, AD_UNIT_ID1, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    public void fetchAdX() {
        if (BuildConfig.DEBUG) {
            return;
        }
        if (isAdAvailable()) {
            return;
        }
        loadCallback = new AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                PCliq_AppOpenManager.this.appOpenAd = ad;
                PCliq_AppOpenManager.this.loadTime = new Date().getTime();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

            }
        };
        if (taskPreferenceClass == null) {
            taskPreferenceClass = new PCliq_PreferenceClass(myApplication);
        }
        AD_UNIT_ID1 = taskPreferenceClass.getAdsId("GoogleAppopenAd");
         AD_UNIT_ID2 = taskPreferenceClass.getAdsId("AdxAppOpenID");

        Log.e("TAG%%OpenAds", "AdxAppOpenID: " + taskPreferenceClass.getAdsId("AdxAppOpenID"));
       // Log.e("TAG%%", "fbInterstitialAdId: " + taskPreferenceClass.getAdsId("FbInterstitialAd"));
        AdRequest request = getAdRequest();
        AppOpenAd.load(myApplication, AD_UNIT_ID2, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        if (BuildConfig.DEBUG) {
            return false;
        }
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void sendRequest() {
        if (!isShowingAd && isAdAvailable()) {
            if (MyApplication.isShowingAppOpen) {

            }
        } else {
            fetchAd();
        }
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable() {
        if (BuildConfig.DEBUG) {
            return;
        }
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            if (MyApplication.isShowingAppOpen) {
                FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        PCliq_AppOpenManager.this.appOpenAd = null;
                        isShowingAd = false;
                        fetchAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        isShowingAd = true;
                    }
                };

                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                appOpenAd.show(currentActivity);
            }
        } else {
            fetchAd();
        }
    }

    public void showAdIfSplashAvailable(@NonNull final Activity activity, @NonNull MyApplication.OnShowAdCompleteListener onShowAdCompleteListener) {
        if (BuildConfig.DEBUG) {
            onShowAdCompleteListener.onShowAdComplete();
            return;
        }
        if (!isShowingAd && isAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    PCliq_AppOpenManager.this.appOpenAd = null;
                    isShowingAd = false;
                    fetchAd();
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                }
            };
            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(activity);
        } else {
            loadCallback = new AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(AppOpenAd ad) {
                    PCliq_AppOpenManager.this.appOpenAd = ad;
                    PCliq_AppOpenManager.this.loadTime = (new Date()).getTime();

                    FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            PCliq_AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
                            fetchAd();
                            onShowAdCompleteListener.onShowAdComplete();
                            Log.e("TAG", "##AppOpen 1: ");
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            onShowAdCompleteListener.onShowAdComplete();
                            Log.e("TAG", "##AppOpen 2:Fail 0  "+adError.getMessage());
                            Log.e("TAG", "##AppOpen 2:Fail 1  "+adError.getCode());
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                            Log.e("TAG", "##AppOpen 3:  ");
                        }
                    };
                    appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                    appOpenAd.show(currentActivity);
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    onShowAdCompleteListener.onShowAdComplete();
                    Log.e("TAG", "##AppOpen 4:Fail 0  "+loadAdError.getMessage());
                    Log.e("TAG", "##AppOpen 4:Fail 1  "+loadAdError.getCode());
                }
            };
            if (taskPreferenceClass == null) {
                taskPreferenceClass = new PCliq_PreferenceClass(myApplication);
            }
            AD_UNIT_ID1 = taskPreferenceClass.getAdsId("GoogleAppopenAd");
            AD_UNIT_ID2 = taskPreferenceClass.getAdsId("AdxAppOpenID");
            AdRequest request = getAdRequest();
            AppOpenAd.load(myApplication, AD_UNIT_ID1, request,AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
        }
    }

    public void showAdIfAvailable(@NonNull final Activity activity, @NonNull MyApplication.OnShowAdCompleteListener onShowAdCompleteListener) {
        if (BuildConfig.DEBUG) {
            onShowAdCompleteListener.onShowAdComplete();
            return;
        }
        if (!isShowingAd && isAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    PCliq_AppOpenManager.this.appOpenAd = null;
                    isShowingAd = false;
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                }
            };
            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(activity);
        } else {
            onShowAdCompleteListener.onShowAdComplete();
        }
    }

    /**
     * ActivityLifecycleCallback methods
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        if (!MyApplication.isAdsSplash) {
            showAdIfAvailable();
        }
    }

    /**
     * Utility method to check if ad was loaded more than n hours ago.
     */
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

}