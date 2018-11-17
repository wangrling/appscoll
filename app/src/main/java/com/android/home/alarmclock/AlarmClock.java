package com.android.home.alarmclock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.android.home.ItemListener;

/**
 * AlarmClock application.
 */

public class AlarmClock extends Activity implements ItemListener.OnItemClickListener {

    static final String PREFERENCES = "AlarmClock";
    static final String PREF_CLOCK_FACE = "face";
    static final String PREF_SHOW_CLOCK = "show_clock";

    /** Cap alarm count at this number */
    static final int MAX_ALARM_COUNT = 12;

    /** This must be false for production.  If true, turns on logging,
     test code, etc. */
    static final boolean DEBUG = true;

    private SharedPreferences mPrefs;
    private LayoutInflater mFactory;
    private ViewGroup mClockLayout;
    private View mClock = null;
    private ListView mAlarmsList;
    private Cursor mCursor;

    private String mAm, mPm;

    /**
     * Which clock face to show
     */
    private int mFace = -1;


    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onLongItemClick(View view, int position) {

    }
}
