package com.photo.pose.photoshoot.cliq.PCliq_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterColors;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterPose;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemPoseList;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_SearchPoseActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_PoseDetailsActivity;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_RecyclerViewClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_RecyclerItemClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class PCliq_FragmentPosePopular extends Fragment {

    private PCliq_DBHelper PdbHelper;
    private RecyclerView PrecyclerView;
    private PCliq_AdapterPose Padapter;
    private ArrayList<PCliq_ItemPose> ParrayList;
    private CircularProgressBar PprogressBar;
    private PCliq_Methods Pmethods;
    private TextView PtextView_empty;
    private StaggeredGridLayoutManager grid;
    private String PwallType = "", PwallTempType = "", Pcolor_ids = "";
    private FloatingActionButton fab;
    PCliq_AdapterColors PadapterColors = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pcliq_fragment_pose_by_cat, container, false);

        grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        grid.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

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

        PprogressBar = rootView.findViewById(R.id.pb_wallcat);
        PtextView_empty = rootView.findViewById(R.id.tv_empty_wallcat);

        fab = rootView.findViewById(R.id.fab);
        PrecyclerView = rootView.findViewById(R.id.rv_wall_by_cat);
        PrecyclerView.setHasFixedSize(true);

        PrecyclerView.setLayoutManager(grid);

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
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
                SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
                Pmethods.styleSearchView(searchView);
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
                    if(getActivity() != null) {
                        if (response.body() != null && response.body().getArrayListWallpaper() != null) {
                            for (int i = 0; i < response.body().getArrayListWallpaper().size(); i++) {
                                com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose item = response.body().getArrayListWallpaper().get(i);
                                String img = item.getImage();
                                if (img == null || img.trim().isEmpty() || img.endsWith("/")) continue;
                                PdbHelper.addWallpaper(item, "latest", "");
                                ParrayList.add(item);

                                int abc = ParrayList.lastIndexOf(null);
                                if (((ParrayList.size() - (abc + 1)) % new PCliq_PreferenceClass(getContext()).getInt("rv_count", 4) == 0) ) {
                                    ParrayList.add(null);
                                }

//                                if (PCliq_Constant.isNativeAd) {
//                                    int abc = ParrayList.lastIndexOf(null);
//                                    if (((ParrayList.size() - (abc + 1)) % PCliq_Constant.nativeAdShow == 0) && (response.body().getArrayListWallpaper().size() - 1 != i || response.body().getArrayListWallpaper().size() != response.body().getTotalRecords())) {
//                                        ParrayList.add(null);
//                                    }
//                                }
                            }
                            PsetAdapter();
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
        AnimationAdapter adapterAnim = new AlphaInAnimationAdapter(Padapter);
        adapterAnim.setFirstOnly(true);
        adapterAnim.setDuration(500);
        adapterAnim.setInterpolator(new OvershootInterpolator(.9f));
        PrecyclerView.setAdapter(adapterAnim);
        PsetEmpty();
    }

    private void PsetEmpty() {
        PprogressBar.setVisibility(View.GONE);
        if (ParrayList.size() == 0) {
            PtextView_empty.setText(getString(R.string.no_data_found));
            PtextView_empty.setVisibility(View.VISIBLE);
            PrecyclerView.setVisibility(View.GONE);
        } else {
            PrecyclerView.setVisibility(View.VISIBLE);
            PtextView_empty.setVisibility(View.GONE);
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
            PwallTempType = "";
            PwallType = "";
            Pcolor_ids = "";
            if (PadapterColors != null) {
                PadapterColors.clearSelected();
            }

            ParrayList.clear();
            if (Padapter != null) {
                Padapter.notifyDataSetChanged();
            }
            PgetWallpaperData();
            dialog_filter.dismiss();
        });

        button_filter.setOnClickListener(v -> {
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

    @Override
    public void onDestroy() {
        if (Padapter != null) {
            Padapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}
