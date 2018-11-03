package com.android.home.rssimage;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import com.android.home.R;

public class RssImageFeed extends FragmentActivity implements
        FragmentManager.OnBackStackChangedListener {

    // A handle to the main screen view.
    View mMainView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Sets fullscreen-related flags for the display
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);

        super.onCreate(savedInstanceState);

        mMainView = getLayoutInflater().inflate(R.layout.fragment_host, null);

        setContentView(mMainView);
    }

    @Override
    public void onBackStackChanged() {

    }
}
