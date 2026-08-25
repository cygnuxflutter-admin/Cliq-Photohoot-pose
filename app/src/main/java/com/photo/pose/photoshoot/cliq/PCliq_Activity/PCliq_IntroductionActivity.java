package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_MyViewPagerAdapter;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;
import com.photo.pose.photoshoot.cliq.R;


public class PCliq_IntroductionActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int PERMISSION_EXTERNAL = 1118481;
    private int[] mLayoutList;

    public TextView PC_tvNext, PC_tv_skip;
    ImageView PC_iv_back;
    private ViewPager vpgIntroActivityIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_introduction);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (PCliq_SharedPref.getCheckPermission(this)) {
            PCliq_MyViewPagerAdapter.launchMainScreen(this);
        }
        this.vpgIntroActivityIntro = (ViewPager) findViewById(R.id.vpg_intro_activity__intro);
        this.PC_tvNext = (TextView) findViewById(R.id.tv_next);
        this.PC_iv_back = (ImageView) findViewById(R.id.iv_back);
        this.PC_tv_skip = (TextView) findViewById(R.id.tv_skip);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(1280);
        }
        int[] iArr = {R.layout.pcliq_intro_slide_1, R.layout.pcliq_intro_slide_2, R.layout.pcliq_intro_slide_3, R.layout.pcliq_intro_slide_4, R.layout.pcliq_intro_slide_5};
        this.mLayoutList = iArr;
        this.vpgIntroActivityIntro.setAdapter(new PCliq_MyViewPagerAdapter(this, iArr));
        this.vpgIntroActivityIntro.setOffscreenPageLimit(3);
        initDots();
        updateDots(0);

        this.vpgIntroActivityIntro.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageSelected(int i) {
                updateDots(i);
                if (i == 0) {
                    PCliq_IntroductionActivity.this.PC_tvNext.setText("Next");
                    PCliq_IntroductionActivity.this.PC_tvNext.setVisibility(View.VISIBLE);
                    PCliq_IntroductionActivity.this.PC_tv_skip.setVisibility(View.VISIBLE);
                    PCliq_IntroductionActivity.this.PC_iv_back.setVisibility(View.GONE);
                } else if (i == 4) {
                    PCliq_IntroductionActivity.this.PC_tvNext.setText("Get Started");
                    PCliq_IntroductionActivity.this.PC_tvNext.setVisibility(View.VISIBLE);
                    PCliq_IntroductionActivity.this.PC_tv_skip.setVisibility(View.GONE);
                    PCliq_IntroductionActivity.this.PC_iv_back.setVisibility(View.VISIBLE);
                } else {
                    PCliq_IntroductionActivity.this.PC_tvNext.setText("Next");
                    PCliq_IntroductionActivity.this.PC_tvNext.setVisibility(View.VISIBLE);
                    PCliq_IntroductionActivity.this.PC_tv_skip.setVisibility(View.VISIBLE);
                    PCliq_IntroductionActivity.this.PC_iv_back.setVisibility(View.VISIBLE);
                }
            }

            public void onPageScrolled(int i, float f, int i2) {
            }
        });
        this.PC_tvNext.setOnClickListener(this);
        this.PC_iv_back.setOnClickListener(this);
        this.PC_tv_skip.setOnClickListener(this);
    }

    private View[] dots;

    private void initDots() {
        dots = new View[5];
        dots[0] = findViewById(R.id.dot_0);
        dots[1] = findViewById(R.id.dot_1);
        dots[2] = findViewById(R.id.dot_2);
        dots[3] = findViewById(R.id.dot_3);
        dots[4] = findViewById(R.id.dot_4);
    }

    private void updateDots(int position) {
        if (dots == null) return;
        int activeWidth = (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics());
        int inactiveWidth = (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
        int height = (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());

        for (int i = 0; i < dots.length; i++) {
            if (dots[i] != null) {
                if (i == position) {
                    dots[i].setBackgroundResource(R.drawable.pcliq_dot_active);
                    dots[i].getLayoutParams().width = activeWidth;
                    dots[i].getLayoutParams().height = height;
                } else {
                    dots[i].setBackgroundResource(R.drawable.pcliq_dot_inactive);
                    dots[i].getLayoutParams().width = inactiveWidth;
                    dots[i].getLayoutParams().height = height;
                }
                dots[i].requestLayout();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1118481 && Build.VERSION.SDK_INT >= 30) {
            if (Environment.isExternalStorageManager()) {
                PCliq_MyViewPagerAdapter.launchMainScreen(this);
            } else {
                finish();
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 1118481 && iArr.length > 0 && strArr[0].equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
            if (iArr[0] == 0) {
                PCliq_MyViewPagerAdapter.launchMainScreen(this);
            } else {
//                Reader_PermissionUtils.isPermission(PERMISSION_EXTERNAL, this);
            }
        }
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    private void openSettingsDevice() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), (String) null));
        startActivityForResult(intent, 101);
    }

    public void onClick(View view) {
        if (view == this.PC_tvNext) {
            int currentItem = this.vpgIntroActivityIntro.getCurrentItem();
            if (currentItem == 0) {
                this.vpgIntroActivityIntro.setCurrentItem(1);
            } else if (currentItem == 1) {
                this.vpgIntroActivityIntro.setCurrentItem(2);
            } else if (currentItem == 2) {
                this.vpgIntroActivityIntro.setCurrentItem(3);
            } else if (currentItem == 3) {
                this.vpgIntroActivityIntro.setCurrentItem(4);
            } else if (currentItem == 4) {
                startActivity(new Intent(PCliq_IntroductionActivity.this, PCliq_WelcomeActivity.class));
            }
        }
        if (view == this.PC_iv_back) {
            int currentItem = this.vpgIntroActivityIntro.getCurrentItem();
            if (currentItem == 1) {
                this.vpgIntroActivityIntro.setCurrentItem(0);
            } else if (currentItem == 2) {
                this.vpgIntroActivityIntro.setCurrentItem(1);
            } else if (currentItem == 3) {
                this.vpgIntroActivityIntro.setCurrentItem(2);
            } else if (currentItem == 4) {
                this.vpgIntroActivityIntro.setCurrentItem(3);
            }
        }
        if (view == this.PC_tv_skip) {
            this.vpgIntroActivityIntro.setCurrentItem(4);
        }
    }
}
