package com.photo.pose.photoshoot.cliq.PCliq_CustomSuggestion;

import android.app.Activity;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;


public class PCliq_MaterialShowcaseSequence implements PCliq_IDetachedListener {

    PCliq_PrefsManager mPrefsManager;
    Queue<PCliq_MaterialShowcaseView> mShowcaseQueue;
    private boolean mSingleUse = false;
    Activity mActivity;
    private PCliq_ShowcaseConfig mConfig;
    private int mSequencePosition = 0;

    private OnSequenceItemShownListener mOnItemShownListener = null;
    private OnSequenceItemDismissedListener mOnItemDismissedListener = null;

    public PCliq_MaterialShowcaseSequence(Activity activity) {
        mActivity = activity;
        mShowcaseQueue = new LinkedList<>();
    }

    public PCliq_MaterialShowcaseSequence(Activity activity, String sequenceID) {
        this(activity);
        this.singleUse(sequenceID);
    }

    public PCliq_MaterialShowcaseSequence addSequenceItem(View targetView, String content, String dismissText) {
        addSequenceItem(targetView, "", content, dismissText);
        return this;
    }

    public PCliq_MaterialShowcaseSequence addSequenceItem(View targetView, String title, String content, String dismissText) {

        PCliq_MaterialShowcaseView sequenceItem = new PCliq_MaterialShowcaseView.Builder(mActivity)
                .setTarget(targetView)
                .setTitleText(title)
                .setDismissText(dismissText)
                .setContentText(content)
                .setSequence(true)
                .build();

        if (mConfig != null) {
            sequenceItem.setConfig(mConfig);
        }

        mShowcaseQueue.add(sequenceItem);
        return this;
    }

    public PCliq_MaterialShowcaseSequence addSequenceItem(PCliq_MaterialShowcaseView sequenceItem) {

        if (mConfig != null) {
            sequenceItem.setConfig(mConfig);
        }

        mShowcaseQueue.add(sequenceItem);
        return this;
    }

    public PCliq_MaterialShowcaseSequence singleUse(String sequenceID) {
        mSingleUse = true;
        mPrefsManager = new PCliq_PrefsManager(mActivity, sequenceID);
        return this;
    }

    public void setOnItemShownListener(OnSequenceItemShownListener listener) {
        this.mOnItemShownListener = listener;
    }

    public void setOnItemDismissedListener(OnSequenceItemDismissedListener listener) {
        this.mOnItemDismissedListener = listener;
    }

    public boolean hasFired() {

        if (mPrefsManager.getSequenceStatus() == PCliq_PrefsManager.SEQUENCE_FINISHED) {
            return true;
        }

        return false;
    }

    public void start() {

        /**
         * Check if we've already shot our bolt and bail out if so         *
         */
        if (mSingleUse) {
            if (hasFired()) {
                return;
            }

            /**
             * See if we have started this sequence before, if so then skip to the point we reached before
             * instead of showing the user everything from the start
             */
            mSequencePosition = mPrefsManager.getSequenceStatus();

            if (mSequencePosition > 0) {
                for (int i = 0; i < mSequencePosition; i++) {
                    mShowcaseQueue.poll();
                }
            }
        }


        // do start
        if (mShowcaseQueue.size() > 0)
            showNextItem();
    }

    private void showNextItem() {

        if (mShowcaseQueue.size() > 0 && !mActivity.isFinishing()) {
            PCliq_MaterialShowcaseView sequenceItem = mShowcaseQueue.remove();
            sequenceItem.setDetachedListener(this);
            sequenceItem.show(mActivity);
            if (mOnItemShownListener != null) {
                mOnItemShownListener.onShow(sequenceItem, mSequencePosition);
            }
        } else {
            /**
             * We've reached the end of the sequence, save the fired state
             */
            if (mSingleUse) {
                mPrefsManager.setFired();
            }
        }
    }

    private void skipTutorial() {

        mShowcaseQueue.clear();

        if (mShowcaseQueue.size() > 0 && !mActivity.isFinishing()) {
            PCliq_MaterialShowcaseView sequenceItem = mShowcaseQueue.remove();
            sequenceItem.setDetachedListener(this);
            sequenceItem.show(mActivity);
            if (mOnItemShownListener != null) {
                mOnItemShownListener.onShow(sequenceItem, mSequencePosition);
            }
        } else {
            /**
             * We've reached the end of the sequence, save the fired state
             */
            if (mSingleUse) {
                mPrefsManager.setFired();
            }
        }
    }


    @Override
    public void onShowcaseDetached(PCliq_MaterialShowcaseView showcaseView, boolean wasDismissed, boolean wasSkipped) {

        showcaseView.setDetachedListener(null);

        /**
         * We're only interested if the showcase was purposefully dismissed
         */
        if (wasDismissed) {

            if (mOnItemDismissedListener != null) {
                mOnItemDismissedListener.onDismiss(showcaseView, mSequencePosition);
            }

            /**
             * If so, update the prefsManager so we can potentially resume this sequence in the future
             */
            if (mPrefsManager != null) {
                mSequencePosition++;
                mPrefsManager.setSequenceStatus(mSequencePosition);
            }

            showNextItem();
        }

        if(wasSkipped){
            if (mOnItemDismissedListener != null) {
                mOnItemDismissedListener.onDismiss(showcaseView, mSequencePosition);
            }

            /**
             * If so, update the prefsManager so we can potentially resume this sequence in the future
             */
            if (mPrefsManager != null) {
                mSequencePosition++;
                mPrefsManager.setSequenceStatus(mSequencePosition);
            }

            skipTutorial();
        }
    }

    public void setConfig(PCliq_ShowcaseConfig config) {
        this.mConfig = config;
    }

    public interface OnSequenceItemShownListener {
        void onShow(PCliq_MaterialShowcaseView itemView, int position);
    }

    public interface OnSequenceItemDismissedListener {
        void onDismiss(PCliq_MaterialShowcaseView itemView, int position);
    }

}
