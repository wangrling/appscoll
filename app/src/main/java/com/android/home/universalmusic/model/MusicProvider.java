package com.android.home.universalmusic.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.AsyncTask;
import android.service.media.MediaBrowserService;
import com.android.home.R;
import com.android.home.universalmusic.utils.LogHelper;
import com.android.home.universalmusic.utils.MediaIDHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.android.home.universalmusic.utils.MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE;
import static com.android.home.universalmusic.utils.MediaIDHelper.MEDIA_ID_ROOT;
import static com.android.home.universalmusic.utils.MediaIDHelper.createMediaID;

/**
 * Simple data provider for music tracks. The actual metadata source is delegated to a
 * MusicProviderSource defined by a constructor argument of this class.
 */


public class MusicProvider {

    private static final String TAG = LogHelper.makeLogTag(MusicProvider.class);

    private MusicProviderSource mSource;


    // Categorized caches for music track data.
    // 每种音乐类型可以有多首音乐，但是每个音乐Id只能对应一首。
    private ConcurrentMap<String, List<MediaMetadata>> mMusicListByGenre;
    private final ConcurrentMap<String, MutableMediaMetadata> mMusicListById;

    private final Set<String> mFavoriteTracks;

    enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    private volatile State mCurrentState = State.NON_INITIALIZED;

    private interface Callback {
        void onMusicCatalogReady(boolean success);
    }

    public MusicProvider() {
        this(new RemoteJSONSource());
    }

    // 返回一个可以迭代的Iterable类型对象。
    public MusicProvider(MusicProviderSource source) {
        mSource = source;
        mMusicListByGenre = new ConcurrentHashMap<>();
        mMusicListById = new ConcurrentHashMap<>();
        mFavoriteTracks = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    }

    /**
     * Get an iterator over the list of genres. (流派，类型)
     */
    public Iterable<String> getGenres() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }

        return mMusicListByGenre.keySet();
    }

    /**
     * Get an iterator over a shuffled collection of all songs.
     */
    public Iterable<MediaMetadata> getShuffledMusic() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }

        List<MediaMetadata> shuffled = new ArrayList<>(mMusicListById.size());
        for (MutableMediaMetadata mutableMetadata : mMusicListById.values()) {
            shuffled.add(mutableMetadata.metadata);
        }
        Collections.shuffle(shuffled);
        return shuffled;
    }

    /**
     * Get music tracks of the given genre.
     * 返回的是二级List对象。
     */
    public List<MediaMetadata> getMusicsByGenre(String genre) {
        if (mCurrentState != State.INITIALIZED || !mMusicListByGenre.containsKey(genre)) {
            return Collections.emptyList();
        }

        return mMusicListByGenre.get(genre);
    }

    /**
     * Very basic implementation of a search that filter music tracks with title containing
     * the given query.
     */
    public List<MediaMetadata> searchMusicBySongTitle(String query) {
        return searchMusic(MediaMetadata.METADATA_KEY_TITLE, query);
    }

    /**
     * Very basic implementation of a search that filter music tracks with album containing
     * the given query.
     */
    public List<MediaMetadata> searchMusicByAlbum(String query) {
        return searchMusic(MediaMetadata.METADATA_KEY_ALBUM, query);
    }

    /**
     * Very basic implementation of a search that filter music tracks with artist containing
     * the given query.
     *
     */
    public List<MediaMetadata> searchMusicByArtist(String query) {
        return searchMusic(MediaMetadata.METADATA_KEY_ARTIST, query);
    }

    /**
     * Very basic implementation of a search that filter music tracks with a genre containing
     * the given query.
     *
     */
    public List<MediaMetadata> searchMusicByGenre(String query) {
        return searchMusic(MediaMetadata.METADATA_KEY_GENRE, query);
    }

    // 根据关键字过滤出相关的音乐。
    private List<MediaMetadata> searchMusic(String metadataField, String query) {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }

        ArrayList<MediaMetadata> result = new ArrayList<>();
        query = query.toLowerCase(Locale.US);

        for (MutableMediaMetadata track : mMusicListById.values()) {
            if (track.metadata.getString(metadataField).toLowerCase(Locale.US)
                    .contains(query)) {
                result.add(track.metadata);
            }
        }
        return result;
    }

    /**
     * Return the MediaMetadata for the given musicID.
     *
     * @param musicId The unique, non-hierarchical music ID.
     */
    public MediaMetadata getMusic(String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId).metadata : null;
    }

    public synchronized void updateMusicArt(String musicId, Bitmap albumArt, Bitmap icon) {
        MediaMetadata metadata = getMusic(musicId);
        metadata = new MediaMetadata.Builder(metadata)

        // set high resolution bitmap in METADATA_KEY_ALBUM_ART. This is used, for
        // example, on the lock screen background when the media session is active.

        .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, albumArt)
        // set small version of the album art in the DISPLAY_ICON. This is used on
        // the MediaDescription and thus it should be small to be serialized if
        // necessary
        .putBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON, icon).build();

        MutableMediaMetadata mutableMetadata = mMusicListById.get(musicId);
        if (mutableMetadata == null) {
            throw new IllegalStateException("Unexpected error: Incosistent data structures in " +
                    "MusicProvider");
        }

        mutableMetadata.metadata = metadata;
    }

    public void setFavorite(String musicId, boolean favorite) {
        if (favorite) {
            mFavoriteTracks.add(musicId);
        } else {
            mFavoriteTracks.remove(musicId);
        }
    }

    public boolean isInitialized() {
        return mCurrentState == State.INITIALIZED;
    }

    public boolean isFavorite(String musicId) {
        return mFavoriteTracks.contains(musicId);
    }

    /**
     * Get the list of music tracks from a server and caches the track information for
     * future reference, keying tracks by musicId and grouping by genre.
     */
    public void retrieveMediaAsync(final Callback callback) {
        LogHelper.d(TAG, "retrieveMediaAsync called");
        if (mCurrentState == State.INITIALIZED) {
            if (callback != null) {
                // Nothing to do, execute callback immediately.
                callback.onMusicCatalogReady(true);
            }

            return ;
        }

        // Asynchronously load the music catalog in a separate thread.
        new AsyncTask<Void, Void, State>() {

            @Override
            protected State doInBackground(Void... voids) {
                retrieveMedia();

                return mCurrentState;
            }

            @Override
            protected void onPostExecute(State current) {
                if (callback != null) {
                    callback.onMusicCatalogReady(current == State.INITIALIZED);
                }
            }
        }.execute();
    }

    private synchronized void buildListsByGenre() {
        ConcurrentMap<String, List<MediaMetadata>> newMusicListByGenre =
                new ConcurrentHashMap<>();

        for (MutableMediaMetadata m : mMusicListById.values()) {
            String genre = m.metadata.getString(MediaMetadata.METADATA_KEY_GENRE);
            List<MediaMetadata> list = newMusicListByGenre.get(genre);
            // 如果没有这种类型，就创建这种类型，如果已经有这种类型，就直接加载这种类型之后。
            if (list == null) {
                list = new ArrayList<>();
                newMusicListByGenre.put(genre, list);
            }
            list.add(m.metadata);
        }
        mMusicListByGenre = newMusicListByGenre;
    }

    private synchronized void retrieveMedia() {
        try {
            if (mCurrentState == State.NON_INITIALIZED) {
                mCurrentState = State.INITIALIZING;
                // mSource是直接通过初始化函数获取的。
                Iterator<MediaMetadata> tracks = mSource.iterator();

                while (tracks.hasNext()) {
                    MediaMetadata item = tracks.next();
                    String musicId = item.getString(MediaMetadata.METADATA_KEY_MEDIA_ID);
                    mMusicListById.put(musicId, new MutableMediaMetadata(musicId, item));
                }

                buildListsByGenre();
                mCurrentState = State.INITIALIZED;
            }
        } finally {
            if (mCurrentState != State.INITIALIZED) {
                // Something bad happened, so we reset state to NON_INITIALIZED to allow
                // retries (eg if the network connection is temporary unavailable)
                mCurrentState = State.NON_INITIALIZED;
            }
        }
    }


    public List<MediaBrowser.MediaItem> getChildren(String mediaId, Resources resources) {
        List<MediaBrowser.MediaItem> mediaItems = new ArrayList<>();

        // 检查mediaId是否含有'|'标志。
        if (!MediaIDHelper.isBrowseable(mediaId)) {
            return mediaItems;
        }

        if (MEDIA_ID_ROOT.equals(mediaId)) {
            mediaItems.add(createBrowsableMediaItemForRoot(resources));

        } else if (MEDIA_ID_MUSICS_BY_GENRE.equals(mediaId)) {
            for (String genre : getGenres()) {
                mediaItems.add(createBrowsableMediaItemForGenre(genre, resources));
            }

        } else if (mediaId.startsWith(MEDIA_ID_MUSICS_BY_GENRE)) {
            String genre = MediaIDHelper.getHierarchy(mediaId)[1];
            for (MediaMetadata metadata : getMusicsByGenre(genre)) {
                mediaItems.add(createMediaItem(metadata));
            }

        } else {
            LogHelper.w(TAG, "Skipping unmatched mediaId: ", mediaId);
        }
        return mediaItems;
    }

    // 获取表头。
    private MediaBrowser.MediaItem createBrowsableMediaItemForRoot(Resources resources) {

        MediaDescription description = new MediaDescription.Builder()
                .setMediaId(MEDIA_ID_MUSICS_BY_GENRE)
                .setTitle(resources.getString(R.string.browse_genres))
                .setSubtitle(resources.getString(R.string.browse_genre_subtitle))
                .setIconUri(Uri.parse("android.resource://com.android.guide/drawable/ic_by_genre"))
                .build();

        return new MediaBrowser.MediaItem(description, MediaBrowser.MediaItem.FLAG_BROWSABLE);

    }

    private MediaBrowser.MediaItem createBrowsableMediaItemForGenre(String genre, Resources resources) {
        MediaDescription description = new MediaDescription.Builder()
                .setMediaId(createMediaID(null, MEDIA_ID_MUSICS_BY_GENRE, genre))
                .setTitle(genre)
                .setSubtitle(resources.getString(R.string.browse_musics_by_genre_subtitle, genre))
                .build();

        return new MediaBrowser.MediaItem(description, MediaBrowser.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowser.MediaItem createMediaItem(MediaMetadata metadata) {
        // Since mediaMetadata fields are immutable, we need to create a copy, so we
        // can set a hierarchy-aware mediaID. We will need to know the media hierarchy
        // when we get a onPlayFromMusicID call, so we can create the proper queue based
        // on where the music was selected from (by artist, by genre, random, etc)
        String genre = metadata.getString(MediaMetadata.METADATA_KEY_GENRE);
        String hierarchyAwareMediaID = MediaIDHelper.createMediaID(
                metadata.getDescription().getMediaId(), MEDIA_ID_MUSICS_BY_GENRE, genre);

        MediaMetadata copy = new MediaMetadata.Builder(metadata)
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                .build();

        return new MediaBrowser.MediaItem(copy.getDescription(), MediaBrowser.MediaItem.FLAG_BROWSABLE);
    }
}
