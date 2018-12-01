package com.android.home.threelibs;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.Nullable;
import com.android.home.threelibs.autofill.AutoFillActivity;
import com.android.home.threelibs.circleimage.CircleImageActivity;
import com.android.home.threelibs.customview.CustomViewActivity;
import com.android.home.threelibs.development.DevelopmentActivity;
import com.android.home.threelibs.gson.ProguardGson;
import com.android.home.threelibs.mpchart.MpChart;
import com.android.home.threelibs.picasso.PicassoActivity;
import com.android.home.threelibs.randommusic.RandomMusicPlayer;
import com.android.home.threelibs.renderscript.RenderIntrinsic;
import com.android.home.threelibs.rxjavasamples.RxJavaSamples;
import com.android.home.threelibs.shimmer.ShimmerActivity;
import com.android.home.threelibs.testing.TestingActivity;
import com.squareup.picasso.Picasso;

public class ThreeLibsSample extends ListActivity {

    private final String[] mItems = {
      "Gson", "Stack widgets",
            "Shimmer", "RenderScript", "MpChart",
            "Picasso", "CircleImage",
            "RxJavaSample",
            "AutoFill", "AndroidAdvanced",
            "CustomView", "RandomMusicPlayer",
            "Testing"
    };

    private final Class[] mClasses = {
            ProguardGson.class,
            ThreeLibsSample.class,
            ShimmerActivity.class,
            RenderIntrinsic.class,
            MpChart.class,
            PicassoActivity.class,
            CircleImageActivity.class,
            RxJavaSamples.class,
            AutoFillActivity.class,
            DevelopmentActivity.class,
            CustomViewActivity.class,
            RandomMusicPlayer.class,
            TestingActivity.class
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, mItems
        ));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(this, mClasses[position]));
    }
}
