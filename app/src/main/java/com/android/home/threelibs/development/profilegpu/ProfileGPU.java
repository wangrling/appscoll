package com.android.home.threelibs.development.profilegpu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.android.home.R;

public class ProfileGPU extends Activity {

    private int toggle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_gpu);
    }

    /**
     * Click handler that swaps between a large and smaller image.
     * Used with performance tools to show memory and GPU performance.
     *
     * There are two "dinosaur" images in res/drawable.
     *     - dinosaur_medium is about 495K and the default
     *     - dinosaur_large is about 1M and you should use it if your device
     *       can handle it as you will get clearer profiling results.
     *
     * @param view
     */
    public void changeImage(View view){
        if (toggle == 0) {
            view.setBackgroundResource(R.drawable.dinosaur_medium);
            toggle = 1;
        } else {
            // Add code to let your app sleep for two screen refreshes
            // before switching the background to the smaller image.
            // This means that instead of refreshing the screen every 16 ms,
            // your app now refreshes every 48 ms with new content.
            // This will be reflected in the bars displayed by the
            // Profile GPU Rendering tool.
            try {
                Thread.sleep(32); // two refreshes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            view.setBackgroundResource(R.drawable.ankylo);
            toggle = 0;
        }
    }
}
