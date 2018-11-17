package com.android.home.calculator;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * 使用外部库计算，目录/external/crcalc/
 * 还有相关的测试集，Calculator真没想象中那么简单。
 * CR.java
 * StringCRFunction.java
 * UnaryCRFunction.java
 *
 * CRTest.java
 * SlowCRTest.java
 */

public class Calculator extends Activity implements
        DragLayout.CloseCallback, DragLayout.DragCallback {

    public static final String TAG = "Calculator";

    private CalculatorDisplay mDisplayView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calculator_main);
        setActionBar((Toolbar)findViewById(R.id.toolbar));

        // Hide all default options in the ActionBar.
        getActionBar().setDisplayOptions(0);

        // Ensure the toolbar stays visible while the options menu is displayed.
        getActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {

            }
        });
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onStartDraggingOpen() {

    }

    @Override
    public void onInstanceStateRestored(boolean isOpen) {

    }

    @Override
    public void whileDragging(float yFraction) {

    }

    @Override
    public boolean shouldCaptureView(View view, int x, int y) {
        return false;
    }

    @Override
    public int getDisplayHeight() {
        return 0;
    }


    // 6.0以下使用。
    interface OnDisplayMemoryOperationsListener {
        boolean shouldDisplayMemory();
    }
}
