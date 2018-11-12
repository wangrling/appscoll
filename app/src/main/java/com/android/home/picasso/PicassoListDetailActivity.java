package com.android.home.picasso;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.home.R;
import com.android.home.dataurl.ImageUrls;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PicassoListDetailActivity extends PicassoActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sample_content, ListFragment.newInstance())
                    .commit();
        }
    }

    void showDetails(String url) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content, DetailFragment.newInstance(url))
                .addToBackStack(null)
                .commit();
    }

    public static class ListFragment extends Fragment {
        public static ListFragment newInstance() {
            return new ListFragment();
        }

        @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                           Bundle savedInstanceState) {
            final PicassoListDetailActivity activity = (PicassoListDetailActivity) getActivity();
            final PicassoListDetailAdapter adapter = new PicassoListDetailAdapter(activity);

            ListView listView = (ListView) LayoutInflater.from(activity)
                    .inflate(R.layout.list_detail_list, container, false);
            listView.setAdapter(adapter);
            listView.setOnScrollListener(new PicassoScrollListener(activity));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    String url = adapter.getItem(position);
                    activity.showDetails(url);
                }
            });
            return listView;
        }
    }

    public static class DetailFragment extends Fragment {
        private static final String KEY_URL = "picasso:url";

        public static DetailFragment newInstance(String url) {
            Bundle arguments = new Bundle();
            arguments.putString(KEY_URL, url);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            return fragment;
        }

        @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                           Bundle savedInstanceState) {
            PicassoListDetailActivity activity = (PicassoListDetailActivity) getActivity();

            View view = inflater.inflate(R.layout.list_detail_detail, container, false);

            TextView urlView = view.findViewById(R.id.url);
            ImageView imageView = view.findViewById(R.id.photo);

            Bundle arguments = getArguments();
            String url = arguments.getString(KEY_URL);

            urlView.setText(url);
            Picasso.get()
                    .load(url)
                    .fit()
                    .tag(activity)
                    .into(imageView);

            return view;
        }
    }

    private static class PicassoListDetailAdapter extends BaseAdapter {

        private final Context context;

        private final List<String> urls = new ArrayList<>();

        PicassoListDetailAdapter(Context c) {
            context = c;
            Collections.addAll(urls, ImageUrls.URLS);
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
        public View getView(int position, View view, ViewGroup parent) {

            ViewHolder holder;

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.list_detail_item, parent, false);
                holder = new ViewHolder();
                holder.image = view.findViewById(R.id.photo);
                holder.text = view.findViewById(R.id.url);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            // Get the image URL for the current position.
            String url = getItem(position);

            holder.text.setText(url);

            // Trigger the download of the URL asynchronously into the image view.
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .resizeDimen(R.dimen.list_detail_image_size, R.dimen.list_detail_image_size)
                    .centerInside()
                    .tag(context)
                    .into(holder.image);

            return view;
        }
    }

    static class ViewHolder {
        ImageView image;
        TextView text;
    }
}