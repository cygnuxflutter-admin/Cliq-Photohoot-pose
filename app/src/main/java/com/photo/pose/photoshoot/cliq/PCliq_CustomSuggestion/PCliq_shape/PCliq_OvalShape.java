package com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion.PCliq_shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion.PCliq_target.PCliq_Target;

public class PCliq_OvalShape implements PCliq_Shape {
    private int radius;
    private boolean adjustToTarget;
    private int padding;

    public PCliq_OvalShape() {
        this.radius = 200;
        this.adjustToTarget = true;
    }

    public PCliq_OvalShape(int radius) {
        this.radius = 200;
        this.adjustToTarget = true;
        this.radius = radius;
    }

    public PCliq_OvalShape(Rect bounds) {
        this(getPreferredRadius(bounds));
    }

    public PCliq_OvalShape(PCliq_Target target) {
        this(target.getBounds());
    }

    public static int getPreferredRadius(Rect bounds) {
        return Math.max(bounds.width(), bounds.height()) / 2;
    }

    public boolean isAdjustToTarget() {
        return this.adjustToTarget;
    }

    public void setAdjustToTarget(boolean adjustToTarget) {
        this.adjustToTarget = adjustToTarget;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void draw(Canvas canvas, Paint paint, int x, int y) {
        if (this.radius > 0) {
            float rad = (float) (this.radius + padding);
            RectF rectF = new RectF(x - rad, y - rad / 2, x + rad, y + rad / 2);
            canvas.drawOval(rectF, paint);
        }

    }

    public void updateTarget(PCliq_Target target) {
        if (this.adjustToTarget) {
            this.radius = getPreferredRadius(target.getBounds());
        }

    }

    @Override
    public int getTotalRadius() {
        return radius + padding;
    }

    @Override
    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int getWidth() {
        return this.radius * 2;
    }

    public int getHeight() {
        return this.radius;
    }
}

