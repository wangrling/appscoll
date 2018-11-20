package com.android.home.alarmclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.home.ItemListener;
import com.android.home.R;

import java.text.DateFormatSymbols;

/**
 * AlarmClock application.
 *
 * 包括一个clock界面，可以添加闹钟。
 * 以及一个ListView显示设定的闹钟，闹钟是用Cursor来访问的。
 */

public class AlarmClock extends Activity implements AdapterView.OnItemClickListener {

    public static final String TAG = "AlarmClock";

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

    /**
     * TODO: it would be nice for this to live in an xml config file.
     */
    static final int[] CLOCKS = {
            R.layout.clock_basic_bw,
            R.layout.clock_googly,
            R.layout.clock_droid2,
            R.layout.clock_droids,
            R.layout.digital_clock
    };

    // 为ListView准备的。
    private class AlarmTimeAdapter extends CursorAdapter {

        public AlarmTimeAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View ret = mFactory.inflate(R.layout.alarm_time, parent, false);


            return ret;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] ampm = new DateFormatSymbols().getAmPmStrings();
        Log.d(TAG, "ampm = " + ampm.toString());

        mAm = ampm[0];
        mPm = ampm[1];

        mFactory = LayoutInflater.from(this);
        mPrefs = getSharedPreferences(PREFERENCES, 0);
        mCursor = Alarms.getAlarmsCursor(getContentResolver());

        updateLayout();

        setClockVisibility(mPrefs.getBoolean(PREF_SHOW_CLOCK, true));
    }

    private final Handler mHandler = new Handler();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Send a message to avoid a possible ANR.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                updateLayout();
                inflateClock();
                setClockVisibility(mPrefs.getBoolean(PREF_SHOW_CLOCK, true));
            }
        });
    }

    private void updateLayout() {
        setContentView(R.layout.alarm_clock);

        mAlarmsList = (ListView) findViewById(R.id.alarms_list);
        mAlarmsList.setAdapter(new AlarmTimeAdapter(this, mCursor));
        mAlarmsList.setVerticalScrollBarEnabled(true);
        mAlarmsList.setOnItemClickListener(this);
        mAlarmsList.setOnCreateContextMenuListener(this);

        mClockLayout = (ViewGroup) findViewById(R.id.clock_layout);
        mClockLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent =
                        new Intent(AlarmClock.this, ClockPicker.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // clock显示的样式。
        int face = mPrefs.getInt(PREF_CLOCK_FACE, 0);
        if (mFace != face) {
            if (face < 0 || face >= AlarmClock.CLOCKS.length) {
                mFace = 0;
            } else {
                mFace = face;
            }

            inflateClock();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ToastMaster.cancelToast();
        // mCursor.deactivate();
    }

    protected void inflateClock() {
        if (mClock != null) {
            mClockLayout.removeView(mClock);
        }

        // 把clock界面inflate到clock layout上面。
        LayoutInflater.from(this).inflate(CLOCKS[mFace], mClockLayout);
        mClock = findViewById(R.id.clock);

        TextView am = (TextView) findViewById(R.id.am);
        TextView pm = findViewById(R.id.pm);

        if (am != null) {
            am.setText(mAm);
        }

        if (pm != null) {
            pm.setText(mPm);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate our menu.
        // getMenuInflater().inflate(R.menu.clock_main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Intent intent = new Intent(this, SetAlarm.class);
        // intent.putExtra(Alarms.ALARM_ID, (int) id);

        // startActivity(intent);
    }

    private boolean getClockVisibility() {
        return mClockLayout.getVisibility() == View.VISIBLE;
    }

    private void setClockVisibility(boolean visible) {
        mClockLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void saveClockVisibility() {
        mPrefs.edit().putBoolean(PREF_SHOW_CLOCK, getClockVisibility()).commit();
    }
}
