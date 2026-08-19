package com.photo.pose.photoshoot.cliq.PCliq_apiservices;

import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PCliq_APIInterface {

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_APP_DETAILS)
    Call<PCliq_ItemAppDetailsList> getAppDetails(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_LOGIN)
    Call<PCliq_ItemUserList> getLogin(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_SOCIAL_LOGIN)
    Call<PCliq_ItemUserList> getSocialLogin(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_REGISTRATION)
    Call<PCliq_ItemUserList> getRegistration(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_FORGOT_PASSWORD)
    Call<PCliq_ItemUserList> getForgotPassword(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_PROFILE)
    Call<PCliq_ItemUserList> getProfile(@Field("data") String data);

    @POST(PCliq_Constant.URL_PROFILE_UPDATE)
    Call<PCliq_ItemUserList> getProfileUpdate(@Body RequestBody imageFile);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_CATEGORIES)
    Call<PCliq_ItemCatList> getCategories(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_SUB_CATEGORIES)
    Call<PCliq_ItemSubCatList> getSubCategories(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_COLORS)
    Call<PCliq_ItemColorsList> getColors(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_LATEST)
    Call<PCliq_ItemPoseList> getWallpapersByLatest(@Field("data") String data, @Query("page") String page);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_CAT)
    Call<PCliq_ItemPoseList> getWallpapersByCat(@Field("data") String data, @Query("page") String page);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_SUB_CAT)
    Call<PCliq_ItemPoseList> getWallpapersBySubCat(@Field("data") String data, @Query("page") String page);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_POPULAR)
    Call<PCliq_ItemPoseList> getWallpapersByPopular(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_FEATURED)
    Call<PCliq_ItemPoseList> getWallpapersByFeatured(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_RATED)
    Call<PCliq_ItemPoseList> getWallpapersByRated(@Field("data") String data);


    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_DOWNLOAD)
    Call<PCliq_ItemPoseList> getWallpapersByDownload(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_RECENT)
    Call<PCliq_ItemPoseList> getWallpapersByRecent(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_FAV)
    Call<PCliq_ItemFavList> getWallpapersByFav(@Field("data") String data);


    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_SEARCH)
    Call<PCliq_ItemPoseList> getWallpapersBySearch(@Field("data") String data, @Query("page") String page);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_BY_RANDOM)
    Call<PCliq_ItemPoseList> getWallpapersByRandom(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_FAV_WALLPAPER)
    Call<PCliq_ItemSuccessList> getDoFavourite(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_DETAILS)
    Call<PCliq_ItemPoseList> getWallpaperDetails(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_LIVE_WALL_LATEST)
    Call<PCliq_ItemPoseList> getLiveWallByLatest(@Field("data") String data, @Query("page") String page);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_LIVE_WALL_POPULAR)
    Call<PCliq_ItemPoseList> getLiveWallByPopular(@Field("data") String data, @Query("page") String page);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_LIVE_WALL_DOWNLOADS)
    Call<PCliq_ItemPoseList> getLiveWallByDownloads(@Field("data") String data, @Query("page") String page);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_LIVE_WALL_RATED)
    Call<PCliq_ItemPoseList> getLiveWallByRated(@Field("data") String data, @Query("page") String page);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_LIVE_WALL_SEARCH)
    Call<PCliq_ItemPoseList> getLiveWallBySearch(@Field("data") String data, @Query("page") String page);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_LIVE_WALL_DETAILS)
    Call<PCliq_ItemPoseList> getLiveWallDetails(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_VIEW)
    Call<PCliq_ItemPoseList> getWallpaperView(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_RATE_WALLPAPER)
    Call<PCliq_ItemSuccessList> getDoRateWallpaper(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_WALLPAPER_DOWNLOAD_COUNT)
    Call<PCliq_ItemSuccessList> getWallpaperDownloadCount(@Field("data") String data);

    @FormUrlEncoded
    @POST(PCliq_Constant.URL_REPORT)
    Call<PCliq_ItemSuccessList> getReport(@Field("data") String data);
}