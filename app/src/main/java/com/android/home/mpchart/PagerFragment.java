package com.android.home.mpchart;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.home.R;

import java.util.ArrayList;
import java.util.List;

public class PagerFragment extends Fragment {

    ViewPager mViewPager;

    List<Fragment> mChartFragments = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mpchart_fragment, container,false);
        mViewPager = view.findViewById(R.id.view_pager);

        initChartFragments();

        return view;
    }

    private void initChartFragments() {
        mChartFragments.add(LineChartFragment.getInstance("Line Chart"));
        mChartFragments.add(LineChartTime.getInstance("Line Chart Time"));
        mChartFragments.add(BarChartFragment.getInstance("Bar Chart"));
    }

    @Override
    public void onResume() {
        super.onResume();

        mViewPager.setAdapter(new ChartPagerFragment(
                getActivity().getSupportFragmentManager()));
    }

    private class ChartPagerFragment extends FragmentPagerAdapter {
        public ChartPagerFragment(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mChartFragments.get(i);
        }

        @Override
        public int getCount() {
            return mChartFragments.size();
        }
    }
}
