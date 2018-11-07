package com.android.home.rssimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.android.home.R;

import java.lang.ref.WeakReference;
import java.net.URL;


/**
 * This class extends the standard Android ImageView View class with some features
 * that are useful for downloading, decoding, and displaying 500px images.
 *
 */

public class PhotoView extends android.support.v7.widget.AppCompatImageView {

    // Indicates if caching should be used.
    private boolean mCacheFlag;

    // Status flag that indicates if onDraw has completed.
    private boolean mIsDrawn;

    /*
     * Creates a weak reference to the ImageView in this object. The weak
     * reference prevents memory leaks and crashes, because it automatically tracks the "state" of
     * the variable it backs. If the reference becomes invalid, the weak reference is garbage-
     * collected.
     * This technique is important for referring to objects that are part of a component lifecycle.
     * Using a hard reference may cause memory leaks as the value continues to change; even worse,
     * it can cause crashes if the underlying component is destroyed. Using a weak reference to
     * a View ensures that the reference is more transitory in nature.
     */
    private WeakReference<View> mThisView;

    // Contains the ID of the internal View
    private int mHideShowResId = -1;

    // The URL that points to the source of the image for this ImageView.
    private URL mImageURL;

    // The Thread that will be used to download the image for this ImageView.
    private PhotoTask mDownloadThread;

    /**
     * Creates an ImageDownloadView with no settings
     * @param context A context for the View
     */
    public PhotoView(Context context) {
        super(context);
    }

    /**
     * Creates an ImageDownloadView and gets attribute values
     * @param context A Context to use with the View
     * @param attributeSet The entire set of attributes for the View
     */
    public PhotoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Gets attributes associated with the attribute set
        getAttributes(attributeSet);
    }

    /**
     * Creates an ImageDownloadView, gets attribute values, and applies a default style
     * @param context A context for the View
     * @param attributeSet The entire set of attributes for the View
     * @param defaultStyle The default style to use with the View
     */
    public PhotoView(Context context, AttributeSet attributeSet, int defaultStyle) {
        super(context, attributeSet, defaultStyle);

        // Gets attributes associated with the attribute set
        getAttributes(attributeSet);
    }

    /**
     * Gets the resource ID for the hideShowSibling resource
     * @param attributeSet The entire set of attributes for the View
     */
    private void getAttributes(AttributeSet attributeSet) {

        // Gets an array of attributes for the View.
        TypedArray attributes =
                getContext().obtainStyledAttributes(attributeSet, R.styleable.ImageDownloaderView);

        // Gets the resource Id of the View to hide or show.
        mHideShowResId = attributes.getResourceId(R.styleable.ImageDownloaderView_hideShowSibling, -1);

        // Returns the array for re-use
        attributes.recycle();
    }




    public void setImageURL(URL localURL, boolean b, Drawable mEmptyDrawable) {

    }
}
