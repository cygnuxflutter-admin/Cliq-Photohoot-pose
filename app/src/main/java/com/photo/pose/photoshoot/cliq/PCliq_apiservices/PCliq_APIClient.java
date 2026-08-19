package com.photo.pose.photoshoot.cliq.PCliq_apiservices;

import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PCliq_APIClient {

    static Retrofit retrofit = null;

    public static Retrofit getClient() {

        OkHttpClient client = new OkHttpClient.Builder().build();

        retrofit = new Retrofit.Builder()
                .baseUrl(PCliq_Constant.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}