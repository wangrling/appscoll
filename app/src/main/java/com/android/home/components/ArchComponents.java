package com.android.home.components;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.home.R;
import com.android.home.threelibs.testing.espresso.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArchComponents extends ListActivity {

    private static List<Pair<String, String>> mSamples = new ArrayList<>();

    List<Activity> activities = Arrays.asList(
    );

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSamples.add(new Pair("BasicSample",
                "Shows how to persist data using a SQLite database and Room. Also uses ViewModels and LiveData."));
        mSamples.add(new Pair("PersistenceContentProviderSample",
                "Shows how to expose data via a Content Provider using Room."));
        mSamples.add(new Pair<>("GithubBrowserSample",
                "An advanced sample that uses the Architecture components, " +
                        "Dagger and the Github API. Requires Android Studio 3.0 or later."));
        mSamples.add(new Pair<>("BasicRxJavaSample", "Shows how to use Room with RxJava 2. Also uses ViewModels."));
        mSamples.add(new Pair<>("PersistenceMigrationsSample", "Shows how to implement migrations in Room."));

        mSamples.add(new Pair<>("BasicRxJavaKotlinSample",
                "Shows how to use ViewModels and Room together with RxJava, in Kotlin."));
        mSamples.add(new Pair<>("PagingSample", "Shows how to use the Paging library with Room, in Kotlin."));

        mSamples.add(new Pair<>("PagingNetworkSample", "Shows how to use the Paging library with " +
                "a backend API via Retrofit, in Kotlin."));

        mSamples.add(new Pair<>("NavigationBasicSample",
                "Shows how to use Navigation (alpha) to perform navigation and deep linking in Kotlin."));

        mSamples.add(new Pair<>("WorkManagerSample",
                "Shows how to use WorkManager (alpha) to do background work, in Java."));


        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mSamples.size();
            }

            @Override
            public Pair<String, String> getItem(int position) {
                return mSamples.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =  getLayoutInflater().inflate(R.layout.two_textview_layout, parent, false);
                ((TextView) view.findViewById(android.R.id.text1)).setText(mSamples.get(position).first);
                ((TextView) view.findViewById(android.R.id.text2)).setText(mSamples.get(position).second);

                return view;
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(this, (activities.get(position)).getClass()));
    }
}
