package com.android.home.rssimage;

import java.util.Locale;

/**
 * Constants used by multiple classes in this package.
 */

public final class Constants {

    // Set to true to turn on verbose logging
    public static final boolean LOGV = false;

    // Set to true to turn on debug logging
    public static final boolean LOGD = true;

    // Custom actions.
    public static final String ACTION_VIEW_IMAGE =
            "com.android.guide.rssimage.ACTION_VIEW_IMAGE";
    public static final String ACTION_ZOOM_IMAGE =
            "com.android.guide.rssimage.ACTION_ZOOM_IMAGE";

    // Defines a custom Intent action.
    public static final String BROADCAST_ACTION = "com.android.home.rssimage.BROADCAST";

    // Fragment tags.
    public static final String PHOTO_FRAGMENT_TAG =
            "com.android.home.rssimage.PHOTO_FRAGMENT_TAG";
    public static final String THUMBNAIL_FRAGMENT_TAG =
            "com.android.home.rssimage.THUMBNAIL_FRAGMENT_TAG";

    // Defines the key for the status "extra" in an Intent.
    public static final String EXTENDED_DATA_STATUS = "com.android.home.rssimage.STATUS";

    // Defines the key for the log "extra" in an Intent.
    public static final String EXTENDED_STATUS_LOG = "com.android.home.rssimage.LOG";

    // Defines the key for storing fullscreen state.
    public static final String EXTENDED_FULLSCREEN =
            "com.android.guide.rssimage.EXTENDED_FULLSCREEN";

    /*
     * A user-agent string that's sent to the HTTP site. It includes information about the device
     * and the build that the device is running.
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android "
            + android.os.Build.VERSION.RELEASE + ";"
            + Locale.getDefault().toString() + "; " + android.os.Build.DEVICE
            + "/" + android.os.Build.ID + ")";

    // The download is starting
    public static final int STATE_ACTION_STARTED = 0;

    // The background thread is connecting to the RSS feed
    public static final int STATE_ACTION_CONNECTING = 1;

    // The background thread is parsing the RSS feed
    public static final int STATE_ACTION_PARSING = 2;

    // The background thread is writing data to the content provider
    public static final int STATE_ACTION_WRITING = 3;

    // The background thread is done
    public static final int STATE_ACTION_COMPLETE = 4;

    // The background thread is doing logging
    public static final int STATE_LOG = -1;

    public static final CharSequence BLANK = " ";
}
