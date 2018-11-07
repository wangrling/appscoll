package com.android.home.rssimage;

import android.graphics.Bitmap;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * This class manages PhotoDownloadRunnable and PhotoDownloadRunnable objects.  It does't perform
 * the download or decode; instead, it manages persistent storage for the tasks that do the work.
 * It does this by implementing the interfaces that the download and decode classes define, and
 * then passing itself as an argument to the constructor of a download or decode object. In effect,
 * this allows PhotoTask to start on a Thread, run a download in a delegate object, then
 * run a decode, and then start over again. This class can be pooled and reused as necessary.
 */

public class PhotoTask implements
        PhotoDownloadRunnable.TaskRunnableDownloadMethods,
        PhotoDecodeRunnable.TaskRunnableDecodeMethods {

    /*
     * Creates a weak reference to the ImageView that this Task will populate.
     * The weak reference prevents memory leaks and crashes, because it
     * automatically tracks the "state" of the variable it backs. If the
     * reference becomes invalid, the weak reference is garbage- collected. This
     * technique is important for referring to objects that are part of a
     * component lifecycle. Using a hard reference may cause memory leaks as the
     * value continues to change; even worse, it can cause crashes if the
     * underlying component is destroyed. Using a weak reference to a View
     * ensures that the reference is more transitory in nature.
     */
    private WeakReference<PhotoView> mImageWeakRef;


    // The image's URL
    private URL mImageURL;

    // The width and height of the decoded image
    private int mTargetHeight;
    private int mTargetWidth;

    // Is the cache enabled for this transaction?
    private boolean mCacheEnabled;

    /**
     * Fields containing references to the two runnable objects that handle downloading and
     * decoding of the images.
     */
    private Runnable mDownloadRunnable;
    private Runnable mDecodeRunnable;

    // A buffer for containing the bytes that make up the image.
    byte[] mImageBuffer;

    // The decoded image.
    private Bitmap mDecodedImage;

    // The Thread on which this task is currently running.
    private Thread mCurrentThread;

    /*
     * An object that contains the ThreadPool singleton.
     */
    private static PhotoManager sPhotoManager;

    /**
     * Creates an PhotoTask containing a download object and a decoder object.
     */
    PhotoTask() {
        // Create the runnables.
        mDownloadRunnable = new PhotoDownloadRunnable(this);
        mDecodeRunnable = new PhotoDecodeRunnable(this);

        sPhotoManager = PhotoManager.getInstance();
    }


    // Delegates handling the current state of the task to the PhotoManager object.
    void handleState(int state) {
        sPhotoManager.handleState(this, state);
    }

    /*
     * Returns the Thread that this Task is running on. The method must first get a lock on a
     * static field, in this case the ThreadPool singleton. The lock is needed because the
     * Thread object reference is stored in the Thread object itself, and that object can be
     * changed by processes outside of this app.
     */
    public Thread getCurrentThread() {
        synchronized(sPhotoManager) {
            return mCurrentThread;
        }
    }

    /*
     * Sets the identifier for the current Thread. This must be a synchronized operation; see the
     * notes for getCurrentThread()
     */
    public void setCurrentThread(Thread thread) {
        synchronized(sPhotoManager) {
            mCurrentThread = thread;
        }
    }

    @Override
    public void setDownloadThread(Thread currentThread) {
        setCurrentThread(currentThread);
    }

    @Override
    public void setImageDecodeThread(Thread currentThread) {
        setCurrentThread(currentThread);
    }


    // Implements HTTPDownloaderRunnable.getByteBuffer.
    @Override
    public byte[] getByteBuffer() {
        // Returns the global field
        return mImageBuffer;
    }


    /*
     * Implements PhotoDecodeRunnable.handleDecodeState(). Passes the decoding state to the
     * ThreadPool object.
     */
    @Override
    public void handleDecodeState(int state) {

        int outState;

        // Converts the decode state to the overall state.
        switch(state) {
            case PhotoDecodeRunnable.DECODE_STATE_COMPLETED:
                outState = PhotoManager.TASK_COMPLETE;
                break;
            case PhotoDecodeRunnable.DECODE_STATE_FAILED:
                outState = PhotoManager.DOWNLOAD_FAILED;
                break;
            default:
                outState = PhotoManager.DECODE_STARTED;
                break;
        }

        // Passes the state to the ThreadPool object.
        handleState(outState);
    }

    // Implements PhotoDownloadRunnable.getTargetWidth. Returns the global target width.
    @Override
    public int getTargetWidth() {
        return mTargetWidth;
    }

    // Implements PhotoDownloadRunnable.getTargetHeight. Returns the global target height.
    @Override
    public int getTargetHeight() {
        return mTargetHeight;
    }


    // Implements ImageCoderRunnable.setImage(). Sets the Bitmap for the current image.
    @Override
    public void setImage(Bitmap image) {
        mDecodedImage = image;
    }

    // Implements PhotoDownloadRunnable.setByteBuffer. Sets the image buffer to a buffer object.
    @Override
    public void setByteBuffer(byte[] buffer) {
        mImageBuffer = buffer;
    }

    @Override
    public void handleDownloadState(int state) {

        int outState;

        // Converts the download state to the overall state.
        switch (state) {
            case PhotoDownloadRunnable.HTTP_STATE_COMPLETED:
                outState = PhotoManager.DOWNLOAD_COMPLETE;
                break;
            case PhotoDownloadRunnable.HTTP_STATE_FAILED:
                outState = PhotoManager.DOWNLOAD_FAILED;
                break;
            default:
                outState = PhotoManager.DOWNLOAD_STARTED;
        }

        // Pass the state to the ThreadPool object.
        handleState(outState);
    }

    // Implements PhotoDownloadRunnable.getImageURL. Returns the global Image URL.
    @Override
    public URL getImageURL() {
        return mImageURL;
    }
}
