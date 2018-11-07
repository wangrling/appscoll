package com.android.home.rssimage;

import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.view.View;

public class PhotoFragment extends Fragment implements View.OnClickListener {

    // Constants
    private static final String LOG_TAG = "ImageDownloaderThread";

    private static final String PHOTO_URL_KEY = "com.android.home.rssimage.PHOTO_URL_KEY";

    PhotoView mPhotoView;

    String mURLString;

    ShareCompat.IntentBuilder mShareCompatIntentBuilder;


    @Override
    public void onClick(View v) {

    }
}
