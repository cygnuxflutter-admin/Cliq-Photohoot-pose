package com.photo.pose.photoshoot.cliq.PCliq_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;

import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_FavouriteActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_MainActivity;
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
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPoseRecent;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPoseFeatured;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPoseRated;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPoseDownloaded;
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
import android.widget.ProgressBar;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_FragmentProfile extends Fragment {

    PCliq_Methods methods;
    PCliq_SharedPref sharedPref;
    NestedScrollView nestedScrollView;
    ImageView iv_profile, iv_profile_edit;
    TextView tv_name, tv_email;
    CircularProgressBar progressBar;
    TextView tv_empty;
    String errorString = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pcliq_fragment_profile, container, false);

        methods = new PCliq_Methods(getActivity());
        sharedPref = new PCliq_SharedPref(getActivity());

        nestedScrollView = rootView.findViewById(R.id.nsv_profile);

        TextView tv_app_version = rootView.findViewById(R.id.tv_app_version);
        if (tv_app_version != null) {
            tv_app_version.setText("Curated Photoshoot Inspiration • v" + com.photo.pose.photoshoot.cliq.BuildConfig.VERSION_NAME);
        }

        iv_profile = rootView.findViewById(R.id.iv_profile);
        iv_profile_edit = rootView.findViewById(R.id.iv_profile_edit);
        tv_name = rootView.findViewById(R.id.tv_profile_name);
        tv_email = rootView.findViewById(R.id.tv_profile_email);
        progressBar = rootView.findViewById(R.id.pb_wallcat);
        tv_empty = rootView.findViewById(R.id.tv_empty_wallcat);
        
        TextView tv_header_activity = rootView.findViewById(R.id.tv_header_activity);
        com.google.android.material.card.MaterialCardView card_favourite_poses = rootView.findViewById(R.id.card_favourite_poses);
        RelativeLayout rl_favourite = rootView.findViewById(R.id.rl_favourite);

        com.google.android.material.card.MaterialCardView card_logout = rootView.findViewById(R.id.card_logout);
        RelativeLayout rl_logout = rootView.findViewById(R.id.rl_logout);
        RelativeLayout rl_recent = rootView.findViewById(R.id.rl_recent);
        RelativeLayout rl_featured = rootView.findViewById(R.id.rl_featured);
        RelativeLayout rl_rated = rootView.findViewById(R.id.rl_rated);
        RelativeLayout rl_downloaded = rootView.findViewById(R.id.rl_downloaded);

        // Always show the header and favourite poses card so guest users know the feature exists
        tv_header_activity.setVisibility(View.VISIBLE);
        card_favourite_poses.setVisibility(View.VISIBLE);

        rl_favourite.setOnClickListener(v -> {
            if (sharedPref.isLogged() && !sharedPref.getUserId().equals("")) {
                Intent intent = new Intent(getActivity(), PCliq_FavouriteActivity.class);
                startActivity(intent);
            } else {
                methods.clickLogin();
            }
        });

        rl_recent.setOnClickListener(v -> {
            if (getActivity() instanceof PCliq_MainActivity) {
                ((PCliq_MainActivity) getActivity()).loadFrag(new PCliq_FragmentPoseRecent(), getString(R.string.recently_viewed), getActivity().getSupportFragmentManager());
            }
        });

        rl_featured.setOnClickListener(v -> {
            if (getActivity() instanceof PCliq_MainActivity) {
                ((PCliq_MainActivity) getActivity()).loadFrag(new PCliq_FragmentPoseFeatured(), getString(R.string.featured), getActivity().getSupportFragmentManager());
            }
        });

        rl_rated.setOnClickListener(v -> {
            if (getActivity() instanceof PCliq_MainActivity) {
                ((PCliq_MainActivity) getActivity()).loadFrag(new PCliq_FragmentPoseRated(), getString(R.string.rated), getActivity().getSupportFragmentManager());
            }
        });

        rl_downloaded.setOnClickListener(v -> {
            if (getActivity() instanceof PCliq_MainActivity) {
                ((PCliq_MainActivity) getActivity()).loadFrag(new PCliq_FragmentPoseDownloaded(), getString(R.string.most_downloaded), getActivity().getSupportFragmentManager());
            }
        });

        rl_logout.setOnClickListener(v -> {
            methods.clickLogin();
        });

        if (sharedPref.isLogged() && !sharedPref.getUserId().equals("")) {
            loadUserProfile();

            card_logout.setVisibility(View.VISIBLE);

            if (iv_profile_edit != null) {
                iv_profile_edit.setImageResource(R.drawable.pcliq_ic_edit_pencil);
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
            if (nestedScrollView != null) nestedScrollView.setVisibility(View.VISIBLE);
            
            card_logout.setVisibility(View.GONE);
            
            if (tv_name != null) tv_name.setText("Guest User");
            if (tv_email != null) tv_email.setText("Login to see your favourite poses");
            if (iv_profile != null) iv_profile.setImageResource(R.drawable.pcliq_ic_profile_placeholder);

            if (iv_profile_edit != null) {
                iv_profile_edit.setImageResource(R.mipmap.pcliq_login);
                iv_profile_edit.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_LoginActivity.class);
                    intent.putExtra("from", "app");
                    startActivity(intent);
                });
            }
        }

                setupSettings(rootView);
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
                            String userImg = response.body().getArrayListUser().get(0).getImage();
                            if (!userImg.equals("") && !userImg.contains("pcliq_user") && !userImg.contains("default") && !userImg.contains("placeholder")) {
                                com.squareup.picasso.Picasso.get()
                                        .load(userImg)
                                        .placeholder(R.drawable.pcliq_ic_profile_placeholder)
                                        .into(iv_profile);
                            } else {
                                iv_profile.setImageResource(R.drawable.pcliq_ic_profile_placeholder);
                            }
                            progressBar.setVisibility(View.GONE);
                        } else {
//                            setEmpty(false, getString(R.string.invalid_user));
                            methods.logout(getActivity(), sharedPref);
                            progressBar.setVisibility(View.GONE);
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

    private void setEmpty() {
        progressBar.setVisibility(View.GONE);
        tv_empty.setText(errorString);
        tv_empty.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        if (tv_name != null && PCliq_Constant.isUpdate) {
            PCliq_Constant.isUpdate = false;
            tv_name.setText(sharedPref.getUserName());
            tv_email.setText(sharedPref.getEmail());
            String userImg = sharedPref.getUserImage();
            if (!userImg.equals("") && !userImg.contains("pcliq_user") && !userImg.contains("default") && !userImg.contains("placeholder")) {
                com.squareup.picasso.Picasso.get()
                        .load(userImg)
                        .placeholder(R.drawable.pcliq_ic_profile_placeholder)
                        .into(iv_profile);
            } else {
                iv_profile.setImageResource(R.drawable.pcliq_ic_profile_placeholder);
            }
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupSettings(View rootView) {
        LinearLayout ll_consent = rootView.findViewById(R.id.ll_consent);
        androidx.recyclerview.widget.RecyclerView rv_pages = rootView.findViewById(R.id.rv_pages);
        androidx.appcompat.widget.SwitchCompat switch_noti = rootView.findViewById(R.id.switch_noti);
        androidx.appcompat.widget.SwitchCompat switch_consent = rootView.findViewById(R.id.switch_consent);
        TextView tv_rateapp = rootView.findViewById(R.id.tv_rateapp);
        TextView tv_moreapp = rootView.findViewById(R.id.tv_moreapp);
        TextView tv_cachesize = rootView.findViewById(R.id.tv_cachesize);
        TextView tv_shareapp = rootView.findViewById(R.id.tv_shareapp);
        TextView tv_about = rootView.findViewById(R.id.tv_about);
        TextView tv_privacy = rootView.findViewById(R.id.tv_privacy_policy);
        LinearLayout ll_clearcache = rootView.findViewById(R.id.ll_cache);
        View view_moreapp = rootView.findViewById(R.id.view_moreapp);
        View rl_moreapp = rootView.findViewById(R.id.rl_moreapp);

        if (getString(R.string.play_more_apps).isEmpty()) {
            if (view_moreapp != null) view_moreapp.setVisibility(View.GONE);
            if (rl_moreapp != null) rl_moreapp.setVisibility(View.GONE);
            if (tv_moreapp != null) tv_moreapp.setVisibility(View.GONE);
        }

        if (switch_noti != null) {
            switch_noti.setChecked(sharedPref.getIsNotification());
            switch_noti.setOnCheckedChangeListener((buttonView, isChecked) -> {
                com.onesignal.OneSignal.disablePush(!isChecked);
                sharedPref.setIsNotification(isChecked);
            });
        }

        if (switch_consent != null) {
            com.google.ads.consent.ConsentStatus consentStatus = com.google.ads.consent.ConsentInformation.getInstance(getActivity()).getConsentStatus();
            switch_consent.setChecked(consentStatus == com.google.ads.consent.ConsentStatus.PERSONALIZED);
            switch_consent.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    com.google.ads.consent.ConsentInformation.getInstance(getActivity()).setConsentStatus(com.google.ads.consent.ConsentStatus.PERSONALIZED);
                } else {
                    com.google.ads.consent.ConsentInformation.getInstance(getActivity()).setConsentStatus(com.google.ads.consent.ConsentStatus.NON_PERSONALIZED);
                }
            });
        }

        if (tv_rateapp != null) {
            tv_rateapp.setOnClickListener(v -> {
                final String appName = getActivity().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                }
            });
        }

        if (tv_moreapp != null) {
            tv_moreapp.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps)))));
        }

        if (tv_about != null) {
            tv_about.setOnClickListener(v -> startActivity(new Intent(getActivity(), com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_AboutActivity.class)));
        }

        if (tv_privacy != null) {
            tv_privacy.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cygnux.in/application-privacy-policy/cliq-photo-policy.html"));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Cannot open link", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (tv_shareapp != null) {
            tv_shareapp.setOnClickListener(v -> {
                Intent ishare = new Intent(Intent.ACTION_SEND);
                ishare.setType("text/plain");
                ishare.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                startActivity(ishare);
            });
        }

        if (ll_clearcache != null && tv_cachesize != null) {
            tv_cachesize.setText(android.text.format.Formatter.formatFileSize(getActivity(), getCacheFolderSize(getActivity().getCacheDir())));
            ll_clearcache.setOnClickListener(v -> {
                com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.clearing_cache));
                progressDialog.show();
                new android.os.Handler().postDelayed(() -> {
                    clearCache();
                    tv_cachesize.setText("0.00 MB");
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show();
                }, 1000);
            });
        }

        if (rv_pages != null) {
            rv_pages.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getActivity()));
            rv_pages.setAdapter(new com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterPages(getActivity(), com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant.arrayListPages));
        }
    }

    private void clearCache() {
        try {
            java.io.File dir = getActivity().getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(java.io.File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new java.io.File(dir, child));
                if (!success) return false;
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private long getCacheFolderSize(java.io.File dir) {
        long size = 0;
        if (dir != null && dir.isDirectory()) {
            for (java.io.File file : dir.listFiles()) {
                if (file.isFile()) size += file.length();
                else size += getCacheFolderSize(file);
            }
        } else if (dir != null && dir.isFile()) {
            size += dir.length();
        }
        return size;
    }

}

