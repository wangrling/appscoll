package com.android.home.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.home.R;
import com.google.common.primitives.Chars;

import java.util.Calendar;

public class DigitalClock extends LinearLayout {

    private final static String M12 = "h:mm";

    private Calendar mCalendar;
    private String mFormat;
    private TextView mTimeDisplay;
    private AmPm mAmPm;
    private boolean mAnimate;
    private ContentObserver mFormatChangeObserver;
    private boolean mLive = true;
    private boolean mAttached;

    private Context mContext;

    /**
     * Called by system on minute ticks.
     */
    private final Handler mHandler = new Handler();

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //　时区改变。
            if (mLive && intent.getAction().equals(
                    Intent.ACTION_TIMEZONE_CHANGED)) {
                mCalendar = Calendar.getInstance();
            }
        }
    };

    static class AmPm {
        private int mColorOn, mColorOff;

        private LinearLayout mAmPmLayout;

        private TextView mAm, mPm;

        AmPm(View parent) {
            mAmPmLayout = (LinearLayout) parent.findViewById(R.id.am_pm);
            mAm = (TextView)mAmPmLayout.findViewById(R.id.am);
            mPm = (TextView)mAmPmLayout.findViewById(R.id.pm);

            Resources r = parent.getResources();
            mColorOn = r.getColor(R.color.ampm_on);
            mColorOff = r.getColor(R.color.ampm_off);
        }

        void setShowAmPm(boolean show) {
            mAmPmLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        void setIsMorning(boolean isMorning) {
            mAm.setTextColor(isMorning ? mColorOn : mColorOff);
            mPm.setTextColor(isMorning ? mColorOff : mColorOn);
        }
    }

    private class FormatChangeObserver extends ContentObserver {

        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setDateFormat();
            updateTime();
        }
    }


    public DigitalClock(Context context) {
        this(context, null);
        mContext = context;
    }

    public DigitalClock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTimeDisplay = findViewById(R.id.timeDisplay);

        mAmPm = new AmPm(this);

        mCalendar = Calendar.getInstance();

        setDateFormat();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (Log.LOGV) Log.v("onAttachedToWindow " + this);

        if (mAttached) return;
        mAttached = true;

        if (mAnimate) {
            setBackgroundResource(R.drawable.animate_circle);
            /* Start the animation (looped playback by default). */
            ((AnimationDrawable) getBackground()).start();
        }

        if (mLive) {
            /* monitor time ticks, time changed, timezone */
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            mContext.registerReceiver(mIntentReceiver, filter, null, mHandler);
        }

        /* monitor 12/24-hour display preference */
        mFormatChangeObserver = new FormatChangeObserver();
        mContext.getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        updateTime();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (!mAttached)
            return;

        Drawable backgournd = getBackground();
        if (backgournd instanceof AnimationDrawable) {
            ((AnimationDrawable) backgournd).stop();
        }

        if (mLive) {
            mContext.unregisterReceiver(mIntentReceiver);
        }

        mContext.getContentResolver().unregisterContentObserver(
                mFormatChangeObserver);
    }

    void updateTime(Calendar c) {
        mCalendar = c;
        updateTime();
    }

    private void updateTime() {
        if (mLive) {
            mCalendar.setTimeInMillis(System.currentTimeMillis());
        }

        CharSequence newTime = DateFormat.format(mFormat, mCalendar);
        mTimeDisplay.setText(newTime);
        mAmPm.setIsMorning(mCalendar.get(Calendar.AM_PM) == 0);
    }

    private void setDateFormat() {
        mFormat = Alarms.get24HourMode(mContext) ? Alarms.M24 : M12;
        mAmPm.setShowAmPm(mFormat == M12);
    }

    void setAnimate() {
        mAnimate = true;
    }

    void setLive(boolean live) {
        mLive = live;
    }
}
