package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class PCliq_SquareImageView extends AppCompatImageView {

  public PCliq_SquareImageView(Context context) {
    super(context);
  }

  public PCliq_SquareImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PCliq_SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @SuppressWarnings("SuspiciousNameCombination")
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
  }
}
