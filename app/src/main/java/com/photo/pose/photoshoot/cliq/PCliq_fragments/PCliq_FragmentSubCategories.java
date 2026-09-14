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
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterColors;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterSubCategories;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterPose;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemSubCatList;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemPoseList;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_SearchPoseActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_PoseDetailsActivity;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_RecyclerViewClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemSubCat;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_EndlessRecyclerViewScrollListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_RecyclerItemClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import android.widget.ProgressBar;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_FragmentSubCategories extends Fragment {

    private PCliq_DBHelper dbHelper;
    private PCliq_Methods methods;
    private RecyclerView rv_wallpapers, rv_sub_cat;
    private PCliq_AdapterSubCategories adapterSubCategories;
    private PCliq_AdapterPose adapterWallpaper;
    private ArrayList<PCliq_ItemSubCat> arrayListSubCat;
    private ArrayList<PCliq_ItemPose> arrayListWallpapers;
    private CircularProgressBar progressBar;
    private TextView textView_empty;
    private SearchView searchView;
    String catID = "";
    private int page = 1, totalRecord = 0;
    private StaggeredGridLayoutManager grid;
    private Boolean isOver = false, isScroll = false, isLoading = false;
    private String wallType = "", wallTempType = "", color_ids = "", subCatId = "", databaseTable = "", databaseID = "";
    PCliq_APIInterface apiInterface;
    PCliq_AdapterColors adapterColors = null;
    Call<PCliq_ItemPoseList> call;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pcliq_fragment_sub_categories, container, false);

        apiInterface = PCliq_APIClient.getClient().create(PCliq_APIInterface.class);

        PCliq_InterAdListener interAdListener = new PCliq_InterAdListener() {
            @Override
            public void onClick(int pos, String type) {
                PCliq_Constant.arrayList.clear();
                PCliq_Constant.arrayList.addAll(arrayListWallpapers);
                PCliq_Constant.arrayList.removeAll(Collections.singleton(null));

                int real_pos = PCliq_Constant.arrayList.indexOf(arrayListWallpapers.get(pos));

                Intent intent = new Intent(getActivity(), PCliq_PoseDetailsActivity.class);
                if (subCatId.equals("")) {
                    intent.putExtra("list_type", getString(R.string.categories));
                    intent.putExtra("cid", catID);
                } else {
                    intent.putExtra("list_type", getString(R.string.sub_categories));
                    intent.putExtra("cid", subCatId);
                }
                intent.putExtra("pos", real_pos);
                intent.putExtra("page", page);
                intent.putExtra("wallType", wallType);
                intent.putExtra("color_ids", color_ids);

                startActivity(intent);
            }
        };

        String catName = "";
        if (getArguments() != null) {
            catID = getArguments().getString("cid", "");
            catName = getArguments().getString("cname", "");
        }
        if (catName.isEmpty()) {
            catName = getTag() != null ? getTag() : "Poses";
        }

        View headerView = rootView.findViewById(R.id.rl_sub_cat_header);
        View backBtn = rootView.findViewById(R.id.iv_cat_back);
        View searchBtn = rootView.findViewById(R.id.iv_cat_search);
        TextView tvHeading = rootView.findViewById(R.id.tv_cat_heading);
        TextView tvSubtitle = rootView.findViewById(R.id.tv_cat_subtitle);

        if (tvHeading != null && !catName.isEmpty()) {
            tvHeading.setText(catName.toLowerCase().contains("pose") ? catName : catName + " Poses");
        }

        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        if (searchBtn != null) {
            searchBtn.setOnClickListener(v -> {
                Intent searchIntent = new Intent(getActivity(), PCliq_SearchPoseActivity.class);
                startActivity(searchIntent);
            });
        }

        dbHelper = new PCliq_DBHelper(getActivity());
        methods = new PCliq_Methods(getActivity(), interAdListener);

        android.widget.RelativeLayout rl_ad = rootView.findViewById(R.id.rl_ad);
        if (com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_NetworkUtils.isNetworkAvailable(getActivity())) {
            if (new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass(getActivity()).getInt("BannerAdStatus") == 1) {
                rl_ad.setVisibility(View.VISIBLE);
                com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_LoadAds.loadAdmobBannerAd(getActivity(), rl_ad);
            } else {
                rl_ad.setVisibility(View.GONE);
            }
        }

        arrayListSubCat = new ArrayList<>();
        arrayListWallpapers = new ArrayList<>();

        progressBar = rootView.findViewById(R.id.pb_cat);
        textView_empty = rootView.findViewById(R.id.tv_empty_cat);
        rv_wallpapers = rootView.findViewById(R.id.rv_cat);
        grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv_wallpapers.setLayoutManager(grid);

        rv_sub_cat = rootView.findViewById(R.id.rv_sub_cat);
        rv_sub_cat.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rv_sub_cat.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(getActivity(), new PCliq_RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    call.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                subCatId = adapterSubCategories.setSelected(position);
                arrayListWallpapers.clear();
                if (adapterWallpaper != null) {
                    adapterWallpaper.notifyDataSetChanged();
                }
                page = 1;
                isOver = false;
                getWallpaperData();
            }
        }));

        rv_wallpapers.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(getActivity(), new PCliq_RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInter(position, "");
            }
        }));

        rv_wallpapers.addOnScrollListener(new PCliq_EndlessRecyclerViewScrollListener(grid) {
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

        getSubCategories();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.pcliq_menu_search_fragment, menu);
                MenuItem item = menu.findItem(R.id.menu_search_frag);
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(queryTextListener);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_filter_frag) {
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
            Intent intent = new Intent(getActivity(), PCliq_SearchPoseActivity.class);
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private void getSubCategories() {
        if (methods.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);

            Call<PCliq_ItemSubCatList> call = apiInterface.getSubCategories(methods.getAPIRequest(PCliq_Constant.URL_SUB_CATEGORIES, 0, "", "", catID, "", "", "", "", "", "", "", "", ""));
            call.enqueue(new Callback<PCliq_ItemSubCatList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemSubCatList> call, @NonNull Response<PCliq_ItemSubCatList> response) {
                    if(getActivity() != null) {
                        if (response.body() != null && response.body().getArrayListSubCat() != null) {
                            arrayListSubCat.addAll(response.body().getArrayListSubCat());
                            setAdapterSubCat();
                            for (int i = 0; i < response.body().getArrayListSubCat().size(); i++) {
                                dbHelper.addToSubCatList(response.body().getArrayListSubCat().get(i), catID);
                            }
                        } else {
                            setEmptySubCat();
                        }
                        getWallpaperData();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemSubCatList> call, @NonNull Throwable t) {
                    call.cancel();
                    setEmptySubCat();
                    getWallpaperData();
                }
            });
        } else {
            arrayListSubCat = dbHelper.getSubCat(catID);
            setAdapterSubCat();

            getWallpaperData();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getWallpaperData() {
        if (methods.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            isLoading = true;

            if (subCatId.equals("")) {
                databaseTable = "cat";
                databaseID = catID;
                call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersByCat(methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_CAT, page, color_ids, wallType, catID, "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""), String.valueOf(page));
            } else {
                databaseTable = "subcat";
                databaseID = subCatId;
                call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersBySubCat(methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_SUB_CAT, page, color_ids, wallType, subCatId, "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""), String.valueOf(page));
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
                                    dbHelper.addWallpaper(response.body().getArrayListWallpaper().get(i), databaseTable, databaseID);

                                    arrayListWallpapers.add(response.body().getArrayListWallpaper().get(i));

                                    int abc = arrayListWallpapers.lastIndexOf(null);
                                    if (((arrayListWallpapers.size() - (abc + 1)) % new PCliq_PreferenceClass(getContext()).getInt("rv_count", 4) == 0) ) {
                                        arrayListWallpapers.add(null);
                                    }

//                                    if (PCliq_Constant.isNativeAd) {

//                                        if (((arrayListWallpapers.size() - (abc + 1)) % 4 == 0) && (response.body().getArrayListWallpaper().size() - 1 != i || totalRecord != response.body().getTotalRecords())) {
//                                            arrayListWallpapers.add(null);
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
            if (subCatId.equals("")) {
                arrayListWallpapers = dbHelper.getWallByCat(catID, wallType);
            } else {
                arrayListWallpapers = dbHelper.getWallBySubCat(subCatId, wallType);
            }
            setAdapter();
            isOver = true;
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setAdapter() {
        if (!isScroll) {
            adapterWallpaper = new PCliq_AdapterPose(getActivity(), arrayListWallpapers, new PCliq_RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    methods.showInter(position, "");
                }
            });

            AnimationAdapter adapterAnim = new AlphaInAnimationAdapter(adapterWallpaper);
            adapterAnim.setFirstOnly(true);
            adapterAnim.setDuration(500);
            adapterAnim.setInterpolator(new OvershootInterpolator(.5f));
            rv_wallpapers.setAdapter(adapterAnim);
        } else {
            adapterWallpaper.notifyDataSetChanged();
        }
        setEmpty();
    }

    private void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayListWallpapers.size() == 0) {
            textView_empty.setText(getString(R.string.no_data_found));
            textView_empty.setVisibility(View.VISIBLE);
            rv_wallpapers.setVisibility(View.GONE);
        } else {
            rv_wallpapers.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }

    public void setAdapterSubCat() {
        adapterSubCategories = new PCliq_AdapterSubCategories(getActivity(), arrayListSubCat);
        rv_sub_cat.setAdapter(adapterSubCategories);
        setEmptySubCat();
    }

    private void setEmptySubCat() {
        if (arrayListSubCat.size() == 0) {
            rv_sub_cat.setVisibility(View.GONE);
        } else {
            rv_sub_cat.setVisibility(View.VISIBLE);
        }
    }

    private void openFilterDialog() {
        View view = getLayoutInflater().inflate(R.layout.pcliq_layout_filter_pose, null);

        BottomSheetDialog dialog_filter = new BottomSheetDialog(getActivity());
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
            LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            rv_colors.setLayoutManager(llm);

            adapterColors = new PCliq_AdapterColors(getActivity(), PCliq_Constant.arrayListColors);
            adapterColors.setMultipleSelected(color_ids);
            rv_colors.setAdapter(adapterColors);

            rv_colors.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(getActivity(), new PCliq_RecyclerItemClickListener.OnItemClickListener() {
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
            isScroll = false;
            arrayListWallpapers.clear();
            if (adapterWallpaper != null) {
                adapterWallpaper.notifyDataSetChanged();
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
            isScroll = false;
            arrayListWallpapers.clear();
            if (adapterWallpaper != null) {
                adapterWallpaper.notifyDataSetChanged();
            }
            getWallpaperData();
            dialog_filter.dismiss();
        });
    }
}
