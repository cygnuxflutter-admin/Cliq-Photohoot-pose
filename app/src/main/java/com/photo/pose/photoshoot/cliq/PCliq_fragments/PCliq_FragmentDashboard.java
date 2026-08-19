package com.photo.pose.photoshoot.cliq.PCliq_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_LoginActivity;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_MainActivity;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class PCliq_FragmentDashboard extends Fragment {

    public static BottomNavigationView bottomNavigationMenu;
    private FragmentManager fm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pcliq_fragment_dashboard, container, false);

        fm = getParentFragmentManager();

        bottomNavigationMenu = rootView.findViewById(R.id.navigation_bottom);
        bottomNavigationMenu.setOnItemSelectedListener(onItemSelectedListener);

//        bottomNavigationMenu.getMenu().findItem(R.id.nav_bottom_live_wallpapers).setVisible(Constant.isLiveWallpaperEnabled);

//        loadFrag(new FragmentWallLatest(), getString(R.string.home));
        loadFrag(new PCliq_FragmentHome(), getString(R.string.home));

        return rootView;
    }

    NavigationBarView.OnItemSelectedListener onItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_bottom_latest) {
                loadFrag(new PCliq_FragmentHome(), getString(R.string.home));
                return true;
            } else if (itemId == R.id.nav_bottom_popular) {
                loadFrag(new PCliq_FragmentPosePopular(), getString(R.string.popular));
                return true;
            } /*else if (itemId == R.id.nav_bottom_live_wallpapers) {
                loadFrag(new FragmentLiveWallpapers(), getString(R.string.live_wallpapers));
                return true;
            } */else if (itemId == R.id.nav_bottom_cat) {
                loadFrag(new PCliq_FragmentCategories(), getString(R.string.categories));
                return true;
            } else if (itemId == R.id.nav_bottom_profile) {
                if(new PCliq_SharedPref(getActivity()).isLogged()) {
                    loadFrag(new PCliq_FragmentProfile(), getString(R.string.profile));
                    return true;
                } else {
                    Intent intent = new Intent(getActivity(), PCliq_LoginActivity.class);
                    intent.putExtra("from", "app");
                    startActivity(intent);
                    return false;
                }
            }
            return false;
        }
    };

    public void loadFrag(Fragment f1, String name) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (name.equals(getString(R.string.search))) {
            ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
            ft.add(R.id.fragment_dash, f1, name);
            ft.addToBackStack(name);
        } else {
            ft.replace(R.id.fragment_dash, f1, name);
        }
        ft.commitAllowingStateLoss();

        ((PCliq_MainActivity) getActivity()).getSupportActionBar().setTitle(name);
    }
}