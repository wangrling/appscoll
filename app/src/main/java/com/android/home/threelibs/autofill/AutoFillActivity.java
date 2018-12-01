package com.android.home.threelibs.autofill;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.home.R;
import com.android.home.threelibs.autofill.commoncases.CommonCasesFragment;
import com.android.home.threelibs.autofill.edgecases.EdgeCasesFragment;
import com.google.android.material.tabs.TabLayout;

/**
 * This is used to launch sample activities that showcase autofill.
 */

public class AutoFillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auto_fill_activity);

        /**
         * ViewPager的本质是ViewGroup的子类，最常见的就是结合fragment使用。
         * 利用PagerAdapter进行数据填充。
         */
        ViewPager viewPager = findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * A simple pager adapter that holds 2 Fragments.
     */
    private static class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        private BaseMainFragment[] fragments = new BaseMainFragment[]{
                new CommonCasesFragment(),
                new EdgeCasesFragment()
        };

        private Context mContext;

        public ScreenSlidePagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }


        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getString(fragments[position].getPageTitleResId());
        }
    }

}
