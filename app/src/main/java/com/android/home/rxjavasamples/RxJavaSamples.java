package com.android.home.rxjavasamples;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class RxJavaSamples extends AppCompatActivity {

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new RxJavaListFragment(), this.toString())
                    .commit();
        }
    }
}
