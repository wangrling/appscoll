package com.android.home.universalmusic.playback;

import android.media.session.MediaSession;
import android.media.session.PlaybackState;

/**
 * Interface representing either Local or Remote Playback. The {@link com.android.home.universalmusic.MusicService}
 * works directly with an instance of the Playback object to make the various calls
 * such as play, pause etc.
 */

public interface Playback {
    /**
     * Start/setup the playback.
     * Resources/listeners would be allocated by implementations.
     */
    void start();

    /**
     * Stop the playback. All resources can be de-allocated by implementations here.
     * @param notifyListeners if true and a callback has been set by setCallback,
     *                       callback.onPlaybackStatusChanged will be called after changing
     *                        the state.
     */
    void stop(boolean notifyListeners);

    /**
     * Set the latest playback state as determined by the caller.
     */
    void setState(int state);

    /**
     * Get the current {@link PlaybackState#getState()}
     */
    int getState();

    /**
     * @return boolean that indicates that this is ready to be used.
     */
    boolean isConnected();

    /**
     * @return boolean indicating whether the player is playing or is
     * supposed to playing when we gain audio focus.
     */
    boolean isPlaying();

    /**
     * @return pos if currently playing an item.
     */
    long getCurrentStreamPosition();

    /**
     * Queries the underlying stream and update the internal last known stream position.
     */
    void updateLastKnownStreamPosition();

    void play(MediaSession.QueueItem item);

    void pause();

    void seekTo(long position);

    void setCurrentMediaId(String mediaId);

    String getCurrentMediaId();

    interface Callback {
        /**
         * On current music completed.
         */
        void onCompletion();

        /**
         * on Playback status changed
         * Implementations can use this callback to update playback
         * state on the media sessions.
         */
        void onPlaybackStatusChanged(int state);

        /**
         * @param error to be added to the PlaybackState.
         */
        void onError(String error);


        /**
         * param mediaId being currently played.
         * @param mediaId
         */
        void setCurrentMediaId(String mediaId);
    }

    void setCallback(Callback callback);
}
