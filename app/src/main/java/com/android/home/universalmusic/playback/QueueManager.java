package com.android.home.universalmusic.playback;

import android.media.MediaMetadata;
import android.media.session.MediaSession;
import com.android.home.universalmusic.model.MusicProvider;
import com.android.home.universalmusic.utils.LogHelper;

import java.util.List;

/**
 * Simple data provider for queues. Keeps track of a current quque and a current index in the
 * queue. Also providers methods to set the current quque based on common queries, relying on
 * a given MusicProvider to provide the actual media metadata.
 */

public class QueueManager {

    private static final String TAG = LogHelper.makeLogTag(QueueManager.class);

    private MusicProvider mMusicProvider;
    private MetadataUpdateListener mListener;

    private interface MetadataUpdateListener {

        void onMetadataChanged(MediaMetadata metadata);
        void onMetadataRetrieveError();
        void onCurrentQuqueIndexUpdated(int queueIndex);
        void onQueueUpdated(String title, List<MediaSession.QueueItem> newQueue);
    }
}
