package com.photo.pose.photoshoot.cliq.PCliq_apiservices;

import com.google.gson.annotations.SerializedName;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemColors;

import java.util.ArrayList;

public class PCliq_ItemColorsList {

    @SerializedName("HD_WALLPAPER_APP")
    ArrayList<PCliq_ItemColors> arrayListColors;

    @SerializedName("success")
    String success;

    @SerializedName("message")
    String message;

    public ArrayList<PCliq_ItemColors> getArrayListColors() {
        return arrayListColors;
    }

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}