package com.android.home.rxjavasamples;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.home.R;

import java.util.ArrayList;
import java.util.List;

public class RxJavaListFragment extends ListFragment {

    private static List<Pair<String, String>> mSamples;

    private List<Fragment> mFragments;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSamples = new ArrayList<>();
        mFragments = new ArrayList<>();

        initSamples();
        initFragments();

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

    private void initSamples() {
        mSamples.add(new Pair("Background work & concurrency (using Schedulers)",
                getString(R.string.msg_demo_concurrency_schedulers)));

        mSamples.add(new Pair("Accumulate calls (using buffer)",
                getString(R.string.msg_demo_buffer)));
        mSamples.add(new Pair("Instant/Auto searching text listeners (using Subjects & debounce)",
                getString(R.string.msg_demo_debounce)));
        mSamples.add(new Pair("Networking with Retrofit & RxJava (using zip, flatmap) " +
                "(太抽象，不知道在写什么，也没有什么现象，沮丧。)",
                getString(R.string.msg_demo_retrofit)));
        mSamples.add(new Pair("Two-way data binding for TextViews (using PublishSubject)",
                getString(R.string.msg_demo_doublebinding)));
        mSamples.add(new Pair("Simple and Advanced polling (using interval and repeatWhen)",
                getString(R.string.msg_demo_polling)));
    }

    private void initFragments() {
        mFragments.add(new ConcurrencyWithSchedulerFragment());
        mFragments.add(new BufferFragment());
        mFragments.add(new DebounceSearchEmitterFragment());
        mFragments.add(new RetrofitFragment());
        mFragments.add(new DoubleBindingTextViewFragment());
        mFragments.add(new PollingFragment());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        clickedOn(mFragments.get(position));
    }

    private void clickedOn(@NonNull Fragment fragment) {
        final String tag = fragment.getClass().toString();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(tag)
                .replace(android.R.id.content, fragment, tag)
                .commit();
    }
}
