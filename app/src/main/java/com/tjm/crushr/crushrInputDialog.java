package com.tjm.crushr;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cymak on 9/30/14.
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class crushrInputDialog extends Activity {

    private EditText newTask;
    private ArrayList<String> tasks;
    private LinearLayout mContainerView;
    private int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.crushr_input_dialog);

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.85);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.85);
        getWindow().setLayout(width, height);

        mContainerView = findViewById(R.id.container);
        newTask = findViewById(R.id.new_task);
        tasks = new ArrayList<>();
        appWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        newTask.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String task = newTask.getText().toString().trim();
                PrefUtils.addItem(getApplicationContext(), task, appWidgetId);
                addItem(task);
                return true;
            }
            return false;
        });

        SharedPreferences prefs = getSharedPreferences(crushrProvider.SHARED_PREF_TAG, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(crushrProvider.SHARED_PREF_LIST+appWidgetId, new HashSet<>());
        for(String item : set) {
            addItem(item);
        }

        findViewById(R.id.settings).setOnClickListener(v -> {
            Intent configIntent = new Intent(getApplicationContext(), crushrConfigActivity.class);
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivity(configIntent);
        });

        findViewById(R.id.input_ok).setOnClickListener(view -> {
            String task = newTask.getText().toString().trim();
            if(!task.isEmpty()) {
                PrefUtils.addItem(getApplicationContext(), task, appWidgetId);
                addItem(task);
            }

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), crushrProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.crushr_listview);
            crushrProvider.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId);

            finish();
        });

        findViewById(R.id.input_add).setOnClickListener(view -> {
            String task = newTask.getText().toString().trim();
            if(task.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.empty_task_error), Toast.LENGTH_LONG).show();
            } else {
                PrefUtils.addItem(getApplicationContext(), task, appWidgetId);
                addItem(task);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), crushrProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.crushr_listview);
        super.onBackPressed();
    }

    private void addItem(final String text) {
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.crushr_add_item, mContainerView, false);

        ((TextView) newView.findViewById(R.id.crushr_task)).setText(text);

        newView.findViewById(R.id.crushr_delete).setOnClickListener(view -> {
            mContainerView.removeView(newView);
            tasks.remove(text);
            PrefUtils.removeItem(getApplicationContext(), text, appWidgetId);
        });

        tasks.add(text);
        newTask.setText("");

        mContainerView.addView(newView, 0);
    }
}
