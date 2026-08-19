package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.onesignal.OneSignal;
import com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_LoadAds;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_NetworkUtils;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterPages;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_AdConsentListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_AdConsent;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class PCliq_SettingActivity extends AppCompatActivity {

    Toolbar toolbar;
    PCliq_Methods methods;
    PCliq_SharedPref sharedPref;
    PCliq_AdConsent adConsent;
    RecyclerView rv_pages;
    PCliq_AdapterPages adapterPages;
    LinearLayout ll_consent, ll_clearcache;
    SwitchCompat switch_consent, switch_noti;
    Boolean isNoti = true;
    View view_moreapp;
    ImageView iv_theme;
    ProgressDialog progressDialog;
    TextView tv_moreapp, tv_rateapp, tv_cachesize, tv_shareapp, tv_about, tv_theme;
    String them_mode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_setting);

        sharedPref = new PCliq_SharedPref(this);
        methods = new PCliq_Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        isNoti = sharedPref.getIsNotification();
        them_mode = methods.getDarkMode();

        toolbar = this.findViewById(R.id.toolbar_setting);
        toolbar.setTitle(getString(R.string.action_settings));
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(PCliq_SettingActivity.this);
        progressDialog.setMessage(getString(R.string.clearing_cache));

        RelativeLayout rl_ad = this.findViewById(R.id.rl_ad);
        if (PCliq_NetworkUtils.isNetworkAvailable(this)) {
            if (new PCliq_PreferenceClass(PCliq_SettingActivity.this).getInt("BannerAdStatus") == 1) {
                PCliq_LoadAds.loadAdmobBannerAd(this, rl_ad);
            } else {
                rl_ad.setVisibility(View.GONE);
            }
        }

        adConsent = new PCliq_AdConsent(this, new PCliq_AdConsentListener() {
            @Override
            public void onConsentUpdate() {
                setConsentSwitch();
            }
        });

        ll_consent = findViewById(R.id.ll_consent);
        rv_pages = findViewById(R.id.rv_pages);
        switch_noti = findViewById(R.id.switch_noti);
        switch_consent = findViewById(R.id.switch_consent);
        iv_theme = findViewById(R.id.iv_theme);
        tv_theme = findViewById(R.id.tv_theme);
        tv_rateapp = findViewById(R.id.tv_rateapp);
        tv_moreapp = findViewById(R.id.tv_moreapp);
        tv_cachesize = findViewById(R.id.tv_cachesize);
        tv_shareapp = findViewById(R.id.tv_shareapp);
        tv_about = findViewById(R.id.tv_about);
        ll_clearcache = findViewById(R.id.ll_cache);
        view_moreapp = findViewById(R.id.view_moreapp);

        if(getString(R.string.play_more_apps).equals("")) {
            view_moreapp.setVisibility(View.GONE);
            tv_moreapp.setVisibility(View.GONE);
        }

        if (methods.isDarkMode()) {
            iv_theme.setImageResource(R.mipmap.pcliq_mode_dark);
        } else {
            iv_theme.setImageResource(R.mipmap.pcliq_mode_icon);
        }

        initializeCache();

        if (adConsent.isUserFromEEA()) {
            setConsentSwitch();
        } else {
            ll_consent.setVisibility(View.GONE);
        }

        switch_noti.setChecked(isNoti);

        String mode = methods.getDarkMode();
        switch (mode) {
            case PCliq_Constant.DARK_MODE_SYSTEM:
                tv_theme.setText(getString(R.string.system_default));
                break;
            case PCliq_Constant.DARK_MODE_OFF:
                tv_theme.setText(getString(R.string.light));
                break;
            case PCliq_Constant.DARK_MODE_ON:
                tv_theme.setText(getString(R.string.dark));
                break;
        }

        switch_noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OneSignal.unsubscribeWhenNotificationsAreDisabled(!isChecked);
                sharedPref.setIsNotification(isChecked);
            }
        });

        switch_consent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ConsentInformation.getInstance(PCliq_SettingActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                } else {
                    ConsentInformation.getInstance(PCliq_SettingActivity.this).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                }
            }
        });

        tv_rateapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appName = getPackageName();//your application package name i.e play store application url
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="
                                    + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + appName)));
                }
            }
        });

        tv_moreapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
            }
        });

        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PCliq_SettingActivity.this, PCliq_AboutActivity.class);
                startActivity(intent);
            }
        });

        tv_shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ishare = new Intent(Intent.ACTION_SEND);
                ishare.setType("text/plain");
                ishare.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(ishare);
            }
        });

        ll_consent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adConsent.requestConsent();
            }
        });

        ll_clearcache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<String, String, String>() {
                    @Override
                    protected void onPreExecute() {
                        progressDialog.show();
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(String... strings) {
                        FileUtils.deleteQuietly(getCacheDir());
                        FileUtils.deleteQuietly(getExternalCacheDir());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(PCliq_SettingActivity.this, getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show();
                        tv_cachesize.setText("0 MB");
                        super.onPostExecute(s);
                    }
                }.execute();
            }
        });

        changeThemeColor();

        if(PCliq_Constant.arrayListPages.size() > 0) {
            rv_pages.setLayoutManager(new LinearLayoutManager(PCliq_SettingActivity.this));
            adapterPages = new PCliq_AdapterPages(PCliq_SettingActivity.this, PCliq_Constant.arrayListPages);
            rv_pages.setAdapter(adapterPages);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void openThemeDialog() {
        final Dialog dialog = new Dialog(PCliq_SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pcliq_layout_dialog_theme);
        if (getString(R.string.isRTL).equals("true")) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup_them);
        MaterialButton btn_ok = dialog.findViewById(R.id.textView_ok_them);
        MaterialButton btn_cancel = dialog.findViewById(R.id.textView_cancel_them);

        String mode = methods.getDarkMode();
        assert mode != null;
        switch (mode) {
            case PCliq_Constant.DARK_MODE_SYSTEM:
                radioGroup.check(radioGroup.getChildAt(0).getId());
                break;
            case PCliq_Constant.DARK_MODE_OFF:
                radioGroup.check(radioGroup.getChildAt(1).getId());
                break;
            case PCliq_Constant.DARK_MODE_ON:
                radioGroup.check(radioGroup.getChildAt(2).getId());
                break;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MaterialRadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    if (checkedId == R.id.radioButton_system_them) {
                        them_mode = PCliq_Constant.DARK_MODE_SYSTEM;
                    } else if (checkedId == R.id.radioButton_light_them) {
                        them_mode = PCliq_Constant.DARK_MODE_OFF;
                    } else if (checkedId == R.id.radioButton_dark_them) {
                        them_mode = PCliq_Constant.DARK_MODE_ON;
                    }
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setDarkMode(them_mode);
                switch (them_mode) {
                    case PCliq_Constant.DARK_MODE_SYSTEM:
                        tv_theme.setText(getResources().getString(R.string.system_default));
                        break;
                    case PCliq_Constant.DARK_MODE_OFF:
                        tv_theme.setText(getResources().getString(R.string.light));
                        break;
                    case PCliq_Constant.DARK_MODE_ON:
                        tv_theme.setText(getResources().getString(R.string.dark));
                        break;
                    default:
                        break;
                }
                dialog.dismiss();

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
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void setConsentSwitch() {
        switch_consent.setChecked(ConsentInformation.getInstance(this).getConsentStatus() == ConsentStatus.PERSONALIZED);
    }

    private void changeThemeColor() {

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
        };

        int[] thumbColors = new int[]{
                ContextCompat.getColor(PCliq_SettingActivity.this, R.color.switch_thumb_disable),
                ContextCompat.getColor(PCliq_SettingActivity.this, R.color.primary),
        };

        int[] trackColors = new int[]{
                ContextCompat.getColor(PCliq_SettingActivity.this, R.color.switch_track),
                ContextCompat.getColor(PCliq_SettingActivity.this, R.color.switch_track),
        };
        DrawableCompat.setTintList(DrawableCompat.wrap(switch_noti.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(switch_noti.getTrackDrawable()), new ColorStateList(states, trackColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(switch_consent.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(switch_consent.getTrackDrawable()), new ColorStateList(states, trackColors));
    }

    private void initializeCache() {
        long size = 0;
        size += getDirSize(this.getCacheDir());
        size += getDirSize(this.getExternalCacheDir());
        tv_cachesize.setText(readableFileSize(size));
    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}