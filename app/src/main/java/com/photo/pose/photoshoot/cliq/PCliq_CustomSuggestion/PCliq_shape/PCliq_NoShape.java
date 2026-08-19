package com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion.PCliq_shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion.PCliq_target.PCliq_Target;

/**
 * A Shape implementation that draws nothing.
 */
public class PCliq_NoShape implements PCliq_Shape {

    @Override
    public void updateTarget(PCliq_Target target) {
        // do nothing
    }

    @Override
    public int getTotalRadius() {
        return 0;
    }

    @Override
    public void setPadding(int padding) {
        // do nothing
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int x, int y) {
        // do nothing
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
