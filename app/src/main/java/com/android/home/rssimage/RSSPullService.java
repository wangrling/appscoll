package com.android.home.rssimage;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class RSSPullService extends IntentService {

    // Used to write to the system log from this class.
    public static final String LOG_TAG = "RSSPullService";

    // Defines and instantiates an object for handling status updates.
    private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

    /**
     * An IntentService must always have a constructor that calls the super constructor. The
     * string supplied to the super constructor is used to give a name to the IntentService's
     * background thread.
     */
    public RSSPullService() {

        super("RSSPullService");
    }


    /**
     * In an IntentService, onHandleIntent is run on a background thread.  As it
     * runs, it broadcasts its current status using the LocalBroadcastManager.
     * @param workIntent The Intent that starts the IntentService. This Intent contains the
     * URL of the web site from which the RSS parser gets data.
     */
    @Override
    protected void onHandleIntent(@Nullable @android.support.annotation.Nullable Intent workIntent) {

        // Gets a URL to read from the incoming Intent's "data" value.
        String localUrlString = workIntent.getDataString();

        // Creates a projection to use in querying the modification date table in the provider.
        final String[] dateProjection = new String[]
                {
                        DataProviderContract.ROW_ID,
                        DataProviderContract.DATA_DATE_COLUMN
                };

        // A URL that's local to this method
        URL localURL;

        // A cursor that's local to this method.
        Cursor cursor = null;

        /*
         * A block that tries to connect to the Picasa featured picture URL passed as the "data"
         * value in the incoming Intent. The block throws exceptions (see the end of the block).
         */
        try {

            // Convert the incoming data string to a URL.
            localURL = new URL(localUrlString);

            /*
             * Tries to open a connection to the URL. If an IO error occurs, this throws an
             * IOException
             */
            URLConnection localURLConnection = localURL.openConnection();

            // If the connection is an HTTP connection, continue.
            if ((localURLConnection instanceof HttpURLConnection)) {

                // Broadcasts an Intent indicating that processing has started.
                mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_STARTED);

                // Casts the connection to a HTTP connection.
                HttpURLConnection localHttpURLConnection = (HttpURLConnection) localURLConnection;

                // Sets the user agent for this request.
                localHttpURLConnection.setRequestProperty("User-Agent", Constants.USER_AGENT);

                /*
                 * Queries the content provider to see if this URL was read previously, and when.
                 * The content provider throws an exception if the URI is invalid.
                 */
                cursor = getContentResolver().query(
                        DataProviderContract.DATE_TABLE_CONTENT_URI,
                        dateProjection, null, null, null);

                // Flag to indicate that new metadata was retrieved
                boolean newMetadataRetrieved;

                /*
                 * Tests to see if the table contains a modification date for the URL
                 */
                if (null != cursor && cursor.moveToFirst()) {

                    // Find the URL's last modified date in the content provider.
                    long storedModifiedDate =
                            cursor.getLong(cursor.getColumnIndex(
                                    DataProviderContract.DATA_DATE_COLUMN));

                    /*
                     * If the modified date isn't 0, sets another request property to ensure that
                     * data is only downloaded if it has changed since the last recorded
                     * modification date. Formats the date according to the RFC1123 format.
                     */

                    // Marks that new metadata does not need to be retrieved.
                    newMetadataRetrieved =false;
                } else {

                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
