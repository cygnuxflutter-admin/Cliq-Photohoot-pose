package com.photo.pose.photoshoot.cliq;


import android.app.Activity;
import android.app.Dialog;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_AppOpenManager;
import com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_InterstitialAdManager;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_GlobalContext;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;


public class MyApplication extends android.app.Application {

    public static boolean isShowingAppOpen = true, isAdsSplash = true;
    public PCliq_AppOpenManager taskAppOpenManager;
    private PCliq_InterstitialAdManager taskInterstitialAdManager;
    public static MyApplication mInstance;
    public static boolean AUTHENTICATED = false;
    PCliq_SharedPref sharedPref;
    public static SharedPreferences preferences;

    private static RequestQueue localRequestQueue, searchRequestQueue;


    public static void showInterstitialAd(Activity activity, PCliq_InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        ((MyApplication) activity.getApplication()).getInterstitialAdManager().showAdIfAvailable(activity, onAdLoadInterface);
    }

    public static void showInterstitialAdWithOutCount(Activity activity, PCliq_InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        ((MyApplication) activity.getApplication()).getInterstitialAdManager().showInterstitialAd(activity, onAdLoadInterface);
    }

    public static void showFaceBookInterstitial(Activity activity, PCliq_InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        ((MyApplication) activity.getApplication()).getInterstitialAdManager().showFaceBookInterstitial(activity, onAdLoadInterface);
    }

    public static void showEditInterstitialAd(Activity activity, PCliq_InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        ((MyApplication) activity.getApplication()).getInterstitialAdManager().showEDitAdIfAvailable(activity, onAdLoadInterface);
    }

    public PCliq_InterstitialAdManager getInterstitialAdManager() {
        if (taskInterstitialAdManager == null) {
            taskInterstitialAdManager = new PCliq_InterstitialAdManager(this);
        }
        return taskInterstitialAdManager;
    }

    public void loadInterstitialAd() {
        if (taskInterstitialAdManager == null)
            taskInterstitialAdManager = new PCliq_InterstitialAdManager(MyApplication.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupGlobalNetworkListener();
        mInstance = this;

        // Enable verbose OneSignal logging to debug issues if needed.
        // OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("5e89cd25-7026-45fb-802d-e17f5256e157");
        OneSignal.promptForPushNotifications();
        // OneSignal.sendTag("Apps", "Cliq Poses");

        AudienceNetworkAds.initialize(this);

//        List<String> testDeviceIds = Collections.singletonList("9EB1C89D5458256B2C93F844BAAC93F5");
//        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        MobileAds.setRequestConfiguration(configuration);

        MobileAds.initialize(this, initializationStatus -> Log.d(" AD", " RTO open ad"));
        taskAppOpenManager = new PCliq_AppOpenManager(this);
        PCliq_GlobalContext.initialize(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        FirebaseAnalytics.getInstance(getApplicationContext());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        localRequestQueue = Volley.newRequestQueue(this);

        try {
            PCliq_DBHelper dbHelper = new PCliq_DBHelper(getApplicationContext());
            dbHelper.onCreate(dbHelper.getWritableDatabase());
            PCliq_Constant.arrayListColors.clear();
            PCliq_Constant.arrayListColors.addAll(dbHelper.getColors());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        FacebookSdk.sdkInitialize(getApplicationContext());

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

    }

    public static synchronized MyApplication getInstance() {
        MyApplication myApp;
        synchronized (MyApplication.class) {
            myApp = mInstance;
        }
        return myApp;
    }

//    public static User me;

//    public void checkAuthenticationState() {
//        // check if authenticated
//        StringRequest checkIfAuthenticatedRequest = new StringRequest(Request.Method.GET, UrlBuilder.getProfile(), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "onResponse: 200 OK\n" + response);
//                me = new Serializer().getUser(response);
////                MainActivity.populateNavHeader();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, "onErrorResponse: " + error.toString());
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return Params.getAuthenticatedParams(MyApplication.this);
//            }
//
//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                int responseCode = response.statusCode;
//                if (responseCode == 200) {
//                    MyApplication.AUTHENTICATED = true;
//                    Log.d(TAG, "parseNetworkResponse: AUTHENTICATED");
//                } else {
//                    MyApplication.AUTHENTICATED = false;
//                    Log.d(TAG, "parseNetworkResponse: NOT AUTHENTICATED");
//                    SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.access_token_shared_preferences), MODE_PRIVATE).edit();
//                    editor.putString(getResources().getString(R.string.access_token_storage_key), null);
//                    editor.apply();
//                }
//                return super.parseNetworkResponse(response);
//            }
//        };
//
//        localRequestQueue.add(checkIfAuthenticatedRequest);
//    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    public void showAdIfAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        taskAppOpenManager.showAdIfSplashAvailable(activity, onShowAdCompleteListener);
    }

    public void showAdIfHomeAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        taskAppOpenManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    public void sendRequest() {
        taskAppOpenManager.sendRequest();
    }

    public boolean isAdAvailable() {
        return taskAppOpenManager.isAdAvailable();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    private Dialog noInternetDialog;
    private Activity currentActivity;
    private Activity dialogActivity;
    private boolean isNetworkConnected = true;

    private void setupGlobalNetworkListener() {
        ConnectivityManager initialCm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (initialCm != null) {
            android.net.NetworkInfo activeNetwork = initialCm.getActiveNetworkInfo();
            isNetworkConnected = activeNetwork != null && activeNetwork.isConnected();
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                currentActivity = activity;
                updateNetworkDialogVisibility();
            }
            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivity = activity;
                updateNetworkDialogVisibility();
            }
            @Override
            public void onActivityPaused(@NonNull Activity activity) {}
            @Override
            public void onActivityStopped(@NonNull Activity activity) {}
            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (currentActivity == activity) {
                    currentActivity = null;
                }
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    isNetworkConnected = true;
                    updateNetworkDialogVisibility();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    android.net.NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                    isNetworkConnected = activeNetwork != null && activeNetwork.isConnected();
                    updateNetworkDialogVisibility();
                }
            });
        }
    }

    private void updateNetworkDialogVisibility() {
        if (currentActivity != null) {
            currentActivity.runOnUiThread(() -> {
                if (isNetworkConnected) {
                    if (noInternetDialog != null && noInternetDialog.isShowing()) {
                        try { noInternetDialog.dismiss(); } catch (Exception e) {}
                    }
                } else {
                    if (noInternetDialog == null || dialogActivity != currentActivity) {
                        if (noInternetDialog != null && noInternetDialog.isShowing()) {
                            try { noInternetDialog.dismiss(); } catch (Exception e) {}
                        }
                        dialogActivity = currentActivity;
                        noInternetDialog = new Dialog(currentActivity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                        noInternetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        noInternetDialog.setContentView(R.layout.pcliq_layout_no_internet);
                        noInternetDialog.setCancelable(false);
                        Window window = noInternetDialog.getWindow();
                        if (window != null) {
                            window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        }
                    }
                    if (!noInternetDialog.isShowing() && !currentActivity.isFinishing()) {
                        try { noInternetDialog.show(); } catch (Exception e) {}
                    }
                }
            });
        }
    }

    public static RequestQueue getLocalRequestQueue() {

        return localRequestQueue;
    }
}