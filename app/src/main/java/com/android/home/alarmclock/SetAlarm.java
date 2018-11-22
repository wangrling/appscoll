package com.android.home.alarmclock;

import android.app.TimePickerDialog;
import android.content.Intent;
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

        // 编辑标签。
        mLabel = (EditTextPreference) findPreference("label");
        mLabel.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        // Set the summary baed on the new label.
                        preference.setSummary((String) newValue);

                        return true;
                    }
                });

        mTimePref = findPreference("time");

        // 表示铃声的类型。
        mAlarmPref = (AlarmPreference) findPreference("alarm");
        mVibratePref = (CheckBoxPreference) findPreference("vibrate");
        mRepeatPref = (RepeatPreference) findPreference("setRepeat");

        Intent i = getIntent();
        mId = i.getIntExtra(Alarms.ALARM_ID, -1);
        if (Log.LOGV) {
            Log.v("In SetAlarm, alarm id = " + mId);
        }

        /* load alarm details from database */
        // 先查看之前的数据中的闹钟设置。
        Alarm alarm = Alarms.getAlarms(getContentResolver(), mId);

        // Bad alarm, bail to avoid a NPE.
        if (alarm == null) {
            finish();
            return;
        }

        mEnabled = alarm.enabled;
        mLabel.setText(alarm.label);
        mLabel.setSummary(alarm.label);
        mHour = alarm.hour;
        mMinutes = alarm.minutes;

        mRepeatPref.setDaysOfWeek(alarm.daysOfWeek);
        mVibratePref.setChecked(alarm.vibrate);

        // Give the alarm uri to the preference.
        mAlarmPref.setAlert(alarm.alert);
        updateTime();


    }


    private void updateTime() {
        if (Log.LOGV) {
            Log.v("updateTime " + mId);
        }

        mTimePref.setSummary(Alarms.formatTime(this, mHour, mMinutes,
                mRepeatPref.getDaysOfWeek()));
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
