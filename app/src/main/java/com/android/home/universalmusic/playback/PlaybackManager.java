package com.android.home.universalmusic.playback;

import com.android.home.universalmusic.model.MusicProvider;
import com.android.home.universalmusic.utils.LogHelper;

/**
 * Manage the interactions among the container service, the queue manager and the actual playback.
 */

public class PlaybackManager implements Playback.Callback {

    private static final String TAG = LogHelper.makeLogTag(PlaybackManager.class);

    private MusicProvider mMusicProvider;

    @Override
    public void onCompletion() {

    }

    @Override
    public void onPlaybackStatusChanged(int state) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void setCurrentMediaId(String mediaId) {

    }
}
