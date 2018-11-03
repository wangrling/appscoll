package com.android.home.plaid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import com.android.home.R;

public class PlaidApp extends Activity {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private RecyclerView grid;
    private ImageButton fab;
    private RecyclerView filtersList;
    private ProgressBar loading;

    ImageView noConnection;

    private int columns;

    @Override
    protected void onCreate(@Nullable @android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plaid_app);

        bindResource();

    }

    private void bindResource() {
        drawer = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        grid = findViewById(R.id.grid);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> { fabClick(); });
        filtersList = findViewById(R.id.filters);
        loading = findViewById(android.R.id.empty);
        noConnection = findViewById(R.id.no_connection);

        columns = getResources().getInteger(R.integer.num_columns);
    }

    protected void fabClick() {

    }
}
