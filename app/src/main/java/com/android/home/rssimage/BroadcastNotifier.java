package com.android.home.rssimage;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastNotifier {

    private LocalBroadcastManager mBroadcaster;

    /**
     * Creates a BroadcastNotifier containing an instance of LocalBroadcastManager.
     * LocalBroadcastManager is more efficient than BroadcastManager; because it only
     * broadcasts to components within the app, it doesn't have to do parceling and so forth.
     *
     * @param context a Context from which to get the LocalBroadcastManager
     */
    public BroadcastNotifier(Context context) {

        // Gets an instance of the support library local broadcast manager.
        mBroadcaster = LocalBroadcastManager.getInstance(context);
    }

    public void broadcastIntentWithState(int status) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app.
        localIntent.setAction(Constants.BROADCAST_ACTION);

        // Puts the status into the Intent.
        localIntent.putExtra(Constants.EXTENDED_DATA_STATUS, status);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent.
        mBroadcaster.sendBroadcast(localIntent);
    }

    /**
     * Uses LocalBroadcastManager to send an {@link String} containing a logcat message.
     * {@link Intent} has the action {@code BROADCAST_ACTION} and the category {@code DEFAULT}.
     *
     * @param logData a {@link String} to insert into the log.
     */
    public void notifyProgress(String logData) {

        Intent localIntent = new Intent();

        // The intent contains the custom broadcast action for this app.
        localIntent.setAction(Constants.BROADCAST_ACTION);

        localIntent.putExtra(Constants.EXTENDED_DATA_STATUS, -1);

        // Puts log data into the Intent.
        localIntent.putExtra(Constants.EXTENDED_STATUS_LOG, logData);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent.
        mBroadcaster.sendBroadcast(localIntent);
    }
}
