package com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion.PCliq_shape;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion.PCliq_target.PCliq_Target;

public class PCliq_RectangleShape implements PCliq_Shape {

    private boolean fullWidth = false;

    private int width = 0;
    private int height = 0;
    private boolean adjustToTarget = true;

    private Rect rect;
    private int padding;

    public PCliq_RectangleShape(int width, int height) {
        this.width = width;
        this.height = height;
        init();
    }

    public PCliq_RectangleShape(Rect bounds) {
        this(bounds, false);
    }

    public PCliq_RectangleShape(Rect bounds, boolean fullWidth) {
        this.fullWidth = fullWidth;
        height = bounds.height();
        if (fullWidth)
            width = Integer.MAX_VALUE;
        else width = bounds.width();
        init();
    }

    public boolean isAdjustToTarget() {
        return adjustToTarget;
    }

    public void setAdjustToTarget(boolean adjustToTarget) {
        this.adjustToTarget = adjustToTarget;
    }

    private void init() {
        rect = new Rect(-width / 2, -height / 2, width / 2, height / 2);
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int x, int y) {
        if (!rect.isEmpty()) {
            canvas.drawRect(
                    rect.left + x - padding,
                    rect.top + y - padding,
                    rect.right + x + padding,
                    rect.bottom + y + padding,
                    paint
            );
        }
    }

    @Override
    public void updateTarget(PCliq_Target target) {
        if (adjustToTarget) {
            Rect bounds = target.getBounds();
            height = bounds.height();
            if (fullWidth)
                width = Integer.MAX_VALUE;
            else width = bounds.width();
            init();
        }
    }

    @Override
    public int getTotalRadius() {
        return (height / 2) + padding;
    }

    @Override
    public void setPadding(int padding) {
        this.padding = padding;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}