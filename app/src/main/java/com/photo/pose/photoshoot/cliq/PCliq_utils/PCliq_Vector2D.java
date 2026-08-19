package com.photo.pose.photoshoot.cliq.PCliq_utils;


import android.graphics.PointF;

public class PCliq_Vector2D extends PointF {

    public PCliq_Vector2D() {
        super();
    }

    public PCliq_Vector2D(float x, float y) {
        super(x, y);
    }

    public static float getAngle(PCliq_Vector2D vector1, PCliq_Vector2D vector2) {
        vector1.normalize();
        vector2.normalize();
        double degrees = (180.0 / Math.PI) * (Math.atan2(vector2.y, vector2.x) - Math.atan2(vector1.y, vector1.x));
        return (float) degrees;
    }

    public void normalize() {
        float length = (float) Math.sqrt(x * x + y * y);
        x /= length;
        y /= length;
    }
}