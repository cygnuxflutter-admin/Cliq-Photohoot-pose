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
import com.photo.pose.photoshoot.cliq.R;
import com.squareup.picasso.Picasso;
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
        RoundedImageView iv_wallpaper;
        RelativeLayout native_banner_ad_container;
        private final RelativeLayout ad_layout;
        LinearLayout rootlayout;

        private MyViewHolder(View view) {
            super(view);
            iv_wallpaper = view.findViewById(R.id.iv_wallpaper);
            likeButton = view.findViewById(R.id.button_wall_fav);
            tv_title = view.findViewById(R.id.tv_wall_cat);
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
        ((MyViewHolder) holder).native_banner_ad_container.getLayoutParams().width = PCliq_Constant.columnWidth;
        ((MyViewHolder) holder).native_banner_ad_container.getLayoutParams().height = PCliq_Constant.columnHeight;
        ((MyViewHolder) holder).native_banner_ad_container.invalidate();


        if (arrayList.get(position) != null) {
            ((MyViewHolder) holder).rootlayout.setVisibility(View.VISIBLE);
            ((MyViewHolder) holder).ad_layout.setVisibility(View.GONE);

            ((MyViewHolder) holder).likeButton.setLiked(arrayList.get(position).getIsFav());
            ((MyViewHolder) holder).tv_title.setText(arrayList.get(position).getTitle());
            Log.e("TAG", "onBindViewHolder wall: getPosetips" + arrayList.get(position).getPosetips());
            Log.e("TAG", "onBindViewHolder wall:getTitle " + arrayList.get(position).getTitle());

            int placeholder;
            if (arrayList.get(position).getType().equals(PCliq_Constant.TAG_PORTRAIT)) {
                placeholder = R.drawable.pcliq_ic_placeholder_portrait;
            } else if (arrayList.get(position).getTitle().equals(PCliq_Constant.TAG_LANDSCAPE)) {
                placeholder = R.drawable.pcliq_ic_placeholder_landscape;
            } else {
                placeholder = R.drawable.pcliq_ic_placeholder_square;
            }
            Picasso.get()
                    .load(arrayList.get(position).getImage())
                    .resize(methods.getImageThumbWidth(arrayList.get(holder.getAbsoluteAdapterPosition()).getType()), methods.getImageThumbHeight(arrayList.get(holder.getAbsoluteAdapterPosition()).getType()))
                    .centerCrop()
                    .placeholder(placeholder)
                    .into(((MyViewHolder) holder).iv_wallpaper);

            if (sharedPref.isLogged()) {
                ((MyViewHolder) holder).likeButton.setOnLikeListener(new OnLikeListener() {
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
                ((MyViewHolder) holder).likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!sharedPref.isLogged()) {
                            methods.clickLogin();
                        }
                    }
                });
            }

            ((MyViewHolder) holder).iv_wallpaper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClickListener.onClick(holder.getAbsoluteAdapterPosition());
                }
            });


            int width = ((MyViewHolder) holder).rootlayout.getWidth();
            int height = ((MyViewHolder) holder).rootlayout.getHeight();
            Log.e("TAG", "onBindViewHolder: " + width + height);
        } else {
            ((MyViewHolder) holder).rootlayout.setVisibility(View.GONE);
            ((MyViewHolder) holder).ad_layout.setVisibility(View.VISIBLE);
            nativeAdUtil.fillAdmobNativeAd(((MyViewHolder) holder).native_banner_ad_container);
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
            return 1000 + position;
        } else {
            return position;
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