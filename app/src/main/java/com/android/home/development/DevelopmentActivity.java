package com.android.home.development;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Launcher activity for the CardView.
 */

public class DevelopmentActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, CardViewFragment.newInstance())
                    .commit();
        }
    }
}
