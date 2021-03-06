package com.android.home.threelibs.testing;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.home.R;
import com.android.home.threelibs.testing.espresso.*;
import com.android.home.threelibs.testing.junitrunner.CalculatorActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestingActivity extends ListActivity {

    private static List<Pair<String, String>> mSamples = new ArrayList<>();

    static {
        mSamples.add(new Pair("BasicEspresso", "Basic Espresso sample."));
        mSamples.add(new Pair("CustomMatcher", "Shows how to extend Espresso to match the *hint* property of an EditText."));
        mSamples.add(new Pair("LongList", "Showcases the `onData()` entry point for Espresso, for lists and AdapterViews."));
        mSamples.add(new Pair("IdlingResource", "Synchronization with background jobs"));
        mSamples.add(new Pair("IntentsBasic", "Basic usage of `intended()` and `intending()`"));
        mSamples.add(new Pair("IntentsAdvanced", "Simulates a user fetching a bitmap using the camera"));
        mSamples.add(new Pair("RecyclerView", "RecyclerView actions for Espresso"));
        mSamples.add(new Pair("WebView", "Use Espresso-web to interact with WebViews"));
        mSamples.add(new Pair("AndroidJunitRunner", "Showcases test annotations, parameterized tests and testsuite creation"));
    }

    List<Activity> activities = Arrays.asList(
            new EspressoBasic(),
            new CustomMatcher(),
            new LongListActivity(),
            new IdlingResourceActivity(),
            new IntentsBasicActivity(),
            new IntentsAdvancedActivity(),
            new RecyclerViewActivity(),
            new WebViewActivity(),
            new CalculatorActivity()
    );

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
