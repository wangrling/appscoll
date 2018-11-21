package com.android.home.alarmclock;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.widget.TimePicker;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * Manages each alarm
 */

public class SetAlarm extends PreferenceActivity implements
        TimePickerDialog.OnTimeSetListener {

    private EditTextPreference mLabel;
    private Preference mTimePref;
    private AlarmPreference mAlarmPref;
    private CheckBoxPreference mVibratePref;
    private RepeatPreference mRepeatPref;
    private MenuItem mDeleteAlarmItem;
    private MenuItem mTestAlarmItem;

    private int     mId;
    private boolean mEnabled;
    private int     mHour;
    private int     mMinutes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.alarm_prefs);
    }

    /**
     * Set an alarm.  Requires an Alarms.ALARM_ID to be passed in as an
     * extra. FIXME: Pass an Alarm object like every other Activity.
     */


    public static void popAlarmSetToast(AlarmClock alarmClock, int hour, int minutes, Alarm.DaysOfWeek daysOfWeek) {
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
