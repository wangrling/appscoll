package com.android.home.syncadapter;

import android.os.Bundle;
import android.view.Menu;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.android.home.R;

/**
 * Activity for holding EntryListFragment.
 */

public class BasicSyncActivity extends FragmentActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.basic_sync_adapter);
    }


}