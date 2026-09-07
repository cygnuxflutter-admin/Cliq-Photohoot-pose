package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PCliq_PreferenceClass {

    private final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private final SharedPreferences prefs;

    public PCliq_PreferenceClass(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirstTimeLaunch() {
        return prefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        prefs.edit().putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).apply();
    }

    public void setDataType(String type, String value) {
        prefs.edit().putString(type, value).apply();
    }

    public String getDataType(String type) {
        return prefs.getString(type, "");
    }

    public String getDataType(String type, String defaultValue) {
        return prefs.getString(type, defaultValue);
    }

    public void setAdsId(String type, String value) {
        prefs.edit().putString(type, value).apply();
    }

    public String getAdsId(String type) {
        if (prefs != null) {
            String id = prefs.getString(type, "");
            if (id != null && !id.trim().isEmpty()) {
                return id;
            }
        }
        // Google Test Ad IDs as default fallback
        switch (type) {
            case "GoogleNativeAd":
            case "AdxNativeUnitID":
                return "ca-app-pub-3940256099942544/2247696110";
            case "GoogleBannerAd":
            case "AdxBannerAdunitID":
                return "ca-app-pub-3940256099942544/6300978111";
            case "GoogleInterstitialAd":
            case "AdxInterstitalAdunitID":
                return "ca-app-pub-3940256099942544/1033173712";
            case "GoogleRewardedAd":
            case "AdxRewardVideoUnitID":
                return "ca-app-pub-3940256099942544/5224354917";
            case "GoogleAppopenAd":
            case "AdxAppOpenID":
                return "ca-app-pub-3940256099942544/3419835294";
            case "GoogleInterstialRewardAd":
                return "ca-app-pub-3940256099942544/5354046379";
            default:
                return "";
        }
    }

    public void setRateSubmited(String type, Boolean value) {
        prefs.edit().putBoolean(type, value).apply();
    }

    public Boolean getRateSubmited(String type) {
        if (prefs != null) {
            return prefs.getBoolean(type, false);
        }
        return false;
    }

    public void setAdsStatus(String type, int value) {
        prefs.edit().putInt(type, value).apply();
    }

    public int getAdsStatus(String type) {
        return prefs.getInt(type, 1);
    }

    public void setInt(String type, int value) {
        prefs.edit().putInt(type, value).apply();
    }

    public int getInt(String type) {
        return prefs.getInt(type, 0);
    }

    public int getInt(String type, int def) {
        return prefs.getInt(type, def);
    }

    public void setFirstDate(String firstDate) {
        prefs.edit().putString("firstDate", firstDate).apply();
    }

    public String getFirstDate() {
        return prefs.getString("firstDate", null);
    }

    public void setDecryptionType(int type) {
        prefs.edit().putInt("decryptionType", type).apply();
    }

    public int getDecryptionType() {
        return prefs.getInt("decryptionType", 0);
    }

    public void setLong(String type, long value) {
        prefs.edit().putLong(type, value).apply();
    }

    public long getLong(String type) {
        return prefs.getLong(type, 0);
    }
}
