package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_LoadAds;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPoseDownloaded;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPoseLatest;
import com.photo.pose.photoshoot.cliq.PCliq_fragments.PCliq_FragmentPosePopular;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_NetworkUtils;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;

public class PCliq_PopularPoseActivity extends AppCompatActivity {
    private FragmentManager fm = getSupportFragmentManager();

    String cate_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_popular_pose);

        cate_name = getIntent().getStringExtra("cate_name");

        Toolbar toolbar = findViewById(R.id.toolbar_wall_by_cat);
        toolbar.setTitle(cate_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RelativeLayout rl_ad = this.findViewById(R.id.rl_ad);
        if (PCliq_NetworkUtils.isNetworkAvailable(this)) {
            if (new PCliq_PreferenceClass(PCliq_PopularPoseActivity.this).getInt("BannerAdStatus") == 1) {
                PCliq_LoadAds.loadAdmobBannerAd(this, rl_ad);
            } else {
                rl_ad.setVisibility(View.GONE);
            }
        }

        if (cate_name.equals("Popular")) {
            loadFrag(new PCliq_FragmentPosePopular(), getString(R.string.popular));
        } else if (cate_name.equals("Download")) {
            loadFrag(new PCliq_FragmentPoseDownloaded(), getString(R.string.download));
        } else if (cate_name.equals("Latest")) {
            loadFrag(new PCliq_FragmentPoseLatest(), getString(R.string.latest));
        }
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
    }
}