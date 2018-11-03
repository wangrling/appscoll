package com.android.home.camera.app;

import android.app.Application;
import android.content.Context;
import android.os.Debug;
import android.provider.ContactsContract;
import com.android.home.camera.stats.profiler.Profile;
import com.android.home.camera.util.AndroidContext;

/**
 * The Camera application class containing important services and functionality
 * to be used across modules.
 */

public class CameraApp extends Application {
    /**
     * This is for debugging only: If set to true, application will not start
     * until a debugger is attached.
     * <p>
     * Use this if you need to debug code that is executed while the app starts
     * up and it would be too late to attach a debugger afterwards.
     */
    private static final boolean WAIT_FOR_DEBUGGER_ON_START = false;

    @Override
    public void onCreate() {
        super.onCreate();

        if (WAIT_FOR_DEBUGGER_ON_START) {
            Debug.waitForDebugger();
        }

        // Android context must be the first item initialized.
        Context context = getApplicationContext();
        AndroidContext.initialize(context);

        // This will measure and write to the exception handler if
        // the time between any two calls or the total time from
        // start to stop is over 10ms.
        Profile guard;
    }
}
