package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.onesignal.OneSignal;
import com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_LoadAds;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemAppDetailsList;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemColorsList;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentDashboard;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPoseDownloaded;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPoseFeatured;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPoseRated;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPoseRecent;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_AdConsentListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_AdConsent;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_AdManagerInterAdmob;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_AdManagerInterApplovin;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_AdManagerInterStartApp;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_AdManagerInterWortise;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_NetworkUtils;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.photo.pose.photoshoot.cliq.R;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    PCliq_Methods PC_methods;
    PCliq_DBHelper PC_dbHelper;
    FragmentManager PC_fm;
    Toolbar PC_toolbar;
    PCliq_AdConsent PC_adConsent;
    DrawerLayout PC_drawer;
    PCliq_SharedPref PC_sharedPref;
    NavigationView PC_navigationView;
    MenuItem PC_menu_login;
    ImageView PC_iv_demo_filter;
    private ConsentInformation consentInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_main);

        PC_sharedPref = new PCliq_SharedPref(this);
        PC_dbHelper = new PCliq_DBHelper(this);
        PC_methods = new PCliq_Methods(this);
        PC_methods.setStatusColor(getWindow());
        PC_methods.forceRTLIfSupported(getWindow());

        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            this,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    Log.w("TAG52451", String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }
                            }
                    );
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w("TAG54697", String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });


        RelativeLayout rl_ad = this.findViewById(R.id.rl_ad);
        if (PCliq_NetworkUtils.isNetworkAvailable(this)) {
            if (new PCliq_PreferenceClass(PCliq_MainActivity.this).getInt("BannerAdStatus") == 1) {
                PCliq_LoadAds.loadAdmobBannerAd(this, rl_ad);
            } else {
                rl_ad.setVisibility(View.GONE);
            }
        }

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("bf7794b1-32fd-4647-8d33-d94e4d79a96f");
        OneSignal.promptForPushNotifications();

        PC_toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(PC_toolbar);
        PC_fm = getSupportFragmentManager();

        PC_drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, PC_drawer, PC_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PC_drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.setHomeAsUpIndicator(R.mipmap.pcliq_nav);
        PC_drawer.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);

        PCliq_Constant.isLiveWallpaperEnabled = PC_sharedPref.getIsLiveWallpaper();

        PC_navigationView = findViewById(R.id.nav_view);
        PC_navigationView.setNavigationItemSelectedListener(this);

        PC_iv_demo_filter = findViewById(R.id.iv_demo_filter);

        PC_adConsent = new PCliq_AdConsent(this, new PCliq_AdConsentListener() {
            @Override
            public void onConsentUpdate() {

            }
        });

        getAppDetails();
        if (PCliq_Constant.arrayListColors.size() == 0 || !PC_sharedPref.getIsColorSaved()) {
            getColors();
        }

        Menu menu = PC_navigationView.getMenu();
        PC_menu_login = menu.findItem(R.id.nav_login);
        changeLoginName();

        PCliq_FragmentDashboard f1 = new PCliq_FragmentDashboard();
        loadFrag(f1, getResources().getString(R.string.home), PC_fm);
        PC_navigationView.setCheckedItem(R.id.nav_home);
    }
    private void getAppDetails() {
        if (PC_methods.isNetworkAvailable()) {
            Call<PCliq_ItemAppDetailsList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getAppDetails(PC_methods.getAPIRequest(PCliq_Constant.URL_APP_DETAILS, 0, "", "", "", "", "", "", "", "", "", "", "", ""));
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

                        PC_adConsent.checkForConsent();
                        PC_dbHelper.addtoAbout();

                        PC_sharedPref.setIsLiveWallpaper(PCliq_Constant.isLiveWallpaperEnabled);

                        PC_methods.initializeAds();

                        PC_sharedPref.setAdDetails(PCliq_Constant.isBannerAd, PCliq_Constant.isInterAd, PCliq_Constant.isNativeAd, PCliq_Constant.bannerAdType,
                                PCliq_Constant.interstitialAdType, PCliq_Constant.nativeAdType, PCliq_Constant.bannerAdID, PCliq_Constant.interstitialAdID, PCliq_Constant.nativeAdID, PCliq_Constant.startappAppId, PCliq_Constant.interstitialAdShow, PCliq_Constant.nativeAdShow);
                        PC_sharedPref.setSocialDetails();

                        if (PCliq_Constant.isInterAd) {
                            switch (PCliq_Constant.interstitialAdType) {
                                case PCliq_Constant.AD_TYPE_ADMOB:
                                case PCliq_Constant.AD_TYPE_FACEBOOK:
                                    PCliq_AdManagerInterAdmob adManagerInterAdmob = new PCliq_AdManagerInterAdmob(getApplicationContext());
                                    adManagerInterAdmob.createAd();
                                    break;
                                case PCliq_Constant.AD_TYPE_STARTAPP:
                                    PCliq_AdManagerInterStartApp adManagerInterStartApp = new PCliq_AdManagerInterStartApp(getApplicationContext());
                                    adManagerInterStartApp.createAd();
                                    break;
                                case PCliq_Constant.AD_TYPE_APPLOVIN:
                                    PCliq_AdManagerInterApplovin adManagerInterApplovin = new PCliq_AdManagerInterApplovin(PCliq_MainActivity.this);
                                    adManagerInterApplovin.createAd();
                                    break;
                                case PCliq_Constant.AD_TYPE_WORTISE:
                                    PCliq_AdManagerInterWortise adManagerInterWortise = new PCliq_AdManagerInterWortise(PCliq_MainActivity.this);
                                    adManagerInterWortise.createAd();
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemAppDetailsList> call, @NonNull Throwable t) {
                    call.cancel();
                }
            });
        } else {
            PCliq_FragmentDashboard f1 = new PCliq_FragmentDashboard();
            loadFrag(f1, getResources().getString(R.string.home), PC_fm);
            PC_navigationView.setCheckedItem(R.id.nav_home);

            PC_adConsent.checkForConsent();
            PC_dbHelper.getAbout();
        }
    }

    private void getColors() {
        if (PC_methods.isNetworkAvailable()) {
            Call<PCliq_ItemColorsList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getColors(PC_methods.getAPIRequest(PCliq_Constant.URL_COLORS, 0, "", "", "", "", "", "", "", "", "", "", PC_sharedPref.getUserId(), ""));
            call.enqueue(new Callback<PCliq_ItemColorsList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemColorsList> call, @NonNull Response<PCliq_ItemColorsList> response) {
                    if (response.body() != null && response.body().getArrayListColors() != null) {
                        PC_dbHelper.removeColors();
                        PCliq_Constant.arrayListColors.clear();
                        PCliq_Constant.arrayListColors.addAll(response.body().getArrayListColors());
                        for (int i = 0; i < PCliq_Constant.arrayListColors.size(); i++) {
                            PC_dbHelper.addtoColorList(PCliq_Constant.arrayListColors.get(i));
                        }
                        PC_sharedPref.setColorSaved();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemColorsList> call, @NonNull Throwable t) {
                    call.cancel();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (PC_fm.getBackStackEntryCount() != 0) {
            String title = PC_fm.getFragments().get(PC_fm.getBackStackEntryCount() - 1).getTag();
            if (title.equals(getString(R.string.dashboard)) || title.equals(getString(R.string.home))) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.home));
                }
                PC_navigationView.setCheckedItem(R.id.nav_home);
            }
            super.onBackPressed();
        } else {
            exitDialog();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        clickNav(item.getItemId());
        return true;
    }

    private void clickNav(int item) {
        if (item == R.id.nav_home) {
            PCliq_FragmentDashboard fhome = new PCliq_FragmentDashboard();
            loadFrag(fhome, getResources().getString(R.string.home), PC_fm);
        } else if (item == R.id.nav_recent) {
            PCliq_FragmentPoseRecent frecent = new PCliq_FragmentPoseRecent();
            loadFrag(frecent, getResources().getString(R.string.recently_viewed), PC_fm);
        } else if (item == R.id.nav_featured) {
            PCliq_FragmentPoseFeatured ffeatured = new PCliq_FragmentPoseFeatured();
            loadFrag(ffeatured, getResources().getString(R.string.featured), PC_fm);
        } else if (item == R.id.nav_rated) {
            PCliq_FragmentPoseRated frated = new PCliq_FragmentPoseRated();
            loadFrag(frated, getResources().getString(R.string.rated), PC_fm);
        } else if (item == R.id.nav_downloaded) {
            PCliq_FragmentPoseDownloaded fdownloaded = new PCliq_FragmentPoseDownloaded();
            loadFrag(fdownloaded, getResources().getString(R.string.most_downloaded), PC_fm);
        } else if (item == R.id.nav_setting) {
            Intent intent_set = new Intent(PCliq_MainActivity.this, PCliq_SettingActivity.class);
            startActivity(intent_set);
        } else if (item == R.id.nav_login) {
            PC_methods.clickLogin();
        } /*else if (item == R.id.nav_unsplash) {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        }*/
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (!name.equals(getString(R.string.home))) {
            ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
            ft.add(R.id.frame_layout, f1, name);
            ft.addToBackStack(name);
        } else {
            ft.replace(R.id.frame_layout, f1, name);
        }

        ft.commitAllowingStateLoss();

        getSupportActionBar().setTitle(name);
    }

    private void exitDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PCliq_MainActivity.this, R.style.ThemeDialog);

        alert.setTitle(getString(R.string.exit));
        alert.setMessage(getString(R.string.sure_exit));
        alert.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    private void changeLoginName() {
        if (PC_menu_login != null) {
            if (new PCliq_SharedPref(PCliq_MainActivity.this).isLogged()) {
                PC_menu_login.setTitle(getResources().getString(R.string.logout));
                PC_menu_login.setIcon(getResources().getDrawable(R.mipmap.pcliq_logout));
            } else {
                PC_menu_login.setTitle(getResources().getString(R.string.login));
                PC_menu_login.setIcon(getResources().getDrawable(R.mipmap.pcliq_login));
            }
        }
    }

    @Override
    protected void onResume() {
        changeLoginName();
        super.onResume();
    }
}