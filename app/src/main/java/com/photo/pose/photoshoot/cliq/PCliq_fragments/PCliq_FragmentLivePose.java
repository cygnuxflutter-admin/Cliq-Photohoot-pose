package com.photo.pose.photoshoot.cliq.PCliq_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterLivePose;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemPoseList;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_LivePoseDetailsActivity;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_SearchLivePoseActivity;
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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_FragmentLivePose extends Fragment {

    private PCliq_DBHelper dbHelper;
    private RecyclerView recyclerView;
    private PCliq_AdapterLivePose adapter;
    private ArrayList<PCliq_ItemPose> arrayList;
    private CircularProgressBar progressBar;
    private PCliq_Methods methods;
    private Boolean isOver = false, isScroll = false, isLoading = false;
    private TextView textView_empty;
    private int page = 1, totalRecord = 0;
    private StaggeredGridLayoutManager grid;
    private String wallType = "", wallTempType = "";
    private FloatingActionButton fab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pcliq_fragment_pose_by_cat, container, false);

        grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        grid.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        PCliq_InterAdListener interAdListener = new PCliq_InterAdListener() {
            @Override
            public void onClick(int position, String type) {

                PCliq_Constant.arrayListLiveWallpapers.clear();
                PCliq_Constant.arrayListLiveWallpapers.addAll(arrayList);
                PCliq_Constant.arrayListLiveWallpapers.removeAll(Collections.singleton(null));

                int real_pos = PCliq_Constant.arrayListLiveWallpapers.indexOf(arrayList.get(position));

                Intent intent = new Intent(getActivity(), PCliq_LivePoseDetailsActivity.class);
                intent.putExtra("pos", real_pos);
                startActivity(intent);
            }
        };

        dbHelper = new PCliq_DBHelper(getActivity());
        methods = new PCliq_Methods(getActivity(), interAdListener);

        dbHelper.getAbout();

        arrayList = new ArrayList<>();

        progressBar = rootView.findViewById(R.id.pb_wallcat);
        textView_empty = rootView.findViewById(R.id.tv_empty_wallcat);

        fab = rootView.findViewById(R.id.fab);
        recyclerView = rootView.findViewById(R.id.rv_wall_by_cat);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(grid);

        recyclerView.addOnScrollListener(new PCliq_EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver && !isLoading && wallType.equals("")) {
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] firstVisibleItem = grid.findFirstVisibleItemPositions(null);

                if (firstVisibleItem[0] > 6) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        getWallpaperData();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.pcliq_menu_search, menu);
                MenuItem item = menu.findItem(R.id.menu_search);
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
                SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
                methods.styleSearchView(searchView);
                searchView.setOnQueryTextListener(queryTextListener);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_filter) {
                    openFilterDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        super.onViewCreated(view, savedInstanceState);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            PCliq_Constant.search_item = s;
            Intent intent = new Intent(getActivity(), PCliq_SearchLivePoseActivity.class);
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private void getWallpaperData() {
        if (methods.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);

            Call<PCliq_ItemPoseList> call;
            if(wallType.equals(getString(R.string.popular))) {
                call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getLiveWallByPopular(methods.getAPIRequest(PCliq_Constant.URL_LIVE_WALL_POPULAR, 0, "", "", "", "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""), String.valueOf(page));
            } else if(wallType.equals(getString(R.string.download))) {
                call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getLiveWallByDownloads(methods.getAPIRequest(PCliq_Constant.URL_LIVE_WALL_DOWNLOADS, 0, "", "", "", "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""), String.valueOf(page));
            } else if(wallType.equals(getString(R.string.rated))) {
                call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getLiveWallByRated(methods.getAPIRequest(PCliq_Constant.URL_LIVE_WALL_RATED, 0, "", "", "", "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""), String.valueOf(page));
            } else {
                isLoading = true;
                call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getLiveWallByLatest(methods.getAPIRequest(PCliq_Constant.URL_LIVE_WALL_LATEST, 0, "", "", "", "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""), String.valueOf(page));
            }
            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {
                    if(getActivity() != null) {
                        if (response.body() != null && response.body().getArrayListWallpaper() != null) {
                            if (response.body().getArrayListWallpaper().size() == 0) {
                                isOver = true;
                                setEmpty();
                            } else {
                                totalRecord = totalRecord + response.body().getArrayListWallpaper().size();

                                for (int i = 0; i < response.body().getArrayListWallpaper().size(); i++) {
                                    dbHelper.addWallpaper(response.body().getArrayListWallpaper().get(i), "live_wall", "");

                                    arrayList.add(response.body().getArrayListWallpaper().get(i));

                                    int abc = arrayList.lastIndexOf(null);
                                    if (((arrayList.size() - (abc + 1)) % new PCliq_PreferenceClass(getContext()).getInt("rv_count", 4) == 0) ) {
                                        arrayList.add(null);
                                    }

//                                    if (PCliq_Constant.isNativeAd) {
//                                        int abc = arrayList.lastIndexOf(null);
//                                        if (((arrayList.size() - (abc + 1)) % PCliq_Constant.nativeAdShow == 0) && (response.body().getArrayListWallpaper().size() - 1 != i || totalRecord != response.body().getTotalRecords())) {
//                                            arrayList.add(null);
//                                        }
//                                    }
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
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Throwable t) {
                    call.cancel();
                    setEmpty();
                    isOver = true;
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            arrayList.addAll(dbHelper.getLiveWallpapers("1"));
            setAdapter();
            isOver = true;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setAdapter() {
        if (!isScroll) {
            adapter = new PCliq_AdapterLivePose(getActivity(), arrayList, new PCliq_RecyclerViewClickListener() {
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
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() == 0) {
            textView_empty.setText(getString(R.string.no_data_found));
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }

    private void openFilterDialog() {
        View view = getLayoutInflater().inflate(R.layout.pcliq_layout_filter_live_pose, null);

        BottomSheetDialog dialog_filter = new BottomSheetDialog(getActivity());
        dialog_filter.setContentView(view);
        dialog_filter.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_filter.show();

        final TextView button_popular = dialog_filter.findViewById(R.id.tv_filter_popular);
        final TextView button_most_download = dialog_filter.findViewById(R.id.tv_filter_most_download);
        final TextView button_rated = dialog_filter.findViewById(R.id.tv_filter_rated);
        final MaterialButton button_filter = dialog_filter.findViewById(R.id.button_filter);
        final MaterialButton button_clear = dialog_filter.findViewById(R.id.button_filter_clear);

        wallTempType = wallType;

        if(wallTempType.equals(getString(R.string.popular))) {
            button_popular.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        } else if(wallTempType.equals(getString(R.string.download))) {
            button_most_download.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        } else if(wallTempType.equals(getString(R.string.rated))) {
            button_rated.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        }

        button_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wallTempType.equals(getString(R.string.popular))) {
                    wallTempType = "";
                    button_popular.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    wallTempType = getString(R.string.popular);
                    button_popular.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
                    button_most_download.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                    button_rated.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                }
            }
        });

        button_most_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wallTempType.equals(getString(R.string.download))) {
                    wallTempType = "";
                    button_most_download.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    wallTempType = getString(R.string.download);
                    button_most_download.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
                    button_popular.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                    button_rated.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                }
            }
        });

        button_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wallTempType.equals(getString(R.string.rated))) {
                    wallTempType = "";
                    button_rated.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    wallTempType = getString(R.string.rated);
                    button_rated.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
                    button_popular.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                    button_most_download.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                }
            }
        });

        button_clear.setOnClickListener(v -> {
            button_popular.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
            button_most_download.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
            button_rated.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
            wallTempType = "";
            wallType = "";
        });

        button_filter.setOnClickListener(v -> {
            wallType = wallTempType;

            page = 1;
            totalRecord = 0;
            isOver = false;
            arrayList.clear();
            if(adapter != null) {
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