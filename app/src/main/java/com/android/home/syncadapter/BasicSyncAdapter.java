package com.android.home.syncadapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.android.home.R;

/**
 * Activity for holding EntryListFragment.
 */

public class BasicSyncAdapter extends FragmentActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.basic_sync_adapter);
    }
}
