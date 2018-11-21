package com.android.home.alarmclock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * Clock face picker for the Alarm Clock application.
 */

// 五种界面提供选择。

public class ClockPicker extends Activity implements
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private LayoutInflater mFactory;
    private Gallery mGallery;

    private SharedPreferences mPrefs;
    private View mClock;
    private ViewGroup mClockLayout;
    private int mPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mFactory = LayoutInflater.from(this);

        setContentView(R.layout.clockpicker);

        mGallery = findViewById(R.id.gallery);

        mGallery.setAdapter(new ClockAdapter());

        mGallery.setOnItemSelectedListener(this);
        mGallery.setOnItemClickListener(this);

        mPrefs = getSharedPreferences(AlarmClock.PREFERENCES, 0);

        int face = mPrefs.getInt(AlarmClock.PREF_CLOCK_FACE, 0);

        if (face < 0 || face >= AlarmClock.CLOCKS.length) face = 0;

        mClockLayout = (ViewGroup) findViewById(R.id.clock_layout);

        mClockLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectClock(mPosition);
            }
        });
    }

    private synchronized  void selectClock(int position) {
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt(AlarmClock.PREF_CLOCK_FACE, position);
        ed.commit();

        setResult(RESULT_OK);

        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectClock(position);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mClock != null) {
            mClockLayout.removeView(mClock);

            mClock = mFactory.inflate(AlarmClock.CLOCKS[position], null);
            mClockLayout.addView(mClock, 0);
            mPosition = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class ClockAdapter extends BaseAdapter {

        public ClockAdapter() {

        }

        @Override
        public int getCount() {
            return AlarmClock.CLOCKS.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View clock = mFactory.inflate(AlarmClock.CLOCKS[position], null);
                    return clock;
        }
    }
}
