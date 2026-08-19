package com.photo.pose.photoshoot.cliq.PCliq_apiservices;

import com.google.gson.annotations.SerializedName;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemCat;

import java.util.ArrayList;

public class PCliq_ItemCatList {

    @SerializedName("HD_WALLPAPER_APP")
    ArrayList<PCliq_ItemCat> arrayListCat;

    @SerializedName("success")
    String success;

    @SerializedName("message")
    String message;

    public ArrayList<PCliq_ItemCat> getArrayListCat() {
        return arrayListCat;
    }

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}