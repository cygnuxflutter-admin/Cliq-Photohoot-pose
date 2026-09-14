package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.View;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.photo.pose.photoshoot.cliq.BuildConfig;
import com.photo.pose.photoshoot.cliq.MyApplication;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemAppDetailsList;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemUserList;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_MaterialDialogUtils;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_NetworkUtils;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.photo.pose.photoshoot.cliq.R;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_SplashActivity extends AppCompatActivity {

    String cid = "", cname = "";
    PCliq_SharedPref sharedPref;
    PCliq_Methods methods;
    PCliq_DBHelper dbHelper;
    private PCliq_PreferenceClass taskPreferenceClass;

    public FirebaseDatabase database;
    private DatabaseReference project_data2;
    private InterstitialAd interstitial = null;
    public com.facebook.ads.InterstitialAd interstitialFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_splash);

        hideStatusBar();
        methods = new PCliq_Methods(this);
        sharedPref = new PCliq_SharedPref(this);
        dbHelper = new PCliq_DBHelper(this);
        taskPreferenceClass = new PCliq_PreferenceClass(this);
        MyApplication.isAdsSplash = true;

        if (getIntent().hasExtra("cid")) {
            cid = getIntent().getStringExtra("cid");
            cname = getIntent().getStringExtra("cname");
        }

        if (sharedPref.getIsFirst()) {
            getAppDetails();
        } else {
            if (!sharedPref.getIsAutoLogin()) {
                new Handler().postDelayed(() -> openMainActivity(), 2000);
            } else {
                if (sharedPref.getLoginType().equals(PCliq_Constant.LOGIN_TYPE_FB)) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        loadSocialLogin(PCliq_Constant.LOGIN_TYPE_FB, sharedPref.getAuthID());
                    } else {
                        sharedPref.setIsAutoLogin(false);
                        openMainActivity();
                    }
                } else if (sharedPref.getLoginType().equals(PCliq_Constant.LOGIN_TYPE_GOOGLE)) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        loadSocialLogin(PCliq_Constant.LOGIN_TYPE_GOOGLE, sharedPref.getAuthID());
                    } else {
                        sharedPref.setIsAutoLogin(false);
                        openMainActivity();
                    }
                } else {
                    loadLogin(PCliq_Constant.LOGIN_TYPE_NORMAL, "");
                }
            }
        }

        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PCliq_Constant.GRID_PADDING, r.getDisplayMetrics());
        PCliq_Constant.columnWidth = (int) ((methods.getScreenWidth() - ((PCliq_Constant.NUM_OF_COLUMNS + 1) * padding)) / PCliq_Constant.NUM_OF_COLUMNS);
        PCliq_Constant.columnHeight = (int) (PCliq_Constant.columnWidth * 1.44);

        startSplashEntranceAnimations();
    }

    private void startSplashEntranceAnimations() {
        View vfTL = findViewById(R.id.iv_vf_tl);
        View vfTR = findViewById(R.id.iv_vf_tr);
        View vfBL = findViewById(R.id.iv_vf_bl);
        View vfBR = findViewById(R.id.iv_vf_br);
        View topBadge = findViewById(R.id.ll_splash_top_badge);
        View logoCard = findViewById(R.id.fl_splash_logo);
        View titleRow = findViewById(R.id.ll_splash_title_row);
        View subtitle = findViewById(R.id.tv_splash_subtitle);
        View archiveBadge = findViewById(R.id.tv_splash_archive_badge);
        View bottomSection = findViewById(R.id.ll_splash_bottom);

        // Viewfinder HUD brackets animation
        View[] brackets = new View[]{vfTL, vfTR, vfBL, vfBR};
        for (View b : brackets) {
            if (b != null) {
                b.setAlpha(0f);
                b.setScaleX(1.4f);
                b.setScaleY(1.4f);
                b.animate().alpha(0.8f).scaleX(1f).scaleY(1f).setDuration(800).setStartDelay(150).start();
            }
        }

        // Top Status Badge
        if (topBadge != null) {
            topBadge.setAlpha(0f);
            topBadge.setTranslationY(-30f);
            topBadge.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(200).start();
        }

        // Central Glowing Logo Card
        if (logoCard != null) {
            logoCard.setAlpha(0f);
            logoCard.setScaleX(0.5f);
            logoCard.setScaleY(0.5f);
            logoCard.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(800)
                    .setInterpolator(new android.view.animation.OvershootInterpolator(1.3f))
                    .start();
        }

        // Title and Subtitle Row
        if (titleRow != null) {
            titleRow.setAlpha(0f);
            titleRow.setTranslationY(40f);
            titleRow.animate().alpha(1f).translationY(0f).setDuration(700).setStartDelay(250).start();
        }

        if (subtitle != null) {
            subtitle.setAlpha(0f);
            subtitle.setTranslationY(20f);
            subtitle.animate().alpha(1f).translationY(0f).setDuration(700).setStartDelay(400).start();
        }

        if (archiveBadge != null) {
            archiveBadge.setAlpha(0f);
            archiveBadge.setTranslationY(20f);
            archiveBadge.animate().alpha(1f).translationY(0f).setDuration(700).setStartDelay(500).start();
        }

        // Bottom Loader and Camera HUD specs
        if (bottomSection != null) {
            bottomSection.setAlpha(0f);
            bottomSection.animate().alpha(1f).setDuration(800).setStartDelay(600).start();
        }
    }

    private void loadLogin(final String loginType, final String authID) {
        if (methods.isNetworkAvailable()) {
            Call<PCliq_ItemUserList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getLogin(methods.getAPIRequest(PCliq_Constant.URL_LOGIN, 0, authID, loginType, "", "", "", "", "", sharedPref.getEmail(), sharedPref.getPassword(), "", "", ""));
            call.enqueue(new Callback<PCliq_ItemUserList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemUserList> call, @NonNull Response<PCliq_ItemUserList> response) {
                    if (response.body() != null && response.body().getArrayListUser() != null && response.body().getArrayListUser().size() > 0) {
                        if (response.body().getArrayListUser().get(0).getSuccess().equals("1")) {
                            sharedPref.setLoginDetails(response.body().getArrayListUser().get(0).getId(), response.body().getArrayListUser().get(0).getName(), sharedPref.getUserMobile(), sharedPref.getEmail(), response.body().getArrayListUser().get(0).getImage(), authID, sharedPref.getIsRemember(), sharedPref.getPassword(), loginType);
                            sharedPref.setIsLogged(true);
                        } else {
                            sharedPref.setLoginDetails("0", "", "", "", "", "", false, "", loginType);
                            sharedPref.setIsLogged(false);
                        }
                    } else {
                        sharedPref.setLoginDetails("0", "", "", "", "", "", false, "", loginType);
                        sharedPref.setIsLogged(false);
                    }
                    openMainActivity();
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemUserList> call, @NonNull Throwable t) {
                    call.cancel();
                    openMainActivity();
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openMainActivity();
                }
            }, 1000);
            Toast.makeText(PCliq_SplashActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSocialLogin(final String loginType, final String authID) {
        if (methods.isNetworkAvailable()) {
            Call<PCliq_ItemUserList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getSocialLogin(methods.getAPIRequest(PCliq_Constant.URL_SOCIAL_LOGIN, 0, authID, loginType, "", "", "", "", sharedPref.getUserName(), sharedPref.getEmail(), "", "", "", ""));
            call.enqueue(new Callback<PCliq_ItemUserList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemUserList> call, @NonNull Response<PCliq_ItemUserList> response) {
                    if (response.body() != null && response.body().getArrayListUser() != null && response.body().getArrayListUser().size() > 0) {
                        if (response.body().getArrayListUser().get(0).getSuccess().equals("1")) {
                            sharedPref.setLoginDetails(response.body().getArrayListUser().get(0).getId(), response.body().getArrayListUser().get(0).getName(), sharedPref.getUserMobile(), sharedPref.getEmail(), response.body().getArrayListUser().get(0).getImage(), authID, sharedPref.getIsRemember(), sharedPref.getPassword(), loginType);
                            sharedPref.setIsLogged(true);
                        }
                    }
                    openMainActivity();
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemUserList> call, @NonNull Throwable t) {
                    call.cancel();
                    openMainActivity();
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openMainActivity();
                }
            }, 1000);
            Toast.makeText(PCliq_SplashActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void getAppDetails() {
        if (methods.isNetworkAvailable()) {
            Call<PCliq_ItemAppDetailsList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getAppDetails(methods.getAPIRequest(PCliq_Constant.URL_APP_DETAILS, 0, "", "", "", "", "", "", "", "", "", "", "", ""));
            call.enqueue(new Callback<PCliq_ItemAppDetailsList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemAppDetailsList> call, @NonNull Response<PCliq_ItemAppDetailsList> response) {
                    if (response.body() != null && response.body().getArrayListAbout() != null && response.body().getArrayListAbout().size() > 0) {
                        PCliq_Constant.itemAbout = response.body().getArrayListAbout().get(0);

                        PCliq_Constant.showUpdateDialog = response.body().getArrayListAbout().get(0).isShowAppUpdate();
                        PCliq_Constant.appVersion = response.body().getArrayListAbout().get(0).getAppUpdateVersion();
                        PCliq_Constant.appUpdateMsg = response.body().getArrayListAbout().get(0).getAppUpdateMessage();
                        PCliq_Constant.appUpdateURL = response.body().getArrayListAbout().get(0).getAppUpdateLink();
                        PCliq_Constant.appUpdateCancel = response.body().getArrayListAbout().get(0).isAppUpdateCancel();

//                        Constant.appUpdateCancel = c.getBoolean("google_play_link");
                        PCliq_Constant.urlYoutube = response.body().getArrayListAbout().get(0).getYoutubeLink();
                        PCliq_Constant.urlInstagram = response.body().getArrayListAbout().get(0).getInstagramLink();
                        PCliq_Constant.urlTwitter = response.body().getArrayListAbout().get(0).getTwitterLink();
                        PCliq_Constant.urlFacebook = response.body().getArrayListAbout().get(0).getFacebookLink();

                        PCliq_Constant.isLiveWallpaperEnabled = response.body().getArrayListAbout().get(0).isLiveWallpaperOn();

                        if (response.body().getArrayListAbout().get(0).getArrayListAds() != null && response.body().getArrayListAbout().get(0).getArrayListAds().size() > 0) {
                            switch (response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getAdType()) {
                                case "Admob":
                                case "Facebook":
                                    PCliq_Constant.publisherAdID = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getPublisherId();
                                    break;
                                case "StartApp":
                                    PCliq_Constant.startappAppId = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getPublisherId();
                                    break;
                                case "Wortise":
                                    PCliq_Constant.wortiseAppId = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getPublisherId();
                                    break;
                            }
                            PCliq_Constant.bannerAdID = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getBannerID();
                            PCliq_Constant.interstitialAdID = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getInterstitialID();
                            PCliq_Constant.nativeAdID = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getNativeID();

                            PCliq_Constant.isBannerAd = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getIsBannerOn().equals("1");
                            PCliq_Constant.isInterAd = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getIsInterstitialOn().equals("1");
                            PCliq_Constant.isNativeAd = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getIsNativeOn().equals("1");

                            PCliq_Constant.interstitialAdShow = Integer.parseInt(response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getInterAdsClick());
                            PCliq_Constant.nativeAdShow = Integer.parseInt(response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getItemAdsDetails().getNativeAdsPos());

                            PCliq_Constant.bannerAdType = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getAdType();
                            PCliq_Constant.interstitialAdType = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getAdType();
                            PCliq_Constant.nativeAdType = response.body().getArrayListAbout().get(0).getArrayListAds().get(0).getAdType();


                        } else {
                            PCliq_Constant.isBannerAd = false;
                            PCliq_Constant.isInterAd = false;
                            PCliq_Constant.isNativeAd = false;
                        }

                        if (response.body().getArrayListAbout().get(0).getArrayListPages() != null && response.body().getArrayListAbout().get(0).getArrayListPages().size() > 0) {
                            PCliq_Constant.arrayListPages.clear();
                            for (int i = 0; i < response.body().getArrayListAbout().get(0).getArrayListPages().size(); i++) {
                                if (!response.body().getArrayListAbout().get(0).getArrayListPages().get(i).getId().equals("1")) {
                                    PCliq_Constant.arrayListPages.add(response.body().getArrayListAbout().get(0).getArrayListPages().get(i));
                                } else {
                                    PCliq_Constant.itemAbout.setAppDesc(response.body().getArrayListAbout().get(0).getArrayListPages().get(i).getContent());
                                }
                            }
                        }
                    }

                    sharedPref.setAdDetails(PCliq_Constant.isBannerAd, PCliq_Constant.isInterAd, PCliq_Constant.isNativeAd, PCliq_Constant.bannerAdType,
                            PCliq_Constant.interstitialAdType, PCliq_Constant.nativeAdType, PCliq_Constant.bannerAdID, PCliq_Constant.interstitialAdID, PCliq_Constant.nativeAdID, PCliq_Constant.startappAppId, PCliq_Constant.interstitialAdShow, PCliq_Constant.nativeAdShow);
                    sharedPref.setSocialDetails();

                    dbHelper.addtoAbout();
                    openLoginActivity();
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemAppDetailsList> call, @NonNull Throwable t) {
                    call.cancel();
                    openLoginActivity();
                }
            });
        } else {
            errorDialog(getString(R.string.internet_not_connected), getString(R.string.error_connect_net_tryagain));
        }
    }

    private View splashNoInternetView;
    private ConnectivityManager.NetworkCallback splashNetworkCallback;

    private void errorDialog(String title, String message) {
        if (title.equals(getString(R.string.internet_not_connected))) {
            // Show our nice No Internet overlay instead of ugly AlertDialog
            android.view.ViewGroup root = findViewById(android.R.id.content);
            if (splashNoInternetView == null) {
                splashNoInternetView = getLayoutInflater().inflate(R.layout.pcliq_layout_no_internet, root, false);
                splashNoInternetView.setClickable(true);
                splashNoInternetView.setElevation(100f);
                root.addView(splashNoInternetView);
            }

            // Register a listener to auto-retry when network comes back
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null && splashNetworkCallback == null) {
                splashNetworkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull android.net.Network network) {
                        runOnUiThread(() -> {
                            // Network is back! Remove overlay and retry
                            if (splashNoInternetView != null && splashNoInternetView.getParent() != null) {
                                ((android.view.ViewGroup) splashNoInternetView.getParent()).removeView(splashNoInternetView);
                                splashNoInternetView = null;
                            }
                            if (cm != null && splashNetworkCallback != null) {
                                try { cm.unregisterNetworkCallback(splashNetworkCallback); } catch (Exception e) {}
                                splashNetworkCallback = null;
                            }
                            getAppDetails();
                        });
                    }
                };
                android.net.NetworkRequest request = new android.net.NetworkRequest.Builder()
                        .addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build();
                cm.registerNetworkCallback(request, splashNetworkCallback);
            }
        } else {
            // Server error - show AlertDialog as before
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PCliq_SplashActivity.this, R.style.ThemeDialog);
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setCancelable(false);
            alertDialog.setNegativeButton(getString(R.string.try_again), (dialog, which) -> getAppDetails());
            alertDialog.setPositiveButton(getString(R.string.exit), (dialog, which) -> finish());
            alertDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashNetworkCallback != null) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                try { cm.unregisterNetworkCallback(splashNetworkCallback); } catch (Exception e) {}
            }
            splashNetworkCallback = null;
        }
    }

    private void openLoginActivity() {
        getData();
    }

    private void openMainActivity() {

        getData();

//        Intent intent;
//        if (!cid.equals("")) {
//            intent = new Intent(PCliq_SplashActivity.this, PCliq_WallpaperByCatActivity.class);
//            intent.putExtra("cid", cid);
//            intent.putExtra("cname", cname);
//            intent.putExtra("from", "noti");
//        } else {
//            intent = new Intent(PCliq_SplashActivity.this, PCliq_MainActivity.class);
//            intent.putExtra("from", "");
//        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
    }

    private void hideStatusBar() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(androidx.core.content.ContextCompat.getColor(this, R.color.bg_warm));
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(androidx.core.content.ContextCompat.getColor(this, R.color.bg_warm));
        }
    }


    private void getData() {
        if (PCliq_NetworkUtils.isNetworkAvailable(this)) {
            database = FirebaseDatabase.getInstance();
            project_data2 = database.getReference("Ads_data");
            project_data2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.e("snapshot", "" + snapshot);
                    try {
                        taskPreferenceClass.setInt("splashscreen", Integer.parseInt(snapshot.child("SplashScreenAdsManage").getValue().toString()));
                        taskPreferenceClass.setInt("UpdateAvailable", Integer.parseInt(snapshot.child("UpdateAvailable").getValue().toString()));
                        taskPreferenceClass.setDataType("UpdateVersionName", Objects.requireNonNull(snapshot.child("UpdateVersionName").getValue()).toString());
//                      ----------------------------------------------- Live ADS -----------------------------------------------

                        taskPreferenceClass.setDataType("GoogleBannerAd", Objects.requireNonNull(snapshot.child("GoogleBannerAd").getValue()).toString());
                        taskPreferenceClass.setDataType("GoogleAppopenAd", Objects.requireNonNull(snapshot.child("GoogleAppopenAd").getValue()).toString());
                        taskPreferenceClass.setDataType("GoogleInterstitialAd", Objects.requireNonNull(snapshot.child("GoogleInterstitialAd").getValue()).toString());
                        taskPreferenceClass.setDataType("GoogleInterstialRewardAd", Objects.requireNonNull(snapshot.child("GoogleInterstialRewardAd").getValue()).toString());
                        taskPreferenceClass.setDataType("GoogleRewardedAd", Objects.requireNonNull(snapshot.child("GoogleRewardedAd").getValue()).toString());
                        taskPreferenceClass.setDataType("GoogleNativeAd", Objects.requireNonNull(snapshot.child("GoogleNativeAd").getValue()).toString());

                        taskPreferenceClass.setDataType("FbNativeAd", Objects.requireNonNull(snapshot.child("FbNativeAd").getValue()).toString());
                        taskPreferenceClass.setDataType("FbInterstitialAd", Objects.requireNonNull(snapshot.child("FbInterstitialAd").getValue()).toString());
                        taskPreferenceClass.setDataType("FbBannerAd", Objects.requireNonNull(snapshot.child("FbBannerAd").getValue()).toString());

                        taskPreferenceClass.setDataType("AdxBannerAdunitID", Objects.requireNonNull(snapshot.child("AdxBannerAdunitID").getValue()).toString());
                        taskPreferenceClass.setDataType("AdxInterstitalAdunitID", Objects.requireNonNull(snapshot.child("AdxInterstitalAdunitID").getValue()).toString());
                        taskPreferenceClass.setDataType("AdxRewardVideoUnitID", Objects.requireNonNull(snapshot.child("AdxRewardVideoUnitID").getValue()).toString());
                        taskPreferenceClass.setDataType("AdxNativeUnitID", Objects.requireNonNull(snapshot.child("AdxNativeUnitID").getValue()).toString());
                        taskPreferenceClass.setDataType("AdxAppOpenID", Objects.requireNonNull(snapshot.child("AdxAppOpenID").getValue()).toString());

//                      ----------------------------------------------- Test ADS -----------------------------------------------

                        taskPreferenceClass.setDataType("GoogleNativeAd", "ca-app-pub-3940256099942544/2247696110");
                        taskPreferenceClass.setDataType("GoogleAppopenAd", "ca-app-pub-3940256099942544/3419835294");
                        taskPreferenceClass.setDataType("GoogleBannerAd", "ca-app-pub-3940256099942544/6300978111");
                        taskPreferenceClass.setDataType("GoogleInterstitialAd", "ca-app-pub-3940256099942544/1033173712");
                        taskPreferenceClass.setDataType("GoogleRewardedAd", "ca-app-pub-3940256099942544/5224354917");
                        taskPreferenceClass.setDataType("GoogleInterstialRewardAd", "ca-app-pub-3940256099942544/5354046379");

                        taskPreferenceClass.setDataType("FbNativeAd", "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
                        taskPreferenceClass.setDataType("FbInterstitialAd", "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
                        taskPreferenceClass.setDataType("FbBannerAd", "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");

                        taskPreferenceClass.setDataType("AdxBannerAdunitID", "ca-app-pub-3940256099942544/6300978111");
                        taskPreferenceClass.setDataType("AdxInterstitalAdunitID", "ca-app-pub-3940256099942544/1033173712");
                        taskPreferenceClass.setDataType("AdxRewardVideoUnitID", "ca-app-pub-3940256099942544/5224354917");
                        taskPreferenceClass.setDataType("AdxNativeUnitID", "ca-app-pub-3940256099942544/2247696110");
                        taskPreferenceClass.setDataType("AdxAppOpenID", "ca-app-pub-3940256099942544/3419835294");

//                    --------------------------------------------------------------------------------------------------------
                        taskPreferenceClass.setInt("BannerAdStatus", Integer.parseInt(snapshot.child("BannerAdStatus").getValue().toString()));
                        taskPreferenceClass.setInt("rv_count", Integer.parseInt(snapshot.child("rv_count").getValue().toString()));//native ads count in Pose

                        taskPreferenceClass.setInt("InerstialClickCount", Integer.parseInt(Objects.requireNonNull(snapshot.child("InerstialClickCount").getValue().toString())));//Premium background Ads Type Reward / Full
                    } catch (Exception e) {
                        e.getMessage();
                    }

                    try {
                        int updateAvailable = taskPreferenceClass.getInt("UpdateAvailable");
                        String serverVersion = taskPreferenceClass.getAdsId("UpdateVersionName");
                        boolean isNewerVersionAvailable = false;
                        if (updateAvailable == 1 && serverVersion != null && !serverVersion.trim().isEmpty()) {
                            try {
                                String cleanServerVer = serverVersion.replaceAll("[^0-9.]", "").trim();
                                String cleanCurrentVer = BuildConfig.VERSION_NAME.replaceAll("[^0-9.]", "").trim();
                                if (!cleanServerVer.isEmpty() && !cleanCurrentVer.isEmpty()) {
                                    float sVer = Float.parseFloat(cleanServerVer);
                                    float cVer = Float.parseFloat(cleanCurrentVer);
                                    if (sVer > cVer) {
                                        isNewerVersionAvailable = true;
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        }

                        if (isNewerVersionAvailable) {
                            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PCliq_SplashActivity.this, R.style.ThemeDialog);
                            alertBuilder.setTitle("Update Available");
                            alertBuilder.setMessage("A new update is available. Please update the app to continue using the latest features.");
                            alertBuilder.setCancelable(false);
                            alertBuilder.setPositiveButton("Update Now", (dialog, which) -> {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                } catch (ActivityNotFoundException unused) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                                }
                                next();
                            });
                            alertBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                                next();
                            });
                            alertBuilder.show();
                        } else {
                            next();
                        }
                    } catch (Exception e) {
                        next();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    next();
                }
            });
        } else {
            next();
        }
    }

    public void next() {
        new Handler().postDelayed(new Runnable() {
            @Override // java.lang.Runnable
            public final void run() {

                startIntent();
            }
        }, 1000);
    }

    private void startIntent() {
        callMainActivity();
    }

    public void callStartActivity() {
        Application application = getApplication();
        ((MyApplication) application).showAdIfAvailable(PCliq_SplashActivity.this, () -> {
            MyApplication.isAdsSplash = false;
            ((MyApplication) getApplicationContext()).loadInterstitialAd();

//            Intent intent;
//            if (!cid.equals("")) {
//                intent = new Intent(PCliq_SplashActivity.this, PCliq_WallpaperByCatActivity.class);
//                intent.putExtra("cid", cid);
//                intent.putExtra("cname", cname);
//                intent.putExtra("from", "noti");
//            } else {
//                intent = new Intent(PCliq_SplashActivity.this, Reader_IntroductionActivity.class);
//                intent.putExtra("from", "");
//            }
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();

            Intent intent;
            if (sharedPref.getIsFirst()) {
                sharedPref.setIsFirst(false);
                intent = new Intent(PCliq_SplashActivity.this, PCliq_IntroductionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else {
                intent = new Intent(PCliq_SplashActivity.this, PCliq_MainActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();

        });
    }

    private void callMainActivity() {
        MyApplication.isAdsSplash = false;
        if (methods.isNetworkAvailable()) {
            ((MyApplication) getApplicationContext()).sendRequest();
            ((MyApplication) getApplicationContext()).loadInterstitialAd();
        }

        Intent intent;
        if (sharedPref.getIsFirst()) {
            sharedPref.setIsFirst(false);
            intent = new Intent(PCliq_SplashActivity.this, PCliq_IntroductionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(PCliq_SplashActivity.this, PCliq_MainActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
//        Intent intent;
//        if (!cid.equals("")) {
//            intent = new Intent(PCliq_SplashActivity.this, PCliq_WallpaperByCatActivity.class);
//            intent.putExtra("cid", cid);
//            intent.putExtra("cname", cname);
//            intent.putExtra("from", "noti");
//        } else {
//            intent = new Intent(PCliq_SplashActivity.this, Reader_IntroductionActivity.class);
//            intent.putExtra("from", "");
//        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
    }

}
