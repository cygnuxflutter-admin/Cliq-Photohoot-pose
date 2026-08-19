package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

public class PCliq_StatusBarView extends View {
    private int mStatusBarHeight;

    public PCliq_StatusBarView(Context context) {
        this(context, null);

    }

    public PCliq_StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStatusBarHeight = insets.getSystemWindowInsetTop();
            return insets.consumeSystemWindowInsets();
        }
        return insets;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mStatusBarHeight);
    }
}
