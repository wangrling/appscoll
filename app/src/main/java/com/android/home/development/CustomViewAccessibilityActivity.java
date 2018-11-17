package com.android.home.development;


import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * Demonstrates how to implement accessibility support of custom views. Custom view
 * is a tailored widget developed by extending the base classes in the android.view
 * package. This sample shows how to implement the accessibility behavior via both
 * inheritance (non backwards compatible) and composition (backwards compatible).
 * <p>
 * While the Android framework has a diverse portfolio of views tailored for various
 * use cases, sometimes a developer needs a specific functionality not implemented
 * by the standard views. A solution is to write a custom view that extends one the
 * base view classes. While implementing the desired functionality a developer should
 * also implement accessibility support for that new functionality such that
 * disabled users can leverage it.
 * </p>
 */

public class CustomViewAccessibilityActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_view_accessibility);
    }
}
