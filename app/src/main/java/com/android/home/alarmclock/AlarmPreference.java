package com.android.home.alarmclock;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.util.AttributeSet;
import com.android.home.R;

/**
 * The RingtonePreference does not have a way to get/set the current ringtone so
 * we override onSaveRingtone and onRestoreRingtone to get the same behavior.
 */

public class AlarmPreference extends RingtonePreference {

    private Uri mAlert;
    private boolean mChangeDefault;

    public AlarmPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSaveRingtone(Uri ringtoneUri) {
        setAlert(ringtoneUri);

        if (mChangeDefault) {
            // update the default alert in the system.
            // 设置到系统属性中。
            Settings.System.putString(getContext().getContentResolver(),
                    Settings.System.ALARM_ALERT,
                    ringtoneUri == null ? null : ringtoneUri.toString());
        }
    }

    @Override
    protected Uri onRestoreRingtone() {
        if (RingtoneManager.isDefault(mAlert)) {
            return RingtoneManager.getActualDefaultRingtoneUri(getContext(),
                    RingtoneManager.TYPE_ALARM);
        }

        return mAlert;
    }

    public void setAlert(Uri alert) {
        mAlert = alert;
        if (alert != null) {
            final Ringtone r = RingtoneManager.getRingtone(getContext(), alert);

            if (r != null) {
                setSummary(r.getTitle(getContext()));
            }
        } else {
            setSummary(R.string.silent_alarm_summary);
        }
    }

    public String getAlertString() {
        if (mAlert != null) {
            return mAlert.toString();
        }

        return Alarms.ALARM_ALERT_SILENT;
    }

    public void setChangeDefault() {
        mChangeDefault = true;
    }
}
