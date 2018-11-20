package com.android.home.alarmclock;

import android.content.ContentResolver;
import android.database.Cursor;

/**
 * The Alarms provider supplies info about Alarm Clock settings.
 */


public class Alarms {



    // This string is used to indicate a silent alarm in the db.
    public static final String ALARM_ALERT_SILENT = "silent";



    public static Cursor getAlarmsCursor(ContentResolver contentResolver) {
        return null;
    }
}
