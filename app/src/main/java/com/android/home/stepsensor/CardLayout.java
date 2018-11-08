package com.android.home.stepsensor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

/**
 * Custom Button with a special 'pressed' effect for touch events.
 */


public class CardLayout extends RelativeLayout {

    private boolean mSwiping = false;
    private float mDownX = 0.f;
    private float mDownY = 0.f;

    private float mTouchSlop = 0.f;

    public CardLayout(Context context) {
        super(context);

        init();
    }

    public CardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public CardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        setClickable(true);

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledDoubleTapSlop() * 2.f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mSwiping = false;
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!mSwiping) {
                    mSwiping = Math.abs(mDownX - event.getX()) > mTouchSlop;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mSwiping = false;
                break;
        }


        return mSwiping;
    }
}
