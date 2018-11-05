package com.android.home.universalmusic.ui;
import android.support.v4.app.Fragment;

/**
 * A Fragment that lists all the various browsable quques available
 * from a {@link android.service.media.MediaBrowserService}.
 *
 * It uses a {@link android.media.browse.MediaBrowser} to connect to the
 * {@link com.android.home.randommusic.MusicService}.
 *
 * Once connected, the fragment subscribes to get all the children.
 *
 * All {@link android.media.browse.MediaBrowser.MediaItem}'s that can be browsed are
 * shown in a ListView.
 */


public class MediaBrowserFragment extends Fragment {

    public interface MediaFragmentListener extends MediaBrowserProvider {

    }
}
