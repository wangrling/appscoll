package com.android.home.widgets.stackwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import com.android.home.R;

public class StackWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "StackWidgetProvider";

    public static final String TOAST_ACTION = "com.android.home.stackwidget.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.android.home.stackwidget.EXTRA_ITEM";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.d(TAG, "onUpdate");

        // 创建widget的时候会调用onUpdate函数。
        // Update each of the widgets with the remote adapter.
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Log.d(TAG, "appWidgetIds = " + i);

            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            Intent intent = new Intent(context, StackWidgetService.class);
            // 将id传递过去。
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            // When intents are compared, the extras are ignored, so we ned to embed the extras
            // into the data so that the extras will no be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.stack_view, R.id.empty_view);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
