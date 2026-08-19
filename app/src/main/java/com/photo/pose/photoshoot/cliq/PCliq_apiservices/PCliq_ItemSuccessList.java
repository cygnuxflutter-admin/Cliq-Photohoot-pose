package com.photo.pose.photoshoot.cliq.PCliq_apiservices;

import com.google.gson.annotations.SerializedName;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemSuccess;

import java.util.ArrayList;

public class PCliq_ItemSuccessList {

    @SerializedName("HD_WALLPAPER_APP")
    ArrayList<PCliq_ItemSuccess> arrayListSuccess;

    public ArrayList<PCliq_ItemSuccess> getArrayListSuccess() {
        return arrayListSuccess;
    }
}