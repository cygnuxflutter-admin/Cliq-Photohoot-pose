package com.photo.pose.photoshoot.cliq.PCliq_utils;

//import static com.vehicle.information.trending.rtoexam.rto.Task_adManager.Task_NativeAdUtil.loadNativeAd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

//import com.afollestad.materialdialogs.MaterialDialog;
//import com.vehicle.information.trending.rtoexam.rto.BuildConfig;
//import com.vehicle.information.trending.rtoexam.rto.R;
//import com.vehicle.information.trending.rtoexam.rto.Task_interfaces.Task_DialogClickListener;

import com.afollestad.materialdialogs.MaterialDialog;
import com.photo.pose.photoshoot.cliq.R;

import java.util.Objects;

public class PCliq_MaterialDialogUtils {

    private PCliq_MaterialDialogUtils() {
    }

    public static PCliq_MaterialDialogUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public MaterialDialog createAnimationDialog(Context activity) {
        return new MaterialDialog.Builder(activity)
                .customView(R.layout.pcliq_lottie_anim_dialog, false)
                .contentColor(Color.TRANSPARENT)
                .backgroundColor(Color.TRANSPARENT)
                .build();
    }

    private static class SingletonHolder {
        static final PCliq_MaterialDialogUtils INSTANCE = new PCliq_MaterialDialogUtils();
    }

    public void errorDialog(Activity activity, String msg) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.pcliq_error_dialog, false)
                .contentColor(Color.TRANSPARENT)
                .backgroundColor(Color.TRANSPARENT)
                .cancelable(false)
                .build();

        Objects.requireNonNull(materialDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
//         @SuppressLint("ResourceType")
//         Dialog materialDialog = new Dialog(activity, 16974126);
//         materialDialog.requestWindowFeature(1);
//         materialDialog.setContentView(R.layout.error_dialog);
//         materialDialog.setCancelable(false);

        materialDialog.show();

        TextView description = (TextView) materialDialog.findViewById(R.id.description);

        description.setText(msg);

        TextView btn_ok = (TextView) materialDialog.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(v -> {
            materialDialog.dismiss();
            activity.finish();
        });
    }



//    public void rewardDialog(Activity activity, String title, String message, Task_DialogClickListener positiveButton, Task_DialogClickListener negativeButton) {
//        //        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
//        //                .customView(R.layout.reward_dialog, false)
//        //                .contentColor(Color.TRANSPARENT)
//        //                .backgroundColor(Color.TRANSPARENT)
//        //                .cancelable(false)
//        //                .build();
//
//        @SuppressLint("ResourceType")
//        Dialog materialDialog = new Dialog(activity, 16974126);
//        materialDialog.requestWindowFeature(1);
//        materialDialog.setContentView(R.layout.task_reward_dialog);
//        materialDialog.setCancelable(false);
//
//
//        if (!materialDialog.isShowing())
//            materialDialog.show();
//
//        TextView tv_title = materialDialog.findViewById(R.id.title);
//        TextView tv_description = materialDialog.findViewById(R.id.description);
//
//        TextView button1 = materialDialog.findViewById(R.id.button1);
//        TextView button2 = materialDialog.findViewById(R.id.button2);
//
//        tv_title.setText(title);
//        tv_description.setText(message);
//
//        button2.setOnClickListener(v -> positiveButton.onClick(materialDialog));
//        button1.setOnClickListener(v -> negativeButton.onClick(materialDialog));
//
//        RelativeLayout native_banner_ad_container = materialDialog.findViewById(R.id.native_banner_ad_containers);
//
//        loadNativeAd(native_banner_ad_container, activity);
//
//    }

//    public void PermissionDialog(Activity activity) {
////        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
////                .customView(R.layout.permission_dialog, false)
////                .contentColor(Color.TRANSPARENT)
////                .backgroundColor(Color.TRANSPARENT)
////                .cancelable(false)
////                .build();
//
//        @SuppressLint("ResourceType")
//        Dialog materialDialog = new Dialog(activity, 16974126);
//        materialDialog.requestWindowFeature(1);
//        materialDialog.setContentView(R.layout.task_permission_dialog);
//        materialDialog.setCancelable(false);
//
//        materialDialog.show();
//
//        TextView btn_cancel = materialDialog.findViewById(R.id.btn_cancel);
//        TextView btn_settings = materialDialog.findViewById(R.id.btn_settings);
//
//        btn_cancel.setOnClickListener(v -> materialDialog.dismiss());
//
//        btn_settings.setOnClickListener(v -> {
//            materialDialog.dismiss();
//            activity.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
//        });
//    }


}
