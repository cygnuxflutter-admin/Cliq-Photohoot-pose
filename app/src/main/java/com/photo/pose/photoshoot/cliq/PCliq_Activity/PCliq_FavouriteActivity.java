package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterPose;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemFavList;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_RecyclerViewClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.photo.pose.photoshoot.cliq.R;

import java.util.ArrayList;
import java.util.Collections;

import android.widget.ProgressBar;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_FavouriteActivity extends AppCompatActivity {

    PCliq_Methods methods;
    PCliq_SharedPref sharedPref;
    RecyclerView recyclerView;
    PCliq_AdapterPose adapter;
    ArrayList<PCliq_ItemPose> arrayList;
    ProgressBar progressBar;
    TextView tv_empty;
    StaggeredGridLayoutManager grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_favourite);

        sharedPref = new PCliq_SharedPref(this);
        
        PCliq_InterAdListener interAdListener = new PCliq_InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Intent intent;
                PCliq_Constant.arrayList.clear();
                PCliq_Constant.arrayList.addAll(arrayList);
                PCliq_Constant.arrayList.removeAll(Collections.singleton(null));

                int real_pos = PCliq_Constant.arrayList.indexOf(arrayList.get(position));

                intent = new Intent(PCliq_FavouriteActivity.this, PCliq_PoseDetailsActivity.class);
                intent.putExtra("pos", real_pos);
                intent.putExtra("list_type", getString(R.string.favourite));
                intent.putExtra("page", 1);
                intent.putExtra("wallType", "");
                intent.putExtra("color_ids", "");
                startActivity(intent);
            }
        };

        methods = new PCliq_Methods(this, interAdListener);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        arrayList = new ArrayList<>();

        TextView tv_heading = findViewById(R.id.tv_cat_heading);
        tv_heading.setText("Favourite Poses");
        TextView tv_subtitle = findViewById(R.id.tv_cat_subtitle);
        tv_subtitle.setText("Your Saved Poses");

        FrameLayout iv_back = findViewById(R.id.iv_cat_back);
        iv_back.setOnClickListener(v -> onBackPressed());

        FrameLayout iv_search = findViewById(R.id.iv_cat_search);
        iv_search.setVisibility(View.GONE);

        RecyclerView rv_sub_cat = findViewById(R.id.rv_sub_cat);
        rv_sub_cat.setVisibility(View.GONE);

        FrameLayout fab_camera = findViewById(R.id.fab_cat_camera);
        fab_camera.setVisibility(View.GONE);

        progressBar = findViewById(R.id.pb_cat);
        tv_empty = findViewById(R.id.tv_empty_cat);
        recyclerView = findViewById(R.id.rv_cat);

        grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        grid.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(grid);
        recyclerView.setHasFixedSize(true);

        android.widget.RelativeLayout rl_ad = findViewById(R.id.rl_ad);
        if (com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_NetworkUtils.isNetworkAvailable(this)) {
            if (new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass(this).getInt("BannerAdStatus") == 1) {
                rl_ad.setVisibility(View.VISIBLE);
                com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_LoadAds.loadAdmobBannerAd(this, rl_ad);
            } else {
                rl_ad.setVisibility(View.GONE);
            }
        }

        getWallpaperData();
    }

    private void getWallpaperData() {
        if (methods.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            Call<PCliq_ItemFavList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersByFav(methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_FAV, 0, "", "", "", "", "", "", "", "", "", "", sharedPref.getUserId(), "Wallpaper"));

            call.enqueue(new Callback<PCliq_ItemFavList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemFavList> call, @NonNull Response<PCliq_ItemFavList> response) {
                    if (response.body() != null && response.body().getItemFavPost() != null) {
                        if (response.body().getItemFavPost().getArrayListWallpapers().size() == 0) {
                            setEmptyList();
                        } else {
                            for (int i = 0; i < response.body().getItemFavPost().getArrayListWallpapers().size(); i++) {
                                arrayList.add(response.body().getItemFavPost().getArrayListWallpapers().get(i));

                                int abc = arrayList.lastIndexOf(null);
                                if (((arrayList.size() - (abc + 1)) % new PCliq_PreferenceClass(PCliq_FavouriteActivity.this).getInt("rv_count", 4) == 0)) {
                                    arrayList.add(null);
                                }
                            }
                            setAdapter();
                        }
                    } else {
                        setEmptyList();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemFavList> call, @NonNull Throwable t) {
                    call.cancel();
                    setEmptyList();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            setAdapter();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setAdapter() {
        adapter = new PCliq_AdapterPose(this, arrayList, new PCliq_RecyclerViewClickListener() {
            @Override
            public void onClick(int position) {
                methods.showInter(position, getString(R.string.wallpapers));
            }
        });
        AnimationAdapter adapterAnim = new AlphaInAnimationAdapter(adapter);
        adapterAnim.setFirstOnly(true);
        adapterAnim.setDuration(500);
        adapterAnim.setInterpolator(new OvershootInterpolator(.9f));
        recyclerView.setAdapter(adapterAnim);
        setEmptyList();
    }

    private void setEmptyList() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() == 0) {
            tv_empty.setText(getString(R.string.no_data_found));
            tv_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}

