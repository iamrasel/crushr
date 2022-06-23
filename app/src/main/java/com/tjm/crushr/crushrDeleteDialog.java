package com.tjm.crushr;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by cymak on 9/30/14.
 */
public class crushrDeleteDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.crushr_delete_dialog);

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.30);
        getWindow().setLayout(width, height);

        final String task = getIntent().getExtras().getString(crushrProvider.EXTRA_WORD);
        final int appWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

        ((TextView)findViewById(R.id.message)).setText(Html.fromHtml(getString(R.string.delete_task, task)));

        findViewById(R.id.input_cancel).setOnClickListener(view -> finish());

        findViewById(R.id.input_ok).setOnClickListener(view -> {
            PrefUtils.removeItem(getApplicationContext(), task, appWidgetId);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), crushrProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.crushr_listview);
            crushrProvider.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId);

            finish();
        });
    }
}
