package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterLivePose;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemPoseList;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_RecyclerViewClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_EndlessRecyclerViewScrollListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_SearchLivePoseActivity extends AppCompatActivity {

    PCliq_DBHelper dbHelper;
    Toolbar toolbar;
    RecyclerView rv_wallpapers;
    PCliq_AdapterLivePose adapter;
    ArrayList<PCliq_ItemPose> arrayList;
    ProgressBar progressBar;
    PCliq_Methods methods;
    PCliq_InterAdListener interAdListener;
    Boolean isOver = false, isScroll = false, isLoading = false;
    TextView textView_empty;
    int page = 1, totalRecord = 0;
    StaggeredGridLayoutManager grid;
    FloatingActionButton fab;
    PCliq_SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_search_pose);

        sharedPref = new PCliq_SharedPref(this);

        grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        interAdListener = new PCliq_InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                PCliq_Constant.arrayListLiveWallpapers.clear();
                PCliq_Constant.arrayListLiveWallpapers.addAll(arrayList);
                PCliq_Constant.arrayListLiveWallpapers.removeAll(Collections.singleton(null));

                int real_pos = PCliq_Constant.arrayListLiveWallpapers.indexOf(arrayList.get(position));

                Intent intent = new Intent(PCliq_SearchLivePoseActivity.this, PCliq_LivePoseDetailsActivity.class);
                intent.putExtra("pos", real_pos);
                startActivity(intent);
            }
        };

        dbHelper = new PCliq_DBHelper(this);
        methods = new PCliq_Methods(this, interAdListener);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        LinearLayout ll_ad = findViewById(R.id.ll_ad_search);
        dbHelper.getAbout();
        methods.showBannerAd(ll_ad);

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

        arrayList = new ArrayList<>();

        progressBar = findViewById(R.id.pb_wallcat);
        textView_empty = findViewById(R.id.tv_empty_wallcat);

        fab = findViewById(R.id.fab);
        rv_wallpapers = findViewById(R.id.rv_wall_by_cat);
        rv_wallpapers.setHasFixedSize(true);
        rv_wallpapers.setLayoutManager(grid);

        rv_wallpapers.addOnScrollListener(new PCliq_EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver && !isLoading) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            getWallpapers();
                        }
                    }, 0);
                }
            }
        });

        rv_wallpapers.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                int firstVisibleItem = grid.findFirstVisibleItemPosition();
//
//                if (firstVisibleItem > 6) {
//                    fab.show();
//                } else {
//                    fab.hide();
//                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_wallpapers.smoothScrollToPosition(0);
            }
        });

        getWallpapers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pcliq_menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        menu.findItem(R.id.menu_filter).setVisible(false);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
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
            page=1;
            isOver = false;
            PCliq_Constant.search_item = s;
            getWallpapers();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private void getWallpapers() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (methods.isNetworkAvailable()) {
            isLoading = true;
            progressBar.setVisibility(View.VISIBLE);
            Call<PCliq_ItemPoseList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getLiveWallBySearch(methods.getAPIRequest(PCliq_Constant.URL_LIVE_WALL_SEARCH, 0, "", "", "", PCliq_Constant.search_item, "", "", "", "", "", "", new PCliq_SharedPref(PCliq_SearchLivePoseActivity.this).getUserId(), ""), String.valueOf(page));
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
                                dbHelper.addWallpaper(response.body().getArrayListWallpaper().get(i), "search", "");

                                arrayList.add(response.body().getArrayListWallpaper().get(i));
                                int abc = arrayList.lastIndexOf(null);
                                if (((arrayList.size() - (abc + 1)) % new PCliq_PreferenceClass(PCliq_SearchLivePoseActivity.this).getInt("rv_count", 4) == 0) ) {
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
            isLoading = false;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setAdapter() {
        if (!isScroll) {
            adapter = new PCliq_AdapterLivePose(PCliq_SearchLivePoseActivity.this, arrayList, new PCliq_RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    methods.showInter(position, "");
                }
            });
            AnimationAdapter adapterAnim = new AlphaInAnimationAdapter(adapter);
            adapterAnim.setFirstOnly(true);
            adapterAnim.setDuration(500);
            adapterAnim.setInterpolator(new OvershootInterpolator(.5f));
            rv_wallpapers.setAdapter(adapterAnim);
            setEmpty();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void setEmpty() {
        progressBar.setVisibility(View.INVISIBLE);
        if (arrayList.size() == 0) {
            textView_empty.setVisibility(View.VISIBLE);
            rv_wallpapers.setVisibility(View.GONE);
        } else {
            rv_wallpapers.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
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