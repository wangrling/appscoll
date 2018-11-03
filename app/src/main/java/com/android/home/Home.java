package com.android.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.home.autofill.AutoFillFramework;
import com.android.home.plaid.PlaidApp;
import com.android.home.shimmer.ShimmerActivity;
import com.android.home.camerabasic.CameraBasic;
import com.android.home.circleimage.CircleImageActivity;
import com.android.home.displaybitmaps.DisplayBitmaps;
import com.android.home.mpchart.MpChart;
import com.android.home.picasso.PicassoActivity;
import com.android.home.rajawali.Rajawali3D;
import com.android.home.randommusic.RandomMusicPlayer;
import com.android.home.rssimage.RssImageFeed;
import com.android.home.rxjavasamples.RxJavaSamples;
import com.android.home.soundrecorder.SoundRecorder;
import com.android.home.syncadapter.BasicSyncAdapter;
import com.android.home.todomvp.TodoMvpActivity;
import com.android.home.universalmusic.ui.UniversalMusicPlayer;


import java.util.ArrayList;
import java.util.List;

public class Home extends Activity {

    public static final String APP_SERVER = "http://www.aidoufu.cn/";

    RecyclerView mRecyclerView;

    List<AppView> appViewList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recycler_view);

        appViewList.add(new AppView(R.drawable.todo, "Todo-MVP",
                "Provide a basic Model-View-Presenter (MVP) architecture without " +
                        "using any architectural frameworks.", TodoMvpActivity.class));
        appViewList.add(new AppView(R.drawable.picasso, "Picasso",
                "A powerful image downloading and caching library for Android.", PicassoActivity.class));
        appViewList.add(new AppView(R.drawable.camera_basic, "CameraBasic",
                "Demonstrates how to use basic functionalities " +
                "of Camera2 API. You can learn how to iterate through characteristics of all the " +
                "cameras attached to the device, display a camera preview, and take " +
                "pictures.", CameraBasic.class));
        appViewList.add(new AppView(R.drawable.random_music, "RandomMusicPlayer",
                "A simple music player that illustrates how to make a multimedia application " +
                        "that manages media playback from a service.", RandomMusicPlayer.class));
        appViewList.add(new AppView(R.drawable.plaid, "Plaid",
                "An Android app which provides design news & inspiration as well as being " +
                        "an example of implementing material design. ", PlaidApp.class));
        appViewList.add(new AppView(R.drawable.soundrecorder, "SoundRecorder",
                "系统的录音机应用。", SoundRecorder.class));
        appViewList.add(new AppView(R.drawable.sunflower, "Sunflower",
                        "A gardening app illustrating Android development best practices with Android Jetpack.",
                        null));
        appViewList.add(new AppView(R.drawable.circle, "CircleImage",
                "A fast circular ImageView perfect for profile images.", CircleImageActivity.class));
        appViewList.add(new AppView(R.drawable.mpchart, "MpChart",
                "A powerful & easy to use chart library for Android.", MpChart.class));
        appViewList.add(new AppView(R.drawable.calculator, "Calculator",
                "计算器", null));
        appViewList.add(new AppView(R.drawable.rxjava, "RxJavaSamples",
                "A repository with real-world useful examples of using RxJava " +
                        "with Android.", RxJavaSamples.class));
        appViewList.add(new AppView(R.drawable.shimmer, "Shimmer",
                "An Android library that provides an easy way to " +
                        "add a shimmer effect to any view in your Android app.", ShimmerActivity.class));
        appViewList.add(new AppView(R.drawable.dungeon, "PixelDungeon",
                "Traditional roguelike game with pixel-art graphics and simple interface.", null));
        appViewList.add(new AppView(R.drawable.rajawali, "Rajawali",
                "Rajawali is a 3D engine for Android based on OpenGL ES 2.0/3.0. " +
                        "It can be used for normal apps as well as live wallpapers.", Rajawali3D.class));
        appViewList.add(new AppView(R.drawable.displaybitmaps, "DisplayBitmaps",
                "Demonstrates how to load large bitmaps efficiently off the main UI thread, caching" +
                        "bitmaps (both in memory and on disk), managing bitmap memory and displaying bitmaps " +
                        "in UI elements such as ViewPager and ListView/GridView.", DisplayBitmaps.class));
        appViewList.add(new AppView(R.drawable.camera, "Camera", "" +
                "系统相机应用", null));
        appViewList.add(new AppView(R.drawable.calendar, "Calendar", "" +
                "系统日历", null));
        appViewList.add(new AppView(R.drawable.rss_image, "RssImageFeed",
                "Use a background thread to download 500px's \"featured image\" RSS feed.", RssImageFeed.class));
        appViewList.add(new AppView(R.drawable.fresco, "Fresco",
                "Fresco is a powerful system for displaying images in Android applications.", null));
        appViewList.add(new AppView(R.drawable.universal_music, "UniversalMusicPlayer",
                "Shows how to implement an audio media app that works across multiple form factors.",
                UniversalMusicPlayer.class));
        appViewList.add(new AppView(R.drawable.sync_adapter, "SyncAdapter",
                "Periodically downloads the feed from the Android Developer Blog and " +
                        "caches the data in a content provider. At runtime, the cached feed data is displayed " +
                        "inside a ListView.", BasicSyncAdapter.class));
        appViewList.add(new AppView(R.drawable.auto_fill, "AutoFillFramework",
                "Autofill Framework includes implementations of client Activities with views " +
                        "that should be autofilled, and a Service that can provide autofill data to " +
                        "client Activities.", AutoFillFramework.class));
        appViewList.add(new AppView(R.drawable.ime, "IME",
                "Demonstrates how to write an keyboard which sends rich content (such as images) to text\n" +
                        "fields using the Commit Content API.", null));
        appViewList.add(new AppView(R.drawable.sensor, "BatchStepSensor",
                "Demonstrating how to set up SensorEventListeners for step " +
                        "detectors and step counters.", null));

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(new AppAdapter());

        mRecyclerView.addOnItemTouchListener(
                new ItemListener(this, mRecyclerView, new ItemListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        startActivity(new Intent(Home.this, appViewList.get(position).getActivityClass()));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    private class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.home_row, viewGroup, false);
            ViewHolder vh = new ViewHolder(view);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            // Get element from your data set at this position.
            // replace the contents of the view with that element.
            AppView appView = appViewList.get(position);

            int red = (int) (100 + Math.random() * 155);
            int green = (int) (100 + Math.random() * 155);
            int blue = (int) (100 + Math.random() * 155);
            int color = 0xff000000 | red << 16 | green << 8 | blue;

            viewHolder.itemView.setBackgroundColor(color);
            viewHolder.icon.setImageDrawable(getResources().getDrawable(appView.getIconUrl(), null));
            viewHolder.title.setText(appView.getTitle());
            viewHolder.intro.setText(appView.getIntro());
        }

        @Override
        public int getItemCount() {
            return appViewList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView icon;
            public TextView title;
            public TextView intro;

            public ViewHolder(@NonNull View view) {
                super(view);

                icon = view.findViewById(R.id.home_icon);
                title = view.findViewById(R.id.home_title);
                intro = view.findViewById(R.id.home_intro);
            }

        }
    }
}
