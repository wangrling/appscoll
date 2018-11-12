package com.android.home.displaybitmaps.ui;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.android.home.R;
import com.android.home.displaybitmaps.Images;
import com.android.home.displaybitmaps.util.ImageCache;
import com.android.home.displaybitmaps.util.ImageFetcher;
import com.android.home.displaybitmaps.util.Utils;

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load children
 * asynchronously, keeping the UI nice and smooth and caching thumbnails for quick retrieval. The
 * cache is retained over configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */

public class ImageGridFragment extends Fragment implements
        AdapterView.OnItemClickListener {

    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;

    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageGridFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        // 100dp
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        // 1dp
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(getActivity());

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        // Set memory cache to 25% of app memory.
        cacheParams.setMemCacheSizePercent(0.25f);

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously.
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.image_grid_fragment, container, false);

        final GridView mGridView = v.findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging.
                // Honeycomb之前在滑动的时候停止加载。
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance.
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));

                            if (numColumns > 0) {
                                // 设置为正方形。
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);

                                Log.d(TAG, "onCreateView - numColumns set to " + numColumns);

                                if (Utils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();

        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);

        // Cache是怎样的存在？
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mImageFetcher.closeCache();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE, (int) id);
        if (Utils.hasJellyBean()) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
            // show plus the thumbnail image in GridView is cropped. so using
            // makeScaleUpAnimation() instead.
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(),
                            view.getHeight());
            getActivity().startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.clear_cache_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_cache:
                mImageFetcher.clearCache();
                Toast.makeText(getActivity(), R.string.clear_cache_complete_toast,
                        Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {
        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();

            mContext = context;

            mImageViewLayoutParams = new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            // Calculate Action height.
            TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(
                    android.R.attr.actionBarSize, tv, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
            }
        }

        @Override
        public int getCount() {
            // If columns have yet to be determined, return no items.
            if (getNumColumns() == 0) {
                return 0;
            }

            // Size + number of columns for top empty row.
            return Images.imageThumbUrls.length + mNumColumns;
        }



        @Override
        public Object getItem(int position) {
            return position < mNumColumns ?
                    null : Images.imageThumbUrls[position - mNumColumns];
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views.
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // First check if this is the top row.
            if (position < mNumColumns) {
                if (convertView == null) {
                    convertView = new View(mContext);
                }

                // Set empty view with height of ActionBar
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));

                return convertView;
            }

            // Now handle the main ImageView thumbnails.
            ImageView imageView;
            // If it's not recycled, instantiate and initialize.
            if (convertView == null) {
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else {
                // Otherwise re-use the converted view.
                imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width.
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread tuns.
            mImageFetcher.loadImage(Images.imageThumbUrls[position - mNumColumns], imageView);

            return imageView;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        private void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }

            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);

            mImageFetcher.setImageSize(height);

            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }
}
