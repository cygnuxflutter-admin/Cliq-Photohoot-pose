package com.photo.pose.photoshoot.cliq.PCliq_items;

import com.google.gson.annotations.SerializedName;

public class PCliq_ItemSuccess {

    @SerializedName("success")
    String success;

    @SerializedName("msg")
    String message;

    @SerializedName("total_rate")
    int totalRate;

    @SerializedName("download")
    String totalDownloads;

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getTotalRate() {
        return totalRate;
    }

    public String getTotalDownloads() {
        return totalDownloads;
    }
}