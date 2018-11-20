package com.android.home.alarmclock;

import android.util.Config;

/**
 * package-level logging flag
 */

class Log {
    public final static String LOGTAG = "AlarmClock";

    static final boolean LOGV = AlarmClock.DEBUG ? Config.LOGD : Config.LOGV;

    static void v(String logMe) {
        android.util.Log.v(LOGTAG, /* SystemClock.uptimeMillis() + " " + */ logMe);
    }

    static void e(String logMe) {
        android.util.Log.e(LOGTAG, logMe);
    }

    static void e(String logMe, Exception ex) {
        android.util.Log.e(LOGTAG, logMe, ex);
    }
}
