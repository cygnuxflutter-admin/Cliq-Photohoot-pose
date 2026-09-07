package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.eventbus.EventAction;
import com.eventbus.GlobalBus;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemPoseList;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_LoginActivity;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_SetAsLivePoseService;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_SetWallpaperActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_WelcomeActivity;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_PoseRetrieveListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.facebook.login.LoginManager;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookMediationAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.wortise.ads.AdError;
import com.wortise.ads.WortiseSdk;
import com.wortise.ads.banner.BannerAd;
import com.wortise.ads.interstitial.InterstitialAd;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PCliq_Methods {

    Context context;
    PCliq_InterAdListener interAdListener;

    // constructor
    public PCliq_Methods(Context context) {
        this.context = context;
    }

    // constructor
    public PCliq_Methods(Context context, PCliq_InterAdListener interAdListener) {
        this.context = context;
        this.interAdListener = interAdListener;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public int getScreenHeight() {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        point.y = display.getHeight();

        return point.y;
    }

    public void forceRTLIfSupported(Window window) {
        if (context.getResources().getString(R.string.isRTL).equals("true")) {
            window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public void clickLogin() {
        PCliq_SharedPref sharedPref = new PCliq_SharedPref(context);
        if (sharedPref.isLogged()) {
            logout((Activity) context, sharedPref);
            Toast.makeText(context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(context, PCliq_WelcomeActivity.class);
            intent.putExtra("from", "app");
            context.startActivity(intent);
        }
    }

    public void logout(Activity activity, PCliq_SharedPref sharedPref) {
        if (sharedPref.getLoginType().equals(PCliq_Constant.LOGIN_TYPE_FB)) {
            LoginManager.getInstance().logOut();
        } else if (sharedPref.getLoginType().equals(PCliq_Constant.LOGIN_TYPE_GOOGLE)) {
            FirebaseAuth.getInstance().signOut();
        }

        sharedPref.setIsAutoLogin(false);
        sharedPref.setIsLogged(false);
        sharedPref.setLoginDetails("", "", "", "", "", "", false, "", PCliq_Constant.LOGIN_TYPE_NORMAL);
        Intent intent1 = new Intent(context, PCliq_LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("from", "");
        context.startActivity(intent1);
        activity.finish();
    }


    public static void setAsLiveWallPaper(Context context, String str) {
        try {
            WallpaperManager.getInstance(context).clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (DeviceDetectUtil.isMiUi()) {
//            Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
//            intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context, str));
//            intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
//            context.startActivity(intent);
//            return;
//        }
        Intent intent2 = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
        intent2.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context, str));
        intent2.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
        context.startActivity(intent2);
    }

    public boolean isAdmobFBAds() {
        return PCliq_Constant.bannerAdType.equals(PCliq_Constant.AD_TYPE_ADMOB) ||
                PCliq_Constant.interstitialAdType.equals(PCliq_Constant.AD_TYPE_ADMOB) ||
                PCliq_Constant.nativeAdType.equals(PCliq_Constant.AD_TYPE_ADMOB) ||
                PCliq_Constant.bannerAdType.equals(PCliq_Constant.AD_TYPE_FACEBOOK) ||
                PCliq_Constant.interstitialAdType.equals(PCliq_Constant.AD_TYPE_FACEBOOK) ||
                PCliq_Constant.nativeAdType.equals(PCliq_Constant.AD_TYPE_FACEBOOK);
    }

    public boolean isStartAppAds() {
        return PCliq_Constant.bannerAdType.equals(PCliq_Constant.AD_TYPE_STARTAPP) ||
                PCliq_Constant.interstitialAdType.equals(PCliq_Constant.AD_TYPE_STARTAPP) ||
                PCliq_Constant.nativeAdType.equals(PCliq_Constant.AD_TYPE_STARTAPP);
    }

    public boolean isApplovinAds() {
        return PCliq_Constant.bannerAdType.equals(PCliq_Constant.AD_TYPE_APPLOVIN) ||
                PCliq_Constant.interstitialAdType.equals(PCliq_Constant.AD_TYPE_APPLOVIN) ||
                PCliq_Constant.nativeAdType.equals(PCliq_Constant.AD_TYPE_APPLOVIN);
    }

    public boolean isWortiseAds() {
        return PCliq_Constant.bannerAdType.equals(PCliq_Constant.AD_TYPE_WORTISE) ||
                PCliq_Constant.interstitialAdType.equals(PCliq_Constant.AD_TYPE_WORTISE) ||
                PCliq_Constant.nativeAdType.equals(PCliq_Constant.AD_TYPE_WORTISE);
    }

    public void initializeAds() {
        if (isAdmobFBAds()) {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }

        if (isStartAppAds()) {
            if (!PCliq_Constant.startappAppId.equals("")) {
                StartAppSDK.init(context, PCliq_Constant.startappAppId, false);
                StartAppAd.disableSplash();
            }
        }

        if (isApplovinAds()) {
            if (!AppLovinSdk.getInstance(context).isInitialized()) {
                AppLovinSdk.initializeSdk(context);
                AppLovinSdk.getInstance(context).setMediationProvider("max");
                AppLovinSdk.getInstance(context).getSettings().setTestDeviceAdvertisingIds(Arrays.asList("bb6822d9-18de-41b0-994e-41d4245a4d63", "749d75a2-1ef2-4ff9-88a5-c50374843ac6"));
            }
        }

        if (isWortiseAds() && !WortiseSdk.isInitialized()) {
            WortiseSdk.initialize(context, PCliq_Constant.wortiseAppId);
        }
    }

    @SuppressLint("MissingPermission")
    private void showPersonalizedAds(LinearLayout linearLayout) {
        AdView adView = new AdView(context);
        AdRequest adRequest;
        if (PCliq_Constant.interstitialAdType.equals(PCliq_Constant.AD_TYPE_ADMOB)) {
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, new Bundle())
                    .build();
        } else {
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, new Bundle())
                    .addNetworkExtrasBundle(FacebookMediationAdapter.class, new Bundle())
                    .build();
        }
        adView.setAdUnitId(PCliq_Constant.bannerAdID);
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    @SuppressLint("MissingPermission")
    private void showNonPersonalizedAds(LinearLayout linearLayout) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        AdView adView = new AdView(context);
        AdRequest adRequest;
        if (PCliq_Constant.interstitialAdType.equals(PCliq_Constant.AD_TYPE_ADMOB)) {
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
        } else {
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .addNetworkExtrasBundle(FacebookMediationAdapter.class, extras)
                    .build();
        }
        adView.setAdUnitId(PCliq_Constant.bannerAdID);
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    public void showBannerAd(LinearLayout linearLayout) {
        if (isNetworkAvailable() && PCliq_Constant.isBannerAd) {
            switch (PCliq_Constant.bannerAdType) {
                case PCliq_Constant.AD_TYPE_ADMOB:
                case PCliq_Constant.AD_TYPE_FACEBOOK:
                    if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                        showNonPersonalizedAds(linearLayout);
                    } else {
                        showPersonalizedAds(linearLayout);
                    }
                    break;
                case PCliq_Constant.AD_TYPE_STARTAPP:
                    Banner startAppBanner = new Banner(context);
                    startAppBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(startAppBanner);
                    startAppBanner.loadAd();
                    break;
                case PCliq_Constant.AD_TYPE_APPLOVIN:
                    MaxAdView adView = new MaxAdView(PCliq_Constant.bannerAdID, context);
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx = context.getResources().getDimensionPixelSize(R.dimen.banner_height);
                    adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                    linearLayout.addView(adView);
                    adView.loadAd();
                    break;
                case PCliq_Constant.AD_TYPE_WORTISE:
                    BannerAd mBannerAd = new BannerAd(context);
                    mBannerAd.setAdSize(com.wortise.ads.AdSize.HEIGHT_50);
                    mBannerAd.setAdUnitId(PCliq_Constant.bannerAdID);
                    linearLayout.addView(mBannerAd);
                    mBannerAd.loadAd();
                    break;
            }
        }
    }

    public void showInter(final int pos, final String type) {
        if (PCliq_Constant.isInterAd) {
            PCliq_Constant.adCount = PCliq_Constant.adCount + 1;
            if (PCliq_Constant.adCount % PCliq_Constant.interstitialAdShow == 0) {
                switch (PCliq_Constant.interstitialAdType) {
                    case PCliq_Constant.AD_TYPE_ADMOB:
                    case PCliq_Constant.AD_TYPE_FACEBOOK:
                        final PCliq_AdManagerInterAdmob adManagerInterAdmob = new PCliq_AdManagerInterAdmob(context);
                        if (adManagerInterAdmob.getAd() != null) {
                            adManagerInterAdmob.getAd().setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    PCliq_AdManagerInterAdmob.setAd(null);
                                    adManagerInterAdmob.createAd();
                                    interAdListener.onClick(pos, type);
                                    super.onAdDismissedFullScreenContent();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
                                    PCliq_AdManagerInterAdmob.setAd(null);
                                    adManagerInterAdmob.createAd();
                                    interAdListener.onClick(pos, type);
                                    super.onAdFailedToShowFullScreenContent(adError);
                                }
                            });
                            adManagerInterAdmob.getAd().show((Activity) context);
                        } else {
                            PCliq_AdManagerInterAdmob.setAd(null);
                            adManagerInterAdmob.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case PCliq_Constant.AD_TYPE_STARTAPP:
                        final PCliq_AdManagerInterStartApp adManagerInterStartApp = new PCliq_AdManagerInterStartApp(context);
                        if (adManagerInterStartApp.getAd() != null && adManagerInterStartApp.getAd().isReady()) {
                            adManagerInterStartApp.getAd().showAd(new AdDisplayListener() {
                                @Override
                                public void adHidden(Ad ad) {
                                    PCliq_AdManagerInterStartApp.setAd(null);
                                    adManagerInterStartApp.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void adDisplayed(Ad ad) {

                                }

                                @Override
                                public void adClicked(Ad ad) {

                                }

                                @Override
                                public void adNotDisplayed(Ad ad) {
                                    PCliq_AdManagerInterStartApp.setAd(null);
                                    adManagerInterStartApp.createAd();
                                    interAdListener.onClick(pos, type);
                                }
                            });
                        } else {
                            PCliq_AdManagerInterStartApp.setAd(null);
                            adManagerInterStartApp.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case PCliq_Constant.AD_TYPE_APPLOVIN:
                        final PCliq_AdManagerInterApplovin adManagerInterApplovin = new PCliq_AdManagerInterApplovin(context);
                        if (adManagerInterApplovin.getAd() != null && adManagerInterApplovin.getAd().isReady()) {
                            adManagerInterApplovin.getAd().setListener(new MaxAdListener() {
                                @Override
                                public void onAdLoaded(MaxAd ad) {

                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {

                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    PCliq_AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {

                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    PCliq_AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                    PCliq_AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }
                            });
                            adManagerInterApplovin.getAd().showAd();
                        } else {
                            PCliq_AdManagerInterStartApp.setAd(null);
                            adManagerInterApplovin.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;

                    case PCliq_Constant.AD_TYPE_WORTISE:
                        final PCliq_AdManagerInterWortise adManagerInterWortise = new PCliq_AdManagerInterWortise(context);
                        if (adManagerInterWortise.getAd() != null && adManagerInterWortise.getAd().isAvailable()) {
                            adManagerInterWortise.getAd().setListener(new InterstitialAd.Listener() {
                                @Override
                                public void onInterstitialClicked(@NonNull InterstitialAd interstitialAd) {

                                }

                                @Override
                                public void onInterstitialDismissed(@NonNull InterstitialAd interstitialAd) {
                                    PCliq_AdManagerInterWortise.setAd(null);
                                    adManagerInterWortise.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialFailed(@NonNull InterstitialAd interstitialAd, @NonNull AdError adError) {
                                    PCliq_AdManagerInterWortise.setAd(null);
                                    adManagerInterWortise.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialLoaded(@NonNull InterstitialAd interstitialAd) {

                                }

                                @Override
                                public void onInterstitialShown(@NonNull InterstitialAd interstitialAd) {

                                }
                            });
                            adManagerInterWortise.getAd().showAd();
                        } else {
                            PCliq_AdManagerInterWortise.setAd(null);
                            adManagerInterWortise.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                }
            } else {
                interAdListener.onClick(pos, type);
            }
        } else {
            interAdListener.onClick(pos, type);
        }
    }

    public void showSnackBar(View linearLayout, String message) {
        Snackbar snackbar = Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundResource(R.drawable.pcliq_bg_grt_toolbar);
        snackbar.show();
    }

    public void setStatusColor(Window window) {
        if (window == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setStatusBarColor(ContextCompat.getColor(context, R.color.bg_warm));
            if (window.getInsetsController() != null) {
                window.getInsetsController().setSystemBarsAppearance(
                        android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(context, R.color.bg_warm));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(context, R.color.bg_warm));
        }
    }

    public void styleSearchView(androidx.appcompat.widget.SearchView searchView) {
        if (searchView == null) return;
        try {
            int textColor = ContextCompat.getColor(context, R.color.text_espresso);
            int hintColor = ContextCompat.getColor(context, R.color.text_light_brown);
            int goldColor = ContextCompat.getColor(context, R.color.gold_primary);

            androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            if (searchAutoComplete != null) {
                searchAutoComplete.setTextColor(textColor);
                searchAutoComplete.setHintTextColor(hintColor);
                searchAutoComplete.setTextSize(15);
                try {
                    searchAutoComplete.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(context, R.font.pcliq_leaguespartan_medium));
                } catch (Exception ignored) {}
            }

            ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
            if (closeButton != null) {
                closeButton.setColorFilter(textColor, android.graphics.PorterDuff.Mode.SRC_IN);
            }

            ImageView searchMagIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
            if (searchMagIcon != null) {
                searchMagIcon.setColorFilter(goldColor, android.graphics.PorterDuff.Mode.SRC_IN);
            }

            ImageView searchButton = searchView.findViewById(androidx.appcompat.R.id.search_button);
            if (searchButton != null) {
                searchButton.setColorFilter(goldColor, android.graphics.PorterDuff.Mode.SRC_IN);
            }

            ImageView voiceButton = searchView.findViewById(androidx.appcompat.R.id.search_voice_btn);
            if (voiceButton != null) {
                voiceButton.setColorFilter(textColor, android.graphics.PorterDuff.Mode.SRC_IN);
            }

            View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
            if (searchPlate != null) {
                searchPlate.setBackgroundColor(Color.TRANSPARENT);
            }

            View submitArea = searchView.findViewById(androidx.appcompat.R.id.submit_area);
            if (submitArea != null) {
                submitArea.setBackgroundColor(Color.TRANSPARENT);
            }
        } catch (Exception ignored) {}
    }

    //check dark mode or not
    public boolean isDarkMode() {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                return true;
            default:
                return false;
        }
    }

    public String getDarkMode() {
        PCliq_SharedPref sharedPref = new PCliq_SharedPref(context);
        return sharedPref.getDarkMode();
    }

    public GradientDrawable getRoundDrawable(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(10);
        return gd;
    }

    public GradientDrawable getRoundDrawableRadis(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(radius);
        return gd;
    }

    public String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public void showUpdateAlert(String message, boolean isFromSplash) {
        if (message == null || message.trim().isEmpty()) {
            message = "A new update is available for " + context.getString(R.string.app_name) + ". Please update the app to continue using the latest features.";
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(context.getString(R.string.update));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(context.getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = PCliq_Constant.appUpdateURL;
                if (url == null || url.equals("")) {
                    url = "http://play.google.com/store/apps/details?id=" + context.getPackageName();
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);

                ((Activity) context).finish();
            }
        });
        if (PCliq_Constant.appUpdateCancel) {
            alertDialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (isFromSplash) {
                        new PCliq_SharedPref(context).setIsFirst(false);
                        Intent intent = new Intent(context, PCliq_WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }
            });
        } else {
            alertDialog.setNegativeButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity) context).finish();
                }
            });
        }
        alertDialog.show();
    }

    public void getVerifyDialog(String title, String message) {
        if (message == null || message.trim().isEmpty()) {
            message = title;
        }
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                finish();
            }
        });
        alertDialog.show();
    }

    public int getImageThumbWidth(String type) {
        int width = 100;
        if (type.equals(context.getString(R.string.portrait)) || type.equals("")) {
            width = 300;
        } else if (type.equals(context.getString(R.string.landscape))) {
            width = 300;
        } else if (type.equals(context.getString(R.string.square))) {
            width = 300;
        } else if (type.equals(context.getString(R.string.details))) {
            width = 500;
        } else if (type.equals(context.getString(R.string.categories))) {
            width = 300;
        } else if (type.equals(context.getString(R.string.sub_categories))) {
            width = 200;
        }
        return width;
    }

    public int getImageThumbHeight(String type) {
        int height = 100;
        if (type.equals(context.getString(R.string.portrait)) || type.equals("")) {
            height = 525;
        } else if (type.equals(context.getString(R.string.landscape))) {
            height = 170;
        } else if (type.equals(context.getString(R.string.square))) {
            height = 300;
        } else if (type.equals(context.getString(R.string.details))) {
            height = 500;
        } else if (type.equals(context.getString(R.string.categories))) {
            height = 200;
        } else if (type.equals(context.getString(R.string.sub_categories))) {
            height = 200;
        }
        return height;
    }

    public int getColumnWidth(int column, int grid_padding) {
        Resources r = context.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, grid_padding, r.getDisplayMetrics());
        return (int) ((getScreenWidth() - ((column + 1) * padding)) / column);
    }

    public void saveImage(String img_url, String option, View coordinatorLayout, String postType) {
        new LoadShare(option, postType, coordinatorLayout).execute(img_url, FilenameUtils.getName(img_url));
    }

    public class LoadShare extends AsyncTask<String, String, String> {
        private PCliq_CustomProgressDialog pDialog;
        View coordinatorLayout;
        String option, filePath, postType;
        File file;

        LoadShare(String option, String postType, View coordinatorLayout) {
            this.option = option;
            this.postType = postType;
            this.coordinatorLayout = coordinatorLayout;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new PCliq_CustomProgressDialog(context);
            if (option.equals(context.getString(R.string.download))) {
                if (postType.equals("wallpaper")) {
                    pDialog.setMessage(context.getResources().getString(R.string.downloading_wallpaper));
                } else {
                    pDialog.setMessage(context.getResources().getString(R.string.downloading_live_wall));
                }
            } else {
                pDialog.setMessage(context.getResources().getString(R.string.please_wait));
            }
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String name = strings[1];
            try {
                if (!option.equalsIgnoreCase(context.getString(R.string.download))) {
                    filePath = context.getExternalCacheDir().getAbsoluteFile().getAbsolutePath() + File.separator + name;
                } else {
                    if (postType.equals("wallpaper")) {
                        filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + context.getString(R.string.wallpapers) + File.separator + name;
                    } else {
                        filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + context.getString(R.string.live_wallpapers) + File.separator + name;
                    }
                }
                file = new File(filePath);
                if (!file.exists()) {
                    URL url = new URL(strings[0]);

                    InputStream inputStream;

                    if (strings[0].contains("https://")) {
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "*/*");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                    } else {
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "*/*");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                    }

                    if (option.equalsIgnoreCase(context.getString(R.string.download))) {
                        boolean isSaved = false;
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (postType.equals("wallpaper")) {
                            isSaved = saveImage(bitmap, name, option, postType, null);
                        } else {
                            isSaved = saveImage(bitmap, name, option, postType, IOUtils.toByteArray(inputStream));
                        }

                        if (isSaved) {
                            return "1";
                        } else {
                            return "2";
                        }
                    } else {
                        if (file.createNewFile()) {
                            file.createNewFile();
                        }

                        FileOutputStream fileOutput = new FileOutputStream(file);

                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                        }
                        fileOutput.close();
                        return "1";
                    }
                } else {
                    return "2";
                }
            } catch (MalformedURLException e) {
                return "0";
            } catch (IOException e) {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            pDialog.dismiss();

            if (option.equals(context.getString(R.string.download))) {
                if (s.equals("2")) {
                    if (postType.equals("wallpaper")) {
                        showSnackBar(coordinatorLayout, context.getResources().getString(R.string.wallpaper_already_saved));
                    } else {
                        showSnackBar(coordinatorLayout, context.getResources().getString(R.string.live_wall_already_saved));
                    }
                } else {
                    if (isNetworkAvailable()) {
                        GlobalBus.getBus().postSticky(new EventAction(context.getString(R.string.download)));
                    }
                    if (postType.equals("wallpaper")) {
                        showSnackBar(coordinatorLayout, context.getResources().getString(R.string.wallpaper_saved));
                    } else {
                        showSnackBar(coordinatorLayout, context.getResources().getString(R.string.live_wall_saved));
                    }
                }
            } else if (option.equals(context.getString(R.string.set_wallpaper))) {
                PCliq_Constant.uri_set = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);
                if (postType.equals("wallpaper")) {
                    Intent intent = new Intent(context, PCliq_SetWallpaperActivity.class);
                    context.startActivity(intent);
                } else {
                    PCliq_SetAsLivePoseService.setAsWallPaper(context);
                }
            } else if (option.equals(context.getString(R.string.share))) {
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
                if (postType.equals("wallpaper")) {
                    share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.get_more_wall) + "\n" + context.getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                } else {
                    share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.get_more_live_wall) + "\n" + context.getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                }
                share.putExtra(Intent.EXTRA_STREAM, contentUri);
                context.startActivity(Intent.createChooser(share, context.getString(R.string.share)));
            }

            super.onPostExecute(s);
        }
    }

    private boolean saveImage(Bitmap bitmap, String fileName, String type, String postType, byte[] bytes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && type.equalsIgnoreCase(context.getString(R.string.download))) {
            ContentValues values = new ContentValues();
            if (postType.equals("wallpaper")) {
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + context.getString(R.string.app_name) + "/" + context.getString(R.string.wallpapers));
            } else {
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + context.getString(R.string.app_name) + "/" + context.getString(R.string.live_wallpapers));
            }
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);

            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();

                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                } catch (Exception e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            File directory;

            if (!type.equals(context.getString(R.string.download))) {
                directory = new File(context.getExternalCacheDir().getAbsoluteFile().getAbsolutePath() + File.separator);
            } else {
                if (postType.equals("wallpaper")) {
                    directory = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + context.getString(R.string.wallpapers) + File.separator);
                } else {
                    directory = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + context.getString(R.string.live_wallpapers) + File.separator);
                }
            }

            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);

            try {
                OutputStream outputStream = new FileOutputStream(file);

                if (postType.equals("wallpaper")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } else {
                    outputStream.write(bytes);
                    outputStream.flush();
                }
                outputStream.close();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    @SuppressLint("Range")
    public String getPathImage(Uri uri) {
        try {
            String filePath = "";
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            if (uri == null) {
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String returnn = cursor.getString(column_index);
                cursor.close();

                if (returnn == null) {
                    String path = null, image_id = null;
                    Cursor cursor2 = context.getContentResolver().query(uri, null, null, null, null);
                    if (cursor2 != null) {
                        cursor2.moveToFirst();
                        image_id = cursor2.getString(0);
                        image_id = image_id.substring(image_id.lastIndexOf(":") + 1);
                        cursor2.close();
                    }

                    Cursor cursor3 = context.getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
                    if (cursor3 != null) {
                        cursor3.moveToFirst();
                        path = cursor3.getString(cursor3.getColumnIndex(MediaStore.Images.Media.DATA));
                        cursor3.close();
                    }
                    return path;
                }
                return returnn;
            }
            // this is our fallback here
            return uri.getPath();
        }
    }

    public void getWallpapers(PCliq_PoseRetrieveListener wallpaperRetrieveListener) {
        ArrayList<PCliq_ItemPose> arrayListWallpapers = new ArrayList<>();
        if (isNetworkAvailable()) {
            Call<PCliq_ItemPoseList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersByRandom(getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_RANDOM, 0, "", "", "", "", "", "", "", "", "", "", "", ""));
            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {

                    if (response.body() != null && response.body().getArrayListWallpaper() != null) {
                        arrayListWallpapers.addAll(response.body().getArrayListWallpaper());
                    }
                    wallpaperRetrieveListener.onSuccess(arrayListWallpapers);
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Throwable t) {
                    call.cancel();
                    wallpaperRetrieveListener.onSuccess(arrayListWallpapers);
                }
            });
        } else {
            wallpaperRetrieveListener.onSuccess(arrayListWallpapers);
        }
    }

    public Boolean checkPer() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ((Activity) context).requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 22);
                return false;
            } else {
                return true;
            }
        } else {
            if ((ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((Activity) context).requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 22);
                    return false;
                }
            }
            return true;
        }
    }

    public String getAPIRequest(String method, int page, String colorID, String type, String cat_id, String searchText, String itemID, String rate, String name, String email, String password, String phone, String userID, String report_PostType) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new PCliq_API());
//        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {
            case PCliq_Constant.URL_CATEGORIES:
                if (!type.equals("")) {
                    jsObj.addProperty("wall_type", type);
                }
                break;
            case PCliq_Constant.URL_SUB_CATEGORIES:
                jsObj.addProperty("cat_id", cat_id);
                break;

            case PCliq_Constant.URL_WALLPAPER_BY_LATEST:
            case PCliq_Constant.URL_WALLPAPER_BY_POPULAR:
            case PCliq_Constant.URL_WALLPAPER_BY_SUB_CAT:
            case PCliq_Constant.URL_WALLPAPER_BY_FEATURED:
            case PCliq_Constant.URL_WALLPAPER_BY_DOWNLOAD:
            case PCliq_Constant.URL_WALLPAPER_BY_RATED:
                jsObj.addProperty("sub_cat_id", cat_id);
                jsObj.addProperty("color_id", colorID);
                jsObj.addProperty("user_id", userID);
                if (!type.equals("")) {
                    jsObj.addProperty("wall_type", type);
                }
                break;
            case PCliq_Constant.URL_WALLPAPER_BY_FAV:
                jsObj.addProperty("user_id", userID);
                if (!report_PostType.equals("")) {
                    jsObj.addProperty("post_type", report_PostType);
                }
                break;

            case PCliq_Constant.URL_WALLPAPER_BY_SEARCH:
                jsObj.addProperty("search_text", searchText);
                jsObj.addProperty("color_id", colorID);
                jsObj.addProperty("user_id", userID);
                if (!type.equals("")) {
                    jsObj.addProperty("wall_type", type);
                }
                break;

            case PCliq_Constant.URL_WALLPAPER_BY_CAT:
                jsObj.addProperty("cat_id", cat_id);
                jsObj.addProperty("color_id", colorID);
                jsObj.addProperty("user_id", userID);
                if (!type.equals("")) {
                    jsObj.addProperty("wall_type", type);
                }
                break;

            case PCliq_Constant.URL_WALLPAPER_BY_RECENT:
                jsObj.addProperty("recent_post_ids", itemID);
                break;

            case PCliq_Constant.URL_WALLPAPER_DETAILS:
                jsObj.addProperty("wallpaper_id", itemID);
                jsObj.addProperty("user_id", userID);
                break;

            case PCliq_Constant.URL_LIVE_WALL_LATEST:
            case PCliq_Constant.URL_LIVE_WALL_POPULAR:
            case PCliq_Constant.URL_LIVE_WALL_DOWNLOADS:
            case PCliq_Constant.URL_LIVE_WALL_RATED:
                jsObj.addProperty("user_id", userID);
                break;

            case PCliq_Constant.URL_LIVE_WALL_SEARCH:
                jsObj.addProperty("search_text", searchText);
                jsObj.addProperty("user_id", userID);
                break;

            case PCliq_Constant.URL_LIVE_WALL_DETAILS:
                jsObj.addProperty("live_wallpaper_id", itemID);
                jsObj.addProperty("user_id", userID);
                break;

            case PCliq_Constant.URL_WALLPAPER_VIEW:
            case PCliq_Constant.URL_WALLPAPER_DOWNLOAD_COUNT:
                jsObj.addProperty("post_id", itemID);
                jsObj.addProperty("post_type", report_PostType);
                break;

            case PCliq_Constant.URL_RATE_WALLPAPER:
                jsObj.addProperty("post_id", itemID);
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("rate", rate);
                jsObj.addProperty("post_type", report_PostType);
                break;

            case PCliq_Constant.URL_LOGIN:
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                break;

            case PCliq_Constant.URL_SOCIAL_LOGIN:
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("login_type", type);
                jsObj.addProperty("social_id", colorID);
                break;

            case PCliq_Constant.URL_REGISTRATION:
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                break;

            case PCliq_Constant.URL_FORGOT_PASSWORD:
                jsObj.addProperty("email", email);
                break;

            case PCliq_Constant.URL_PROFILE:
                jsObj.addProperty("user_id", userID);
                break;

            case PCliq_Constant.URL_PROFILE_UPDATE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                break;

            case PCliq_Constant.URL_REPORT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("post_id", itemID);
                jsObj.addProperty("message", searchText);
                jsObj.addProperty("post_type", report_PostType);
                break;

            case PCliq_Constant.URL_DO_FAV:
                jsObj.addProperty("post_type", report_PostType);
                jsObj.addProperty("post_id", colorID);
                jsObj.addProperty("user_id", userID);
                break;
        }

 //       Log.e("URL_WALLPAPER_BY_LATEST", API.toBase64(jsObj.toString()));
        return PCliq_API.toBase64(jsObj.toString());
    }
}