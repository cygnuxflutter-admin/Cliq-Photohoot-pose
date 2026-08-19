package com.photo.pose.photoshoot.cliq.PCliq_apiservices;

import com.google.gson.annotations.SerializedName;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;

import java.util.ArrayList;

public class PCliq_ItemPoseList {

    @SerializedName("HD_WALLPAPER_APP")
    ArrayList<PCliq_ItemPose> arrayListWallpaper;

    @SerializedName("success")
    String success;

    @SerializedName("message")
    String message;

    @SerializedName("total_records")
    int totalRecords;

    public ArrayList<PCliq_ItemPose> getArrayListWallpaper() {
        return arrayListWallpaper;
    }

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getTotalRecords() {
        return totalRecords;
    }
}