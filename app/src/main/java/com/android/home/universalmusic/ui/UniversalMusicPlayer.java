package com.android.home.universalmusic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.android.home.R;
import com.android.home.universalmusic.utils.LogHelper;

/**
 * This class hold the MediaBrowser and the MediaController instances. It will create a MediaBrowser
 * when it is created and connect/disconnect on start/stop. Thus, a MediaBrowser will be always
 * connected while this activity is running.
 */

public class UniversalMusicPlayer extends BaseActivity {

    private static final String TAG = LogHelper.makeLogTag(UniversalMusicPlayer.class);

    private static final String SAVED_MEDIA_ID = "com.android.guide.MEDIA_ID";
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "com.android.guide.CURRENT_MEDIA_DESCRIPTION";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogHelper.d(TAG, "Activity onCreate");

        setContentView(R.layout.universal_music_player);

        initializeToolbar();

        initializeFromParams(savedInstanceState, getIntent());

        // Only check if a full screen player is needed on the first time.
        if (savedInstanceState == null) {
            startFullScreenActivityIfNeeded(getIntent());
        }
    }

    private void startFullScreenActivityIfNeeded(Intent intent) {

    }

    private void initializeFromParams(Bundle savedInstanceState, Intent intent) {
        String mediaId = null;

        if (savedInstanceState != null) {
            // If there is a saved media ID, use it.
            mediaId = savedInstanceState.getString(SAVED_MEDIA_ID);
        }
        navigateToBrowser(mediaId);
    }

    private void navigateToBrowser(String mediaId) {

    }
}
