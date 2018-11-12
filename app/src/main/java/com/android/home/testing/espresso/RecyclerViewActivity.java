package com.android.home.testing.espresso;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * Shows a list using a RecyclerView.
 */

public class RecyclerViewActivity extends Activity {

    private static final int DATASET_COUNT = 50;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recycler_view_activity);

        // Create a RecyclerView, a LayoutManager, a data Adapter and wire everything up.
    }
}
