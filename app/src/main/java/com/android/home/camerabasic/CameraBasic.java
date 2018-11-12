package com.android.home.camerabasic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.android.home.R;

public class CameraBasic extends AppCompatActivity {
    final static String TAG = "CameraBasic";

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_basic);

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.camera_container, CameraBasicFragment.newInstance())
                    .commit();
        }
    }
}
