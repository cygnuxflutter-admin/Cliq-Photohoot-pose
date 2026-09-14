package com.photo.pose.photoshoot.cliq.PCliq_adManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.res.ColorStateList;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
//import com.vehicle.information.trending.rtoexam.rto.R;
//import com.vehicle.information.trending.rtoexam.rto.Task_utils.Task_PreferenceClass;

public class PCliq_NativeAdUtil {

    private final Context context;
    private final PCliq_PreferenceClass taskPreferenceClass;
    private final int width;
    private final int height;
    private NativeAdView adView;
    private NativeAd nativeAd;
    public java.util.HashMap<Integer, NativeAd> cachedAds = new java.util.HashMap<>();
    public java.util.HashMap<Integer, Boolean> loadingAds = new java.util.HashMap<>();
    
    public void destroyAll() {
        for (NativeAd ad : cachedAds.values()) {
            if (ad != null) ad.destroy();
        }
        cachedAds.clear();
        if (this.nativeAd != null) this.nativeAd.destroy();
    }


    public PCliq_NativeAdUtil(Context context, int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;
        this.taskPreferenceClass = new PCliq_PreferenceClass(context);
    }

    public PCliq_NativeAdUtil(Context context) {
        this.context = context;
        this.width = -1;
        this.height = -1;
        this.taskPreferenceClass = new PCliq_PreferenceClass(context);
    }

    public static void loadNativeAd(RelativeLayout nativeAdContainer, Activity context) {

        nativeAdContainer.addView(getLoadingView(context));//ads loading View

        nativeAdContainer.setVisibility(View.VISIBLE);
        PCliq_NativeAdUtil taskNativeAdUtil = new PCliq_NativeAdUtil(context);
        taskNativeAdUtil.fillAdmobNativeAd(nativeAdContainer);
    }

    private static View getLoadingView(Activity context) {
        View adView = LayoutInflater.from(context).inflate(R.layout.pcliq_native_ad_layout_loading, null);
        ShimmerFrameLayout shimmerLayout = adView.findViewById(R.id.shimmerLayout);

        shimmerLayout.startShimmer(); // Start the shimmer effect
        shimmerLayout.setVisibility(View.VISIBLE);
        return adView;
    }

    public void showAdLoading(final RelativeLayout nativeAdContainer) {
        if (nativeAdContainer == null) return;
        nativeAdContainer.removeAllViews();
        View loadingView = LayoutInflater.from(context).inflate(R.layout.pcliq_native_ad_layout_loading, nativeAdContainer, false);
        com.facebook.shimmer.ShimmerFrameLayout shimmerLayout = loadingView.findViewById(R.id.shimmerLayout);
        if (shimmerLayout != null) {
            shimmerLayout.startShimmer();
        }
        nativeAdContainer.addView(loadingView);
    }

    public void fillAdmobNativeAd(final RelativeLayout nativeAdContainer) {
        fillAdmobNativeAd(nativeAdContainer, -1);
    }

    public void fillAdmobNativeAd(final RelativeLayout nativeAdContainer, final int position) {
        if (nativeAdContainer == null) return;
        
        if (position != -1 && cachedAds.containsKey(position)) {
            nativeAdContainer.removeAllViews();
            NativeAd ad = cachedAds.get(position);
            NativeAdView adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.pcliq_native_ad_layout, nativeAdContainer, false);
            populateUnifiedNativeAdView(ad, adView);
            nativeAdContainer.addView(adView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            nativeAdContainer.setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        if (position != -1 && loadingAds.containsKey(position)) {
            showAdLoading(nativeAdContainer);
            return;
        }
        if (position != -1) loadingAds.put(position, true);
        
        showAdLoading(nativeAdContainer);

        String nativeAdId = taskPreferenceClass.getAdsId("GoogleNativeAd");
        if (nativeAdId == null || nativeAdId.trim().isEmpty()) {
            if (position != -1) loadingAds.remove(position);
            return;
        }

        AdLoader.Builder builder = new AdLoader.Builder(context, nativeAdId);
        Log.e("TAG%%Native", "GoogleNativeAd: " + nativeAdId);

        builder.forNativeAd(nativeAd -> {
            if (position != -1) {
                cachedAds.put(position, nativeAd);
                loadingAds.remove(position);
            } else {
                if (this.nativeAd != null) this.nativeAd.destroy();
                this.nativeAd = nativeAd;
            }
            adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.pcliq_native_ad_layout, nativeAdContainer, false);
            populateUnifiedNativeAdView(nativeAd, adView);
            nativeAdContainer.removeAllViews();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            nativeAdContainer.addView(adView, lp);
            nativeAdContainer.setBackgroundColor(Color.TRANSPARENT);
        });

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (position != -1) loadingAds.remove(position);
                // fillAdXNativeAd(nativeAdContainer);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    public void fillAdmobNativeAdHorizontal(final RelativeLayout nativeAdContainer) {
        fillAdmobNativeAdHorizontal(nativeAdContainer, -1);
    }

    public void fillAdmobNativeAdHorizontal(final RelativeLayout nativeAdContainer, final int position) {
        if (nativeAdContainer == null) return;
        
        if (position != -1 && cachedAds.containsKey(position)) {
            nativeAdContainer.removeAllViews();
            NativeAd ad = cachedAds.get(position);
            NativeAdView adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.pcliq_native_ad_layout_horizontal, nativeAdContainer, false);
            populateUnifiedNativeAdView(ad, adView);
            nativeAdContainer.addView(adView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            nativeAdContainer.setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        if (position != -1 && loadingAds.containsKey(position)) {
            showAdLoading(nativeAdContainer);
            return;
        }
        if (position != -1) loadingAds.put(position, true);
        
        showAdLoading(nativeAdContainer);

        String nativeAdId = taskPreferenceClass.getAdsId("GoogleNativeAd");
        if (nativeAdId == null || nativeAdId.trim().isEmpty()) {
            if (position != -1) loadingAds.remove(position);
            return;
        }

        AdLoader.Builder builder = new AdLoader.Builder(context, nativeAdId);
        Log.e("TAG%%Native", "GoogleNativeAd: " + nativeAdId);

        builder.forNativeAd(nativeAd -> {
            if (position != -1) {
                cachedAds.put(position, nativeAd);
                loadingAds.remove(position);
            } else {
                if (this.nativeAd != null) this.nativeAd.destroy();
                this.nativeAd = nativeAd;
            }
            adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.pcliq_native_ad_layout_horizontal, nativeAdContainer, false);
            populateUnifiedNativeAdView(nativeAd, adView);
            nativeAdContainer.removeAllViews();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            nativeAdContainer.addView(adView, lp);
            nativeAdContainer.setBackgroundColor(Color.TRANSPARENT);
        });

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (position != -1) loadingAds.remove(position);
                // fillAdXNativeAd(nativeAdContainer);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    public void fillAdXNativeAd(final RelativeLayout nativeAdContainer) {
        if (nativeAdContainer == null) return;
        String adxId = taskPreferenceClass.getAdsId("AdxNativeUnitID");
        if (adxId == null || adxId.trim().isEmpty()) {
            return;
        }
        showAdLoading(nativeAdContainer);
        AdLoader.Builder builder = new AdLoader.Builder(context, adxId);
        Log.e("TAG%%Native", "AdxNativeUnitID: " + adxId);

        builder.forNativeAd(nativeAd -> {
            if (this.nativeAd != null) {
                this.nativeAd.destroy();
            }
            this.nativeAd = nativeAd;
            int fixedH = (int) (280 * context.getResources().getDisplayMetrics().density);
            adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.pcliq_native_ad_layout, nativeAdContainer, false);
            populateUnifiedNativeAdView(nativeAd, adView);
            nativeAdContainer.removeAllViews();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    fixedH
            );
            nativeAdContainer.addView(adView, lp);
            nativeAdContainer.setBackgroundColor(Color.TRANSPARENT);
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                fbNativeAd(nativeAdContainer);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private void fbNativeAd(final RelativeLayout nativeAdContainer) {
        com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(context, taskPreferenceClass.getAdsId("FbNativeAd"));
        Log.e("TAG%%Native", "FbNativeAd: " + taskPreferenceClass.getAdsId("FbNativeAd"));
        Log.e("TAG", "fb fetch native ad");
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                Log.e("TAG@$", "fb fetch native ad" + ad);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("TAG@$", "onError" + adError.getErrorCode());
                Log.e("TAG@$", "onError" + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.e("TAG@$", "onAdLoaded" + ad);
                if (nativeAd != ad) {
                    return;
                }
                nativeAdContainer.removeAllViews();
                View adView = com.facebook.ads.NativeAdView.render(context, nativeAd);
                nativeAdContainer.addView(adView);
                nativeAdContainer.setBackgroundColor(Color.parseColor("#151515"));

                // Native ad is loaded and ready to be displayed
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
            }
        };

        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());

    }

    public void populateUnifiedNativeAdView(NativeAd unifiedNativeAd, NativeAdView unifiedNativeAdView) {

        RelativeLayout relativeLayout = unifiedNativeAdView.findViewById(R.id.parentLyt);

        /*if (width != -1 && height != -1) {
            relativeLayout.getLayoutParams().width = width;
            relativeLayout.getLayoutParams().height = 300;
            relativeLayout.invalidate();
        }*/

        MediaView mediaView = unifiedNativeAdView.findViewById(R.id.ad_media);
        unifiedNativeAdView.setMediaView(mediaView);

        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(R.id.ad_headline));
        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(R.id.ad_body));
        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(R.id.ad_call_to_action));

        ImageView imageView = unifiedNativeAdView.findViewById(R.id.unified_image_view);

        populateNativeAdView(unifiedNativeAd, unifiedNativeAdView, mediaView, imageView);
    }

    private void populateNativeAdView(NativeAd unifiedNativeAd, NativeAdView unifiedNativeAdView, MediaView mediaView, ImageView imageView) {
        int i = 0;
       /* MediaContent mediaContent = unifiedNativeAd.getMediaContent();
        if (mediaContent != null) {
            boolean hasVideo = mediaContent.getVideoController().hasVideoContent();
            if (hasVideo) {

                unifiedNativeAdView.setMediaView(mediaView);
                imageView.setVisibility(View.GONE);
            } else {
                unifiedNativeAdView.setImageView(imageView);
                mediaView.setVisibility(View.GONE);
                List<NativeAd.Image> images = unifiedNativeAd.getImages();
                if (images.size() > 0) {
                    while (true) {
                        if (i >= images.size()) {
                            break;
                        }
                        NativeAd.Image image = images.get(i);
                        if (image != null) {
                            Drawable drawable = image.getDrawable();
                            imageView.setImageDrawable(drawable);
                            break;
                        }
                        i++;
                    }
                }
            }
        } else {
            unifiedNativeAdView.setImageView(imageView);
            mediaView.setVisibility(View.GONE);
            List<NativeAd.Image> images = unifiedNativeAd.getImages();
            if (images.size() > 0) {
                while (true) {
                    if (i >= images.size()) {
                        break;
                    }
                    NativeAd.Image image = images.get(i);
                    if (image != null) {
                        Drawable drawable = image.getDrawable();
                        imageView.setImageDrawable(drawable);
                        break;
                    }
                    i++;
                }
            }
        }*/

        TextView headlineView = (TextView) unifiedNativeAdView.getHeadlineView();
        if (headlineView != null) {
            headlineView.setText(unifiedNativeAd.getHeadline());
        }

        View bodyView = unifiedNativeAdView.getBodyView();
        if (unifiedNativeAd.getBody() == null) {
            if (bodyView != null) {
                bodyView.setVisibility(View.INVISIBLE);
            }
        } else {
            if (bodyView != null) {
                bodyView.setVisibility(View.VISIBLE);
                ((TextView) bodyView).setText(unifiedNativeAd.getBody());
            }
        }

        Button callToActionView = (Button) unifiedNativeAdView.getCallToActionView();
        if (callToActionView != null) {
            callToActionView.setText(unifiedNativeAd.getCallToAction());
        }

        unifiedNativeAdView.setNativeAd(unifiedNativeAd);
    }
}
