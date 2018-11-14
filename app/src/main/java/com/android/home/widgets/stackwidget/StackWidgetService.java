package com.android.home.widgets.stackwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.android.home.R;

import java.util.ArrayList;
import java.util.List;

public class StackWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private class StackRemoteViewsFactory implements RemoteViewsFactory {

        private static final int mCount = 10;

        private Context mContext;
        private int mAppWidgetId;

        private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();

        public StackRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            // 获取id值。
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            // In onCreate() you setup any connections / cursors to your data source.
            // Heavy lifting, for example downloading or creating content etc, should be
            // deferred to onDataSetChanged() or getViewAt().
            // Taking more than 20 seconds in thia call will result in an ANR.
            for (int i = 0; i < mCount; i++) {
                mWidgetItems.add(new WidgetItem(i + "!"));
            }
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            // In onDestroy() you should tear down anything that was setup for your data source,
            // eg. cursors, connections, etc.
            mWidgetItems.clear();
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            // position will always range from 0 to getCount() - 1.
            // We construct a remote views item based on our widget item xml file, and set the
            // text based on the position.
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
            rv.setTextViewText(R.id.widget_item, mWidgetItems.get(position).text);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            // You can create a custom loading view (for instance when getViewAt() is slow.) If you
            // return null here, you will get the default loading view.
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
