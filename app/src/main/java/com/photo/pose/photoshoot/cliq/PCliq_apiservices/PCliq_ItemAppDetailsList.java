package com.photo.pose.photoshoot.cliq.PCliq_apiservices;

import com.google.gson.annotations.SerializedName;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemAbout;

import java.io.Serializable;
import java.util.ArrayList;

public class PCliq_ItemAppDetailsList implements Serializable {

    @SerializedName("HD_WALLPAPER_APP")
    ArrayList<PCliq_ItemAbout> arrayListAbout;

    public ArrayList<PCliq_ItemAbout> getArrayListAbout() {
        return arrayListAbout;
    }
}