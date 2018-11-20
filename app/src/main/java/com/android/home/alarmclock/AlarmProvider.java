package com.android.home.alarmclock;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AlarmProvider extends ContentProvider {

    private SQLiteOpenHelper mOpenHelper;

    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;
    private static final UriMatcher sURLMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURLMatcher.addURI("com.android.home.alarmclock", "alarm", ALARMS);
        sURLMatcher.addURI("com.android.home.alarmclock", "alarm/#", ALARMS_ID);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "alarms.db";
        private static final int DATABASE_VERSION = 5;


        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE alarms (" +
                    "_id INTEGER PRIMARY KEY," +
                    "hour INTEGER, " +
                    "minutes INTEGER, " +
                    "daysofweek INTEGER, " +
                    "alarmtime INTEGER, " +
                    "enabled INTEGER, " +
                    "vibrate INTEGER, " +
                    "message TEXT, " +
                    "alert TEXT);");

            // Insert default alarms.
            String insertMe = "INSERT INTO alarms " +
                    "(hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, " +
                    "alert) VALUES ";
            db.execSQL(insertMe + "(7, 0, 127, 0, 0, 1, '', '');");
            db.execSQL(insertMe + "(8, 30, 31, 0, 0, 1, '', '');");
            db.execSQL(insertMe + "(9, 00, 0, 0, 0, 1, '', '');");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (Log.LOGV) Log.v(
                    "Upgrading alarms database from version " +
                            oldVersion + " to " + newVersion +
                            ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS alarms");
            onCreate(db);
        }
    }

    public AlarmProvider() {

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Generate the body of the query.
        int match = sURLMatcher.match(uri);
        switch (match) {
            case ALARMS:
                qb.setTables("alarms");
                break;
            case ALARMS_ID:
                qb.setTables("alarms");
                qb.appendWhere("_id=");
                // Uri通过/分解，下面将会获取数字。
                qb.appendWhere(uri.getPathSegments().get(1));
                break;

            default:
                throw  new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);

        if (ret == null) {
            if (Log.LOGV) Log.v("Alarms.query: failed");
        } else {
            // 通知ContentResolver查询完成。
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sURLMatcher.match(uri);
        switch (match) {
            case ALARMS:
                return "vnd.android.cursor.dir/alarms";
            case ALARMS_ID:
                return "vnd.android.cursor.item/alarms";
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues initialValues) {
        if (sURLMatcher.match(uri) != ALARMS) {
            throw new IllegalArgumentException("Cannot insert into URL: " + uri);
        }

        ContentValues values;
        if (initialValues != null)
            values = new ContentValues(initialValues);
        else
            values = new ContentValues();

        if (!values.containsKey(Alarm.Columns.HOUR))
            values.put(Alarm.Columns.HOUR, 0);

        if (!values.containsKey(Alarm.Columns.MINUTES))
            values.put(Alarm.Columns.MINUTES, 0);

        if (!values.containsKey(Alarm.Columns.DAYS_OF_WEEK))
            values.put(Alarm.Columns.DAYS_OF_WEEK, 0);

        if (!values.containsKey(Alarm.Columns.ALARM_TIME))
            values.put(Alarm.Columns.ALARM_TIME, 0);

        if (!values.containsKey(Alarm.Columns.ENABLED))
            values.put(Alarm.Columns.ENABLED, 0);

        if (!values.containsKey(Alarm.Columns.VIBRATE))
            values.put(Alarm.Columns.VIBRATE, 1);

        if (!values.containsKey(Alarm.Columns.MESSAGE))
            values.put(Alarm.Columns.MESSAGE, "");

        if (!values.containsKey(Alarm.Columns.ALERT))
            values.put(Alarm.Columns.ALERT, "");

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert("alarms", Alarm.Columns.MESSAGE, values);

        if (rowId < 0) {
            throw new SQLException("Failed to insert row into " + uri);
        }
        if (Log.LOGV) Log.v("Added alarm rowId = " + rowId);

        Uri newUri = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(newUri, null);

        return newUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        long rowId = 0;

        switch (sURLMatcher.match(uri)) {
            case ALARMS:
                count = db.delete("alarms", selection, selectionArgs);
                break;
            case ALARMS_ID:
                String segment = uri.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                if (TextUtils.isEmpty(selection)) {
                    selection = "_id=" + segment;
                } else {
                    selection = "_id=" + segment + " AND (" + selection + ")";
                }
                count = db.delete("alarms", selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        long rowId = 0;
        int match = sURLMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match) {
            case ALARMS_ID: {
                String segment = uri.getPathSegments().get(1);
                Log.v("update segment = " + segment);
                rowId = Long.parseLong(segment);
                count = db.update("alarms", values, "_id=" + rowId, null);
                break;
            }
            default: {
                throw new UnsupportedOperationException(
                        "Cannot update URL: " + uri);
            }
        }
        if (Log.LOGV) Log.v("*** notifyChange() rowId: " + rowId + " url " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
