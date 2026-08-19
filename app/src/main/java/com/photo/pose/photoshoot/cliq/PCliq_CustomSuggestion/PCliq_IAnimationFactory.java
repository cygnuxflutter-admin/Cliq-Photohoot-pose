package com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion;

import android.graphics.Point;
import android.view.View;


public interface PCliq_IAnimationFactory {

    void animateInView(View target, Point point, long duration, AnimationStartListener listener);

    void animateOutView(View target, Point point, long duration, AnimationEndListener listener);

    void animateTargetToPoint(PCliq_MaterialShowcaseView showcaseView, Point point);

    public interface AnimationStartListener {
        void onAnimationStart();
    }

    public interface AnimationEndListener {
        void onAnimationEnd();
    }
}

