package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;


public class PCliq_MyViewPagerAdapter extends PagerAdapter {
    private TextView btnIntroGet;

    public Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private int[] mLayoutList;

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public PCliq_MyViewPagerAdapter(Activity activity, int[] iArr) {
        this.mActivity = activity;
        this.mLayoutList = iArr;
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) this.mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLayoutInflater = layoutInflater;
        View inflate = layoutInflater.inflate(this.mLayoutList[i], viewGroup, false);
        viewGroup.addView(inflate);
        return inflate;
    }

    public static void launchMainScreen(Activity activity) {
        PCliq_SharedPref.setCheckPermission(activity, true);
    }

    public int getCount() {
        return this.mLayoutList.length;
    }

    public void destroyItem(View view, int i, Object obj) {
        ((ViewPager) view).removeView((View) obj);
    }
}
