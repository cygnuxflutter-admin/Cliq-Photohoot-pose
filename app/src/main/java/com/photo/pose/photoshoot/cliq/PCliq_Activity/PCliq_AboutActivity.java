package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_LoadAds;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_NetworkUtils;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
import com.squareup.picasso.Picasso;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemAppDetailsList;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_AboutActivity extends AppCompatActivity {

    Toolbar PC_toolbar;
    WebView PC_webView;
    TextView PC_textView_appname, PC_textView_email, PC_textView_website, PC_textView_company, PC_textView_contact, PC_textView_version;
    ImageView PC_imageView_logo;
    LinearLayout PC_ll_email, PC_ll_website, PC_ll_company, PC_ll_contact;
    String PC_website, PC_email, PC_desc, PC_applogo, PC_appname, PC_appversion, PC_appauthor, PC_appcontact;
    PCliq_DBHelper PC_dbHelper;
    ProgressDialog PC_pbar;
    PCliq_Methods PC_methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_about);

        PC_dbHelper = new PCliq_DBHelper(this);
        PC_methods = new PCliq_Methods(this);
        PC_methods.setStatusColor(getWindow());
        PC_methods.forceRTLIfSupported(getWindow());

        PC_toolbar = this.findViewById(R.id.toolbar_about);
        PC_toolbar.setTitle(getString(R.string.menu_about));
        this.setSupportActionBar(PC_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RelativeLayout rl_ad = this.findViewById(R.id.rl_ad);
        if (PCliq_NetworkUtils.isNetworkAvailable(this)) {
            if (new PCliq_PreferenceClass(PCliq_AboutActivity.this).getInt("BannerAdStatus") == 1) {
                PCliq_LoadAds.loadAdmobBannerAd(this, rl_ad);
            } else {
                rl_ad.setVisibility(View.GONE);
            }
        }

        PC_pbar = new ProgressDialog(this);
        PC_pbar.setMessage(getResources().getString(R.string.loading));
        PC_pbar.setCancelable(false);

        PC_webView = findViewById(R.id.webView);
        PC_textView_appname = findViewById(R.id.textView_about_appname);
        PC_textView_email = findViewById(R.id.textView_about_email);
        PC_textView_website = findViewById(R.id.textView_about_site);
        PC_textView_company = findViewById(R.id.textView_about_company);
        PC_textView_contact = findViewById(R.id.textView_about_contact);
        PC_textView_version = findViewById(R.id.textView_about_appversion);
        PC_imageView_logo = findViewById(R.id.imageView_about_logo);

        PC_ll_email = findViewById(R.id.ll_email);
        PC_ll_website = findViewById(R.id.ll_website);
        PC_ll_contact = findViewById(R.id.ll_contact);
        PC_ll_company = findViewById(R.id.ll_company);

        getAppDetails();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void getAppDetails() {
        if (PC_methods.isNetworkAvailable()) {
            PC_pbar.show();
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
                    }

                    if (PC_pbar.isShowing()) {
                        PC_pbar.dismiss();
                    }

                    new PCliq_SharedPref(PCliq_AboutActivity.this).setSocialDetails();
                    setVariables();
                    PC_dbHelper.addtoAbout();
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemAppDetailsList> call, @NonNull Throwable t) {
                    if (PC_pbar.isShowing()) {
                        PC_pbar.dismiss();
                    }
                    call.cancel();
                }
            });
        } else {
            if (PC_dbHelper.getAbout()) {
                setVariables();
            }
        }
    }

    public void setVariables() {

        PC_appname = PCliq_Constant.itemAbout.getAppName();
        PC_applogo = PCliq_Constant.itemAbout.getAppLogo();
        PC_desc = PCliq_Constant.itemAbout.getAppDesc();
        PC_appversion = PCliq_Constant.itemAbout.getAppVersion();
        PC_appauthor = PCliq_Constant.itemAbout.getAuthor();
        PC_appcontact = PCliq_Constant.itemAbout.getContact();
        PC_email = PCliq_Constant.itemAbout.getEmail();
        PC_website = PCliq_Constant.itemAbout.getWebsite();

        PC_textView_appname.setText(PC_appname);
        if (!PC_email.trim().isEmpty()) {
            PC_ll_email.setVisibility(View.VISIBLE);
            PC_textView_email.setText(PC_email);
        }

        if (!PC_website.trim().isEmpty()) {
            PC_ll_website.setVisibility(View.VISIBLE);
            PC_textView_website.setText(PC_website);
        }

        if (!PC_appauthor.trim().isEmpty()) {
            PC_ll_company.setVisibility(View.VISIBLE);
            PC_textView_company.setText(PC_appauthor);
        }

        if (!PC_appcontact.trim().isEmpty()) {
            PC_ll_contact.setVisibility(View.VISIBLE);
            PC_textView_contact.setText(PC_appcontact);
        }

        if (!PC_appversion.trim().isEmpty()) {
            PC_textView_version.setText(PC_appversion);
        }

        if (PC_applogo.trim().isEmpty()) {
            PC_imageView_logo.setVisibility(View.GONE);
        } else {
            Picasso
                    .get()
                    .load(PC_applogo)
                    .into(PC_imageView_logo);
        }

        String mimeType = "text/html";
        String encoding = "utf-8";

        String text;
        if (PC_methods.isDarkMode()) {
            text = "<html><head>"
                    + "<style> body{color:#fff !important;text-align:left}"
                    + "</style></head>"
                    + "<body>"
                    + PC_desc
                    + "</body></html>";
        } else {
            text = "<html><head>"
                    + "<style> body{color:#000 !important;text-align:left}"
                    + "</style></head>"
                    + "<body>"
                    + PC_desc
                    + "</body></html>";
        }

        PC_webView.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            PC_webView.loadData(text, mimeType, encoding);
        } else {
            PC_webView.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
        }
    }
}