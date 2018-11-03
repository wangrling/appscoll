package com.android.home.picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.home.R;

final class PicassoSampleAdapter extends BaseAdapter {

    private static final int NOTIFICATION_ID = 666;
    private static final String CHANNEL_ID = "channel-id";

    private final LayoutInflater inflater;

    PicassoSampleAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }


    enum Sample {
        GRID_VIEW("Image Grid View", PicassoGridViewActivity.class),
        GALLERY("Load from Gallery", PicassoGalleryActivity.class),
        CONTACTS("Contact Photos", PicassoContactsActivity.class);

        private final Class<? extends Activity> activityClass;
        final String name;

        Sample(String name, Class<? extends Activity> activityClass) {
            this.activityClass = activityClass;
            this.name = name;
        }

        public void launch(Activity activity) {
            activity.startActivity(new Intent(activity, activityClass));
            activity.finish();
        }
    }


    @Override
    public int getCount() {
        return Sample.values().length;
    }

    @Override
    public Sample getItem(int position) {
        return Sample.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) convertView;
        if (view == null) {
            view = (TextView) inflater.inflate(R.layout.picasso_sample_activity_item, parent, false);
        }

        view.setText(getItem(position).name);

        return view;
    }
}
