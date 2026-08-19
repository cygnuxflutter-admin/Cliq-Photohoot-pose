package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.content.Context;


public class PCliq_GlobalContext {
    private static PCliq_GlobalContext mInstance;
    private Context context;

    private PCliq_GlobalContext(Context context2) {
        if (this.context == null) {
            this.context = context2;
        }
    }

    public static void initialize(Context context2) {
        if (mInstance == null) {
            mInstance = new PCliq_GlobalContext(context2);
        }
    }

    public static PCliq_GlobalContext getInstance() {
        return mInstance;
    }

    public Context getContext() {
        return this.context;
    }
}
