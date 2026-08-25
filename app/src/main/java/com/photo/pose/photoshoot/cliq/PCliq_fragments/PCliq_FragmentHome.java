package com.photo.pose.photoshoot.cliq.PCliq_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.photo.pose.photoshoot.cliq.MyApplication;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_PopularPoseActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_SearchPoseActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_PoseDetailsActivity;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterColors;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterPose;
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
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_RecyclerItemClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.photo.pose.photoshoot.cliq.R;

import java.util.ArrayList;
import java.util.Collections;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_NewCameraActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_SettingActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_PoseByCatActivity;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterHomeChips;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemCat;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemCatList;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import androidx.core.widget.NestedScrollView;
import android.os.Handler;
import android.os.Looper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class PCliq_FragmentHome extends Fragment {

    private ImageView ivHeroImage;
    private final Handler heroHandler = new Handler(Looper.getMainLooper());
    private final ArrayList<String> heroImageUrls = new ArrayList<>();
    private int currentHeroIndex = 0;
    private final Runnable heroCyclerRunnable = new Runnable() {
        @Override
        public void run() {
            if (getContext() != null && ivHeroImage != null && !heroImageUrls.isEmpty() && isAdded()) {
                currentHeroIndex = (currentHeroIndex + 1) % heroImageUrls.size();
                String nextUrl = heroImageUrls.get(currentHeroIndex);
                try {
                    Glide.with(PCliq_FragmentHome.this)
                            .load(nextUrl)
                            .transition(DrawableTransitionOptions.withCrossFade(700))
                            .centerCrop()
                            .into(ivHeroImage);
                } catch (Exception ignored) {}
            }
            heroHandler.postDelayed(this, 4000);
        }
    };

    private PCliq_DBHelper dbHelper;
    private RecyclerView recyclerView;
    private PCliq_AdapterPose adapter;
    private ArrayList<PCliq_ItemPose> arrayList;
    private CircularProgressBar progressBar;
    private PCliq_Methods methods;
    private Boolean isOver = false, isScroll = false, isLoading = false;
    private TextView textView_empty;
    private int page = 1, totalRecord = 0;
    private StaggeredGridLayoutManager grid;
    private String wallType = "", wallTempType = "", color_ids = "";
    private FloatingActionButton fab;
    PCliq_AdapterColors adapterColors = null;

    private RecyclerView rvHomeChips;
    private PCliq_AdapterHomeChips adapterHomeChips;
    private ArrayList<PCliq_ItemCat> arrayListHomeCats;
    private String selectedCatId = "";
    private String searchQuery = "";
    private TextView tvTrendingSectionTitle;
    private EditText etHomeSearch;
    private ImageView ivClearSearch;

    // DOWNLOAD
    private PCliq_DBHelper DdbHelper;
    private RecyclerView DrecyclerView;
    private PCliq_AdapterPose Dadapter;
    private ArrayList<PCliq_ItemPose> DarrayList;
    private CircularProgressBar DprogressBar;
    private PCliq_Methods Dmethods;
    private TextView DtextView_empty;
    private StaggeredGridLayoutManager Dgrid;
    private String DwallType = "", DwallTempType = "", Dcolor_ids = "";

    PCliq_AdapterColors DadapterColors = null;

//  POPULAR

    private PCliq_DBHelper PdbHelper;
    private RecyclerView PrecyclerView;
    private PCliq_AdapterPose Padapter;
    private ArrayList<PCliq_ItemPose> ParrayList;
    private CircularProgressBar PprogressBar;
    private PCliq_Methods Pmethods;
    private TextView PtextView_empty;
    private String PwallType = "", PwallTempType = "", Pcolor_ids = "";
    PCliq_AdapterColors PadapterColors = null;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pcliq_fragment_home, container, false);

        // Fix top and bottom system bar overlap using WindowInsets
        View topHeader = rootView.findViewById(R.id.rl_top_header);
        NestedScrollView nestedScrollView = (NestedScrollView) ((android.view.ViewGroup) rootView).getChildAt(1);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            if (topHeader != null) {
                topHeader.setPadding(
                        topHeader.getPaddingLeft(),
                        statusBarHeight + (int) (6 * getResources().getDisplayMetrics().density),
                        topHeader.getPaddingRight(),
                        (int) (14 * getResources().getDisplayMetrics().density)
                );
            }
            if (nestedScrollView != null) {
                nestedScrollView.setPadding(
                        nestedScrollView.getPaddingLeft(),
                        nestedScrollView.getPaddingTop(),
                        nestedScrollView.getPaddingRight(),
                        navBarHeight + (int) (90 * getResources().getDisplayMetrics().density)
                );
            }
            return insets;
        });

        grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        grid.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        PCliq_InterAdListener interAdListener = new PCliq_InterAdListener() {
            @Override
            public void onClick(int position, String type) {

                PCliq_Constant.arrayList.clear();
                PCliq_Constant.arrayList.addAll(arrayList);
                PCliq_Constant.arrayList.removeAll(Collections.singleton(null));

                int real_pos = PCliq_Constant.arrayList.indexOf(arrayList.get(position));

                Intent intent = new Intent(getActivity(), PCliq_PoseDetailsActivity.class);
                intent.putExtra("pos", real_pos);
                intent.putExtra("list_type", getString(R.string.latest));
                intent.putExtra("page", page);
                intent.putExtra("wallType", wallType);
                intent.putExtra("color_ids", color_ids);

                startActivity(intent);
            }
        };

        dbHelper = new PCliq_DBHelper(getActivity());
        methods = new PCliq_Methods(getActivity(), interAdListener);

        dbHelper.getAbout();

        arrayList = new ArrayList<>();

        // Wire top header settings
        View ivHomeSettings = rootView.findViewById(R.id.iv_home_settings);
        if (ivHomeSettings != null) {
            ivHomeSettings.setOnClickListener(v -> startActivity(new Intent(getActivity(), PCliq_SettingActivity.class)));
        }

        // Wire search bar
        View llSearchBar = rootView.findViewById(R.id.ll_search_bar);
        if (llSearchBar != null) {
            llSearchBar.setOnClickListener(v -> startActivity(new Intent(getActivity(), PCliq_SearchPoseActivity.class)));
        }

        // Live search bar
        etHomeSearch = rootView.findViewById(R.id.et_home_search);
        ivClearSearch = rootView.findViewById(R.id.iv_clear_search);
        tvTrendingSectionTitle = rootView.findViewById(R.id.tv_trending_section_title);

        if (etHomeSearch != null) {
            etHomeSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {
                    searchQuery = etHomeSearch.getText().toString().trim();
                    if (!searchQuery.isEmpty()) {
                        if (tvTrendingSectionTitle != null) {
                            tvTrendingSectionTitle.setText("Results for \"" + searchQuery + "\"");
                        }
                    } else {
                        if (tvTrendingSectionTitle != null) {
                            tvTrendingSectionTitle.setText(selectedCatId.isEmpty() ? "Trending Poses" : "Filtered Poses");
                        }
                    }
                    try {
                        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(etHomeSearch.getWindowToken(), 0);
                        }
                    } catch (Exception ignored) {}
                    page = 1;
                    isOver = false;
                    arrayList.clear();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    getWallpaperData();
                    return true;
                }
                return false;
            });

            etHomeSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (ivClearSearch != null) {
                        ivClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }

        if (ivClearSearch != null) {
            ivClearSearch.setOnClickListener(v -> {
                if (etHomeSearch != null) {
                    etHomeSearch.setText("");
                }
                searchQuery = "";
                if (tvTrendingSectionTitle != null) {
                    tvTrendingSectionTitle.setText(selectedCatId.isEmpty() ? "Trending Poses" : "Filtered Poses");
                }
                page = 1;
                isOver = false;
                arrayList.clear();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                getWallpaperData();
            });
        }



        // Setup Category Filter Chips
        rvHomeChips = rootView.findViewById(R.id.rv_home_chips);
        if (rvHomeChips != null) {
            rvHomeChips.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            arrayListHomeCats = new ArrayList<>();
            // Initial categories with "All"
            arrayListHomeCats.add(new PCliq_ItemCat("0", "All", ""));
            arrayListHomeCats.add(new PCliq_ItemCat("1", "Boy", ""));
            arrayListHomeCats.add(new PCliq_ItemCat("2", "Bride", ""));
            arrayListHomeCats.add(new PCliq_ItemCat("3", "Couple", ""));
            arrayListHomeCats.add(new PCliq_ItemCat("4", "Father", ""));
            arrayListHomeCats.add(new PCliq_ItemCat("5", "Girl", ""));
            arrayListHomeCats.add(new PCliq_ItemCat("6", "Mother", ""));

            adapterHomeChips = new PCliq_AdapterHomeChips(getContext(), arrayListHomeCats, (item, position) -> {
                if (position == 0 || item.getId().equals("0")) {
                    selectedCatId = "";
                    if (tvTrendingSectionTitle != null) {
                        tvTrendingSectionTitle.setText("Trending Poses");
                    }
                } else {
                    selectedCatId = item.getId();
                    if (tvTrendingSectionTitle != null) {
                        tvTrendingSectionTitle.setText(item.getName() + " Poses");
                    }
                }
                searchQuery = "";
                if (etHomeSearch != null) {
                    etHomeSearch.setText("");
                }
                page = 1;
                isOver = false;
                arrayList.clear();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                getWallpaperData();
            });
            rvHomeChips.setAdapter(adapterHomeChips);
            loadHomeCategories();
        }

        // Wire Studio Pro Quick Actions
        View actionCamera = rootView.findViewById(R.id.ll_action_camera);
        if (actionCamera != null) {
            actionCamera.setOnClickListener(v -> openLiveCameraWithPose());
        }

        View actionSurprise = rootView.findViewById(R.id.ll_action_surprise);
        if (actionSurprise != null) {
            actionSurprise.setOnClickListener(v -> pickSurprisePose());
        }

        View actionPopular = rootView.findViewById(R.id.ll_action_popular);
        if (actionPopular != null) {
            actionPopular.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), PCliq_PopularPoseActivity.class);
                intent.putExtra("cate_name", "Popular");
                MyApplication.showInterstitialAd(getActivity(), () -> startActivity(intent));
            });
        }

        View actionTips = rootView.findViewById(R.id.ll_action_tips);
        if (actionTips != null) {
            actionTips.setOnClickListener(v -> openPhotoshootTipsDialog());
        }

        View tipCard = rootView.findViewById(R.id.ll_daily_tip_card);
        if (tipCard != null) {
            tipCard.setOnClickListener(v -> openPhotoshootTipsDialog());
        }

        progressBar = rootView.findViewById(R.id.pb_wallcat);
        textView_empty = rootView.findViewById(R.id.tv_empty_wallcat);

        fab = rootView.findViewById(R.id.fab);
        recyclerView = rootView.findViewById(R.id.rv_wall_by_cat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(grid);

        View viewAllLatest = rootView.findViewById(R.id.view_all_latest);
        if (viewAllLatest != null) {
            viewAllLatest.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), PCliq_PopularPoseActivity.class);
                intent.putExtra("cate_name", "Latest");
                MyApplication.showInterstitialAd(getActivity(), () -> startActivity(intent));
            });
        }

        View viewAllPopular = rootView.findViewById(R.id.view_all_popular);
        if (viewAllPopular != null) {
            viewAllPopular.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), PCliq_PopularPoseActivity.class);
                intent.putExtra("cate_name", "Popular");
                MyApplication.showInterstitialAd(getActivity(), () -> startActivity(intent));
            });
        }

        View viewAllDownload = rootView.findViewById(R.id.view_all_download);
        if (viewAllDownload != null) {
            viewAllDownload.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), PCliq_PopularPoseActivity.class);
                intent.putExtra("cate_name", "Download");
                MyApplication.showInterstitialAd(getActivity(), () -> startActivity(intent));
            });
        }

        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (v.getChildAt(0) != null && scrollY >= (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 600)) {
                    if (!isOver && !isLoading) {
                        isScroll = true;
                        getWallpaperData();
                    }
                }
            });
        }

        getWallpaperData();


//        DOWNLOAD

        PCliq_InterAdListener DinterAdListener = new PCliq_InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                PCliq_Constant.arrayList.clear();
                PCliq_Constant.arrayList.addAll(DarrayList);
                PCliq_Constant.arrayList.removeAll(Collections.singleton(null));

                int real_pos = PCliq_Constant.arrayList.indexOf(DarrayList.get(position));

                Intent intent = new Intent(getActivity(), PCliq_PoseDetailsActivity.class);
                intent.putExtra("pos", real_pos);
                intent.putExtra("list_type", getString(R.string.most_downloaded));
                intent.putExtra("page", 1);
                intent.putExtra("wallType", DwallType);
                intent.putExtra("color_ids", Dcolor_ids);
                startActivity(intent);
            }
        };

        DdbHelper = new PCliq_DBHelper(getActivity());
        Dmethods = new PCliq_Methods(getActivity(), DinterAdListener);

        DdbHelper.getAbout();

        DarrayList = new ArrayList<>();

        DprogressBar = rootView.findViewById(R.id.pb_wallcat_download);
        DtextView_empty = rootView.findViewById(R.id.tv_empty_wallcat_download);

        fab = rootView.findViewById(R.id.fab);
        DrecyclerView = rootView.findViewById(R.id.rv_wall_by_cat_download);
        DrecyclerView.setHasFixedSize(true);

//        DrecyclerView.setLayoutManager(Dgrid);
        DrecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        DrecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                DrecyclerView.smoothScrollToPosition(0);
            }
        });

        DgetWallpaperData();


//        POPULAR


        PCliq_InterAdListener PinterAdListener = new PCliq_InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                PCliq_Constant.arrayList.clear();
                PCliq_Constant.arrayList.addAll(ParrayList);
                PCliq_Constant.arrayList.removeAll(Collections.singleton(null));

                int real_pos = PCliq_Constant.arrayList.indexOf(ParrayList.get(position));

                Intent intent = new Intent(getActivity(), PCliq_PoseDetailsActivity.class);
                intent.putExtra("pos", real_pos);
                intent.putExtra("list_type", getString(R.string.popular));
                intent.putExtra("page", 1);
                intent.putExtra("wallType", PwallType);
                intent.putExtra("color_ids", Pcolor_ids);
                startActivity(intent);
            }
        };

        PdbHelper = new PCliq_DBHelper(getActivity());
        Pmethods = new PCliq_Methods(getActivity(), PinterAdListener);

        PdbHelper.getAbout();

        ParrayList = new ArrayList<>();

        PprogressBar = rootView.findViewById(R.id.pb_wallcat_pupular);
        PtextView_empty = rootView.findViewById(R.id.tv_empty_wallcat_pupular);

        fab = rootView.findViewById(R.id.fab);
        PrecyclerView = rootView.findViewById(R.id.rv_wall_by_cat_pupular);
        PrecyclerView.setHasFixedSize(true);

        PrecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        PrecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                PrecyclerView.smoothScrollToPosition(0);
            }
        });

        PgetWallpaperData();


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
                MenuItem item1 = menu.findItem(R.id.menu_filter);
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
                SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
                methods.styleSearchView(searchView);
                searchView.setOnQueryTextListener(queryTextListener);
                searchView.setOnQueryTextListener(DqueryTextListener);
                searchView.setOnQueryTextListener(PqueryTextListener);

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


    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
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

    private void getWallpaperData() {
        if (methods.isNetworkAvailable()) {
            isLoading = true;
            progressBar.setVisibility(View.VISIBLE);

            Call<PCliq_ItemPoseList> call;
            if (!searchQuery.isEmpty()) {
                call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersBySearch(methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_SEARCH, 0, color_ids, wallType, "", searchQuery, "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""), String.valueOf(page));
            } else if (!selectedCatId.isEmpty()) {
                call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersByCat(methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_CAT, page, color_ids, wallType, selectedCatId, "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""), String.valueOf(page));
            } else {
                call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersByLatest(methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_LATEST, page, color_ids, wallType, "", "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""), String.valueOf(page));
            }

            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {
                    if (getActivity() != null) {
                        if (response.body() != null && response.body().getArrayListWallpaper() != null) {
                            if (response.body().getArrayListWallpaper().size() == 0) {
                                isOver = true;
                                setEmpty();
                            } else {
                                totalRecord = totalRecord + response.body().getArrayListWallpaper().size();
                                for (int i = 0; i < response.body().getArrayListWallpaper().size(); i++) {
                                    dbHelper.addWallpaper(response.body().getArrayListWallpaper().get(i), "latest", "");

                                    arrayList.add(response.body().getArrayListWallpaper().get(i));

                                    try {
                                        int abc = arrayList.lastIndexOf(null);
                                        if (((arrayList.size() - (abc + 1)) % new PCliq_PreferenceClass(getContext()).getInt("rv_count", 4) == 0)) {
                                            arrayList.add(null);
                                        }
                                    } catch (Exception e) {

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
                                updateHeroImages(arrayList);
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
            arrayList = dbHelper.getWallpapers("id", wallType);
            setAdapter();
            updateHeroImages(arrayList);
            isOver = true;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setAdapter() {
        if (!isScroll) {
            adapter = new PCliq_AdapterPose(getActivity(), arrayList, new PCliq_RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    methods.showInter(position, "");
                }
            });
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(adapter);
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

//        DOWNLOAD

        if (PCliq_Constant.isColorOn && PCliq_Constant.arrayListColors.size() > 0) {
            LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            rv_colors.setLayoutManager(llm);

            DadapterColors = new PCliq_AdapterColors(getActivity(), PCliq_Constant.arrayListColors);
            DadapterColors.setMultipleSelected(Dcolor_ids);
            rv_colors.setAdapter(DadapterColors);

            rv_colors.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(getActivity(), new PCliq_RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    DadapterColors.setSelected(position);
                }
            }));

        } else {
            dialog_filter.findViewById(R.id.tv3).setVisibility(View.GONE);
        }
        DwallTempType = DwallType;

        if (DwallTempType.equals(getString(R.string.portrait))) {
            button_portrait.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        } else if (DwallTempType.equals(getString(R.string.landscape))) {
            button_landscape.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        } else if (DwallTempType.equals(getString(R.string.square))) {
            button_square.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        }


//        POPULAR

        if (PCliq_Constant.isColorOn && PCliq_Constant.arrayListColors.size() > 0) {
            LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            rv_colors.setLayoutManager(llm);

            PadapterColors = new PCliq_AdapterColors(getActivity(), PCliq_Constant.arrayListColors);
            PadapterColors.setMultipleSelected(Pcolor_ids);
            rv_colors.setAdapter(PadapterColors);

            rv_colors.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(getActivity(), new PCliq_RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    PadapterColors.setSelected(position);
                }
            }));

        } else {
            dialog_filter.findViewById(R.id.tv3).setVisibility(View.GONE);
        }
        PwallTempType = PwallType;

        if (PwallTempType.equals(getString(R.string.portrait))) {
            button_portrait.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        } else if (PwallTempType.equals(getString(R.string.landscape))) {
            button_landscape.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
        } else if (PwallTempType.equals(getString(R.string.square))) {
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

                if (DwallTempType.equals(getString(R.string.portrait))) {
                    DwallTempType = "";
                    button_portrait.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    DwallTempType = getString(R.string.portrait);
                    button_portrait.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
                    button_landscape.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                    button_square.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                }

                if (PwallTempType.equals(getString(R.string.portrait))) {
                    PwallTempType = "";
                    button_portrait.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    PwallTempType = getString(R.string.portrait);
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

                if (DwallTempType.equals(getString(R.string.landscape))) {
                    DwallTempType = "";
                    button_landscape.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    DwallTempType = getString(R.string.landscape);
                    button_landscape.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
                    button_portrait.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                    button_square.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                }

                if (PwallTempType.equals(getString(R.string.landscape))) {
                    PwallTempType = "";
                    button_landscape.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    PwallTempType = getString(R.string.landscape);
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

                if (DwallTempType.equals(getString(R.string.square))) {
                    DwallTempType = "";
                    button_square.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    DwallTempType = getString(R.string.square);
                    button_square.setBackgroundResource(R.drawable.pcliq_bg_gradient_round);
                    button_portrait.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                    button_landscape.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                }

                if (PwallTempType.equals(getString(R.string.square))) {
                    PwallTempType = "";
                    button_square.setBackgroundResource(R.drawable.pcliq_bg_button_filter);
                } else {
                    PwallTempType = getString(R.string.square);
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

            DwallTempType = "";
            DwallType = "";
            Dcolor_ids = "";
            if (DadapterColors != null) {
                DadapterColors.clearSelected();
            }

            PwallTempType = "";
            PwallType = "";
            Pcolor_ids = "";
            if (PadapterColors != null) {
                PadapterColors.clearSelected();
            }
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

            if (DadapterColors != null) {
                Dcolor_ids = DadapterColors.getSelected();
            }
            DwallType = DwallTempType;

            DarrayList.clear();
            if (Dadapter != null) {
                Dadapter.notifyDataSetChanged();
            }
            DgetWallpaperData();

            if (PadapterColors != null) {
                Pcolor_ids = PadapterColors.getSelected();
            }
            PwallType = PwallTempType;

            ParrayList.clear();
            if (Padapter != null) {
                Padapter.notifyDataSetChanged();
            }
            PgetWallpaperData();

            dialog_filter.dismiss();

        });


    }

    private void updateHeroImages(ArrayList<PCliq_ItemPose> list) {
        if (list == null || list.isEmpty()) return;
        boolean wasEmpty = heroImageUrls.isEmpty();
        for (PCliq_ItemPose item : list) {
            if (item != null && item.getImage() != null && !item.getImage().trim().isEmpty()) {
                String img = item.getImage().replace(" ", "%20");
                if (!heroImageUrls.contains(img)) {
                    heroImageUrls.add(img);
                }
            }
        }
        if (wasEmpty && !heroImageUrls.isEmpty() && getContext() != null && ivHeroImage != null && isAdded()) {
            try {
                Glide.with(PCliq_FragmentHome.this)
                        .load(heroImageUrls.get(0))
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .centerCrop()
                        .into(ivHeroImage);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        heroHandler.removeCallbacks(heroCyclerRunnable);
        heroHandler.postDelayed(heroCyclerRunnable, 4000);
    }

    @Override
    public void onPause() {
        super.onPause();
        heroHandler.removeCallbacks(heroCyclerRunnable);
    }

    @Override
    public void onDestroy() {
        heroHandler.removeCallbacks(heroCyclerRunnable);
        if (adapter != null) {
            adapter.destroyNativeAds();
        }
        if (Dadapter != null) {
            Dadapter.destroyNativeAds();
        }
        super.onDestroy();
    }

    //   DOWNLOAD
    private SearchView.OnQueryTextListener DqueryTextListener = new SearchView.OnQueryTextListener() {
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

    private void DgetWallpaperData() {
        if (Dmethods.isNetworkAvailable()) {
            DprogressBar.setVisibility(View.VISIBLE);

            Call<PCliq_ItemPoseList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersByDownload(Dmethods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_DOWNLOAD, 0, Dcolor_ids, DwallType, "", "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""));
            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {
                    if (getActivity() != null) {
                        if (response.body() != null && response.body().getArrayListWallpaper() != null) {
                            for (int i = 0; i < response.body().getArrayListWallpaper().size(); i++) {
                                DdbHelper.addWallpaper(response.body().getArrayListWallpaper().get(i), "latest", "");
                                DarrayList.add(response.body().getArrayListWallpaper().get(i));

                                try {
                                    int abc = DarrayList.lastIndexOf(null);
                                    if (((DarrayList.size() - (abc + 1)) % new PCliq_PreferenceClass(getContext()).getInt("rv_count", 4) == 0)) {
                                        DarrayList.add(null);
                                    }
                                } catch (Exception e) {

                                }

//                                if (PCliq_Constant.isNativeAd) {
//                                    int abc = DarrayList.lastIndexOf(null);
//                                    if (((DarrayList.size() - (abc + 1)) % PCliq_Constant.nativeAdShow == 0) && (response.body().getArrayListWallpaper().size() - 1 != i || response.body().getArrayListWallpaper().size() != response.body().getTotalRecords())) {
//                                        DarrayList.add(null);
//                                    }
//                                }
                                DsetAdapter();
                                updateHeroImages(DarrayList);
                            }
                        } else {
                            DsetEmpty();
                        }
                        DprogressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Throwable t) {
                    call.cancel();
                    DsetEmpty();
                    DprogressBar.setVisibility(View.GONE);
                }
            });
        } else {
            DarrayList = DdbHelper.getWallpapers("download", DwallType);
            DsetAdapter();
            updateHeroImages(DarrayList);
            DprogressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void DsetAdapter() {
        com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterHorizontalPose hAdapter = 
                new com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterHorizontalPose(getActivity(), DarrayList, new PCliq_RecyclerViewClickListener() {
            @Override
            public void onClick(int position) {
                Dmethods.showInter(position, "");
            }
        });
        DrecyclerView.setNestedScrollingEnabled(false);
        DrecyclerView.setAdapter(hAdapter);
        DsetEmpty();
    }

    private void DsetEmpty() {
        DprogressBar.setVisibility(View.GONE);
        if (DarrayList.size() == 0) {
            DtextView_empty.setText(getString(R.string.no_data_found));
            DtextView_empty.setVisibility(View.VISIBLE);
            DrecyclerView.setVisibility(View.GONE);
        } else {
            DrecyclerView.setVisibility(View.VISIBLE);
            DtextView_empty.setVisibility(View.GONE);
        }
    }

//POPULAR


    private SearchView.OnQueryTextListener PqueryTextListener = new SearchView.OnQueryTextListener() {
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

    private void PgetWallpaperData() {
        if (Pmethods.isNetworkAvailable()) {
            PprogressBar.setVisibility(View.VISIBLE);

            Call<PCliq_ItemPoseList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersByPopular(Pmethods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_POPULAR, 0, Pcolor_ids, PwallType, "", "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""));
            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {
                    if (getActivity() != null) {
                        if (response.body() != null && response.body().getArrayListWallpaper() != null) {
                            for (int i = 0; i < response.body().getArrayListWallpaper().size(); i++) {
                                PdbHelper.addWallpaper(response.body().getArrayListWallpaper().get(i), "latest", "");
                                ParrayList.add(response.body().getArrayListWallpaper().get(i));
                                try {
                                    int abc = ParrayList.lastIndexOf(null);
                                    if (((ParrayList.size() - (abc + 1)) % new PCliq_PreferenceClass(getContext()).getInt("rv_count", 4) == 0)) {
                                        ParrayList.add(null);
                                    }
                                } catch (Exception e) {

                                }

//                                if (PCliq_Constant.isNativeAd) {
//                                    int abc = ParrayList.lastIndexOf(null);
//                                    if (((ParrayList.size() - (abc + 1)) % PCliq_Constant.nativeAdShow == 0) && (response.body().getArrayListWallpaper().size() - 1 != i || response.body().getArrayListWallpaper().size() != response.body().getTotalRecords())) {
//                                        ParrayList.add(null);
//                                    }
//                                }
                            }
                            PsetAdapter();
                            updateHeroImages(ParrayList);
                        }
                        PprogressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Throwable t) {
                    call.cancel();
                    PsetEmpty();
                    PprogressBar.setVisibility(View.GONE);
                }
            });
        } else {
            ParrayList = PdbHelper.getWallpapers("views", PwallType);
            PsetAdapter();
            updateHeroImages(ParrayList);
            PprogressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void PsetAdapter() {
        Padapter = new PCliq_AdapterPose(getActivity(), ParrayList, new PCliq_RecyclerViewClickListener() {
            @Override
            public void onClick(int position) {
                Pmethods.showInter(position, "");
            }
        });
        PrecyclerView.setNestedScrollingEnabled(false);
        PrecyclerView.setAdapter(Padapter);
        PsetEmpty();
    }

    private void PsetEmpty() {
        if (PprogressBar != null) {
            PprogressBar.setVisibility(View.GONE);
        }
        if (PtextView_empty != null && PrecyclerView != null) {
            if (ParrayList.size() == 0) {
                PtextView_empty.setText(getString(R.string.no_data_found));
                PtextView_empty.setVisibility(View.VISIBLE);
                PrecyclerView.setVisibility(View.GONE);
            } else {
                PrecyclerView.setVisibility(View.VISIBLE);
                PtextView_empty.setVisibility(View.GONE);
            }
        }
    }

    private void loadHomeCategories() {
        try {
            Call<PCliq_ItemCatList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class)
                    .getCategories(methods.getAPIRequest(PCliq_Constant.URL_CATEGORIES, 0, "", "", "", "", "", "", "", "", "", "", "", ""));
            call.enqueue(new Callback<PCliq_ItemCatList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemCatList> call, @NonNull Response<PCliq_ItemCatList> response) {
                    if (response.body() != null && response.body().getArrayListCat() != null && response.body().getArrayListCat().size() > 0) {
                        arrayListHomeCats.clear();
                        arrayListHomeCats.add(new PCliq_ItemCat("0", "All", ""));
                        arrayListHomeCats.addAll(response.body().getArrayListCat());
                        if (adapterHomeChips != null) {
                            adapterHomeChips.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemCatList> call, @NonNull Throwable t) {
                    // Keep initial default categories
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openPhotoshootTipsDialog() {
        if (getActivity() == null) return;
        View view = getLayoutInflater().inflate(R.layout.pcliq_layout_photo_tips, null);
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(view);
        if (dialog.getWindow() != null && dialog.getWindow().findViewById(R.id.design_bottom_sheet) != null) {
            dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        }
        dialog.show();

        View ivClose = view.findViewById(R.id.iv_close_tips);
        if (ivClose != null) {
            ivClose.setOnClickListener(v -> dialog.dismiss());
        }

        View btnCamera = view.findViewById(R.id.btn_open_camera_from_tips);
        if (btnCamera != null) {
            btnCamera.setOnClickListener(v -> {
                dialog.dismiss();
                openLiveCameraWithPose();
            });
        }
    }

    private void openLiveCameraWithPose() {
        Intent intent = new Intent(getActivity(), PCliq_NewCameraActivity.class);
        if (arrayList != null && !arrayList.isEmpty()) {
            intent.putExtra("image", arrayList.get(0).getImage());
            intent.putExtra("isposesketch", false);
        } else if (DarrayList != null && !DarrayList.isEmpty()) {
            intent.putExtra("image", DarrayList.get(0).getImage());
            intent.putExtra("isposesketch", false);
        }
        startActivity(intent);
    }

    private void pickSurprisePose() {
        ArrayList<PCliq_ItemPose> pool = new ArrayList<>();
        for (PCliq_ItemPose p : arrayList) {
            if (p != null) pool.add(p);
        }
        if (pool.isEmpty()) {
            for (PCliq_ItemPose p : DarrayList) {
                if (p != null) pool.add(p);
            }
        }

        if (!pool.isEmpty()) {
            int randomIndex = (int) (Math.random() * pool.size());
            PCliq_Constant.arrayList.clear();
            PCliq_Constant.arrayList.addAll(pool);

            Intent intent = new Intent(getActivity(), PCliq_PoseDetailsActivity.class);
            intent.putExtra("pos", randomIndex);
            intent.putExtra("list_type", getString(R.string.latest));
            intent.putExtra("page", 1);
            intent.putExtra("wallType", wallType);
            intent.putExtra("color_ids", color_ids);
            startActivity(intent);
        } else {
            openLiveCameraWithPose();
        }
    }
}