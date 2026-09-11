package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemSuccessList;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_RecyclerViewClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.photo.pose.photoshoot.cliq.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_AdapterHorizontalPose extends RecyclerView.Adapter<PCliq_AdapterHorizontalPose.ViewHolder> {

    private final Activity context;
    private final ArrayList<PCliq_ItemPose> arrayList;
    private final PCliq_RecyclerViewClickListener listener;
    private final PCliq_SharedPref sharedPref;
    private final PCliq_Methods methods;
    private final PCliq_DBHelper dbHelper;
    private final com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_NativeAdUtil nativeAdUtil;

    public PCliq_AdapterHorizontalPose(Activity context, ArrayList<PCliq_ItemPose> arrayList, PCliq_RecyclerViewClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
        this.sharedPref = new PCliq_SharedPref(context);
        this.dbHelper = new PCliq_DBHelper(context);
        this.methods = new PCliq_Methods(context);
        this.nativeAdUtil = new com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_NativeAdUtil(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pcliq_item_horizontal_pose, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final PCliq_ItemPose item = arrayList.get(position);
        
        if (item == null) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }

        holder.itemView.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams itemParams = holder.itemView.getLayoutParams();
        itemParams.width = (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 144, context.getResources().getDisplayMetrics());
        itemParams.height = (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 214, context.getResources().getDisplayMetrics());
        holder.itemView.setLayoutParams(itemParams);

        if (holder.rootlayout != null) holder.rootlayout.setVisibility(View.VISIBLE);
        if (holder.ad_layout != null) holder.ad_layout.setVisibility(View.GONE);

        holder.tvTitle.setText(item.getTitle() != null && !item.getTitle().isEmpty() ? item.getTitle() : "Pose");

        // Pro badge for every 5th item to match showInter logic
        if (holder.tvProBadge != null) {
            holder.tvProBadge.setVisibility(position % 5 == 3 ? View.VISIBLE : View.GONE);
        }

        holder.buttonFav.setLiked(item.getIsFav());

        int placeholder = R.drawable.pcliq_ic_placeholder_portrait;
        try {
            String imgUrl = item.getImage();
            if (imgUrl != null) {
                imgUrl = imgUrl.replace(" ", "%20");
            }
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.2f)
                    .centerCrop()
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            holder.itemView.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.itemView.setVisibility(View.GONE);
                                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                    params.width = 0;
                                    params.height = 0;
                                    holder.itemView.setLayoutParams(params);
                                }
                            });
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.ivWallpaper);
        } catch (Exception ignored) {}

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(position);
            }
        });

        if (sharedPref.isLogged()) {
            holder.buttonFav.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    loadFav(holder.getAbsoluteAdapterPosition());
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    loadFav(holder.getAbsoluteAdapterPosition());
                }
            });
        } else {
            holder.buttonFav.setOnClickListener(view -> {
                if (!sharedPref.isLogged()) {
                    methods.clickLogin();
                }
            });
        }
    }

    private void loadFav(int pos) {
        if (sharedPref.isLogged()) {
            if (methods.isNetworkAvailable()) {
                Call<PCliq_ItemSuccessList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getDoFavourite(methods.getAPIRequest(PCliq_Constant.URL_DO_FAV, 0, arrayList.get(pos).getId(), "", "", "", "", "", "", "", "", "", sharedPref.getUserId(), "Wallpaper"));
                call.enqueue(new Callback<PCliq_ItemSuccessList>() {
                    @Override
                    public void onResponse(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Response<PCliq_ItemSuccessList> response) {
                        if (response.body() != null && response.body().getArrayListSuccess() != null) {
                            if (response.body().getArrayListSuccess().get(0).getSuccess().equals("1")) {
                                arrayList.get(pos).setIsFav(true);
                                Toast.makeText(context, response.body().getArrayListSuccess().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                arrayList.get(pos).setIsFav(false);
                                Toast.makeText(context, response.body().getArrayListSuccess().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Throwable t) {
                        call.cancel();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayList != null ? arrayList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivWallpaper;
        TextView tvTitle;
        TextView tvProBadge;
        LikeButton buttonFav;
        View rootlayout;
        View ad_layout;
        android.widget.RelativeLayout native_banner_ad_container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWallpaper = itemView.findViewById(R.id.iv_wallpaper);
            tvTitle = itemView.findViewById(R.id.tv_wall_cat);
            tvProBadge = itemView.findViewById(R.id.tv_pro_badge);
            buttonFav = itemView.findViewById(R.id.button_wall_fav);
            rootlayout = itemView.findViewById(R.id.rootlayout);
            ad_layout = itemView.findViewById(R.id.ad_layout);
            native_banner_ad_container = itemView.findViewById(R.id.native_banner_ad_container);
        }
    }
}
