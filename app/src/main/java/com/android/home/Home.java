package com.android.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.home.alarmclock.AlarmClock;
import com.android.home.autofill.AutoFillActivity;
import com.android.home.calculator.Calculator;
import com.android.home.calendar.AllInOneActivity;
import com.android.home.components.ArchComponents;
import com.android.home.customview.CustomViewActivity;
import com.android.home.development.DevelopmentActivity;
import com.android.home.displaybitmaps.ui.DisplayBitmaps;
import com.android.home.plaid.PlaidApp;
import com.android.home.rssimage.DisplayActivity;
import com.android.home.shimmer.ShimmerActivity;
import com.android.home.camerabasic.CameraBasic;
import com.android.home.circleimage.CircleImageActivity;
import com.android.home.mpchart.MpChart;
import com.android.home.picasso.PicassoActivity;
import com.android.home.randommusic.RandomMusicPlayer;
import com.android.home.rxjavasamples.RxJavaSamples;
import com.android.home.soundrecorder.SoundRecorder;
import com.android.home.stepsensor.BatchStepSensor;
import com.android.home.syncadapter.BasicSyncActivity;
import com.android.home.testing.TestingActivity;
import com.android.home.todomvp.TodoMvpActivity;
import com.android.home.universalmusic.ui.MusicPlayerActivity;


import java.util.ArrayList;
import java.util.List;
/**
 * <html>
 *     <p>onTouchEvent是View事件的起点，button只是实现OnClickListener监听，本质还是通过onTouchEvent的调用。</p>
 *     <code>
 *         public boolean onTouchEvent(MotionEvent event) {
 *              final int action = event.getAction();
 *              switch(action) {
 *                  case MotionEvent.ACTION_UP:
 *                      if (isFocusable() && isFocusableInTouchMode() && !isFocused()) {
 *                          // isFocused()为true所以不会走这个if语句。
 *                          focusTaken = requestFocus();
 *                      }
 *                      if (!focusTaken) {
 *                          // Use a Runnable and post this rather than calling performClick
 *                          // directly. This lets other visual state of the view update
 *                          // before click actions start.
 *                          if (mPerformClick == null) {
 *                              mPerformClick = new PerformClick();
 *                          }
 *                          if (!post(mPerformClick)) {
 *                              performClick();
 *                          }
 *                      }
 *              }
 *         }
 *
 *         public boolean performClick() {
 *             final ListenerInfo li = mListenerInfo;
 *             if (li != null && li.mOnClickListener != null) {
 *                 playSoundEffect(SoundEffectConstants.CLICK);
 *                 // 调用点击事件，需要子类实现。
 *                 li.mOnClickListener.onClick(this);
 *                 result = true;
 *             } else {
 *                 result = false;
 *             }
 *             sendAccessibilityEvent(...);
 *             notifyEnterOrExitForAutoFillIfNeeded(true);
 *             return result;
 *         }
 *
 *         // Register a callback to be invoked when this view is clicked. If this view is not
 *         // clickable, it becomes clickable.
 *         public void setOnClickListener(@Nullable OnClickListener l) {
 *             if (!isClickable()) {
 *                 setClickable(true)
 *             }
 *             getListenerInfo().mOnClickListener = 1;
 *         }
 *     </code>
 *
 *     <p>首先传递到Activity的dispatchTouchEvent，如果传递到ViewGroup里面的dispatchTouchEvent,
 *     如果返回true,则传递给本ViewGroup的onTouchEvent处理，如果返回false，则进一步往下传递。</p>
 * </html>
 */

/**
 * 计算项目的行数，只包括java文件。
 * find . -type f -name "*.java" | xargs cat | wc -l
 * 目前已经写完97801行代码！写到100万行，加油！
 *
 * ExactCalculator 11582
 * Camera2 99992
 */

public class Home extends Activity {

    public static final String TAG = "Home";

    public static final String APP_SERVER = "http://www.aidoufu.cn/";

    RecyclerView mRecyclerView;

    List<AppView> appViewList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recycler_view);

        /**
         * 将要写的项目在这里表示。
         * https://github.com/googlesamples/android-topeka
         */

        /**
         * 以后超过1000行的程序都要先进行分析，慌慌张张地写很容易进入迷途。
         */


        appViewList.add(new AppView(R.drawable.alarmclock, "AlarmClock",
                "系统的闹钟程序", AlarmClock.class, ""));

        appViewList.add(new AppView(R.drawable.home, "Development",
                "development/samples目录下面自己还掌握的内容，大部分都是几百到上千行的代码实例。",
                DevelopmentActivity.class, ""));

        /**
         * 需要长按Launcher把widgets调出来。
         */
        appViewList.add(new AppView(R.drawable.widgets, "Widgets",
                "StackWidget小组件，为写Calendar作准备，" +
                        "WeatherListWidget没有list显示，沮丧的心情难以言表。", Home.class));


        appViewList.add(new AppView(R.drawable.components, "ArchComponents",
                "A collection of samples using the Architecture Components.",
                ArchComponents.class, ""));

        appViewList.add(new AppView(R.drawable.testing, "Testing",
                "A collection of samples demonstrating different frameworks and techniques for automated testing.",
                TestingActivity.class,
                "Espresso, UiAutomator, AndroidJunitRunner, JUnit4 Rules测试工具。"));

        appViewList.add(new AppView(R.drawable.todo, "Todo-MVP",
                "Provide a basic Model-View-Presenter (MVP) architecture without " +
                        "using any architectural frameworks.", TodoMvpActivity.class,
                "中级，完成。\n" +
                        "使用Model-View-Presenter架构写的一个便签。\n" +
                        "data/表示model，使用room进行本地数据存放，也模拟远程进行读取，任务都使用TasksRepository进行管理。\n" +
                        "三个模块使用Presenter，分别是addedittask/，statistics/，taskdetail/。\n" +
                        "View就是Activity和TasksFragment等。\n"));

        appViewList.add(new AppView(R.drawable.picasso, "Picasso",
                "A powerful image downloading and caching library for Android.", PicassoActivity.class,
                "初级，只完成demo部分，lib没有写。\n" +
                        "五个示例：（1）从网络中加载图片显示在GridView中；（2）从本地图库中加载图片显示；" +
                        "（3）从联系中读取照片；（4）显示图片的详细信息；（5）图片显示在通知栏中。"));


        appViewList.add(new AppView(R.drawable.camera_basic, "CameraBasic",
                "Demonstrates how to use basic functionalities " +
                "of Camera2 API. You can learn how to iterate through characteristics of all the " +
                "cameras attached to the device, display a camera preview, and take " +
                "pictures.", CameraBasic.class,
                "初级，完成。\n" +
                        "实现相机拍照并且保存，都是系统已经定义好的接口。"));

        appViewList.add(new AppView(R.drawable.random_music, "RandomMusicPlayer",
                "A simple music player that illustrates how to make a multimedia application " +
                        "that manages media playback from a service.", RandomMusicPlayer.class,
                "初级，完成。\n" +
                        "随机播放本地音乐，也可以按照网址播放远程音乐。使用Activity控制Service进行播放，Service进行" +
                        "封装MediaPlayer相关接口。"));

        appViewList.add(new AppView(R.drawable.plaid, "Plaid",
                "An Android app which provides design news & inspiration as well as being " +
                        "an example of implementing material design. ", PlaidApp.class,
                "高级，未完成。\n" +
                        "没有描述。"));

        appViewList.add(new AppView(R.drawable.soundrecorder, "SoundRecorder",
                "系统的录音机应用。", SoundRecorder.class,
                "中级，完成。\n" +
                        "简单的录音机应用，调用MediaRecorder接口，通过getMaxAmplitude获取波幅。"));

        appViewList.add(new AppView(R.drawable.sunflower, "Sunflower",
                        "A gardening app illustrating Android development best practices with Android Jetpack.",
                        null,
                "高级，未开始。"));

        appViewList.add(new AppView(R.drawable.circle, "CircleImage",
                "A fast circular ImageView perfect for profile images.", CircleImageActivity.class,
                "初级，完成。\n" +
                        "给图片添加圆形边框，主要是重写ImageView类。"));

        appViewList.add(new AppView(R.drawable.mpchart, "MpChart",
                "A powerful & easy to use chart library for Android.", MpChart.class,
                "中级，完成部分demo显示。\n" +
                        "简单易用的图表库，显示实际应用中的各种图表。"));

        appViewList.add(new AppView(R.drawable.calculator, "ExactCalculator",
                "计算器", Calculator.class));

        appViewList.add(new AppView(R.drawable.rxjava, "RxJavaSamples",
                "A repository with real-world useful examples of using RxJava " +
                        "with Android.", RxJavaSamples.class,
                "中级，未完成。\n" +
                        "Android RxJava学习示例。"));

        appViewList.add(new AppView(R.drawable.shimmer, "Shimmer",
                "An Android library that provides an easy way to " +
                        "add a shimmer effect to any view in your Android app.", ShimmerActivity.class,
                "中级，完成。\n" +
                        "通过Drawable, ValueAnimator, FrameLayout设计动画，同时添加很多属性。"));

        /*
        appViewList.add(new AppView(R.drawable.dungeon, "PixelDungeon",
                "Traditional roguelike game with pixel-art graphics and simple interface.",
                PixelDungeon.class,
                "高级，游戏，完成。\n" +
                        "二维的地图游戏，代码都是复制过来的，类太多，感觉太难写！"));
         */

        /*
            把这个调用接口显示的剔除出去，写的代码就像是堆柴火，无法建立高楼大厦。
        appViewList.add(new AppView(R.drawable.rajawali, "Rajawali",
                "Rajawali is a 3D engine for Android based on OpenGL ES 2.0/3.0. " +
                        "It can be used for normal apps as well as live wallpapers.", null));

        */
        appViewList.add(new AppView(R.drawable.displaybitmaps, "DisplayBitmaps",
                "Demonstrates how to load large bitmaps efficiently off the main UI thread, caching" +
                        "bitmaps (both in memory and on disk), managing bitmap memory and displaying bitmaps " +
                        "in UI elements such as ViewPager and ListView/GridView.", DisplayBitmaps.class,
                "示例虽然文件不多，但是比较难。ImageWorker <- ImageResizer <- ImageFetcher的继承关系。" +
                        "点击小图可以查看大图。"));

        appViewList.add(new AppView(R.drawable.camera, "Camera", "" +
                "系统相机应用", null));

        appViewList.add(new AppView(R.drawable.calendar, "Calendar", "" +
                "系统日历", AllInOneActivity.class,
                "高级，未完成。\n" +
                        "在沮丧中度过，不要过于乐观。"));

        /**
         * sqlite> .tables
         * DateMetadatData   PictureUrlData    android_metadata
         * sqlite> .tables PictureUrlData
         * PictureUrlData
         * sqlite> select * from PictureUrlData
         * 1||http://www.aidoufu.cn/img/v2_002.jpeg||v2_002.jpeg
         */
        appViewList.add(new AppView(R.drawable.rss_image, "RssImageFeed",
                "Use a background thread to download 500px's \"featured image\" RSS feed.", DisplayActivity.class,
                "中级，完成。\n" +
                        "主要讲述多任务，打开程序，首先会调用RSSService去进行查询，更新数据库。然后通过PhotoManager去调用" +
                        "Download, Decode两个任务。最后显示在主界面，可以点击进行放大。\n" +
                        "修改XML配置文件，存储在aidoufu服务器，满足RSSPullParser的要求。\n" +
                        "学会如何查看手机sqlite中的数据，将db复制出来之后，使用sqlite常规命令进行查看。"));

        appViewList.add(new AppView(R.drawable.fresco, "Fresco",
                "Fresco is a powerful system for displaying images in Android applications.", null));


        appViewList.add(new AppView(R.drawable.universal_music, "UniversalMusicPlayer",
                "Shows how to implement an audio media app that works across multiple form factors.",
                MusicPlayerActivity.class,
                "中级，完成。\n" +
                        "已经去掉不能使用的cast功能，可以在多个界面上播放音乐。使用的是MediaController作为桥梁，沟通" +
                        "UI和MediaPlayer进行播放。"));

        appViewList.add(new AppView(R.drawable.sync_adapter, "SyncAdapter",
                "Periodically downloads the feed from the Android Developer Blog and " +
                        "caches the data in a content provider. At runtime, the cached feed data is displayed " +
                        "inside a ListView.", BasicSyncActivity.class,
                "中级，完成。\n" +
                        "使用Account获取sync权限，更新数据的内容，然后显示在列表上，已经掌握ContentProvider知识，" +
                        "基本上代码都是自己手敲的，没有大规模复制粘贴，是一个可喜的进步。"));

        appViewList.add(new AppView(R.drawable.auto_fill, "AutoFillActivity",
                "Autofill Framework includes implementations of client Activities with views " +
                        "that should be autofilled, and a Service that can provide autofill data to " +
                        "client Activities.", AutoFillActivity.class,
                "中级，未完成。需要最低的SDK版本为26，没有办法继续编写。"));

        /*
        appViewList.add(new AppView(R.drawable.ime, "IME",
                "Demonstrates how to write an keyboard which sends rich content (such as images) to text\n" +
                        "fields using the Commit Content API.", null));
        */

        appViewList.add(new AppView(R.drawable.sensor, "BatchStepSensor",
                "Demonstrating how to set up SensorEventListeners for step " +
                        "detectors and step counters.", BatchStepSensor.class,
                "中级，完成。\n" +
                        "原理并不难，就是注册监听，以固定的频率接收数据。\n" +
                        "但是里面有各种花式布局，让程序更加生动，所以需要分清主次。"));

        // 未来先弄清楚目录结构，再决定是否继续往下写！！！！
        appViewList.add(new AppView(R.drawable.home, "CustomView",
                "ApiDemos/src/com/example/android/apis/graphics", CustomViewActivity.class,
                "编写View类的实现。"));


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
                        new AlertDialog.Builder(Home.this)
                                .setTitle(appViewList.get(position).getTitle())
                                .setIcon(appViewList.get(position).getIconUrl())
                                .setMessage(appViewList.get(position).getDetail())
                                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Nothing to do.
                                    }
                                })
                                .show();
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
