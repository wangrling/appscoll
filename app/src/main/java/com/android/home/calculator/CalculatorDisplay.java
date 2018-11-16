package com.android.home.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class CalculatorDisplay extends LinearLayout
        implements AccessibilityManager.AccessibilityStateChangeListener {

    /**
     * The duration in milliseconds after which to hide the toolbar.
     */
    private static final long AUTO_HIDE_DELAY_MILLIS = 3000L;

    /**
     * The duration in milliseconds to fade in/out the toolbar.
     */
    private static final long FADE_DURATION = 200L;

    private final AccessibilityManager mAccessibilityManager;

    private Toolbar mToolbar;

    public CalculatorDisplay(Context context) {
        this(context, null /* attrs */);
    }

    public CalculatorDisplay(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0  /* defStyle */);
    }

    public CalculatorDisplay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mAccessibilityManager =
                (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);


    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {

    }
}
