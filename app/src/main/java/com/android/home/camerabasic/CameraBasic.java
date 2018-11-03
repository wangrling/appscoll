package com.android.home.camerabasic;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.android.home.R;

public class CameraBasic extends AppCompatActivity {
    final static String TAG = "CameraBasic";

    @Override
    protected void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_basic);

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.camera_container, CameraBasicFragment.newInstance())
                    .commit();
        }
    }
}
