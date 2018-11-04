package com.android.home.universalmusic.model;

import android.media.MediaMetadata;
import android.support.annotation.Nullable;
import android.text.TextUtils;


/**
 * Holder class that encapsulates a MediaMetadata and allows the actual metadata
 * to be modified without requiring to rebuild the collections the metadata is in.
 */

public class MutableMediaMetadata {

    public MediaMetadata metadata;
    public final String trackId;

    public MutableMediaMetadata(String trackId, MediaMetadata metadata) {
        this.metadata = metadata;
        this.trackId = trackId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != MutableMediaMetadata.class) {
            return false;
        }

        MutableMediaMetadata that = (MutableMediaMetadata) obj;

        return TextUtils.equals(trackId, that.trackId);
    }

    @Override
    public int hashCode() {
        return trackId.hashCode();
    }
}
