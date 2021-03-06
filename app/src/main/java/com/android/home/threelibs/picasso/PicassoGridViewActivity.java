package com.android.home.threelibs.picasso;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.android.home.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.android.home.Home.URLS;

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
            Collections.addAll(urls, URLS);
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
}
