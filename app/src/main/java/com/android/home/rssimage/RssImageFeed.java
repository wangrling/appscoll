package com.android.home.rssimage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * This activity displays 500px current featured images. It uses a service running
 * a background thread to download 500px "featured image" RSS feed.
 * <p>
 * An IntentHandler is used to communicate between the active Fragment and this activity.
 * This pattern simulates some of the communication used between activities, and
 * allows this activity to make choices of how to manage the fragments.
 */


public class RssImageFeed extends FragmentActivity implements
        FragmentManager.OnBackStackChangedListener {

    // A handle to the main screen view.
    View mMainView;

    // An instance of the status broadcast receiver.
    DownloadStateReceiver mDownloadStateReceiver;

    // Tracks whether Fragments are displaying side-by-side
    boolean mSideBySide;

    // Instantiates a new broadcast receiver for handling Fragment state.
    private FragmentDisplayer mFragmentDisplayer = new FragmentDisplayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Sets fullscreen-related flags for the display
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);

        super.onCreate(savedInstanceState);

        mMainView = getLayoutInflater().inflate(R.layout.fragment_host, null);

        // Sets the content view for the Activity.
        setContentView(mMainView);

        /**
         * Create an intent filter for DownloadStateReceiver that intercepts broadcast Intents.
         */
        // The filter's action is BROADCAST_ACTION.
        IntentFilter statusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);

        // Sets the filter's category to DEFAULT
        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Instantiates a new DownloadStateReceiver
        mDownloadStateReceiver = new DownloadStateReceiver();

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDownloadStateReceiver, statusIntentFilter);

        /**
         * Create intent filters for the FragmentDisplayer.
         */
        // One filter is for the action ACTION_VIEW_IMAGE.
        IntentFilter displayerIntentFilter = new IntentFilter(
                Constants.ACTION_VIEW_IMAGE);

        // Adds a data filter for the HTTP scheme.
        displayerIntentFilter.addDataScheme("http");

        // Registers the receiver.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mFragmentDisplayer, displayerIntentFilter
        );

        // Creates a second filter for ACTION_ZOOM_IMAGE.
        displayerIntentFilter = new IntentFilter(Constants.ACTION_ZOOM_IMAGE);

        // Registers the receiver.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mFragmentDisplayer, displayerIntentFilter);


        // Gets an instance of the support library FragmentManager.
        FragmentManager localFragmentManager = getSupportFragmentManager();

        /*
         * Detects if side-by-side display should be enabled. It's only available on xlarge and
         * sw600dp devices (for example, tablets). The setting in res/values/ is "false", but this
         * is overridden in values-xlarge and values-sw600dp.
         */
        mSideBySide = getResources().getBoolean(R.bool.sideBySide);
    }

    @Override
    public void onBackStackChanged() {

    }

    /**
     * This class uses the BroadcastReceiver framework to detect and handle status messages from
     * the service that downloads URLs.
     */
    private class DownloadStateReceiver extends BroadcastReceiver {

        private DownloadStateReceiver() {
            // Prevent instantiation by other packages.
        }

        /**
         *
         * This method is called by the system when a broadcast Intent is matched by this class'
         * intent filters
         *
         * @param context An Android context
         * @param intent The incoming broadcast Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    /**
     * This class uses the broadcast receiver framework to detect incoming broadcast Intents
     * and change the currently-visible fragment based on the Intent action.
     * It adds or replaces Fragments as necessary, depending on how much screen real-estate is
     * available.
     */
    private class FragmentDisplayer extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
