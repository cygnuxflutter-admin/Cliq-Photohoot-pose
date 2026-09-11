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

import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterPose;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemPoseList;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_PoseDetailsActivity;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_RecyclerViewClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.widget.ProgressBar;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_FragmentPoseRecent extends Fragment {

    private PCliq_DBHelper dbHelper;
    private RecyclerView recyclerView;
    private PCliq_AdapterPose adapter;
    private ArrayList<PCliq_ItemPose> arrayList;
    private ProgressBar progressBar;
    private PCliq_Methods methods;
    private TextView textView_empty;
    private StaggeredGridLayoutManager grid;
    private String errorString = "";
    private FloatingActionButton fab;
    int totalRecord = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pcliq_fragment_pose_by_cat, container, false);

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
                intent.putExtra("list_type", getString(R.string.recently_viewed));
                intent.putExtra("page", 1);
                intent.putExtra("wallType", "");
                intent.putExtra("color_ids", "");
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
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        super.onViewCreated(view, savedInstanceState);
    }

    private void getWallpaperData() {
        if (methods.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            String ids = dbHelper.getRecentWallpapersID("");
            Call<PCliq_ItemPoseList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersByRecent(methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_RECENT, 0, "", "", "", "", ids, "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), ""));
            call.enqueue(new Callback<PCliq_ItemPoseList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Response<PCliq_ItemPoseList> response) {
                    if (getActivity() != null) {
                        errorString = getString(R.string.no_data_found);
                        if (response.body() != null && response.body().getArrayListWallpaper() != null) {
                            totalRecord = totalRecord + response.body().getArrayListWallpaper().size();
                            for (int i = 0; i < response.body().getArrayListWallpaper().size(); i++) {

                                arrayList.add(response.body().getArrayListWallpaper().get(i));

                                int abc = arrayList.lastIndexOf(null);
                                if (((arrayList.size() - (abc + 1)) % new PCliq_PreferenceClass(getContext()).getInt("rv_count", 4) == 0) ) {
                                    arrayList.add(null);
                                }

//                                if (PCliq_Constant.isNativeAd) {
//                                    int abc = arrayList.lastIndexOf(null);
//                                    if (((arrayList.size() - (abc + 1)) % PCliq_Constant.nativeAdShow == 0) && (response.body().getArrayListWallpaper().size() - 1 != i || totalRecord != response.body().getTotalRecords())) {
//                                        arrayList.add(null);
//                                    }
//                                }
                            }
                            setAdapter();
                        } else {
                            setEmpty();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemPoseList> call, @NonNull Throwable t) {
                    call.cancel();
                    errorString = getString(R.string.server_error);
                    setEmpty();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            arrayList = dbHelper.getWallByRecent("1", "");
            errorString = getString(R.string.server_error);
            setAdapter();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setAdapter() {
        adapter = new PCliq_AdapterPose(getActivity(), arrayList, new PCliq_RecyclerViewClickListener() {
            @Override
            public void onClick(int position) {
                methods.showInter(position, "");
            }
        });
        AnimationAdapter adapterAnim = new AlphaInAnimationAdapter(adapter);
        adapterAnim.setFirstOnly(true);
        adapterAnim.setDuration(500);
        adapterAnim.setInterpolator(new OvershootInterpolator(.5f));
        recyclerView.setAdapter(adapterAnim);
        setEmpty();
    }

    private void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() == 0) {
            textView_empty.setText(errorString);
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
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
