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
import android.widget.ImageView;
import android.widget.TextView;

import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterLivePose;
import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterPose;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemFavList;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemUserList;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_LivePoseDetailsActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_ProfileEditActivity;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_PoseDetailsActivity;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_RecyclerViewClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.core.widget.NestedScrollView;
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

public class PCliq_FragmentProfile extends Fragment {

    PCliq_Methods methods;
    PCliq_SharedPref sharedPref;
    RecyclerView recyclerView;
    PCliq_AdapterPose adapter;
    PCliq_AdapterLivePose adapterLiveWallpaper;
    ArrayList<PCliq_ItemPose> arrayList;
    NestedScrollView nestedScrollView;
//    Spinner sp_fav_type;
    ImageView iv_profile, iv_profile_edit;
    TextView tv_name, tv_email;
    CircularProgressBar progressBar;
    TextView tv_empty, tv_empty_list;
    int totalRecord = 0;
    StaggeredGridLayoutManager grid;
    String errorString = "";
    FloatingActionButton fab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pcliq_fragment_profile, container, false);

        grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        grid.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        PCliq_InterAdListener interAdListener = new PCliq_InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Intent intent;
                if (type.equals(getString(R.string.wallpapers))) {

                    PCliq_Constant.arrayList.clear();
                    PCliq_Constant.arrayList.addAll(arrayList);
                    PCliq_Constant.arrayList.removeAll(Collections.singleton(null));

                    int real_pos = PCliq_Constant.arrayList.indexOf(arrayList.get(position));

                    intent = new Intent(getActivity(), PCliq_PoseDetailsActivity.class);
                    intent.putExtra("pos", real_pos);
                    intent.putExtra("list_type", getString(R.string.favourite));
                    intent.putExtra("page", 1);
                    intent.putExtra("wallType", "");
                    intent.putExtra("color_ids", "");
                } else {

                    PCliq_Constant.arrayListLiveWallpapers.clear();
                    PCliq_Constant.arrayListLiveWallpapers.addAll(arrayList);
                    PCliq_Constant.arrayListLiveWallpapers.removeAll(Collections.singleton(null));

                    int real_pos = PCliq_Constant.arrayListLiveWallpapers.indexOf(arrayList.get(position));

                    intent = new Intent(getActivity(), PCliq_LivePoseDetailsActivity.class);
                    intent.putExtra("pos", real_pos);
                }
                startActivity(intent);
            }
        };

        methods = new PCliq_Methods(getActivity(), interAdListener);
        sharedPref = new PCliq_SharedPref(getActivity());

        arrayList = new ArrayList<>();

        nestedScrollView = rootView.findViewById(R.id.nsv_profile);

//        sp_fav_type = rootView.findViewById(R.id.sp_prof_fav);
        iv_profile = rootView.findViewById(R.id.iv_profile);
        iv_profile_edit = rootView.findViewById(R.id.iv_profile_edit);
        tv_name = rootView.findViewById(R.id.tv_profile_name);
        tv_email = rootView.findViewById(R.id.tv_profile_email);
        progressBar = rootView.findViewById(R.id.pb_wallcat);
        tv_empty = rootView.findViewById(R.id.tv_empty_wallcat);
        tv_empty_list = rootView.findViewById(R.id.tv_empty_list);

//        ArrayList<String> arrayListType = new ArrayList<>();
//        arrayListType.add(getString(R.string.wallpapers));
//        arrayListType.add(getString(R.string.live_wallpapers));
//        SpinAdapter adapterFavType = new SpinAdapter(getActivity(), android.R.layout.simple_spinner_item, arrayListType);
//        sp_fav_type.setAdapter(adapterFavType);

//        sp_fav_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                arrayList.clear();
//
//                if (position == 0) {
//                    if (adapter != null) {
//                        adapter.notifyDataSetChanged();
//                    }
//                } else {
//                    if (adapterLiveWallpaper != null) {
//                        adapterLiveWallpaper.notifyDataSetChanged();
//                    }
//                }
                getWallpaperData();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        if (sharedPref.isLogged() && !sharedPref.getUserId().equals("")) {
            loadUserProfile();

            fab = rootView.findViewById(R.id.fab);
            recyclerView = rootView.findViewById(R.id.rv_wall_by_cat);
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(grid);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int[] firstVisibleItem = grid.findFirstVisibleItemPositions(null);

                    if (fab != null) {
                        if (firstVisibleItem[0] > 6) {
                            fab.show();
                        } else {
                            fab.hide();
                        }
                    }
                }
            });

            if (fab != null) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recyclerView != null) {
                            recyclerView.smoothScrollToPosition(0);
                        }
                    }
                });
            }

            if (iv_profile_edit != null) {
                iv_profile_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PCliq_ProfileEditActivity.class);
                        startActivity(intent);
                    }
                });
            }

        } else {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (nestedScrollView != null) nestedScrollView.setVisibility(View.GONE);
            if (tv_empty != null) {
                tv_empty.setText(getString(R.string.not_log));
                tv_empty.setVisibility(View.VISIBLE);
            }
        }

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

    private void loadUserProfile() {
        if (methods.isNetworkAvailable()) {

            progressBar.setVisibility(View.VISIBLE);

            Call<PCliq_ItemUserList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getProfile(methods.getAPIRequest(PCliq_Constant.URL_PROFILE, 0, "", "", "", "", "", "", "", "", "", "", sharedPref.getUserId(), ""));
            call.enqueue(new Callback<PCliq_ItemUserList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemUserList> call, @NonNull Response<PCliq_ItemUserList> response) {
                    if (response.body() != null && response.body().getArrayListUser() != null && response.body().getArrayListUser().size() > 0) {
                        if (response.body().getArrayListUser().get(0).getSuccess().equals("1")) {

                            sharedPref.setUserName(response.body().getArrayListUser().get(0).getName());
                            sharedPref.setEmail(response.body().getArrayListUser().get(0).getEmail());
                            sharedPref.setUserMobile(response.body().getArrayListUser().get(0).getMobile());
                            sharedPref.setUserImage(response.body().getArrayListUser().get(0).getImage());

                            tv_name.setText(response.body().getArrayListUser().get(0).getName());
                            tv_email.setText(response.body().getArrayListUser().get(0).getEmail());
                            if (!response.body().getArrayListUser().get(0).getImage().equals("")) {
                                Picasso.get()
                                        .load(response.body().getArrayListUser().get(0).getImage())
                                        .placeholder(R.drawable.pcliq_user)
                                        .into(iv_profile);
                            }
                        } else {
//                            setEmpty(false, getString(R.string.invalid_user));
                            methods.logout(getActivity(), sharedPref);
                        }
                    } else {
                        errorString = getString(R.string.server_error);
                        setEmpty();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemUserList> call, @NonNull Throwable t) {
                    call.cancel();
                    errorString = getString(R.string.server_error);
                    setEmpty();
                }
            });
        } else {
            errorString = getString(R.string.internet_not_connected);
            setEmpty();
        }
    }

    private void getWallpaperData() {
        if (methods.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            Call<PCliq_ItemFavList> call;
//            if (sp_fav_type.getSelectedItemPosition() == 0) {
                call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getWallpapersByFav(methods.getAPIRequest(PCliq_Constant.URL_WALLPAPER_BY_FAV, 0, "", "", "", "", "", "", "", "", "", "", new PCliq_SharedPref(getActivity()).getUserId(), "Wallpaper"));
//            } else {
//                call = APIClient.getClient().create(APIInterface.class).getWallpapersByFav(methods.getAPIRequest(Constant.URL_WALLPAPER_BY_FAV, 0, "", "", "", "", "", "", "", "", "", "", new SharedPref(getActivity()).getUserId(), "LiveWallpaper"));
//            }

            call.enqueue(new Callback<PCliq_ItemFavList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemFavList> call, @NonNull Response<PCliq_ItemFavList> response) {
                    if (getActivity() != null) {
                        if (response.body() != null && response.body().getItemFavPost() != null) {
                            if (response.body().getItemFavPost().getArrayListWallpapers().size() == 0 &&
                                    response.body().getItemFavPost().getArrayListLiveWallpapers().size() == 0) {
                                setEmptyList();
                            } else {
//                                if (sp_fav_type.getSelectedItemPosition() == 0) {
                                    totalRecord = response.body().getItemFavPost().getArrayListWallpapers().size();
                                    for (int i = 0; i < response.body().getItemFavPost().getArrayListWallpapers().size(); i++) {

                                        arrayList.add(response.body().getItemFavPost().getArrayListWallpapers().get(i));

                                        int abc = arrayList.lastIndexOf(null);
                                        if (((arrayList.size() - (abc + 1)) % new PCliq_PreferenceClass(getContext()).getInt("rv_count", 4) == 0) ) {
                                            arrayList.add(null);
                                        }

//                                        if (PCliq_Constant.isNativeAd) {
//                                            int abc = arrayList.lastIndexOf(null);
//                                            if (((arrayList.size() - (abc + 1)) % PCliq_Constant.nativeAdShow == 0) && (response.body().getItemFavPost().getArrayListWallpapers().size() - 1 != i || totalRecord != response.body().getItemFavPost().getArrayListWallpapers().size())) {
//                                                arrayList.add(null);
//                                            }
//                                        }
                                    }
//                                } else {
//                                    totalRecord = response.body().getItemFavPost().getArrayListLiveWallpapers().size();
//                                    for (int i = 0; i < response.body().getItemFavPost().getArrayListLiveWallpapers().size(); i++) {
//
//                                        arrayList.add(response.body().getItemFavPost().getArrayListLiveWallpapers().get(i));
//
//                                        if (Constant.isNativeAd) {
//                                            int abc = arrayList.lastIndexOf(null);
//                                            if (((arrayList.size() - (abc + 1)) % Constant.nativeAdShow == 0) && (response.body().getItemFavPost().getArrayListLiveWallpapers().size() - 1 != i || totalRecord != response.body().getItemFavPost().getArrayListLiveWallpapers().size())) {
//                                                arrayList.add(null);
//                                            }
//                                        }
//                                    }
//                                }

                                setAdapter();
                            }
                        } else {
                            setEmptyList();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
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
//        if (sp_fav_type.getSelectedItemPosition() == 0) {
            adapterLiveWallpaper = null;
            adapter = new PCliq_AdapterPose(getActivity(), arrayList, new PCliq_RecyclerViewClickListener() {
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
//        } else {
//            adapter = null;
//            adapterLiveWallpaper = new AdapterLiveWallpaper(getActivity(), arrayList, new RecyclerViewClickListener() {
//                @Override
//                public void onClick(int position) {
//                    methods.showInter(position, getString(R.string.live_wallpapers));
//                }
//            });
//            AnimationAdapter adapterAnim = new AlphaInAnimationAdapter(adapterLiveWallpaper);
//            adapterAnim.setFirstOnly(true);
//            adapterAnim.setDuration(500);
//            adapterAnim.setInterpolator(new OvershootInterpolator(.9f));
//            recyclerView.setAdapter(adapterAnim);
//        }
        setEmptyList();
    }

    private void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() == 0) {
            tv_empty.setText(errorString);
            tv_empty.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
        } else {
            nestedScrollView.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
    }

    private void setEmptyList() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() == 0) {
            tv_empty_list.setText(getString(R.string.no_data_found));
            tv_empty_list.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tv_empty_list.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        if (tv_name != null && PCliq_Constant.isUpdate) {
            PCliq_Constant.isUpdate = false;
            tv_name.setText(sharedPref.getUserName());
            tv_email.setText(sharedPref.getEmail());
            if (!sharedPref.getUserImage().equals("")) {
                Picasso.get()
                        .load(sharedPref.getUserImage())
                        .placeholder(R.drawable.pcliq_user)
                        .into(iv_profile);
            }
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}