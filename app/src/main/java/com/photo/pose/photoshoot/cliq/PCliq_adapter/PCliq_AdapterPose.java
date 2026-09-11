package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.nativead.NativeAd;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_NativeAdUtil;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemSuccessList;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_RecyclerViewClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.photo.pose.photoshoot.cliq.R;
import com.startapp.sdk.ads.nativead.NativeAdDetails;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PCliq_AdapterPose extends RecyclerView.Adapter {

    ArrayList<PCliq_ItemPose> arrayList;
    Activity context;
    PCliq_SharedPref sharedPref;
    PCliq_RecyclerViewClickListener recyclerViewClickListener;
    PCliq_Methods methods;

    final int VIEW_PROG = -1;
    final int VIEW_ITEM = 1;
    final int VIEW_AD = 2;

    Boolean isAdLoaded = false;
    List<NativeAd> mNativeAdsAdmob = new ArrayList<>();
    List<NativeAdDetails> nativeAdsStartApp = new ArrayList<>();
    private final PCliq_NativeAdUtil nativeAdUtil;

    public PCliq_AdapterPose(Activity context, ArrayList<PCliq_ItemPose> arrayList, PCliq_RecyclerViewClickListener recyclerViewClickListener) {
        this.arrayList = arrayList;
        this.context = context;
        methods = new PCliq_Methods(context);
        sharedPref = new PCliq_SharedPref(context);
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.nativeAdUtil = new PCliq_NativeAdUtil(context);
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {

        LikeButton likeButton;
        TextView tv_title;
        TextView tv_pro_badge;
        RoundedImageView iv_wallpaper;
        RelativeLayout native_banner_ad_container;
        private final View ad_layout;
        View rootlayout;

        private MyViewHolder(View view) {
            super(view);
            iv_wallpaper = view.findViewById(R.id.iv_wallpaper);
            likeButton = view.findViewById(R.id.button_wall_fav);
            tv_title = view.findViewById(R.id.tv_wall_cat);
            tv_pro_badge = view.findViewById(R.id.tv_pro_badge);
            rootlayout = itemView.findViewById(R.id.rootlayout);
            ad_layout = itemView.findViewById(R.id.ad_layout);
            native_banner_ad_container = view.findViewById(R.id.native_banner_ad_container);
        }
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {

        boolean isAdRequested = false;

        private ADViewHolder(View view) {
            super(view);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcliq_layout_pose, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if (arrayList.get(position) != null) {
            MyViewHolder myHolder = (MyViewHolder) holder;

            myHolder.itemView.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams itemParams = myHolder.itemView.getLayoutParams();
            itemParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            itemParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            myHolder.itemView.setLayoutParams(itemParams);

            // Only set ad container dimensions once (use a tag to avoid re-doing this)
            if (myHolder.native_banner_ad_container.getTag() == null) {
                myHolder.native_banner_ad_container.getLayoutParams().width = PCliq_Constant.columnWidth;
                myHolder.native_banner_ad_container.getLayoutParams().height = PCliq_Constant.columnHeight;
                myHolder.native_banner_ad_container.setTag(Boolean.TRUE);
            }

            myHolder.rootlayout.setVisibility(View.VISIBLE);
            myHolder.ad_layout.setVisibility(View.GONE);

            myHolder.likeButton.setLiked(arrayList.get(position).getIsFav());
            myHolder.tv_title.setText(arrayList.get(position).getTitle());

            // Pro badge for every 5th item
            if (myHolder.tv_pro_badge != null) {
                myHolder.tv_pro_badge.setVisibility(position % 5 == 3 ? View.VISIBLE : View.GONE);
            }

            int placeholder;
            String poseType = arrayList.get(position).getType() != null ? arrayList.get(position).getType() : "";
            if (poseType.equals(PCliq_Constant.TAG_PORTRAIT)) {
                placeholder = R.drawable.pcliq_ic_placeholder_portrait;
            } else if (poseType.equals(PCliq_Constant.TAG_LANDSCAPE)) {
                placeholder = R.drawable.pcliq_ic_placeholder_landscape;
            } else {
                placeholder = R.drawable.pcliq_ic_placeholder_square;
            }

            int targetWidth = methods.getImageThumbWidth(poseType);
            int targetHeight = methods.getImageThumbHeight(poseType);

            try {
                String imgUrl = arrayList.get(position).getImage();
                if (imgUrl != null) {
                    imgUrl = imgUrl.replace(" ", "%20");
                }
                Glide.with(context)
                        .load(imgUrl)
                        .override(targetWidth, targetHeight)
                        .placeholder(placeholder)
                        .error(placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.2f)
                        .centerCrop()
                        .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                myHolder.itemView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        myHolder.itemView.setVisibility(View.GONE);
                                        ViewGroup.LayoutParams params = myHolder.itemView.getLayoutParams();
                                        params.width = 0;
                                        params.height = 0;
                                        myHolder.itemView.setLayoutParams(params);
                                    }
                                });
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(myHolder.iv_wallpaper);
            } catch (Exception ignored) {}

            if (sharedPref.isLogged()) {
                myHolder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        try {
                            loadFav(holder.getAbsoluteAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        try {
                            loadFav(holder.getAbsoluteAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                myHolder.likeButton.setOnClickListener(view -> {
                    if (!sharedPref.isLogged()) {
                        methods.clickLogin();
                    }
                });
            }

            myHolder.iv_wallpaper.setOnClickListener(view ->
                    recyclerViewClickListener.onClick(holder.getAbsoluteAdapterPosition()));

        } else {
            MyViewHolder myHolder = (MyViewHolder) holder;
            int fixedH = (int) (280 * context.getResources().getDisplayMetrics().density);
            myHolder.rootlayout.setVisibility(View.GONE);
            myHolder.ad_layout.setVisibility(View.VISIBLE);
            if (myHolder.ad_layout.getLayoutParams() != null) {
                myHolder.ad_layout.getLayoutParams().height = fixedH;
            }
            if (myHolder.native_banner_ad_container.getLayoutParams() != null) {
                myHolder.native_banner_ad_container.getLayoutParams().height = fixedH;
            }
            nativeAdUtil.fillAdmobNativeAd(myHolder.native_banner_ad_container);
        }

    }

    private void loadFav(int pos) {
        if (sharedPref.isLogged()) {
            if (methods.isNetworkAvailable()) {
                Call<PCliq_ItemSuccessList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getDoFavourite(methods.getAPIRequest(PCliq_Constant.URL_DO_FAV, 0, arrayList.get(pos).getId(), "", "", "", "", "", "", "", "", "", sharedPref.getUserId(), "Wallpaper"));
                call.enqueue(new Callback<PCliq_ItemSuccessList>() {
                    @Override
                    public void onResponse(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Response<PCliq_ItemSuccessList> response) {
                        try {
                            if (response.body() != null && response.body().getArrayListSuccess() != null) {
                                if (response.body().getArrayListSuccess().size() > 0) {
                                    arrayList.get(pos).setIsFav(response.body().getArrayListSuccess().get(0).getSuccess().equals("true"));
                                    Toast.makeText(context, response.body().getArrayListSuccess().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Throwable t) {
                        call.cancel();
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            methods.clickLogin();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void hideHeader() {
        try {
//            ProgressViewHolder.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_PROG;
        } else if (arrayList.get(position) == null) {
            return VIEW_AD;
        } else {
            return VIEW_ITEM;
        }
    }
    public void destroyNativeAds() {
        try {
            for (int i = 0; i < mNativeAdsAdmob.size(); i++) {
                mNativeAdsAdmob.get(i).destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}