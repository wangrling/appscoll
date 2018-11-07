package com.android.home.rssimage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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

    // Tracks whether navigation should be hidden.
    boolean mHideNavigation;

    // Tracks whether the app is in full-screen mode.
    boolean mFullScreen;

    // Tracks the number of Fragments on the back stack.
    // 这个变量也没干什么事情，就是检查用户有没有按back键返回。
    int mPreviousStackCount;

    // Instantiates a new broadcast receiver for handling Fragment state.
    private FragmentDisplayer mFragmentDisplayer = new FragmentDisplayer();

    // Sets a tag to use in logging
    private static final String CLASS_TAG = "RssImageFeed";

    public void setFullScreen(boolean fullScreen) {
        // If full screen is set, sets the fullscreen flag in the Window manager
        getWindow().setFlags(
                fullScreen ? WindowManager.LayoutParams.FLAG_FULLSCREEN : 0,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Sets the global fullscreen flag to the current setting
        mFullScreen = fullScreen;

        // If the platform version is Android 3.0 (Honeycomb) or above
        if (Build.VERSION.SDK_INT >= 11) {

            // Sets the View to be "low profile". Status and navigation bar icons will be dimmed
            int flag = fullScreen ? View.SYSTEM_UI_FLAG_LOW_PROFILE : 0;

            // If the platform version is Android 4.0 (ICS) or above
            if (Build.VERSION.SDK_INT >= 14 && fullScreen) {

                // Hides all of the navigation icons
                flag |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            // Applies the settings to the screen View
            mMainView.setSystemUiVisibility(flag);

            // If the user requests a full-screen view, hides the Action Bar.
            if ( fullScreen ) {
                this.getActionBar().hide();
            } else {
                this.getActionBar().show();
            }
        }
    }

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

        /*
         * Detects if hiding navigation controls should be enabled. On xlarge andsw600dp, it should
         * be false, to avoid having the user enter an additional tap.
         */
        mHideNavigation = getResources().getBoolean(R.bool.hideNavigation);

        /*
         * Adds the back stack change listener defined in this Activity as the listener for the
         * FragmentManager. See the method onBackStackChanged().
         */
        localFragmentManager.addOnBackStackChangedListener(this);

        // If the incoming state of the Activity is null, sets the initial view to be thumbnails.
        if (null == savedInstanceState) {

            // 可以写成连贯的一句话。
            // Start a fragment transaction to track the stack.
            FragmentTransaction localFragmentTransaction =
                    localFragmentManager.beginTransaction();

            // Adds the PhotoThumbnailFragment to the host View.
            localFragmentTransaction.add(R.id.fragment_host,
                    new PhotoThumbnailFragment(), Constants.THUMBNAIL_FRAGMENT_TAG);

            // Commits this transaction to display the Fragment.
            localFragmentTransaction.commit();
        } else {

            // Gets the previous state of the fullscreen indicator
            mFullScreen = savedInstanceState.getBoolean(Constants.EXTENDED_FULLSCREEN);

            // Sets the fullscreen flag to its previous state
            setFullScreen(mFullScreen);

            // Gets the previous backstack entry count.
            mPreviousStackCount = localFragmentManager.getBackStackEntryCount();
        }
    }

    @Override
    public void onBackStackChanged() {
        // Gets the previous global stack count.
        int previousStackCount = mPreviousStackCount;

        // Gets a FragmentManager instance.
        FragmentManager localFragmentManager = getSupportFragmentManager();

        // Sets the current back stack count.
        int currentStackCount = localFragmentManager.getBackStackEntryCount();

        // Re-sets the global stack count to be the current count.
        mPreviousStackCount = currentStackCount;

        /*
         * If the current stack count is less than the previous, something was popped off the stack
         * probably because the user clicked Back.
         */
        boolean popping = currentStackCount < previousStackCount;
        Log.d(CLASS_TAG, "backstackchanged: popping = " + popping);

        // When going backwards in the back stack, turns off full screen mode.
        if (popping) {
            setFullScreen(false);
        }
    }

    /*
     * This callback is invoked by the system when the Activity is being killed
     * It saves the full screen status, so it can be restored when the Activity is restored
     *
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.EXTENDED_FULLSCREEN, mFullScreen);
        super.onSaveInstanceState(outState);
    }

    // Stops background threads.
    @Override
    protected void onStop() {

        // Cancel all the running threads managed by the PhotoManager.
        PhotoManager.cancelAll();

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        // If the DownloadStateReceiver still exists, unregister it and set it to null.
        if (mDownloadStateReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownloadStateReceiver);
            mDownloadStateReceiver = null;
        }

        // Unregisters the FragmentDisplayer instance.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFragmentDisplayer);

        // Sets the main View to null.
        mMainView = null;

        super.onDestroy();
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
