package com.android.home.testing.espresso;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.home.R;

import java.util.ArrayList;
import java.util.List;

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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        List<String> dataSet = new ArrayList<>(DATASET_COUNT);

        for (int i = 0; i < DATASET_COUNT; i++) {
            dataSet.add(getString(R.string.item_element_text) + i);
        }

        CustomAdapter adapter = new CustomAdapter(dataSet, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
}
