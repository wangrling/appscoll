package com.android.home.alarmclock;

import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.*;
import android.provider.Settings;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * Settings for the Alarm Clock.
 */

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    private static final int ALARM_STREAM_TYPE_BIT =
            1 << AudioManager.STREAM_ALARM;

    private static final String KEY_ALARM_IN_SILENT_MODE =
            "alarm_in_silent_mode";
    static final String KEY_ALARM_SNOOZE =
            "snooze_duration";
    static final String KEY_VOLUME_BEHAVIOR =
            "volume_button_setting";
    static final String KEY_DEFAULT_RINGTONE =
            "default_ringtone";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.alarm_settings);

        final AlarmPreference ringtone =
                (AlarmPreference) findPreference(KEY_DEFAULT_RINGTONE);
        Uri alert = RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_ALARM);
        if (alert != null) {
            ringtone.setAlert(alert);
        }
        ringtone.setChangeDefault();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (KEY_ALARM_IN_SILENT_MODE.equals(preference.getKey())) {
            CheckBoxPreference pref = (CheckBoxPreference) preference;
            int ringerModeStreamTypes = Settings.System.getInt(
                    getContentResolver(), Settings.System.MODE_RINGER_STREAMS_AFFECTED, 0);

            if (pref.isChecked()) {
                ringerModeStreamTypes &= ~ALARM_STREAM_TYPE_BIT;
            } else {
                ringerModeStreamTypes |= ALARM_STREAM_TYPE_BIT;
            }

            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ListPreference listPref = (ListPreference) preference;
        final int idx = listPref.findIndexOfValue((String) newValue);
        listPref.setSummary(listPref.getEntries()[idx]);
        return true;
    }

    private void refresh() {
        final CheckBoxPreference alarmInSilentModePref =
                (CheckBoxPreference) findPreference(KEY_ALARM_IN_SILENT_MODE);
        final int silentModeStreams =
                Settings.System.getInt(getContentResolver(),
                        Settings.System.MODE_RINGER_STREAMS_AFFECTED, 0);
        alarmInSilentModePref.setChecked(
                (silentModeStreams & ALARM_STREAM_TYPE_BIT) == 0);

        final ListPreference snooze =
                (ListPreference) findPreference(KEY_ALARM_SNOOZE);
        snooze.setSummary(snooze.getEntry());
        snooze.setOnPreferenceChangeListener(this);
    }
}
