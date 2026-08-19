package com.photo.pose.photoshoot.cliq.PCliq_items;

import com.google.gson.annotations.SerializedName;

public class PCliq_ItemSubCat {

    @SerializedName("post_id")
    String id;

    @SerializedName("post_title")
    String name;

    @SerializedName("post_image")
    String image;

    @SerializedName("total_wallpaper")
    String totalWallpapers;

    public PCliq_ItemSubCat(String id, String name, String image, String totalWallpapers) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.totalWallpapers = totalWallpapers;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getTotalWallpapers() {
        return totalWallpapers;
    }
}