package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.eventbus.EventAction;
import com.eventbus.GlobalBus;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterTags;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemSuccessList;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemPoseList;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_RecyclerItemClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.like.LikeButton;
import com.like.OnLikeListener;
//import com.ortiz.touchview.TouchImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_LivePoseDetailsActivity extends AppCompatActivity {

    private Toolbar PC_toolbar;
    private PCliq_Methods PC_methods;
    private PCliq_DBHelper PC_dbHelper;
    private PCliq_SharedPref PC_sharedPref;
    private ViewPager PC_viewpager;
    LikeButton PC_likeButton;
    FloatingActionButton PC_btn_download;
    MaterialButton PC_btn_setas;

    private int PC_position;
    private Dialog PC_dialog_rate;
    private ConstraintLayout PC_coordinatorLayout;
    private com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog PC_progressDialog;
    private BottomSheetDialog PC_dialog_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_live_pose_details);

        PC_progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(PCliq_LivePoseDetailsActivity.this);
        PC_progressDialog.setMessage(getString(R.string.loading));

        PC_sharedPref = new PCliq_SharedPref(this);
        PC_dbHelper = new PCliq_DBHelper(this);
        PC_methods = new PCliq_Methods(this, new PCliq_InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                PC_methods.saveImage(PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).getImage(), type, PC_coordinatorLayout, "live_wall");
            }
        });
        PC_methods.forceRTLIfSupported(getWindow());

        PC_toolbar = this.findViewById(R.id.toolbar_wall_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(PCliq_LivePoseDetailsActivity.this, R.color.bg));
            if (!PC_methods.isDarkMode()) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = 55;
            PC_toolbar.setLayoutParams(params);
        }
        PC_toolbar.setTitle("");
        this.setSupportActionBar(PC_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PC_position = getIntent().getIntExtra("pos", 0);

        LinearLayout ll_adView = findViewById(R.id.ll_adView);
        PC_coordinatorLayout = findViewById(R.id.rl);
        PC_btn_download = findViewById(R.id.btn_details_download);
        PC_likeButton = findViewById(R.id.btn_details_fav);
        PC_btn_setas = findViewById(R.id.btn_details_setas);

        loadViewed(PC_position);

        PC_methods.showBannerAd(ll_adView);

        ImagePagerAdapter adapter = new ImagePagerAdapter();
        PC_viewpager = findViewById(R.id.vp_wall_details);
        PC_viewpager.setAdapter(adapter);
        PC_viewpager.setCurrentItem(PC_position);

        PC_btn_download.setOnClickListener(v -> {
            PC_methods.showInter(0, getString(R.string.download));
        });

        PC_btn_setas.setOnClickListener(v -> {
            PC_methods.showInter(0, getString(R.string.set_wallpaper));
        });

        PC_likeButton.setLiked(PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).getIsFav());
        if (PC_sharedPref.isLogged()) {
            PC_likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    try {
                        loadFav(PC_viewpager.getCurrentItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    try {
                        loadFav(PC_viewpager.getCurrentItem());
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

        PC_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                position = PC_viewpager.getCurrentItem();
                PC_likeButton.setLiked(PCliq_Constant.arrayListLiveWallpapers.get(position).getIsFav());
                loadViewed(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int position) {
            }

            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });

        loadWallpaperDetails(PC_position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pcliq_menu_pose_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
       /*     onBackPressed();
        } else if (itemId == R.id.menu_setwall) {
            methods.showInter(0, getString(R.string.set_wallpaper));
        */} else if (itemId == R.id.menu_rate) {
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
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return PCliq_Constant.arrayListLiveWallpapers.size();

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            View imageLayout = inflater.inflate(R.layout.pcliq_layout_vp_live_pose, container, false);
            assert imageLayout != null;

            final ImageView iv_wallpaper = imageLayout.findViewById(R.id.iv_wallpaper);
            final CircularProgressBar progressBar = imageLayout.findViewById(R.id.pb_wall_details);

            Glide.with(PCliq_LivePoseDetailsActivity.this)
                    .asGif()
                    .load(PCliq_Constant.arrayListLiveWallpapers.get(position).getImage())
                    .placeholder(R.drawable.pcliq_placeholder_pose)
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
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
            Call<PCliq_ItemPoseList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getLiveWallDetails(PC_methods.getAPIRequest(PCliq_Constant.URL_LIVE_WALL_DETAILS, 0, "", "", "", "", PCliq_Constant.arrayListLiveWallpapers.get(pos).getId(), "", "", "", "", "", PC_sharedPref.getUserId(), ""));
            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {
                    if (response.body() != null && response.body().getArrayListWallpaper() != null && response.body().getArrayListWallpaper().size() > 0) {
                        PCliq_Constant.arrayListLiveWallpapers.get(pos).setUserRating(response.body().getArrayListWallpaper().get(0).getUserRating());
                        PCliq_Constant.arrayListLiveWallpapers.get(pos).setTags(response.body().getArrayListWallpaper().get(0).getTags());
                        PC_dbHelper.updateTags(PCliq_Constant.arrayListLiveWallpapers.get(pos).getId(), PCliq_Constant.arrayListLiveWallpapers.get(pos).getTags());
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
            Call<PCliq_ItemPoseList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpaperView(PC_methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_VIEW, 0, "", "", "", "", PCliq_Constant.arrayListLiveWallpapers.get(pos).getId(), "", "", "", "", "", PC_sharedPref.getUserId(), "LiveWallpaper"));
            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {
                    int tot = Integer.parseInt(PCliq_Constant.arrayListLiveWallpapers.get(pos).getTotalViews());
                    PCliq_Constant.arrayListLiveWallpapers.get(pos).setTotalViews("" + (tot + 1));
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
            Call<PCliq_ItemSuccessList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpaperDownloadCount(PC_methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_DOWNLOAD_COUNT, 0, "", "", "", "", PCliq_Constant.arrayListLiveWallpapers.get(pos).getId(), "", "", "", "", "", PC_sharedPref.getUserId(), "LiveWallpaper"));
            call.enqueue(new Callback<PCliq_ItemSuccessList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Response<PCliq_ItemSuccessList> response) {
                    if (response.body() != null && response.body().getArrayListSuccess() != null && response.body().getArrayListSuccess().size() > 0) {
                        PCliq_Constant.arrayListLiveWallpapers.get(pos).setTotalDownloads(response.body().getArrayListSuccess().get(0).getTotalDownloads());
                        PC_dbHelper.updateView(PCliq_Constant.arrayListLiveWallpapers.get(pos).getId(), PCliq_Constant.arrayListLiveWallpapers.get(pos).getTotalViews(), PCliq_Constant.arrayListLiveWallpapers.get(pos).getTotalDownloads());
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

        PC_dialog_rate = new BottomSheetDialog(PCliq_LivePoseDetailsActivity.this);
        PC_dialog_rate.setContentView(view);
        PC_dialog_rate.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        PC_dialog_rate.show();

        final RatingBar ratingBar = PC_dialog_rate.findViewById(R.id.rb_add);
        ratingBar.setRating(1);
        final MaterialButton button_submit = PC_dialog_rate.findViewById(R.id.button_submit_rating);
        final MaterialButton button_later = PC_dialog_rate.findViewById(R.id.button_later_rating);
        final TextView textView = PC_dialog_rate.findViewById(R.id.tv_rate);

        if (PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).getUserRating()==null || PCliq_Constant.arrayList.get(PC_viewpager.getCurrentItem()).getUserRating().equals("0")) {
            textView.setText(getString(R.string.rate_this_wall));
        } else {
            textView.setText(getString(R.string.thanks_for_rating));
            ratingBar.setRating(Float.parseFloat(PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).getUserRating()));
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
                    Toast.makeText(PCliq_LivePoseDetailsActivity.this, getString(R.string.enter_rating), Toast.LENGTH_SHORT).show();
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
        progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(PCliq_LivePoseDetailsActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        Call<PCliq_ItemSuccessList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getDoRateWallpaper(PC_methods.getAPIRequest(PCliq_Constant.URL_RATE_WALLPAPER, 0, "", "", "", "", PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).getId(), rate, "", "", "", "", PC_sharedPref.getUserId(), "LiveWallpaper"));
        call.enqueue(new Callback<PCliq_ItemSuccessList>() {
            @Override
            public void onResponse(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Response<PCliq_ItemSuccessList> response) {
                if (response.body() != null && response.body().getArrayListSuccess() != null) {
                    if (response.body().getArrayListSuccess().size() > 0) {
                        PC_methods.showSnackBar(PC_coordinatorLayout, response.body().getArrayListSuccess().get(0).getMessage());

                        PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).setAverageRate(String.valueOf(response.body().getArrayListSuccess().get(0).getTotalRate()));
                        PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).setUserRating(String.valueOf(rate));

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

        PC_dialog_report = new BottomSheetDialog(PCliq_LivePoseDetailsActivity.this);
        PC_dialog_report.setContentView(view);
        PC_dialog_report.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        PC_dialog_report.show();

        final EditText editText_report;
        MaterialButton button_submit;

        button_submit = PC_dialog_report.findViewById(R.id.button_report_submit);
        editText_report = PC_dialog_report.findViewById(R.id.et_report);

        button_submit.setBackground(PC_methods.getRoundDrawable(getResources().getColor(R.color.colorPrimary)));

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_report.getText().toString().trim().isEmpty()) {
                    Toast.makeText(PCliq_LivePoseDetailsActivity.this, getString(R.string.enter_report), Toast.LENGTH_SHORT).show();
                } else {
                    if (PC_sharedPref.isLogged()) {
                        loadReportSubmit(editText_report.getText().toString());
//                        Toast.makeText(LiveWallpapersDetailsActivity.this, "Report is not available in demo app", Toast.LENGTH_SHORT).show();
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

        PC_dialog_report = new BottomSheetDialog(PCliq_LivePoseDetailsActivity.this);
        PC_dialog_report.setContentView(view);
        PC_dialog_report.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        PC_dialog_report.show();

        RecyclerView rv_tags = view.findViewById(R.id.rv_tags);
        ;
        PCliq_AdapterTags adapterTags;

        RatingBar ratingBar = view.findViewById(R.id.rating_wall_details);
        TextView tv_views = view.findViewById(R.id.tv_wall_details_views);
        TextView tv_downloads = view.findViewById(R.id.tv_wall_details_downloads);
        TextView tv_cat = view.findViewById(R.id.tv_details_cat);

        ratingBar.setRating(Float.parseFloat(PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).getAverageRate()));

        tv_cat.setText(PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).getTitle());
        tv_views.setText(PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).getTotalViews());
        tv_downloads.setText(PCliq_Constant.arrayListLiveWallpapers.get(PC_viewpager.getCurrentItem()).getTotalDownloads());

        rv_tags.setLayoutManager(new LinearLayoutManager(PCliq_LivePoseDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv_tags.setItemAnimator(new DefaultItemAnimator());

        ArrayList<String> arrayListTags = new ArrayList<>(Arrays.asList(PCliq_Constant.arrayListLiveWallpapers.get(PC_position).getTags().split(",")));
        adapterTags = new PCliq_AdapterTags(arrayListTags);
        rv_tags.setAdapter(adapterTags);

        rv_tags.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(PCliq_LivePoseDetailsActivity.this, new PCliq_RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PCliq_Constant.search_item = arrayListTags.get(position);
                Intent intent = new Intent(PCliq_LivePoseDetailsActivity.this, PCliq_SearchPoseActivity.class);
                startActivity(intent);
            }
        }));

    }

    public void loadReportSubmit(String report) {
        if (PC_methods.isNetworkAvailable()) {
            PC_progressDialog.show();
            Call<PCliq_ItemSuccessList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getReport(PC_methods.getAPIRequest(PCliq_Constant.URL_REPORT, 0, "", "", "", report, PCliq_Constant.arrayListLiveWallpapers.get(PC_position).getId(), "", "", "", "", "", PC_sharedPref.getUserId(), "LiveWallpaper"));
            call.enqueue(new Callback<PCliq_ItemSuccessList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Response<PCliq_ItemSuccessList> response) {
                    if (response.body() != null && response.body().getArrayListSuccess() != null && response.body().getArrayListSuccess().size() > 0) {
                        try {
                            PC_dialog_report.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(PCliq_LivePoseDetailsActivity.this, response.body().getArrayListSuccess().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    PC_progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Throwable t) {
                    PC_progressDialog.dismiss();
                    call.cancel();
                    Toast.makeText(PCliq_LivePoseDetailsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFav(final int posi) {
        if (PC_sharedPref.isLogged()) {
            if (PC_methods.isNetworkAvailable()) {
                Call<PCliq_ItemSuccessList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getDoFavourite(PC_methods.getAPIRequest(PCliq_Constant.URL_DO_FAV, 0, PCliq_Constant.arrayListLiveWallpapers.get(posi).getId(), "", "", "", "", "", "", "", "", "", PC_sharedPref.getUserId(), "LiveWallpaper"));
                call.enqueue(new Callback<PCliq_ItemSuccessList>() {
                    @Override
                    public void onResponse(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Response<PCliq_ItemSuccessList> response) {
                        if (response.body() != null && response.body().getArrayListSuccess() != null) {
                            if (response.body().getArrayListSuccess().size() > 0) {
                                PCliq_Constant.arrayListLiveWallpapers.get(posi).setIsFav(response.body().getArrayListSuccess().get(0).getSuccess().equals("true"));
                                Toast.makeText(PCliq_LivePoseDetailsActivity.this, response.body().getArrayListSuccess().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PCliq_ItemSuccessList> call, @NonNull Throwable t) {
                        call.cancel();
                    }
                });
            } else {
                Toast.makeText(PCliq_LivePoseDetailsActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            PC_methods.clickLogin();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDelete(EventAction eventAction) {
        try {
            loadDownloadCount(PC_viewpager.getCurrentItem());
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
}