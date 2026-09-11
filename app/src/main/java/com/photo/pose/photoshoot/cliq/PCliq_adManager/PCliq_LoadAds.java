package com.photo.pose.photoshoot.cliq.PCliq_adManager;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
//import com.vehicle.information.trending.rtoexam.rto.R;
//import com.vehicle.information.trending.rtoexam.rto.Task_utils.Task_PreferenceClass;

public class PCliq_LoadAds {

    public static void loadAdmobBannerAd(Activity activity, RelativeLayout mainLayout) {
        mainLayout.removeAllViews();
        RelativeLayout.LayoutParams bannerParameters =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        bannerParameters.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainLayout.addView(getBannerView(activity));

        String bannerAdunitID = new PCliq_PreferenceClass(activity).getAdsId("GoogleBannerAd");
        Log.e("TAG%%Banner", "GoogleBannerAd: " + bannerAdunitID);

        if (bannerAdunitID != null) {
            AdView adView = new AdView(activity);
            AdSize adSize = getAdSize(activity);
            adView.setAdSize(adSize);
            adView.setAdUnitId(bannerAdunitID);

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    // Keep the shimmer loader visible if ad fails to load
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mainLayout.removeAllViews();
                    mainLayout.addView(adView, bannerParameters);
                }
            });

        }
    }

    private static View getBannerView(Activity activity) {
        View adView =  LayoutInflater.from(activity).inflate(R.layout.pcliq_banner_ad_layout_loading, null);
        ShimmerFrameLayout shimmerLayout = adView.findViewById(R.id.shimmerLayout);

        shimmerLayout.startShimmer(); // Start the shimmer effect
        shimmerLayout.setVisibility(View.VISIBLE);
        return adView;
    }

    private static AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

}
