package com.android.home.threelibs.development;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.android.home.R;

/**
 * Launcher activity for the CardView.
 */

public class DevelopmentActivity extends AppCompatActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button mButton;

    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;


    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    private boolean mShowingFragments = false;

    private String[] mContents = {
            "Fragments    App widgets Sensors",
            "Performance Localization Accessibility",
            "Location   Places Mapping",
            "Custom views   Canvas  Animations",
            "Media  Room, LiveData, ViewModel"

    };

    private ListView mAdvancedContents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_development);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mButton = findViewById(R.id.cardTypeBtn);

        mAdvancedContents = findViewById(R.id.advancedContents);

        mAdvancedContents.setAdapter(new AdvancedAdapter());

        ((CheckBox)findViewById(R.id.checkBox)).setOnCheckedChangeListener(this);
        mButton.setOnClickListener(this);

        mCardAdapter = new CardPagerAdapter(this);
        mCardAdapter.addCardItem(new CardItem("Fragments",
                "Create a fragment that has its own UI, and anbable your activities" +
                        "to communicate with fragments."));
        mCardAdapter.addCardItem(new CardItem("App widgets",
                "Create and update app widgets."));
        mCardAdapter.addCardItem(new CardItem("Sensors",
                "Learn how to work with sensor data and build an app that " +
                        "detects and responds to device orientation."));
        mCardAdapter.addCardItem(new CardItem("Orientation",
                "TiltSpot demonstrates the use of the accelerometer and geomagnetic field " +
                        "sensors to  determine the device orientation."));
        mCardAdapter.addCardItem(new CardItem("ProfileGPU",
                "Demo that swaps between a small and large image " +
                "for testing with Profile GPU Rendering."));



        mFragmentCardAdapter = new CardFragmentPagerAdapter(
                getSupportFragmentManager(), dpToPixels(2, this));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mFragmentCardShadowTransformer = new ShadowTransformer(
                mViewPager, mFragmentCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onClick(View v) {
        if (!mShowingFragments) {
            mButton.setText("Views");
            mViewPager.setAdapter(mFragmentCardAdapter);
            mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        } else {
            mButton.setText("Fragments");
            mViewPager.setAdapter(mCardAdapter);
            mViewPager.setPageTransformer(false, mCardShadowTransformer);
        }

        mShowingFragments = !mShowingFragments;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mCardShadowTransformer.enableScaling(isChecked);
        mFragmentCardShadowTransformer.enableScaling(isChecked);
    }

    public class AdvancedAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mContents.length;
        }

        @Override
        public String getItem(int position) {
            return mContents[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setPadding(8, 6, 0, 6);
            tv.setText(mContents[position]);
            tv.setTextSize(18);
            return tv;
        }
    }
}
