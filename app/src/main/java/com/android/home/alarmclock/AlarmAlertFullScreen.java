package com.android.home.alarmclock;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * Alarm Clock alarm alert: pops visible indicator and plays alarm
 * tone. This activity is the full screen version which shows over the lock
 * screen with the wallpaper as the background.
 */


public class AlarmAlertFullScreen extends Activity {

    // These defaults must match the values in res/xml/settings.xml
    private static final String DEFAULT_SNOOZE = "10";
    private static final String DEFAULT_VOLUME_BEHAVIOR = "2";
    protected static final String SCREEN_OFF = "screen_off";

    protected Alarm mAlarm;

    private int mVolumeBehavior;

    // Receives the ALARM_KILLED action from the AlarmKlaxon.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Alarm alarm = intent.getParcelableExtra(Alarms.ALARM_INTENT_EXTRA);
            if (alarm != null && mAlarm.id == alarm.id) {
                dismiss(true);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 和AlarmReceiver对接上。
        mAlarm = getIntent().getParcelableExtra(Alarms.ALARM_INTENT_EXTRA);

        // Get the volume/camera button behavior setting
        final String vol = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(SettingsActivity.KEY_VOLUME_BEHAVIOR, DEFAULT_VOLUME_BEHAVIOR);

        mVolumeBehavior = Integer.parseInt(vol);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Turn on the screen unless we are being launched from the AlarmAlert
        // subclass.
        if (!getIntent().getBooleanExtra(SCREEN_OFF, false)) {
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        updateLayout();

        // Register to get the alarm killed intent.
        registerReceiver(mReceiver, new IntentFilter(Alarms.ALARM_KILLED));
    }

    private void setTitle() {
        String label = mAlarm.getLabelOrDefault(this);
        TextView title = (TextView) findViewById(R.id.alertTitle);
        title.setText(label);
    }

    private void updateLayout() {
        LayoutInflater inflater = LayoutInflater.from(this);

        setContentView(inflater.inflate(R.layout.alarm_alert, null));

        /**
         * Set clock face.
         */
        SharedPreferences settings =
                getSharedPreferences(AlarmClock.PREFERENCES, 0);
        int face = settings.getInt(AlarmClock.PREF_CLOCK_FACE, 0);
        if (face < 0 && face >= AlarmClock.CLOCKS.length) {
            face = 0;
        }

        ViewGroup clockView = (ViewGroup) findViewById(R.id.clockView);
        inflater.inflate(AlarmClock.CLOCKS[face], clockView);

        View clockLayout = findViewById(R.id.clock);
        if (clockLayout instanceof DigitalClock) {
            ((DigitalClock) clockLayout).setAnimate();
        }

        /* snooze behavior: pop a snooze confirmation view, kick alarm
           manager. */
        Button snooze = (Button) findViewById(R.id.snooze);
        snooze.requestFocus();
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snooze();
            }
        });

        /**
         * Dismiss button: close notification.
         */
        findViewById(R.id.dismiss).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss(false);
                    }
                }
        );

        /* Set the title from the passed in alarm */
        setTitle();
    }

    // Attempt t snooze this alert.
    private void snooze() {

    }

    private void dismiss(boolean killed) {

    }
}
