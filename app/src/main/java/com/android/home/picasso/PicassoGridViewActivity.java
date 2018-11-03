package com.android.home.picasso;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.android.home.R;
import com.android.home.dataurl.ImageUrls;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PicassoGridViewActivity extends PicassoActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.picasso_gridview_activity);

        GridView gv = findViewById(R.id.grid_view);
        gv.setAdapter(new PicassoGridViewAdapter(this));
        gv.setOnScrollListener(new PicassoScrollListener(this));

    }

    private class PicassoGridViewAdapter extends BaseAdapter {


        private final Context context;
        private final List<String> urls = new ArrayList<>();

        public PicassoGridViewAdapter(Context context) {
            this.context = context;

            // Ensure we get a different ordering of images on each run.
            Collections.addAll(urls, ImageUrls.URLS);
            Collections.shuffle(urls);
        }

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public String getItem(int position) {
            return urls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SquaredImageView view = (SquaredImageView) convertView;

            if (view == null) {
                view = new SquaredImageView(context);
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            // Get the image URL for the current position.
            String url = getItem(position);

            // Trigger the download of the URL asynchronously into the image view.

            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fit()
                    .tag(context)
                    .into(view);

            return view;
        }
    }

    public class SquaredImageView extends android.support.v7.widget.AppCompatImageView {
        public SquaredImageView(Context context) {
            super(context);
        }

        public SquaredImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        }
    }
}
