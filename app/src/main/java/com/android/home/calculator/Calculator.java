package com.android.home.calculator;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toolbar;
import androidx.annotation.Nullable;
import com.android.home.R;

public class Calculator extends Activity {

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
}