package com.android.home.universalmusic;

import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.home.universalmusic.playback.PlaybackManager;

import java.util.List;

/**
 * This class provides a MediaBrowser through a service. It exposes the media library to
 * a browsing client, through the onGetRoot and onLoadChildren methods. It also creates
 * a MediaSession and exposes it through its MediaSession.Token, which allows the client
 * to create a MediaController that connects to and send control commands to the
 * MediaSession remotely. This is useful for user interfaces that need to interact with
 * you media session, like Android Auto. You can (should) also use the same service
 * from your app's UI, which gives a seamless playback experience to the user.
 *
 * To implement a MediaBrowserService, you need to:
 *
 * <ul>
 *     <li>Extend {@link android.service.media.MediaBrowserService}, implementing
 *     the media browsing related methods {@link android.service.media.MediaBrowserService#onGetRoot(String, int, Bundle)}
 *     and {@link android.service.media.MediaBrowserService#onLoadChildren(String, MediaBrowserService.Result)};</li>
 *
 *     <li>In onCreate, start a new {@link MediaSession} and notify its parent with the session's token
 *     {@link MediaBrowserService#setSessionToken(MediaSession.Token)};</li>
 *
 *     <li>Set a callback on the {@link MediaSession#setCallback(MediaSession.Callback)}. The callback will receive all the
 *     user's actions, like play, pause, etc;</li>
 *
 *     <li>Handle all the actual music playing using any method your app prefers (for example,
 *     {@link android.media.MediaPlayer};</li>
 *
 *     <li>Update playbackState, "now playing" metadata and queue, using MediaSession proper methods
 *     {@link MediaSession#setPlaybackState(PlaybackState)}
 *     {@link MediaSession#setMetadata(MediaMetadata)} and
 *     {@link MediaSession#setQueue(List)};</li>
 *
 *     <li>Declare and export the service in AndroidManifest with an intent receiver for the action
 *     MediaBrowserService;</li>
 *
 * @see <a href="README.md">README.md</a> for more details.
 * </ul>
 */


public class MusicService extends MediaBrowserService implements PlaybackManager.PlaybackServiceCallback {


    @Nullable
    @android.support.annotation.Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull @android.support.annotation.NonNull String clientPackageName, int clientUid, @Nullable @android.support.annotation.Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull @android.support.annotation.NonNull String parentId, @NonNull @android.support.annotation.NonNull Result<List<MediaBrowser.MediaItem>> result) {

    }
}
