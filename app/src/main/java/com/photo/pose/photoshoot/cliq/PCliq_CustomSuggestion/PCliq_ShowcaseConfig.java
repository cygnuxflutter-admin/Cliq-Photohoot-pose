package com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion;

import android.graphics.Color;
import android.graphics.Typeface;

import com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion.PCliq_shape.PCliq_Shape;


public class PCliq_ShowcaseConfig {

    public static final String DEFAULT_MASK_COLOUR = "#dd335075";

    private long mDelay = -1;
    private int mMaskColour;
    private Typeface mDismissTextStyle;

    private int mContentTextColor;
    private int mDismissTextColor;
    private long mFadeDuration = -1;
    private PCliq_Shape mShape = null;
    private int mShapePadding = -1;
    private Boolean renderOverNav;

    public PCliq_ShowcaseConfig() {
        mMaskColour = Color.parseColor(PCliq_ShowcaseConfig.DEFAULT_MASK_COLOUR);
        mContentTextColor = Color.parseColor("#ffffff");
        mDismissTextColor = Color.parseColor("#ffffff");
    }

    public long getDelay() {
        return mDelay;
    }

    public void setDelay(long delay) {
        this.mDelay = delay;
    }

    public int getMaskColor() {
        return mMaskColour;
    }

    public void setMaskColor(int maskColor) {
        mMaskColour = maskColor;
    }

    public int getContentTextColor() {
        return mContentTextColor;
    }

    public void setContentTextColor(int mContentTextColor) {
        this.mContentTextColor = mContentTextColor;
    }

    public int getDismissTextColor() {
        return mDismissTextColor;
    }

    public void setDismissTextColor(int dismissTextColor) {
        this.mDismissTextColor = dismissTextColor;
    }

    public Typeface getDismissTextStyle() {
        return mDismissTextStyle;
    }

    public void setDismissTextStyle(Typeface dismissTextStyle) {
        this.mDismissTextStyle = dismissTextStyle;
    }

    public long getFadeDuration() {
        return mFadeDuration;
    }

    public void setFadeDuration(long fadeDuration) {
        this.mFadeDuration = fadeDuration;
    }

    public PCliq_Shape getShape() {
        return mShape;
    }

    public void setShape(PCliq_Shape shape) {
        this.mShape = shape;
    }

    public void setShapePadding(int padding) {
        this.mShapePadding = padding;
    }

    public int getShapePadding() {
        return mShapePadding;
    }

    public Boolean getRenderOverNavigationBar() {
        return renderOverNav;
    }

    public void setRenderOverNavigationBar(boolean renderOverNav) {
        this.renderOverNav = renderOverNav;
    }
}
