package com.android.home.stepsensor;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A Layout that contains a stream of card views.
 */

public class CardStreamLinearLayout extends LinearLayout {
    public static final int ANIMATION_SPEED_SLOW = 1001;
    public static final int ANIMATION_SPEED_NORMAL = 1002;
    public static final int ANIMATION_SPEED_FAST = 1003;

    private static final String TAG = "CardStreamLinearLayout";

    private final ArrayList<View> mFixedViewList = new ArrayList<>();

    private final Rect mChildRect = new Rect();

    private CardStreamAnimator mAnimators;

    private OnDismissListener mDismissListener = null;

    private boolean mLayouted = false;
    private boolean mSwiping = false;

    private String mFirstVisibleCardTag = null;

    private boolean mShowInitialAnimation = false;

    /**
     * Handle touch events to fade/move dragged items as they are swiped out.
     */
    private OnTouchListener mTouchListener = new OnTouchListener() {

        private float mDownX;
        private float mDownY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mDownY = event.getY();
                    break;

                case MotionEvent.ACTION_CANCEL:
                    resetAnimatedView(v);
                    mSwiping = false;
                    mDownX = 0;
                    mDownY = 0;
                    break;

                case MotionEvent.ACTION_MOVE: {

                    float x = event.getX() + v.getTranslationX();
                    float y = event.getY() + v.getTranslationY();

                    mDownX = mDownX == 0.f ? x : mDownX;
                    mDownY = mDownY == 0.f ? x : mDownY;

                    float deltaX = x - mDownX;
                    float deltaY = y - mDownY;

                    if (!mSwiping && isSwiping(deltaX, deltaY)) {

                        mSwiping = true;
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        swipeView(v, deltaX, deltaY);
                    }
                }
                break;

                case MotionEvent.ACTION_UP: {

                    // User let go - figure out whether to animate the view out,
                    // or back into place.
                    if (mSwiping) {

                        float x = event.getX() + v.getTranslationX();
                        float y = event.getY() + v.getTranslationY();

                        float deltaX = x - mDownX;
                        float deltaY = y - mDownY;

                        float deltaXAbs = Math.abs(deltaX);

                        boolean remove = deltaXAbs > v.getWidth() / 4 && !isFixedView(v);
                        if (remove) {
                            handleViewSwipingOut(v, deltaX, deltaY);
                        } else {
                            handleViewSwipingIn(v, deltaX, deltaY);
                        }
                    }


                }
            }

            return false;
        }
    };

    private void handleViewSwipingIn(View v, float deltaX, float deltaY) {

    }

    private void handleViewSwipingOut(View v, float deltaX, float deltaY) {

    }

    private boolean isFixedView(View v) {
        return false;
    }

    private void swipeView(View v, float deltaX, float deltaY) {

    }

    private boolean isSwiping(float deltaX, float deltaY) {
        return false;
    }

    private void resetAnimatedView(View v) {

    }


    public CardStreamLinearLayout(Context context) {
        super(context);
    }

    public CardStreamLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardStreamLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnDismisListener(OnDismissListener mCardDismissListener) {

    }




    public interface OnDismissListener {

        public void onDismiss(String tag);
    }

    /**
     * Empty default AnimationListener.
     */
    private abstract class EndAnimationWrapper implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }   // End of inner class.
}
