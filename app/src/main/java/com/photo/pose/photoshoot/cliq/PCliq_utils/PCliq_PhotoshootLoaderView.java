package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class PCliq_PhotoshootLoaderView extends fr.castorflex.android.circularprogressbar.CircularProgressBar {

    private Paint ringPaint;
    private Paint bladePaint;
    private Paint centerPaint;
    private Paint focusPulsePaint;
    private Paint bracketPaint;

    private float rotationAngle = 0f;
    private float pulseRadiusRatio = 0f;
    private float apertureScale = 0.5f;

    private ValueAnimator rotationAnimator;
    private ValueAnimator pulseAnimator;
    private ValueAnimator apertureAnimator;

    private final int GOLD_PRIMARY = Color.parseColor("#C19543");
    private final int GOLD_LIGHT = Color.parseColor("#E6CA85");
    private final int GOLD_DARK = Color.parseColor("#9E752F");

    public PCliq_PhotoshootLoaderView(Context context) {
        super(context);
        init();
    }

    public PCliq_PhotoshootLoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PCliq_PhotoshootLoaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setColor(GOLD_PRIMARY);
        ringPaint.setStrokeWidth(3f);

        bladePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bladePaint.setStyle(Paint.Style.FILL);
        bladePaint.setColor(GOLD_PRIMARY);

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setColor(GOLD_LIGHT);

        focusPulsePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        focusPulsePaint.setStyle(Paint.Style.STROKE);
        focusPulsePaint.setColor(GOLD_PRIMARY);

        bracketPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bracketPaint.setStyle(Paint.Style.STROKE);
        bracketPaint.setColor(GOLD_PRIMARY);
        bracketPaint.setStrokeWidth(2.5f);
        bracketPaint.setStrokeCap(Paint.Cap.ROUND);

        startAnimations();
    }

    private void startAnimations() {
        if (rotationAnimator != null && rotationAnimator.isRunning()) return;

        rotationAnimator = ValueAnimator.ofFloat(0f, 360f);
        rotationAnimator.setDuration(2400);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.addUpdateListener(animation -> {
            rotationAngle = (float) animation.getAnimatedValue();
            invalidate();
        });

        pulseAnimator = ValueAnimator.ofFloat(0f, 1f);
        pulseAnimator.setDuration(1600);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setInterpolator(new LinearInterpolator());
        pulseAnimator.addUpdateListener(animation -> {
            pulseRadiusRatio = (float) animation.getAnimatedValue();
            invalidate();
        });

        apertureAnimator = ValueAnimator.ofFloat(0.35f, 0.7f, 0.35f);
        apertureAnimator.setDuration(1800);
        apertureAnimator.setRepeatCount(ValueAnimator.INFINITE);
        apertureAnimator.addUpdateListener(animation -> {
            apertureScale = (float) animation.getAnimatedValue();
            invalidate();
        });

        rotationAnimator.start();
        pulseAnimator.start();
        apertureAnimator.start();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float maxRadius = Math.min(cx, cy) * 0.85f;
        float baseRadius = maxRadius * 0.65f;

        // 1. Draw Focus Pulse Ripples (Camera Auto-Focus Effect)
        float pulseR1 = baseRadius + (maxRadius - baseRadius) * pulseRadiusRatio;
        int alpha1 = (int) (160 * (1f - pulseRadiusRatio));
        focusPulsePaint.setStrokeWidth(2.5f);
        focusPulsePaint.setColor(Color.argb(alpha1, 193, 149, 67));
        canvas.drawCircle(cx, cy, pulseR1, focusPulsePaint);

        float secondPulse = (pulseRadiusRatio + 0.5f) % 1.0f;
        float pulseR2 = baseRadius + (maxRadius - baseRadius) * secondPulse;
        int alpha2 = (int) (160 * (1f - secondPulse));
        focusPulsePaint.setColor(Color.argb(alpha2, 193, 149, 67));
        canvas.drawCircle(cx, cy, pulseR2, focusPulsePaint);

        // 2. Draw Camera Viewfinder Focus Corner Brackets [  ]
        float bracketSize = maxRadius * 0.95f;
        float cornerLen = maxRadius * 0.22f;
        canvas.drawLine(cx - bracketSize, cy - bracketSize + cornerLen, cx - bracketSize, cy - bracketSize, bracketPaint);
        canvas.drawLine(cx - bracketSize, cy - bracketSize, cx - bracketSize + cornerLen, cy - bracketSize, bracketPaint);
        canvas.drawLine(cx + bracketSize - cornerLen, cy - bracketSize, cx + bracketSize, cy - bracketSize, bracketPaint);
        canvas.drawLine(cx + bracketSize, cy - bracketSize, cx + bracketSize, cy - bracketSize + cornerLen, bracketPaint);
        canvas.drawLine(cx - bracketSize, cy + bracketSize - cornerLen, cx - bracketSize, cy + bracketSize, bracketPaint);
        canvas.drawLine(cx - bracketSize, cy + bracketSize, cx - bracketSize + cornerLen, cy + bracketSize, bracketPaint);
        canvas.drawLine(cx + bracketSize - cornerLen, cy + bracketSize, cx + bracketSize, cy + bracketSize, bracketPaint);
        canvas.drawLine(cx + bracketSize, cy + bracketSize, cx + bracketSize, cy + bracketSize - cornerLen, bracketPaint);

        // 3. Draw Outer Gold Lens Barrel Ring
        ringPaint.setStrokeWidth(3.5f);
        ringPaint.setColor(GOLD_PRIMARY);
        canvas.drawCircle(cx, cy, baseRadius, ringPaint);

        // 4. Draw Rotating Camera Aperture Blades
        canvas.save();
        canvas.rotate(rotationAngle, cx, cy);

        int numBlades = 6;
        float bladeLength = baseRadius * 0.88f;
        float innerHoleRadius = baseRadius * apertureScale * 0.45f;

        for (int i = 0; i < numBlades; i++) {
            canvas.save();
            float bladeAngle = (360f / numBlades) * i;
            canvas.rotate(bladeAngle, cx, cy);

            Path bladePath = new Path();
            bladePath.moveTo(cx, cy - innerHoleRadius);
            bladePath.lineTo(cx + bladeLength * 0.5f, cy - baseRadius * 0.92f);
            bladePath.lineTo(cx, cy - baseRadius * 0.92f);
            bladePath.close();

            bladePaint.setColor(i % 2 == 0 ? GOLD_PRIMARY : GOLD_DARK);
            bladePaint.setAlpha(220);
            canvas.drawPath(bladePath, bladePaint);

            canvas.restore();
        }

        ringPaint.setStrokeWidth(2f);
        ringPaint.setColor(GOLD_LIGHT);
        canvas.drawCircle(cx, cy, innerHoleRadius, ringPaint);

        canvas.restore();

        // 5. Center Camera Shutter Iris Core
        centerPaint.setColor(GOLD_PRIMARY);
        centerPaint.setAlpha(180);
        canvas.drawCircle(cx, cy, innerHoleRadius * 0.5f, centerPaint);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            startAnimations();
        } else {
            if (rotationAnimator != null) rotationAnimator.cancel();
            if (pulseAnimator != null) pulseAnimator.cancel();
            if (apertureAnimator != null) apertureAnimator.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (rotationAnimator != null) rotationAnimator.cancel();
        if (pulseAnimator != null) pulseAnimator.cancel();
        if (apertureAnimator != null) apertureAnimator.cancel();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == View.VISIBLE) {
            startAnimations();
        }
    }
}
