package com.android.home.rssimage;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import com.android.home.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * PhotoThumbnailFragment displays a GridView of picture thumbnails downloaded from 500px.
 */

public class PhotoThumbnailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

    private static final String STATE_IS_HIDDEN =
            "com.android.home.rssimage.STATE_IS_HIDDEN";

    // The width of each column in the grid.
    private int mColumnWidth;

    // A Drawable for a grid cell that's empty.
    private Drawable mEmptyDrawable;

    // The GridView for displaying thumbnails.
    private GridView mGridView;

    // Denotes if the GridView has been loaded.
    private boolean mIsLoaded;

    // Intent for starting the IntentService that downloads the 500px featured picture RSS feed.
    private Intent mServiceIntent;

    // An adapter between a Cursor and the Fragment's GridView
    private GridViewAdapter mAdapter;

    // The URL of the 500px featured picture RSS feed, in String format.
    private static final String IMAGE_RSS_URL =
            "https://500px.com/popular.rss";

    private static final String[] PROJECTION = {
            DataProviderContract._ID,
            DataProviderContract.IMAGE_THUMB_URL_COLUMN,
            DataProviderContract.IMAGE_URL_COLUMN
    };

    // Constants that define the order of columns in the returned cursor
    private static final int IMAGE_THUMB_URL_CURSOR_INDEX = 1;
    private static final int IMAGE_URL_CURSOR_INDEX = 2;

    // Identifies a particular Loader being used in this component.
    private static final int URL_LOADER = 0;

    /*
     * This callback is invoked when the framework is starting or re-starting the Loader. It
     * returns a CursorLoader object containing the desired query
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, @Nullable Bundle bundle) {
        /*
         * Takes action based on the ID of the Loader that's being created.
         */
        switch (loaderID) {
            case URL_LOADER:

                // Returns a new CursorLoader.
                return new CursorLoader(
                        getActivity(),                                          // Context
                        DataProviderContract.PICTURE_URL_TABLE_CONTENT_URI,     // Table to query
                        PROJECTION,                                             // Projection to return
                        null,                                           // No selection clause
                        null,                                       // No selection arguments
                        null                                            // Default sort order
                );

            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        /*
         *  Changes the adapter's Cursor to be the results of the load. This forces the View to
         *  redraw.
         */
        mAdapter.changeCursor(cursor);
    }

    /*
     * Invoked when the CursorLoader is being reset. For example, this is called if the
     * data in the provider changes and the Cursor becomes stale.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        // Sets the Adapter's backing data to null. This prevents memory leaks.
        mAdapter.changeCursor(null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Defines a custom View adapter that extends CursorAdapter. The main reason to do this is to
     * display images based on the backing Cursor, rather than just displaying the URLs that the
     * Cursor contains.
     */
    private class GridViewAdapter extends CursorAdapter {

        /**
         * Simplified constructor that calls the super constructor with the input Context,
         * a null value for Cursor, and no flags
         * @param context A Context for this object
         */
        public GridViewAdapter(Context context) {
            super(context, null, false);
        }


        /**
         * Creates a new View that shows the contents of the Cursor
         *
         *
         * @param context A Context for the View and Cursor
         * @param cursor The Cursor to display. This is a single row of the returned query
         * @param parent The viewGroup that's the parent of the new View
         * @return the newly-created View
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Gets a new layout inflater instance.
            LayoutInflater inflater = LayoutInflater.from(context);

            /*
             * Creates a new View by inflating the specified layout file. The root ViewGroup is
             * the root of the layout file. This View is a FrameLayout
             */
            View layoutView = inflater.inflate(R.layout.gallery_item, null);

            /**
             * Creates a second View to hold the thumbnail image.
             */
            View thumbView = layoutView.findViewById(R.id.thumbImage);

            /*
             * Sets layout parameters for the layout based on the layout parameters of a virtual
             * list. In addition, this sets the layoutView's width to be MATCH_PARENT, and its
             * height to be the column width.
             */
            layoutView.setLayoutParams(new AbsListView.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    PhotoThumbnailFragment.this.mColumnWidth));

            // Sets the layoutView's tag to be the same as the thumbnail image tag.
            layoutView.setTag(thumbView);

            return layoutView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            // Gets a handle to the View.
            PhotoView localImageDownloaderView = (PhotoView) view.getTag();

            // Converts the URL string to a URL and tries to retrieve the picture.
            try {
                URL localURL =
                        new URL(cursor.getString(IMAGE_THUMB_URL_CURSOR_INDEX));

                /**
                 * Invokes setImageURL for the View. If the image isn't already available, this
                 * will download and decode it.
                 */
                localImageDownloaderView.setImageURL(
                        localURL, true, PhotoThumbnailFragment.this.mEmptyDrawable);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
