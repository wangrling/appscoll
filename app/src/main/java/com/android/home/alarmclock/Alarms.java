package com.android.home.alarmclock;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;

import java.util.Calendar;

/**
 * The Alarms provider supplies info about Alarm Clock settings.
 */


public class Alarms {

    // This action triggers the AlarmReceiver as well as the AlarmKlaxon. It
    // is a public action used in the manifest for receiving Alarm broadcasts
    // from the alarm manager.
    public static final String ALARM_ALERT_ACTION = "com.android.alarmclock.ALARM_ALERT";

    // This is a private action used by the AlarmKlaxon to update the UI to
    // show the alarm has been killed.
    public static final String ALARM_KILLED = "alarm_killed";

    // Extra in the ALARM_KILLED intent to indicate to the user how long the
    // alarm played before being killed.
    public static final String ALARM_KILLED_TIMEOUT = "alarm_killed_timeout";

    // This string is used to indicate a silent alarm in the db.
    public static final String ALARM_ALERT_SILENT = "silent";

    // This intent is sent from the notification when the user cancels the
    // snooze alert.
    public static final String CANCEL_SNOOZE = "cancel_snooze";

    // This string is used when passing an Alarm object through an intent.
    public static final String ALARM_INTENT_EXTRA = "intent.extra.alarm";

    // This extra is the raw Alarm object data. It is used in the
    // AlarmManagerService to avoid a ClassNotFoundException when filling in
    // the Intent extras.
    public static final String ALARM_RAW_DATA = "intent.extra.alarm_raw";

    // This string is used to identify the alarm id passed to SetAlarm from the
    // list of alarms.
    public static final String ALARM_ID = "alarm_id";

    final static String PREF_SNOOZE_ID = "snooze_id";
    final static String PREF_SNOOZE_TIME = "snooze_time";

    private final static String DM12 = "E h:mm aa";
    private final static String DM24 = "E k:mm";

    private final static String M12 = "h:mm aa";
    // Shared with DigitalClock
    final static String M24 = "kk:mm";

    /**
     * Create a new Alarm.
     */
    public static Uri addAlarm(ContentResolver contentResolver) {
        ContentValues values = new ContentValues();

        // 默认设置为8.
        values.put(Alarm.Columns.HOUR, 8);

        return contentResolver.insert(Alarm.Columns.CONTENT_URI, values);
    }

    /**
     * Removes an existing Alarm.  If this alarm is snoozing, disables
     * snooze.  Sets next alert.
     */
    public static void deleteAlarm(Context context, int alarmId) {
        ContentResolver contentResolver = context.getContentResolver();
        /**
         * If alarm is snoozing, lost it.
         */
        // snoozing 是什么意思？ 打盹。
        disableSnoozeAlert(context, alarmId);

        Uri uri = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
        contentResolver.delete(uri, "", null);

        setNextAlert(context);
    }

    /**
     * Queries all alarms
     *
     * @return cursor over all alarms.
     */
    public static Cursor getAlarmsCursor(ContentResolver contentResolver) {
        return contentResolver.query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS,
                null, null, Alarm.Columns.DEFAULT_SORT_ORDER);
    }

    // Private method to get a more limited set of alarms from the database.
    // 过滤出开启的闹钟。
    private static Cursor getFilteredAlarmCursor(
            ContentResolver contentResolver) {
        return contentResolver.query(Alarm.Columns.CONTENT_URI,
                Alarm.Columns.ALARM_QUERY_COLUMNS, Alarm.Columns.WHERE_ENABLED, null, null);
    }

    /**
     * Return an alarm object representing the alarm id in the database.
     *
     * Returns null if no alarm exists.
     */
    public static Alarm getAlarm(ContentResolver contentResolver, int alarmId) {
        Cursor cursor = contentResolver.query(
                ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId),
                Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);

        Alarm alarm = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                alarm = new Alarm(cursor);
            }
            cursor.close();
        }

        return alarm;
    }

    /**
     * A convenience method to set an alarm in the Alarms
     * content provider.
     *
     * @param id             corresponds to the _id column
     * @param enabled        corresponds to the ENABLED column
     * @param hour           corresponds to the HOUR column
     * @param minutes        corresponds to the MINUTES column
     * @param daysOfWeek     corresponds to the DAYS_OF_WEEK column
     * @param vibrate        corresponds to the VIBRATE column
     * @param message        corresponds to the MESSAGE column
     * @param alert          corresponds to the ALERT column
     * @return Time when the alarm will fire.
     */
    public static long setAlarm(
            Context context, int id, boolean enabled, int hour, int minutes,
            Alarm.DaysOfWeek daysOfWeek, boolean vibrate, String message,
            String alert) {

        ContentValues values = new ContentValues(8);
        ContentResolver resolver = context.getContentResolver();

        // Set the alarm_time value if this alarm does not repeat. This will be
        // used later to disable expired alarms.
        long time = 0;
        if (!daysOfWeek.isRepeatSet()) {
            time = calculateAlarm(hour, minutes, daysOfWeek).getTimeInMillis();
        }

        if (Log.LOGV) Log.v(
                "**  setAlarm * idx " + id + " hour " + hour + " minutes " +
                        minutes + " enabled " + enabled + " time " + time);

        values.put(Alarm.Columns.ENABLED, enabled ? 1 : 0);
        values.put(Alarm.Columns.HOUR, hour);
        values.put(Alarm.Columns.MINUTES, minutes);
        values.put(Alarm.Columns.ALARM_TIME, time);
        values.put(Alarm.Columns.DAYS_OF_WEEK, daysOfWeek.getCoded());
        values.put(Alarm.Columns.VIBRATE, vibrate);
        values.put(Alarm.Columns.MESSAGE, message);
        values.put(Alarm.Columns.ALERT, alert);
        resolver.update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, id),
                values, null, null);

        long timeInMillis =
                calculateAlarm(hour, minutes, daysOfWeek).getTimeInMillis();

        if (enabled) {
            // If this alarm fires before the next snooze, clear the snooze to
            // enable this alarm.
            SharedPreferences prefs = context.getSharedPreferences(
                    AlarmClock.PREFERENCES, 0);

            long snoozeTime = prefs.getLong(PREF_SNOOZE_TIME, 0);
            if (timeInMillis < snoozeTime) {
                clearSnoozePreference(context, prefs);
            }
        }

        setNextAlarm(context);

        return timeInMillis;
    }

    /**
     * A convenience method to enable or disable an alarm.
     *
     * @param id             corresponds to the _id column
     * @param enabled        corresponds to the ENABLED column
     */
    public static void enableAlarm(
            final Context context, final int id, boolean enabled) {

        enabledAlarmInternal(context, id, enabled);
        setNextAlert(context);
    }

    private static void enabledAlarmInternal(final Context context,
                                             final int id, boolean enabled) {

    }

    private static void enableAlarmInternal(final Context context,
                                            final Alarm alarm, boolean enabled) {
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues(2);

        values.put(Alarm.Columns.ENABLED, enabled ? 1 : 0);

        // If we are enabling the alarm, calculate alarm time since the time
        // value in Alarm may be old.
        if (enabled) {
            long time = 0;
            if (!alarm.daysOfWeek.isRepeatSet()) {
                time = calculateAlarm(alarm.hour, alarm.minutes, alarm.daysOfWeek).getTimeInMillis();
            }

            values.put(Alarm.Columns.ALARM_TIME, time);
        }

        resolver.update(ContentUris.withAppendedId(
                Alarm.Columns.CONTENT_URI, alarm.id), values, null, null);
    }

    /**
     * @return true if clock is set to 24-hour mode
     */
    static boolean get24HourMode(final Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }


    public static String formatTime(AlarmClock alarmClock, Calendar cal) {
        return null;
    }

    public static void deleteAlarm(AlarmClock alarmClock, int id) {
    }
}
