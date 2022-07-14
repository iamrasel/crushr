package rasel.neo.crushr;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.Set;

public class CrushrProvider extends AppWidgetProvider {

    protected static final String SHARED_PREF_TAG = "crushr_shared_pref";
    protected static final String SHARED_PREF_LIST = "crushr_task_list_";
    protected static final String SHARED_PREF_PRIMARY_COLOR = "crushr_primary_color_";
    protected static final String SHARED_PREF_SECONDARY_COLOR = "crushr_secondary_color_";
    protected static final String EXTRA_WORD = "crushr_word";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    protected static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.crushr_widget);

        Intent listIntent = new Intent(context, WidgetService.class);
        listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        listIntent.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.crushr_listview, listIntent);

        Intent addIntent = new Intent(context, NewTaskDialog.class);
        addIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent addPendingIntent = PendingIntent.getActivity(context, appWidgetId, addIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.add_crushr_button, addPendingIntent);

        Intent clickIntent = new Intent(context, SingleTaskDialog.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent clickPI = PendingIntent.getActivity(context, appWidgetId, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setPendingIntentTemplate(R.id.crushr_listview, clickPI);

        SharedPreferences prefs = context.getSharedPreferences(CrushrProvider.SHARED_PREF_TAG, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(CrushrProvider.SHARED_PREF_LIST+appWidgetId, new HashSet<>());
        if(set.isEmpty()) {
            views.setViewVisibility(R.id.empty, View.VISIBLE);
            views.setViewVisibility(R.id.crushr_listview, View.GONE);
        } else {
            views.setViewVisibility(R.id.empty, View.GONE);
            views.setViewVisibility(R.id.crushr_listview, View.VISIBLE);
        }

        int primaryColor = prefs.getInt(CrushrProvider.SHARED_PREF_PRIMARY_COLOR + appWidgetId, ContextCompat.getColor(context, R.color.primary_color_1));
        int secondaryColor = prefs.getInt(CrushrProvider.SHARED_PREF_SECONDARY_COLOR + appWidgetId, ContextCompat.getColor(context, R.color.secondary_color_1));
        views.setInt(R.id.title, "setBackgroundColor", primaryColor);
        views.setInt(R.id.add_crushr_button_bg, "setColorFilter", secondaryColor);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.crushr_listview);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            if(Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction())) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName name = new ComponentName(context, CrushrProvider.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);
                onUpdate(context, appWidgetManager, appWidgetIds);
            }
        }
        super.onReceive(context, intent);
    }
}
