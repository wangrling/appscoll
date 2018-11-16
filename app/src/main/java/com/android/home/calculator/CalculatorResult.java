package com.android.home.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import androidx.annotation.Nullable;

/**
 * A text widget that is "infinitely" scrollable to the right,
 * and obtains the text to display via a callback to Logic.
 */

/**
 * Menu就是右上角的菜单栏。
 */

public class CalculatorResult extends AlignedTextView
        implements MenuItem.OnMenuItemClickListener {


    public CalculatorResult(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}
