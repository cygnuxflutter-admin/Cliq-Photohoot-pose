package com.photo.pose.photoshoot.cliq.PCliq_fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_LoginActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_MainActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_SettingActivity;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class PCliq_FragmentDashboard extends Fragment {

    private FragmentManager fm;
    private ImageView ivHome, ivPoses, ivFavorites, ivProfile;
    private View dotHome, dotPoses, dotFavorites, dotProfile;
    private int currentDockIndex = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pcliq_fragment_dashboard, container, false);

        fm = getParentFragmentManager();

        View topNavBar = rootView.findViewById(R.id.ll_top_nav_bar);
        if (topNavBar != null) {
            ViewCompat.setOnApplyWindowInsetsListener(topNavBar, (v, insets) -> {
                int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                v.setPadding(v.getPaddingLeft(), statusBarHeight + (int) (4 * getResources().getDisplayMetrics().density), v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }

        View capsuleDock = rootView.findViewById(R.id.ll_capsule_dock);
        if (capsuleDock != null) {
            ViewCompat.setOnApplyWindowInsetsListener(capsuleDock, (v, insets) -> {
                int navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                lp.bottomMargin = navBarHeight + (int) (16 * getResources().getDisplayMetrics().density);
                v.setLayoutParams(lp);
                return insets;
            });
        }

        ivHome = rootView.findViewById(R.id.iv_dock_home);
        ivPoses = rootView.findViewById(R.id.iv_dock_poses);
        ivFavorites = rootView.findViewById(R.id.iv_dock_favorites);
        ivProfile = rootView.findViewById(R.id.iv_dock_profile);

        dotHome = rootView.findViewById(R.id.dot_dock_home);
        dotPoses = rootView.findViewById(R.id.dot_dock_poses);
        dotFavorites = rootView.findViewById(R.id.dot_dock_favorites);
        dotProfile = rootView.findViewById(R.id.dot_dock_profile);

        View itemHome = rootView.findViewById(R.id.dock_item_home);
        View itemPoses = rootView.findViewById(R.id.dock_item_poses);
        View itemFavorites = rootView.findViewById(R.id.dock_item_favorites);
        View itemProfile = rootView.findViewById(R.id.dock_item_profile);

        if (itemHome != null) itemHome.setOnClickListener(v -> selectDockTab(0));
        if (itemPoses != null) itemPoses.setOnClickListener(v -> selectDockTab(1));
        if (itemFavorites != null) itemFavorites.setOnClickListener(v -> selectDockTab(2));
        if (itemProfile != null) itemProfile.setOnClickListener(v -> selectDockTab(3));

        ImageView btnSettings = rootView.findViewById(R.id.btn_action_settings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), PCliq_SettingActivity.class);
                startActivity(intent);
            });
        }

        selectDockTab(0);

        return rootView;
    }

    public void selectDockTab(int index) {
        currentDockIndex = index;

        int gold = Color.parseColor("#C19543");
        int inactive = Color.parseColor("#8A7B70");

        if (ivHome != null) ivHome.setImageTintList(ColorStateList.valueOf(index == 0 ? gold : inactive));
        if (dotHome != null) dotHome.setVisibility(index == 0 ? View.VISIBLE : View.INVISIBLE);

        if (ivPoses != null) ivPoses.setImageTintList(ColorStateList.valueOf(index == 1 ? gold : inactive));
        if (dotPoses != null) dotPoses.setVisibility(index == 1 ? View.VISIBLE : View.INVISIBLE);

        if (ivFavorites != null) ivFavorites.setImageTintList(ColorStateList.valueOf(index == 2 ? gold : inactive));
        if (dotFavorites != null) dotFavorites.setVisibility(index == 2 ? View.VISIBLE : View.INVISIBLE);

        if (ivProfile != null) ivProfile.setImageTintList(ColorStateList.valueOf(index == 3 ? gold : inactive));
        if (dotProfile != null) dotProfile.setVisibility(index == 3 ? View.VISIBLE : View.INVISIBLE);

        switch (index) {
            case 0:
                loadFrag(new PCliq_FragmentHome(), getString(R.string.home));
                break;
            case 1:
                loadFrag(new PCliq_FragmentCategories(), getString(R.string.categories));
                break;
            case 2:
                loadFrag(new PCliq_FragmentPosePopular(), getString(R.string.popular));
                break;
            case 3:
                if (getActivity() != null) {
                    if (new PCliq_SharedPref(getActivity()).isLogged()) {
                        loadFrag(new PCliq_FragmentProfile(), getString(R.string.profile));
                    } else {
                        Intent intent = new Intent(getActivity(), PCliq_LoginActivity.class);
                        intent.putExtra("from", "app");
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    public void loadFrag(Fragment f1, String name) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (name.equals(getString(R.string.search))) {
            ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
            ft.add(R.id.fragment_dash, f1, name);
            ft.addToBackStack(name);
        } else {
            ft.replace(R.id.fragment_dash, f1, name);
        }
        ft.commitAllowingStateLoss();

        if (getActivity() instanceof PCliq_MainActivity && ((PCliq_MainActivity) getActivity()).getSupportActionBar() != null) {
            ((PCliq_MainActivity) getActivity()).getSupportActionBar().setTitle(name);
        }
    }
}