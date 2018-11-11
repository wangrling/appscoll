package com.android.home.displaybitmaps.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.android.home.BuildConfig;
import com.android.home.displaybitmaps.util.Utils;

public class DisplayBitmaps extends FragmentActivity {

    private static final String TAG = "DisplayBitmaps";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }

        super.onCreate(savedInstanceState);

        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new ImageGridFragment(), TAG);
            ft.commit();
        }
    }
}
