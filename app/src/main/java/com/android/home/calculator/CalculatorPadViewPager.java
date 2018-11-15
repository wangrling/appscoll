package com.android.home.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 包含数字和符号的输入。
 */

public class CalculatorPadViewPager extends ViewPager {

    private final GestureDetector.SimpleOnGestureListener mGestureWatcher =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    // Return true so calls to onSingleTapUp are not blocked.
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if (mClickedItemIndex != -1) {
                        getChildAt(mClickedItemIndex).performClick();
                        mClickedItemIndex = -1;
                        return true;
                    }
                    return super.onSingleTapUp(e);
                }
            };

    private final GestureDetector mGestureDetector;

    private int mClickedItemIndex = -1;

    public CalculatorPadViewPager(@NonNull Context context) {
        this(context, null /* attrs */);
    }

    public CalculatorPadViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mGestureDetector = new GestureDetector(context, mGestureWatcher);
    }
}
