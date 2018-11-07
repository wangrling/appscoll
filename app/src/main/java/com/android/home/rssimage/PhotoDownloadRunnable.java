package com.android.home.rssimage;

import android.os.Process;
import com.bumptech.glide.load.model.stream.HttpUriLoader;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This task downloads bytes from a resource addressed by a URL. When the task
 * has finished, it calls handleState to report its results.
 *
 * Objects of this class are instantiated and managed by instances of PhotoTask, which
 * implements the methods of {@link PhotoDecodeRunnable.TaskRunnableDecodeMethods}. PhotoTask objects call
 * {@link  #PhotoDownloadRunnable(TaskRunnableDownloadMethods) PhotoDownloadRunnable()} with
 * themselves as the argument. In effect, an PhotoTask object and a
 * PhotoDownloadRunnable object communicate through the fields of the PhotoTask.
 */


class PhotoDownloadRunnable implements Runnable {

    // Sets the size for each read action (bytes)
    private static final int READ_SIZE = 1024 * 2;

    // Sets a tag for this class
    @SuppressWarnings("unused")
    private static final String LOG_TAG = "PhotoDownloadRunnable";

    // Constants for indicating the state of the download
    static final int HTTP_STATE_FAILED = -1;
    static final int HTTP_STATE_STARTED = 0;
    static final int HTTP_STATE_COMPLETED = 1;

    // Defines a field that contains the calling object of type PhotoTask.
    final TaskRunnableDownloadMethods mPhotoTask;


    // 用于和PhotoTask通信。
    interface TaskRunnableDownloadMethods {
        /**
         * Sets the Thread that this instance is running on
         * @param currentThread the current Thread
         */
        void setDownloadThread(Thread currentThread);

        /**
         * Returns the current contents of the download buffer
         * @return The byte array downloaded from the URL in the last read
         */
        byte[] getByteBuffer();

        /**
         * Sets the current contents of the download buffer
         * @param buffer The bytes that were just read
         */
        void setByteBuffer(byte[] buffer);

        /**
         * Defines the actions for each state of the PhotoTask instance.
         * @param state The current state of the task
         */
        void handleDownloadState(int state);

        /**
         * Gets the URL for the image being downloaded
         * @return The image URL
         */
        URL getImageURL();
    }


    public PhotoDownloadRunnable(TaskRunnableDownloadMethods photoTask) {
        mPhotoTask = photoTask;
    }


    /*
     * Defines this object's task, which is a set of instructions designed to be run on a Thread.
     */
    @SuppressWarnings("resource")
    @Override
    public void run() {
        /**
         * Stores the current Thread in the PhotoTask instance, so that the instance
         * can interrupt the Thread.
         */
        mPhotoTask.setDownloadThread(Thread.currentThread());

        // Moves the current Thread into the background.
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        /*
         * Gets the image cache buffer object from the PhotoTask instance. This makes the
         * to both PhotoDownloadRunnable and PhotoTask.
         */
        byte[] byteBuffer = mPhotoTask.getByteBuffer();

        /*
         * A try block that downloads a 500px image from a URL. The URL value is in the field
         * PhotoTask.mImageURL.
         */
        // Tries to download the picture from 500px.
        try {
            // Before continuing, checks to see that the Thread hasn't been interrupted.
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            // If there's no cache buffer for this image.
            if (null == byteBuffer) {

                /**
                 * Calls the PhotoTask implementation of {@link @handleDownloadState} to
                 * set the state of the download.
                 */
                mPhotoTask.handleDownloadState(HTTP_STATE_STARTED);

                // Defines a handle for the byte download stream.
                InputStream byteStream = null;

                // Downloads the image and catches IO errors.
                try {
                    // Opens an HTTP connection to the image's URL
                    HttpURLConnection httpConn =
                            (HttpURLConnection) mPhotoTask.getImageURL().openConnection();

                    // Sets the user agent to report to the server.
                    httpConn.setRequestProperty("User-Agent", Constants.USER_AGENT);

                    // Before continuing, checks to see that the Thread
                    // hasn't been interrupted
                    if (Thread.interrupted()) {

                        throw new InterruptedException();
                    }
                    // Gets the input stream containing the image

                    byteStream = httpConn.getInputStream();

                    if (Thread.interrupted()) {

                        throw new InterruptedException();
                    }


                    /**
                     * Get the size of the file being downloaded. This may or may not be returned.
                     */
                    int contentSize = httpConn.getContentLength();

                    /**
                     * If the size of the image isn't available.
                     */
                    if (-1 == contentSize) {

                        // Allocated a temporary buffer.
                        byte[] tempBuffer = new byte[READ_SIZE];

                        // Records the initial amount of available space.
                        int bufferLeft = tempBuffer.length;

                        /**
                         * Defines the initial offset of the next available
                         * byte in the buffer, and the initial result of
                         * reading the binary.
                         */
                        int bufferOffset = 0;
                        int readResult = 0;

                        /*
                         * The "outer" loop continues until all the bytes
                         * have been downloaded. The inner loop continues
                         * until the temporary buffer is full, and then
                         * allocates more buffer space.
                         */
                        outer: do {
                            while (bufferLeft > 0) {

                                /**
                                 * Reads from the URL location into the temporary buffer, starting at the
                                 * next available free byte and reading as many bytes as are available in
                                 * the buffer.
                                 */
                                readResult = byteStream.read(tempBuffer, bufferOffset, bufferLeft);

                                /**
                                 * InputStream.read() returns zero when the file has been completely read.
                                 */
                                if (readResult < 0) {
                                    // The read is finished, so this breaks
                                    // to the "outer" loop.
                                    break outer;
                                }

                                /**
                                 * The read isn't finished. This sets the next available open position in the
                                 * buffer (the buffer index is 0-based).
                                 */
                                bufferOffset += readResult;

                                // Subtracts the number of bytes read from the amount of buffer left.
                                bufferLeft -= readResult;

                                if (Thread.interrupted()) {
                                    throw new InterruptedException();
                                }
                            }

                            /**
                             * The temporary buffer is full, so the following code creates a new buffer that can
                             * contain the existing contents plus the next read cycle.
                             */

                            // Resets the amount of buffer left to be the max buffer size.
                            bufferLeft = READ_SIZE;

                            /**
                             * Sets a new size that can contain the existing buffer's contents plus space for the
                             * next read cycle.
                             */
                            int newSize = tempBuffer.length + READ_SIZE;

                            /**
                             * Create a new temporary buffer, moves the contents of the old temporary buffer into
                             * it, and then points the temporary buffer variable to the new buffer.
                             */
                            byte[] expandedBuffer = new byte[newSize];

                            System.arraycopy(tempBuffer, 0, expandedBuffer, 0, tempBuffer.length);

                            tempBuffer = expandedBuffer;
                        } while (true);

                        /*
                         * When the entire image has been read, this creates
                         * a permanent byte buffer with the same size as
                         * the number of used bytes in the temporary buffer
                         * (equal to the next open byte, because tempBuffer
                         * is 0=based).
                         */
                        byteBuffer = new byte[bufferOffset];

                        // Copies the temporary buffer to the image buffer.
                        System.arraycopy(tempBuffer, 0, byteBuffer, 0, bufferOffset);

                        /*
                         * The download size is available, so this creates a
                         * permanent buffer of that length.
                         */
                    } else {

                        byteBuffer = new byte[contentSize];

                        // How much of the buffer still remains empty.
                        int remainingLength = contentSize;

                        // The next open space in the buffer.
                        int bufferOffset = 0;

                        /**
                         * Reads into the buffer until the number of bytes
                         * equals to the length of the buffer (the size of
                         * the image) have been read.
                         */
                        while (remainingLength > 0) {
                            int readResult = byteStream.read(byteBuffer,
                                    bufferOffset, remainingLength);

                            /**
                             * EOF should not occur, because the loop should read the
                             * exact # of bytes in the image.
                             */
                            if (readResult < 0) {

                                // Throws an EOF Exception.
                                throw new EOFException();
                            }

                            // Moves the buffer offset to the next open byte.
                            bufferOffset += readResult;

                            // Subtracts the # of bytes read from the
                            // remaining length.
                            if (Thread.interrupted()) {

                                throw new InterruptedException();
                            }
                        }
                    }

                    if (Thread.interrupted()) {

                        throw new InterruptedException();
                    }

                    // If an IO error occurs, returns immediately.

                } catch (IOException e) {

                    e.printStackTrace();
                    return;

                    /**
                     * If the input stream is still open, close it.
                     */
                } finally {
                    if (null != byteStream) {
                        try {
                            byteStream.close();
                        } catch (Exception e) {

                        }
                    }
                }
            }

            /**
             * Stores the download bytes in the byte buffer in the PhotoTask instance.
             */
            mPhotoTask.setByteBuffer(byteBuffer);

            /**
             * Sets the status message in the PhotoTask instance. This sets the
             * ImageView background to indicated that the image is being decoded.
             */
            mPhotoTask.handleDownloadState(HTTP_STATE_COMPLETED);

        // Catches exceptions thrown in response to a queued interrupt.
        } catch (InterruptedException e) {

            e.printStackTrace();

        // In all cases, handle the result.
        } finally {

            // If the byteBuffer is null, reports that the download failed.
            if (null == byteBuffer) {
                mPhotoTask.handleDownloadState(HTTP_STATE_FAILED);
            }

            /*
             * The implementation of setHTTPDownloadThread() in PhotoTask calls
             * PhotoTask.setCurrentThread(), which then locks on the static ThreadPool
             * object and returns the current thread. Locking keeps all references to Thread
             * objects the same until the reference to the current Thread is deleted.
             */

            // Sets the reference to the current Thread to null, releasing its storage
            mPhotoTask.setDownloadThread(null);

            // Clears the Thread's interrupt flag
            Thread.interrupted();
        }
    }
}
