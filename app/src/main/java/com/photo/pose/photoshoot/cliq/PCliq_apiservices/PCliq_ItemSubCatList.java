package com.photo.pose.photoshoot.cliq.PCliq_apiservices;

import com.google.gson.annotations.SerializedName;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemSubCat;

import java.util.ArrayList;

public class PCliq_ItemSubCatList {

    @SerializedName("HD_WALLPAPER_APP")
    ArrayList<PCliq_ItemSubCat> arrayListSubCat;

    @SerializedName("success")
    String success;

    @SerializedName("message")
    String message;

    public ArrayList<PCliq_ItemSubCat> getArrayListSubCat() {
        return arrayListSubCat;
    }

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}