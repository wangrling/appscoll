package com.android.home.rxjavasamples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class RxJavaSamples extends AppCompatActivity {

    @Override
    protected void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new RxJavaListFragment(), this.toString())
                    .commit();
        }
    }
}
