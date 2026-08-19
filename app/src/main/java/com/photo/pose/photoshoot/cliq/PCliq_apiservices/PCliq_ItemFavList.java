package com.photo.pose.photoshoot.cliq.PCliq_apiservices;

import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PCliq_ItemFavList implements Serializable {

    @SerializedName("HD_WALLPAPER_APP")
    ItemFavPost itemFavPost;

    public ItemFavPost getItemFavPost() {
        return itemFavPost;
    }

    public class ItemFavPost implements Serializable {

        @SerializedName("wallpaper_list")
        ArrayList<PCliq_ItemPose> arrayListWallpapers;

        @SerializedName("live_wallpaper_list")
        ArrayList<PCliq_ItemPose> arrayListLiveWallpapers;

        public ArrayList<PCliq_ItemPose> getArrayListWallpapers() {
            return arrayListWallpapers;
        }

        public ArrayList<PCliq_ItemPose> getArrayListLiveWallpapers() {
            return arrayListLiveWallpapers;
        }
    }
}