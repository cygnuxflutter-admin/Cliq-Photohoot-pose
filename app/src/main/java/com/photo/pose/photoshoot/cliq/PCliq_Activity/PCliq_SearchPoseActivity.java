package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterColors;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterPose;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemPoseList;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_RecyclerViewClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_EndlessRecyclerViewScrollListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_RecyclerItemClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.photo.pose.photoshoot.cliq.R;

import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_SearchPoseActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    PCliq_AdapterPose adapter;
    ArrayList<PCliq_ItemPose> arrayList;
    ProgressBar progressBar;
    PCliq_Methods methods;
    PCliq_InterAdListener interAdListener;
    TextView textView_empty;
    StaggeredGridLayoutManager grid;
    int page = 1, totalRecord = 0;
    private Boolean isOver = false, isScroll = false, isLoading = false;
    String wallType = "", wallTempType = "", color_ids = "";
    PCliq_AdapterColors adapterColors;
    PCliq_SharedPref sharedPref;
//    RecyclerView rv_category_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_search_pose);

        sharedPref = new PCliq_SharedPref(this);

        grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        interAdListener = new PCliq_InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                PCliq_Constant.arrayList.clear();
                PCliq_Constant.arrayList.addAll(arrayList);
                PCliq_Constant.arrayList.removeAll(Collections.singleton(null));

                int real_pos = PCliq_Constant.arrayList.indexOf(arrayList.get(position));

                Intent intent = new Intent(PCliq_SearchPoseActivity.this, PCliq_PoseDetailsActivity.class);
                intent.putExtra("pos", real_pos);
                intent.putExtra("list_type", getString(R.string.featured));
                intent.putExtra("page", 1);
                intent.putExtra("wallType", wallType);
                intent.putExtra("color_ids", color_ids);
                startActivity(intent);
            }
        };

        methods = new PCliq_Methods(this, interAdListener);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        toolbar = this.findViewById(R.id.toolbar_wall_by_cat);
        toolbar.setTitle(getString(R.string.search));
        toolbar.setTitleTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_espresso));
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (toolbar.getNavigationIcon() != null) {
                toolbar.getNavigationIcon().setTint(androidx.core.content.ContextCompat.getColor(this, R.color.text_espresso));
            }
        }

        LinearLayout ll_ad = findViewById(R.id.ll_ad_search);
        methods.showBannerAd(ll_ad);

        arrayList = new ArrayList<>();

        progressBar = findViewById(R.id.pb_wallcat);
        textView_empty = findViewById(R.id.tv_empty_wallcat);

//        rv_category_activity = findViewById(R.id.rv_category_activity);
        recyclerView = findViewById(R.id.rv_wall_by_cat);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(grid);

        recyclerView.addOnScrollListener(new PCliq_EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver && !isLoading) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            getWallpaperData();
                        }
                    }, 0);
                }
            }
        });

        getWallpaperData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.pcliq_menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_ALWAYS);
        SearchView searchView = (SearchView) item.getActionView();
        item.expandActionView();
        searchView.setQuery(PCliq_Constant.search_item, false);
        methods.styleSearchView(searchView);
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(androidx.core.content.ContextCompat.getColor(this, R.color.text_espresso));
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            PCliq_Constant.search_item = s;
            page = 1;
            arrayList.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            isOver = false;
            getWallpaperData();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (menuItem.getItemId() == R.id.menu_filter) {
            openFilterDialog();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void getWallpaperData() {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (methods.isNetworkAvailable()) {
            isLoading = true;
            progressBar.setVisibility(View.VISIBLE);
            Log.e(TAG, "getWallpaperData: search" + PCliq_Constant.search_item);
            Call<PCliq_ItemPoseList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersBySearch(methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_SEARCH, 0, color_ids, wallType, "", PCliq_Constant.search_item, "", "", "", "", "", "", new PCliq_SharedPref(PCliq_SearchPoseActivity.this).getUserId(), ""), String.valueOf(page));
            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {
                    if (response.body() != null && response.body().getArrayListWallpaper() != null) {
                        if (response.body().getArrayListWallpaper().size() == 0) {
                            isOver = true;
                            setEmpty();
                        } else {
                            totalRecord = totalRecord + response.body().getArrayListWallpaper().size();
                            for (int i = 0; i < response.body().getArrayListWallpaper().size(); i++) {
                                com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose item = response.body().getArrayListWallpaper().get(i);
                                String img = item.getImage();
                                if (img == null || img.trim().isEmpty() || img.endsWith("/")) continue;
                                arrayList.add(item);

                                int abc = arrayList.lastIndexOf(null);
                                if (((arrayList.size() - (abc + 1)) % new PCliq_PreferenceClass(PCliq_SearchPoseActivity.this).getInt("rv_count", 4) == 0)) {
                                    arrayList.add(null);
                                }

//                                if (PCliq_Constant.isNativeAd) {
//                                    int abc = arrayList.lastIndexOf(null);
//                                    if (((arrayList.size() - (abc + 1)) % PCliq_Constant.nativeAdShow == 0) && (response.body().getArrayListWallpaper().size() - 1 != i || totalRecord != response.body().getTotalRecords())) {
//                                        arrayList.add(null);
//                                    }
//                                }
                            }
                            page = page + 1;
                            setAdapter();
                        }
                    } else {
                        isOver = true;
                        setEmpty();
                    }
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Throwable t) {
                    call.cancel();
                    setEmpty();
                    isOver = true;
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                }
            });
        } else {
            setAdapter();
            isOver = true;
            isLoading = false;
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setAdapter() {
        if (!isScroll) {
            adapter = new PCliq_AdapterPose(PCliq_SearchPoseActivity.this, arrayList, new PCliq_RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    methods.showInter(position, "");
                }
            });
            AnimationAdapter adapterAnim = new AlphaInAnimationAdapter(adapter);
            adapterAnim.setFirstOnly(true);
            adapterAnim.setDuration(500);
            adapterAnim.setInterpolator(new OvershootInterpolator(.9f));
            recyclerView.setAdapter(adapterAnim);
        } else {
            adapter.notifyDataSetChanged();
        }
        setEmpty();
    }

    private void setEmpty() {
        if (arrayList.size() == 0) {
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }

    private void openFilterDialog() {
        View view = getLayoutInflater().inflate(R.layout.pcliq_layout_filter_pose, null);

        BottomSheetDialog dialog_filter = new BottomSheetDialog(PCliq_SearchPoseActivity.this);
        dialog_filter.setContentView(view);
        dialog_filter.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_filter.show();

        final RecyclerView rv_colors = dialog_filter.findViewById(R.id.rv_filter_colors);
        final TextView button_portrait = dialog_filter.findViewById(R.id.tv_filter_portrait);
        final TextView button_landscape = dialog_filter.findViewById(R.id.tv_filter_landscape);
        final TextView button_square = dialog_filter.findViewById(R.id.tv_filter_square);
        final MaterialButton button_filter = dialog_filter.findViewById(R.id.button_filter);
        final MaterialButton button_clear = dialog_filter.findViewById(R.id.button_filter_clear);


        if (PCliq_Constant.isColorOn && PCliq_Constant.arrayListColors.size() > 0) {
            LinearLayoutManager llm = new LinearLayoutManager(PCliq_SearchPoseActivity.this, LinearLayoutManager.HORIZONTAL, false);
            rv_colors.setLayoutManager(llm);

            adapterColors = new PCliq_AdapterColors(PCliq_SearchPoseActivity.this, PCliq_Constant.arrayListColors);
            adapterColors.setMultipleSelected(color_ids);
            rv_colors.setAdapter(adapterColors);

            rv_colors.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(PCliq_SearchPoseActivity.this, new PCliq_RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    adapterColors.setSelected(position);
                }
            }));

        } else {
            dialog_filter.findViewById(R.id.tv3).setVisibility(View.GONE);
        }
        wallTempType = wallType;

        if (wallTempType.equals(getString(R.string.portrait))) {
            button_portrait.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        } else if (wallTempType.equals(getString(R.string.landscape))) {
            button_landscape.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        } else if (wallTempType.equals(getString(R.string.square))) {
            button_square.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        }

        button_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wallTempType.equals(getString(R.string.portrait))) {
                    wallTempType = "";
                    button_portrait.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    wallTempType = getString(R.string.portrait);
                    button_portrait.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
                    button_landscape.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                    button_square.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                }
            }
        });

        button_landscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wallTempType.equals(getString(R.string.landscape))) {
                    wallTempType = "";
                    button_landscape.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    wallTempType = getString(R.string.landscape);
                    button_landscape.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
                    button_portrait.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                    button_square.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                }
            }
        });

        button_square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wallTempType.equals(getString(R.string.square))) {
                    wallTempType = "";
                    button_square.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    wallTempType = getString(R.string.square);
                    button_square.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
                    button_portrait.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                    button_landscape.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                }
            }
        });

        button_clear.setOnClickListener(v -> {
            button_portrait.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
            button_landscape.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
            button_square.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
            wallTempType = "";
            wallType = "";
            color_ids = "";
            if (adapterColors != null) {
                adapterColors.clearSelected();
            }

            page = 1;
            totalRecord = 0;
            isOver = false;
            arrayList.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            getWallpaperData();
            dialog_filter.dismiss();
        });

        button_filter.setOnClickListener(v -> {
            if (adapterColors != null) {
                color_ids = adapterColors.getSelected();
            }
            wallType = wallTempType;

            page = 1;
            totalRecord = 0;
            isOver = false;
            arrayList.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            getWallpaperData();
            dialog_filter.dismiss();
        });
    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}

