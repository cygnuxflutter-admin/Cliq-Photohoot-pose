package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.graphics.drawable.DrawableCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.Gravity;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.eventbus.EventAction;
import com.eventbus.GlobalBus;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.like.LikeButton;
import com.like.OnLikeListener;
//import com.ortiz.touchview.TouchImageView;
import com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_LoadAds;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterSimilarPoses;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterTags;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemSuccessList;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemPoseList;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_NetworkUtils;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_RecyclerItemClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.photo.pose.photoshoot.cliq.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.widget.SwitchCompat;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterTags;

public class PCliq_PoseDetailsActivity extends AppCompatActivity {

    PCliq_DBHelper PC_dbHelper;

    PCliq_Methods PC_methods;
    PCliq_SharedPref PC_sharedPref;
    ImagePagerAdapter PC_pagerAdapter;
    LikeButton PC_likeButton;
    View PC_btn_download;
    int position;

    Dialog PC_dialog_rate;
    RelativeLayout PC_coordinatorLayout;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    final int MY_PERMISSIONS_REQUEST_MANAGE_EXTERNAL_STORAGE = 101;
    com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog PC_progressDialog;
    BottomSheetDialog PC_dialog_report;
    int height = 0, page = 2;
    String PC_wallType = "", PC_color_ids = "", PC_cid = "1", PC_list_type = "";
    Boolean isOver = false;
    ArrayList<String> PC_arrayListTags;
    View PC_iv_camera;
    String PC_imgUrl;
    ImageView PC_iv_pose;

    View PC_iv_more;
    View PC_iv_back;
    TextView PC_tv_pose_name;
    String PC_pass_pose;
    public static Boolean isposesketch = false;
    String PC_iv_pose_sketch;
    String PC_iv_pose_orignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_pose_details);

        PC_methods = new PCliq_Methods(this, new PCliq_InterAdListener() {
            @Override
            public void onClick(int pos, String type) {
                String downloadUrl = (PC_pass_pose != null && !PC_pass_pose.isEmpty())
                        ? PC_pass_pose
                        : PCliq_Constant.arrayList.get(position).getImage();
                PC_methods.saveImage(downloadUrl, type, PC_coordinatorLayout, "wallpaper");
            }
        });
        PC_methods.setStatusColor(getWindow());
        PC_methods.forceRTLIfSupported(getWindow());

        PC_dbHelper = new PCliq_DBHelper(this);
        PC_sharedPref = new PCliq_SharedPref(this);

        RelativeLayout rl_ad = this.findViewById(R.id.rl_ad);
        if (PCliq_NetworkUtils.isNetworkAvailable(this)) {
            if (new PCliq_PreferenceClass(PCliq_PoseDetailsActivity.this).getInt("BannerAdStatus") == 1) {
                PCliq_LoadAds.loadAdmobBannerAd(this, rl_ad);
            } else {
                rl_ad.setVisibility(View.GONE);
            }
        }


        PC_progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(PCliq_PoseDetailsActivity.this);
        PC_progressDialog.setMessage(getString(R.string.loading));

        position = getIntent().getIntExtra("pos", 0);
        PC_list_type = getIntent().getStringExtra("list_type");
        page = getIntent().getIntExtra("page", 0);
        PC_wallType = getIntent().getStringExtra("wallType");
        PC_color_ids = getIntent().getStringExtra("color_ids");
        if (PC_list_type.equals(getString(R.string.categories)) || PC_list_type.equals(getString(R.string.sub_categories))) {
            PC_cid = getIntent().getStringExtra("cid");
        }

        height = PC_methods.getScreenHeight();

        PC_btn_download = findViewById(R.id.btn_details_download);
        PC_likeButton = findViewById(R.id.btn_details_fav);
        PC_coordinatorLayout = findViewById(R.id.rl);
        PC_iv_camera = findViewById(R.id.iv_camera);
        PC_iv_pose = findViewById(R.id.iv_pose);
        PC_tv_pose_name = findViewById(R.id.tv_pose_name);
        PC_iv_more = findViewById(R.id.iv_more);
        PC_iv_back = findViewById(R.id.iv_back);

        View rlTopActions = findViewById(R.id.rl_top_actions);
        if (rlTopActions != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rlTopActions, (v, insets) -> {
                androidx.core.graphics.Insets statusBarInsets = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars());
                v.setPadding(v.getPaddingLeft(), statusBarInsets.top + 16, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }
        RatingBar ratingBar = findViewById(R.id.rating_wall_details);
        TextView tv_views = findViewById(R.id.tv_wall_details_views);
        TextView tv_downloads = findViewById(R.id.tv_wall_details_downloads);
        loadViewed(position);

        PC_pagerAdapter = new ImagePagerAdapter();

        PC_imgUrl = PCliq_Constant.arrayList.get(position).getImage();
        PC_tv_pose_name.setText(PCliq_Constant.arrayList.get(position).getTitle());

        String origImg = PCliq_Constant.arrayList.get(position).getImage();
        if (origImg != null) {
            origImg = origImg.replace(" ", "%20");
        }
        PC_iv_pose_orignal = origImg;
        PC_iv_pose_sketch = origImg + "_sketch.png";

        PC_pass_pose = PC_iv_pose_orignal;

        // Setup Sketch Guide Toggle (Default OFF so real photo is displayed first)
        SwitchCompat switchSketchGuide = findViewById(R.id.switch_sketch_guide);
        TextView tvSketchGuideStatus = findViewById(R.id.tv_sketch_guide_status);
        View rlHeroContainer = findViewById(R.id.rl_hero_photo_container);

        if (switchSketchGuide != null) {
            switchSketchGuide.setThumbTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.pcliq_switch_thumb_selector));
            switchSketchGuide.setTrackTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.pcliq_switch_track_selector));
            switchSketchGuide.setChecked(false);
            if (tvSketchGuideStatus != null) {
                tvSketchGuideStatus.setText("OFF");
                tvSketchGuideStatus.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_sub_brown));
            }
            switchSketchGuide.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isposesketch = isChecked;
                if (tvSketchGuideStatus != null) {
                    tvSketchGuideStatus.setText(isChecked ? "ON" : "OFF");
                    tvSketchGuideStatus.setTextColor(androidx.core.content.ContextCompat.getColor(PCliq_PoseDetailsActivity.this, isChecked ? R.color.gold_primary : R.color.text_sub_brown));
                }
                if (isChecked && PC_iv_pose_sketch != null) {
                    PC_pass_pose = PC_iv_pose_sketch;
                    if (rlHeroContainer != null) {
                        rlHeroContainer.setBackgroundColor(0xFF1E1A18);
                    }
                    if (PC_iv_pose != null) {
                        PC_iv_pose.setBackgroundColor(0xFF1E1A18);
                        PC_iv_pose.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        PC_iv_pose.setPadding(24, 72, 24, 48);
                    }
                    Glide.with(PCliq_PoseDetailsActivity.this)
                            .load(PC_iv_pose_sketch)
                            .placeholder(R.drawable.pcliq_ic_placeholder_portrait)
                            .error(Glide.with(PCliq_PoseDetailsActivity.this).load(PC_iv_pose_orignal))
                            .into(PC_iv_pose);
                } else {
                    PC_pass_pose = PC_iv_pose_orignal;
                    if (rlHeroContainer != null) {
                        rlHeroContainer.setBackgroundColor(0x00000000);
                    }
                    if (PC_iv_pose != null) {
                        PC_iv_pose.setBackgroundColor(0x00000000);
                        PC_iv_pose.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        PC_iv_pose.setPadding(0, 0, 0, 0);
                    }
                    Glide.with(PCliq_PoseDetailsActivity.this)
                            .load(PC_iv_pose_orignal)
                            .placeholder(R.drawable.pcliq_ic_placeholder_portrait)
                            .into(PC_iv_pose);
                }
            });
        }

        // Load Default Photo (Original photoshoot image)
        if (rlHeroContainer != null) {
            rlHeroContainer.setBackgroundColor(0x00000000);
        }
        if (PC_iv_pose != null) {
            PC_iv_pose.setBackgroundColor(0x00000000);
            PC_iv_pose.setScaleType(ImageView.ScaleType.CENTER_CROP);
            PC_iv_pose.setPadding(0, 0, 0, 0);
        }
        Glide.with(this)
                .load(PC_iv_pose_orignal)
                .placeholder(R.drawable.pcliq_ic_placeholder_portrait)
                .into(PC_iv_pose);

        URLReachabilityChecker checker = new URLReachabilityChecker();
        checker.execute(PC_iv_pose_sketch);

        // Populate Metadata & Description with Smart Analysis
        TextView tvDifficulty = findViewById(R.id.tv_meta_difficulty);
        TextView tvTime = findViewById(R.id.tv_meta_time);
        TextView tvProps = findViewById(R.id.tv_meta_props);
        TextView tvDescription = findViewById(R.id.tv_pose_description);
        RecyclerView rvTags = findViewById(R.id.rv_tags);
        RecyclerView rvSimilarPoses = findViewById(R.id.rv_similar_poses);

        String currentTitle = "";
        String currentTags = "";
        String currentServerTips = "";
        if (position < PCliq_Constant.arrayList.size() && PCliq_Constant.arrayList.get(position) != null) {
            currentTitle = PCliq_Constant.arrayList.get(position).getTitle();
            currentTags = PCliq_Constant.arrayList.get(position).getTags();
            currentServerTips = PCliq_Constant.arrayList.get(position).getPosetips();
        }

        if (tvDifficulty != null) {
            tvDifficulty.setText(getSmartDifficulty(position, currentTitle, currentTags));
        }
        if (tvTime != null) {
            tvTime.setText(getSmartLighting(position, currentTitle, currentTags));
        }
        if (tvProps != null) {
            tvProps.setText(getSmartAngle(position, currentTitle, currentTags));
        }
        if (tvDescription != null) {
            tvDescription.setText(getSmartPoseDirection(currentTitle, currentTags, currentServerTips));
        }

        if (rvTags != null && currentTags != null && !currentTags.trim().isEmpty()) {
            rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            ArrayList<String> tagList = new ArrayList<>(Arrays.asList(currentTags.split(",")));
            PCliq_AdapterTags tagsAdapter = new PCliq_AdapterTags(tagList);
            rvTags.setAdapter(tagsAdapter);
        }

        // Setup Similar Poses Carousel
        if (rvSimilarPoses != null && PCliq_Constant.arrayList != null && PCliq_Constant.arrayList.size() > 1) {
            rvSimilarPoses.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            ArrayList<PCliq_ItemPose> similarList = new ArrayList<>();
            for (int i = 0; i < PCliq_Constant.arrayList.size(); i++) {
                if (i != position && PCliq_Constant.arrayList.get(i) != null) {
                    similarList.add(PCliq_Constant.arrayList.get(i));
                }
            }
            if (!similarList.isEmpty()) {
                PCliq_AdapterSimilarPoses similarAdapter = new PCliq_AdapterSimilarPoses(this, similarList, (item, pos) -> {
                    int targetIndex = PCliq_Constant.arrayList.indexOf(item);
                    if (targetIndex >= 0) {
                        Intent intent = new Intent(PCliq_PoseDetailsActivity.this, PCliq_PoseDetailsActivity.class);
                        intent.putExtra("pos", targetIndex);
                        intent.putExtra("list_type", PC_list_type);
                        intent.putExtra("page", page);
                        intent.putExtra("wallType", PC_wallType);
                        intent.putExtra("color_ids", PC_color_ids);
                        if (PC_cid != null) intent.putExtra("cid", PC_cid);
                        startActivity(intent);
                        finish();
                    }
                });
                rvSimilarPoses.setAdapter(similarAdapter);
            } else {
                View container = findViewById(R.id.ll_similar_poses_container);
                if (container != null) container.setVisibility(View.GONE);
            }
        } else {
            View container = findViewById(R.id.ll_similar_poses_container);
            if (container != null) container.setVisibility(View.GONE);
        }

        if (ratingBar != null && PCliq_Constant.arrayList.get(position).getAverageRate() != null) {
            try {
                ratingBar.setRating(Float.parseFloat(PCliq_Constant.arrayList.get(position).getAverageRate()));
            } catch (Exception ignored) {}
        }
        if (tv_views != null) {
            tv_views.setText(PCliq_Constant.arrayList.get(position).getTotalViews());
        }
        if (tv_downloads != null) {
            tv_downloads.setText(PCliq_Constant.arrayList.get(position).getTotalDownloads());
        }

        PC_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        PC_iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextThemeWrapper ctw = new ContextThemeWrapper(PCliq_PoseDetailsActivity.this, R.style.PCliq_PopupOverlay);
                PopupMenu popup = new PopupMenu(ctw, PC_iv_more, Gravity.END);
                popup.getMenuInflater().inflate(R.menu.pcliq_menu_pose_details, popup.getMenu());

                Menu menu = popup.getMenu();
                int espressoColor = ContextCompat.getColor(PCliq_PoseDetailsActivity.this, R.color.text_espresso);
                int goldColor = ContextCompat.getColor(PCliq_PoseDetailsActivity.this, R.color.gold_primary);

                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    if (item.getTitle() != null) {
                        SpannableString s = new SpannableString(item.getTitle());
                        s.setSpan(new ForegroundColorSpan(espressoColor), 0, s.length(), 0);
                        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
                        item.setTitle(s);
                    }
                    Drawable icon = item.getIcon();
                    if (icon != null) {
                        Drawable wrappedIcon = DrawableCompat.wrap(icon.mutate());
                        DrawableCompat.setTint(wrappedIcon, goldColor);
                        item.setIcon(wrappedIcon);
                    }
                }

                try {
                    popup.setForceShowIcon(true);
                } catch (Exception ignored) {
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == android.R.id.home) {
                            onBackPressed();
                        } else if (itemId == R.id.menu_rate) {
                            if (PC_sharedPref.isLogged()) {
                                openRateDialog();
                            } else {
                                PC_methods.clickLogin();
                            }
                        } else if (itemId == R.id.menu_share) {
                            PC_methods.showInter(0, getString(R.string.share));
                        } else if (itemId == R.id.menu_details) {
                            showDetailDialog();
                        } else if (itemId == R.id.menu_report) {
                            showReportDialog();
                        } else {
                            return true;
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });

        PC_btn_download.setOnClickListener(v -> {
            if (checkPer()) {
                String rewardId = new PCliq_PreferenceClass(PCliq_PoseDetailsActivity.this).getAdsId("GoogleInterstialRewardAd");
                if (rewardId != null && !rewardId.trim().isEmpty()) {
                    new androidx.appcompat.app.AlertDialog.Builder(PCliq_PoseDetailsActivity.this)
                            .setTitle("🌟 HD Download")
                            .setMessage("Watch a short video ad to download this pose in high quality.")
                            .setPositiveButton("Watch & Download", (dialog, which) -> {
                                com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_RewardVideoManager.showRewardVideoAd(PCliq_PoseDetailsActivity.this, new com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_RewardVideoManager.OnRewardAdLoadInterface() {
                                    @Override
                                    public void onAdClose(boolean isWithReward) {
                                        String downloadUrl = (PC_pass_pose != null && !PC_pass_pose.isEmpty())
                                                ? PC_pass_pose
                                                : PCliq_Constant.arrayList.get(position).getImage();
                                        PC_methods.saveImage(downloadUrl, getString(R.string.download), PC_coordinatorLayout, "wallpaper");
                                    }

                                    @Override
                                    public void onAdFail() {
                                        String downloadUrl = (PC_pass_pose != null && !PC_pass_pose.isEmpty())
                                                ? PC_pass_pose
                                                : PCliq_Constant.arrayList.get(position).getImage();
                                        PC_methods.saveImage(downloadUrl, getString(R.string.download), PC_coordinatorLayout, "wallpaper");
                                    }
                                });
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    PC_methods.showInter(position, getString(R.string.download));
                }
            }
        });
        PC_iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PCliq_PoseDetailsActivity.this, PCliq_NewCameraActivity.class);
                intent.putExtra("image", PC_pass_pose != null ? PC_pass_pose : PC_iv_pose_orignal);
                intent.putExtra("isposesketch", isposesketch != null ? isposesketch : false);
                startActivity(intent);
            }
        });


        PC_likeButton.setLiked(PCliq_Constant.arrayList.get(position).getIsFav());
        if (PC_sharedPref.isLogged()) {
            PC_likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    try {
                        loadFav(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    try {
                        loadFav(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            PC_likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!PC_sharedPref.isLogged()) {
                        PC_methods.clickLogin();
                    }
                }
            });
        }

        loadWallpaperDetails(position);

    }

    public class URLReachabilityChecker extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            if (urls.length == 0) {
                return false;
            }

            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");

                int responseCode = connection.getResponseCode();

                return (responseCode >= 200 && responseCode <= 399);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isReachable) {
            View llSketchToggle = findViewById(R.id.ll_sketch_toggle);
            if (!isReachable) {
                if (llSketchToggle != null) {
                    llSketchToggle.setVisibility(View.GONE);
                }
                isposesketch = false;
                PC_pass_pose = PC_iv_pose_orignal;
            } else {
                if (llSketchToggle != null) {
                    llSketchToggle.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return PCliq_Constant.arrayList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            View imageLayout = inflater.inflate(R.layout.pcliq_layout_vp_pose, container, false);
            assert imageLayout != null;

            final ImageView iv_wallpaper = imageLayout.findViewById(R.id.iv_wallpaper);

            final CircularProgressBar progressBar = imageLayout.findViewById(R.id.pb_wall_details);

            String vpImgUrl = PCliq_Constant.arrayList.get(position).getImage();
            if (vpImgUrl != null) {
                vpImgUrl = vpImgUrl.replace(" ", "%20");
            }

            Glide.with(PCliq_PoseDetailsActivity.this)
                    .load(vpImgUrl)
                    .placeholder(R.drawable.pcliq_placeholder_pose)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(iv_wallpaper);

            container.addView(imageLayout, 0);

            return imageLayout;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void loadWallpaperDetails(int pos) {
        try {
            Call<PCliq_ItemPoseList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpaperDetails(PC_methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_DETAILS, 0, "", "", "", "", PCliq_Constant.arrayList.get(pos).getId(), "", "", "", "", "", PC_sharedPref.getUserId(), ""));
            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {
                    if (response.body() != null && response.body().getArrayListWallpaper() != null && response.body().getArrayListWallpaper().size() > 0) {
                        PCliq_Constant.arrayList.get(pos).setUserRating(response.body().getArrayListWallpaper().get(0).getUserRating());
                        PCliq_Constant.arrayList.get(pos).setTags(response.body().getArrayListWallpaper().get(0).getTags());
                        PC_dbHelper.updateTags(PCliq_Constant.arrayList.get(pos).getId(), PCliq_Constant.arrayList.get(pos).getTags());

                        PCliq_AdapterTags adapterTags;

                        RecyclerView rv_tags = findViewById(R.id.rv_tags);

                        rv_tags.setLayoutManager(new LinearLayoutManager(PCliq_PoseDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        rv_tags.setItemAnimator(new DefaultItemAnimator());

                        PC_arrayListTags = new ArrayList<>(Arrays.asList(PCliq_Constant.arrayList.get(position).getTags().split(",")));
                        adapterTags = new PCliq_AdapterTags(PC_arrayListTags);
                        rv_tags.setAdapter(adapterTags);

                        rv_tags.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(PCliq_PoseDetailsActivity.this, new PCliq_RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                PCliq_Constant.search_item = PC_arrayListTags.get(position);
                                Intent intent = new Intent(PCliq_PoseDetailsActivity.this, PCliq_SearchPoseActivity.class);
                                startActivity(intent);
                            }
                        }));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Throwable t) {
                    call.cancel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadViewed(int pos) {
        try {
            PC_dbHelper.addWallpaper(PCliq_Constant.arrayList.get(pos), "recent", "");

            Call<PCliq_ItemPoseList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpaperView(PC_methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_VIEW, 0, "", "", "", "", PCliq_Constant.arrayList.get(pos).getId(), "", "", "", "", "", PC_sharedPref.getUserId(), "Wallpaper"));
            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {
                    int tot = Integer.parseInt(PCliq_Constant.arrayList.get(pos).getTotalViews());
                    PCliq_Constant.arrayList.get(pos).setTotalViews("" + (tot + 1));
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Throwable t) {
                    call.cancel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDownloadCount(int pos) {
        try {
            Call<PCliq_ItemSuccessList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpaperDownloadCount(PC_methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_DOWNLOAD_COUNT, 0, "", "", "", "", PCliq_Constant.arrayList.get(pos).getId(), "", "", "", "", "", PC_sharedPref.getUserId(), "Wallpaper"));
            call.enqueue(new Callback<PCliq_ItemSuccessList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Response<PCliq_ItemSuccessList> response) {
                    if (response.body() != null && response.body().getArrayListSuccess() != null && response.body().getArrayListSuccess().size() > 0) {
                        PCliq_Constant.arrayList.get(pos).setTotalDownloads(response.body().getArrayListSuccess().get(0).getTotalDownloads());

                        PC_dbHelper.updateView(PCliq_Constant.arrayList.get(pos).getId(), PCliq_Constant.arrayList.get(pos).getTotalViews(), PCliq_Constant.arrayList.get(pos).getTotalDownloads());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Throwable t) {
                    call.cancel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openRateDialog() {
        View view = getLayoutInflater().inflate(R.layout.pcliq_layout_rating, null);

        PC_dialog_rate = new BottomSheetDialog(PCliq_PoseDetailsActivity.this);
        PC_dialog_rate.setContentView(view);
        PC_dialog_rate.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        PC_dialog_rate.show();

        final RatingBar ratingBar = PC_dialog_rate.findViewById(R.id.rb_add);
        ratingBar.setRating(1);
        final MaterialButton button_submit = PC_dialog_rate.findViewById(R.id.button_submit_rating);
        final MaterialButton button_later = PC_dialog_rate.findViewById(R.id.button_later_rating);
        final TextView textView = PC_dialog_rate.findViewById(R.id.tv_rate);

        if (PCliq_Constant.arrayList.get(position).getUserRating() == null || PCliq_Constant.arrayList.get(position).getUserRating().equals("0")) {
            textView.setText(getString(R.string.rate_this_wall));
        } else {
            textView.setText(getString(R.string.thanks_for_rating));
            ratingBar.setRating(Float.parseFloat(PCliq_Constant.arrayList.get(position).getUserRating()));
        }

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingBar.getRating() != 0) {
                    if (PC_methods.isNetworkAvailable()) {
                        loadRatingApi(String.valueOf(ratingBar.getRating()));
                    } else {
                        PC_methods.showSnackBar(PC_coordinatorLayout, getResources().getString(R.string.internet_not_connected));
                    }
                } else {
                    Toast.makeText(PCliq_PoseDetailsActivity.this, getString(R.string.enter_rating), Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PC_dialog_rate.dismiss();
            }
        });
    }

    private void loadRatingApi(final String rate) {
        final com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog progressDialog;
        progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(PCliq_PoseDetailsActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        Call<PCliq_ItemSuccessList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getDoRateWallpaper(PC_methods.getAPIRequest(PCliq_Constant.URL_RATE_WALLPAPER, 0, "", "", "", "", PCliq_Constant.arrayList.get(position).getId(), rate, "", "", "", "", PC_sharedPref.getUserId(), "Wallpaper"));
        call.enqueue(new Callback<PCliq_ItemSuccessList>() {
            @Override
            public void onResponse(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Response<PCliq_ItemSuccessList> response) {
                if (response.body() != null && response.body().getArrayListSuccess() != null) {
                    if (response.body().getArrayListSuccess().size() > 0) {
                        PC_methods.showSnackBar(PC_coordinatorLayout, response.body().getArrayListSuccess().get(0).getMessage());

                        PCliq_Constant.arrayList.get(position).setAverageRate(String.valueOf(response.body().getArrayListSuccess().get(0).getTotalRate()));
                        PCliq_Constant.arrayList.get(position).setUserRating(String.valueOf(rate));

                        PC_dialog_rate.dismiss();
                    }
                } else {
                    PC_methods.showSnackBar(PC_coordinatorLayout, getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Throwable t) {
                call.cancel();
                PC_methods.showSnackBar(PC_coordinatorLayout, getString(R.string.server_error));
            }
        });
    }

    private void showReportDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.pcliq_layout_report, null);

        PC_dialog_report = new BottomSheetDialog(PCliq_PoseDetailsActivity.this);
        PC_dialog_report.setContentView(view);
        PC_dialog_report.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        PC_dialog_report.show();

        final EditText editText_report;
        MaterialButton button_submit;

        button_submit = PC_dialog_report.findViewById(R.id.button_report_submit);
        editText_report = PC_dialog_report.findViewById(R.id.et_report);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_report.getText().toString().trim().isEmpty()) {
                    Toast.makeText(PCliq_PoseDetailsActivity.this, getString(R.string.enter_report), Toast.LENGTH_SHORT).show();
                } else {
                    if (PC_sharedPref.isLogged()) {
                        loadReportSubmit(editText_report.getText().toString());
//                        Toast.makeText(WallPaperDetailsActivity.this, "Report is not available in demo app", Toast.LENGTH_SHORT).show();
                    } else {
                        PC_methods.clickLogin();
                    }
                }
            }
        });
    }

    private void showDetailDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.pcliq_layout_pose_details, null);

        PC_dialog_report = new BottomSheetDialog(PCliq_PoseDetailsActivity.this);
        PC_dialog_report.setContentView(view);
        PC_dialog_report.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        PC_dialog_report.show();

        RecyclerView rv_tags = view.findViewById(R.id.rv_tags);

        PCliq_AdapterTags adapterTags;

        RatingBar ratingBar = view.findViewById(R.id.rating_wall_details);
        TextView tv_views = view.findViewById(R.id.tv_wall_details_views);
        TextView tv_downloads = view.findViewById(R.id.tv_wall_details_downloads);
        TextView tv_cat = view.findViewById(R.id.tv_details_cat);

        ratingBar.setRating(Float.parseFloat(PCliq_Constant.arrayList.get(position).getAverageRate()));

        tv_cat.setText(PCliq_Constant.arrayList.get(position).getTitle());
        tv_views.setText(PCliq_Constant.arrayList.get(position).getTotalViews());
        tv_downloads.setText(PCliq_Constant.arrayList.get(position).getTotalDownloads());

        rv_tags.setLayoutManager(new LinearLayoutManager(PCliq_PoseDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv_tags.setItemAnimator(new DefaultItemAnimator());

        ArrayList<String> arrayListTags = new ArrayList<>(Arrays.asList(PCliq_Constant.arrayList.get(position).getTags().split(",")));
        adapterTags = new PCliq_AdapterTags(arrayListTags);
        rv_tags.setAdapter(adapterTags);

        rv_tags.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(PCliq_PoseDetailsActivity.this, new PCliq_RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PCliq_Constant.search_item = arrayListTags.get(position);
                Intent intent = new Intent(PCliq_PoseDetailsActivity.this, PCliq_SearchPoseActivity.class);
                startActivity(intent);
            }
        }));

    }

    public void loadReportSubmit(String report) {
        if (PC_methods.isNetworkAvailable()) {
            PC_progressDialog.show();
            Call<PCliq_ItemSuccessList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getReport(PC_methods.getAPIRequest(PCliq_Constant.URL_REPORT, 0, "", "", "", report, PCliq_Constant.arrayList.get(position).getId(), "", "", "", "", "", PC_sharedPref.getUserId(), "wallpaper"));
            call.enqueue(new Callback<PCliq_ItemSuccessList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Response<PCliq_ItemSuccessList> response) {
                    if (response.body() != null && response.body().getArrayListSuccess() != null && response.body().getArrayListSuccess().size() > 0) {
                        try {
                            PC_dialog_report.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(PCliq_PoseDetailsActivity.this, response.body().getArrayListSuccess().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    PC_progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Throwable t) {
                    PC_progressDialog.dismiss();
                    call.cancel();
                    Toast.makeText(PCliq_PoseDetailsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean checkPer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(PCliq_PoseDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PC_methods.showInter(position, getString(R.string.download));
            } else {
                Toast.makeText(this, "Permission denied. Storage permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadFav(final int posi) {
        if (PC_sharedPref.isLogged()) {
            if (PC_methods.isNetworkAvailable()) {
                Call<PCliq_ItemSuccessList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getDoFavourite(PC_methods.getAPIRequest(PCliq_Constant.URL_DO_FAV, 0, PCliq_Constant.arrayList.get(posi).getId(), "", "", "", "", "", "", "", "", "", PC_sharedPref.getUserId(), "Wallpaper"));
                call.enqueue(new Callback<PCliq_ItemSuccessList>() {
                    @Override
                    public void onResponse(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Response<PCliq_ItemSuccessList> response) {
                        if (response.body() != null && response.body().getArrayListSuccess() != null) {
                            if (response.body().getArrayListSuccess().size() > 0) {
                                PCliq_Constant.arrayList.get(posi).setIsFav(response.body().getArrayListSuccess().get(0).getSuccess().equals("true"));
                                Toast.makeText(PCliq_PoseDetailsActivity.this, response.body().getArrayListSuccess().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Throwable t) {
                        call.cancel();
                    }
                });
            } else {
                Toast.makeText(PCliq_PoseDetailsActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            PC_methods.clickLogin();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDelete(EventAction eventAction) {
        try {
            loadDownloadCount(position);
            GlobalBus.getBus().removeStickyEvent(eventAction);
        } catch (Exception e) {
            GlobalBus.getBus().removeStickyEvent(eventAction);
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();
    }

    private String getSmartPoseDirection(String title, String tags, String serverTips) {
        if (serverTips != null && !serverTips.trim().isEmpty() && !serverTips.equalsIgnoreCase("null") && !serverTips.toLowerCase().contains("stand close, foreheads touching")) {
            return serverTips;
        }
        String combined = ((title != null ? title : "") + " " + (tags != null ? tags : "")).toLowerCase();
        if (combined.contains("sit") || combined.contains("sitting") || combined.contains("chair") || combined.contains("bench") || combined.contains("trunk")) {
            return "Sit naturally with an elongated spine. Rest one arm comfortably on the thigh or support, and create natural triangular space with knees and ankles for an effortless look.";
        } else if (combined.contains("stand") || combined.contains("standing") || combined.contains("walk") || combined.contains("street")) {
            return "Shift your weight to the back leg to create an organic silhouette. Keep hands casually engaged in a pocket, jacket collar, or waist, and look slightly off-camera.";
        } else if (combined.contains("couple") || combined.contains("love") || combined.contains("wedding") || combined.contains("bride") || combined.contains("groom")) {
            return "Maintain a close, natural connection. Gently hold hands or waist, tilt heads toward each other, and look just off-camera for candid warmth.";
        } else if (combined.contains("portrait") || combined.contains("close") || combined.contains("face") || combined.contains("eyes")) {
            return "Tilt your chin slightly down and 15° to the side to define the jawline. Relax facial muscles and let soft, natural light catch the eyes.";
        } else if (combined.contains("car") || combined.contains("bike") || combined.contains("urban") || combined.contains("attitude")) {
            return "Lean comfortably against the vehicle or wall. Keep a relaxed, confident gaze with shoulders dropped and fingers loose.";
        } else {
            return "Relax shoulders, create subtle angles with your elbows and knees, and avoid facing the camera straight on for the best depth.";
        }
    }

    private String getSmartDifficulty(int pos, String title, String tags) {
        String combined = ((title != null ? title : "") + " " + (tags != null ? tags : "")).toLowerCase();
        if (combined.contains("action") || combined.contains("jump") || combined.contains("dance") || combined.contains("creative")) {
            return "Creative";
        } else if (combined.contains("sit") || combined.contains("stand") || combined.contains("portrait") || combined.contains("easy")) {
            return "Easy";
        } else {
            return pos % 2 == 0 ? "Easy" : "Moderate";
        }
    }

    private String getSmartLighting(int pos, String title, String tags) {
        String combined = ((title != null ? title : "") + " " + (tags != null ? tags : "")).toLowerCase();
        if (combined.contains("sunset") || combined.contains("golden") || combined.contains("evening") || combined.contains("outdoor")) {
            return "Golden Hr";
        } else if (combined.contains("studio") || combined.contains("neon") || combined.contains("indoor") || combined.contains("flash")) {
            return "Studio Soft";
        } else {
            return pos % 3 == 0 ? "Golden Hr" : (pos % 3 == 1 ? "Natural" : "Soft Light");
        }
    }

    private String getSmartAngle(int pos, String title, String tags) {
        String combined = ((title != null ? title : "") + " " + (tags != null ? tags : "")).toLowerCase();
        if (combined.contains("sit") || combined.contains("sitting") || combined.contains("ground") || combined.contains("trunk")) {
            return "Low Angle";
        } else if (combined.contains("portrait") || combined.contains("close") || combined.contains("headshot")) {
            return "Eye Level";
        } else if (combined.contains("full") || combined.contains("fashion") || combined.contains("street")) {
            return "Waist Level";
        } else {
            return pos % 2 == 0 ? "Eye Level" : "Low Angle";
        }
    }
}