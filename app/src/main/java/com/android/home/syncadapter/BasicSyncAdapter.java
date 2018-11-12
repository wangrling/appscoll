package com.android.home.syncadapter;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
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
