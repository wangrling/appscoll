package com.android.home.displaybitmaps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.android.home.BuildConfig;

/**
 * Simple FragmentActivity to hold the main {@link ImageGridFragment} and not much else.
 */

public class DisplayBitmaps extends FragmentActivity {
    private static final String TAG = "DisplayBitmaps";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new ImageGridFragment(), TAG);
            ft.commit();
        }
    }
}
