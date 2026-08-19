package com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion.PCliq_shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion.PCliq_target.PCliq_Target;

/**
 * Circular shape for target.
 */
public class PCliq_CircleShape implements PCliq_Shape {

    private int radius = 200;
    private boolean adjustToTarget = true;
    private int padding;

    public PCliq_CircleShape() {
    }

    public PCliq_CircleShape(int radius) {
        this.radius = radius;
    }

    public PCliq_CircleShape(Rect bounds) {
        this(getPreferredRadius(bounds));
    }

    public PCliq_CircleShape(PCliq_Target target) {
        this(target.getBounds());
    }

    public void setAdjustToTarget(boolean adjustToTarget) {
        this.adjustToTarget = adjustToTarget;
    }

    public boolean isAdjustToTarget() {
        return adjustToTarget;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int x, int y) {
        if (radius > 0) {
            canvas.drawCircle(x, y, radius + padding, paint);
        }
    }

    @Override
    public void updateTarget(PCliq_Target target) {
        if (adjustToTarget)
            radius = getPreferredRadius(target.getBounds());
    }

    @Override
    public int getTotalRadius() {
        return radius + padding;
    }

    @Override
    public void setPadding(int padding) {
        this.padding = padding;
    }

    @Override
    public int getWidth() {
        return radius * 2;
    }

    @Override
    public int getHeight() {
        return radius * 2;
    }

    public static int getPreferredRadius(Rect bounds) {
        return Math.max(bounds.width(), bounds.height()) / 2;
    }
}
