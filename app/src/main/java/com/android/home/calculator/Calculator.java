package com.android.home.calculator;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.android.home.R;

public class Calculator extends Activity {

    private static final String TAG = "Calculator";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calculator_main);
    }
}
