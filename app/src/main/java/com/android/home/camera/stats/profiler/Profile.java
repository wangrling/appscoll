package com.android.home.camera.stats.profiler;

/**
 * A profile is the primary mechanism used to start, stop,
 * and mark the duration of various things within a method.
 */

public interface Profile {

    /**
     * Start, or restart the timers associated with
     * instance
     */
    public Profile start();

    /**
     * Mark an empty event at the current time.
     */
    public void mark();

    /**
     * Mark something at the current time.
     */
    public void mark(String reason);

    /**
     * Stop the profile.
     */
    public void stop();

    /**
     * Stop the profile for a given reason.
     */
    public void stop(String reason);
}
