package com.android.home.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

// 横着滑动。
public class CalculatorScrollView extends HorizontalScrollView {
    public CalculatorScrollView(Context context) {
        this(context, null  /* attrs */);
    }

    public CalculatorScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /* defStyleAttr */);
    }

    public CalculatorScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static int getChildMeasureSpecCompat(int spec, int padding, int childDimension) {
        if (MeasureSpec.getMode(spec) == MeasureSpec.UNSPECIFIED &&
                (childDimension == MATCH_PARENT || childDimension == WRAP_CONTENT)) {

            final int size = Math.max(9, MeasureSpec.getSize(spec) - padding);

            return MeasureSpec.makeMeasureSpec(size, MeasureSpec.UNSPECIFIED);
        }

        return ViewGroup.getChildMeasureSpec(spec, padding, childDimension);
    }


    // measureChild是一个怎样的过程？
    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {

        Log.d(Calculator.TAG, "measureChild width = " + parentHeightMeasureSpec);
        Log.d(Calculator.TAG, "measureChild height = " + parentHeightMeasureSpec);
        // Allow child to be as wide as they want.
        parentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(parentWidthMeasureSpec), MeasureSpec.UNSPECIFIED);

        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        final int childWidthMeasureSpec = getChildMeasureSpecCompat(parentWidthMeasureSpec,
                lp.leftMargin + lp.rightMargin, lp.width);

        final int childHeightMeasureSpec = getChildMeasureSpecCompat(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        // Allow child to be as wide as they want.
        parentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(parentWidthMeasureSpec), MeasureSpec.UNSPECIFIED);

        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        final int childWidthMeasureSpec = getChildMeasureSpecCompat(parentWidthMeasureSpec,
                lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpecCompat(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }
}
