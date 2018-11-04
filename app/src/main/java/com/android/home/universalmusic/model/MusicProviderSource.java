package com.android.home.universalmusic.model;

import android.media.MediaMetadata;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public interface MusicProviderSource {

    String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";

    Iterator<MediaMetadata> iterator();
}
