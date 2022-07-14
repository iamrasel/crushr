package rasel.neo.crushr;

import android.appwidget.AppWidgetManager;
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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class NewTaskDialog extends AppCompatActivity {

    private EditText newTask;
    private ArrayList<String> tasks;
    private LinearLayout mContainerView;
    private int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.new_task_dialog);

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.85);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.85);
        getWindow().setLayout(width, height);

        if(getIntent().hasExtra("rasel.neo.crushr.ADD")) {
            appWidgetId = getIntent().getExtras().getInt("id");
        } else {
            appWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        }

        mContainerView = findViewById(R.id.container);
        newTask = findViewById(R.id.new_task);
        tasks = new ArrayList<>();

        newTask.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                affirmativeAction();
            }
            return true;
        });

        SharedPreferences prefs = getSharedPreferences(CrushrProvider.SHARED_PREF_TAG, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(CrushrProvider.SHARED_PREF_LIST+appWidgetId, new HashSet<>());
        for(String item : set) {
            addItem(item);
        }

        findViewById(R.id.settings).setOnClickListener(v -> {
            Intent configIntent = new Intent(getApplicationContext(), ConfigActivity.class);
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivity(configIntent);
        });

        findViewById(R.id.save_btn).setOnClickListener(view -> {
            ExtraUtils.refreshListView(getApplicationContext(), appWidgetId);
            finish();
        });

        findViewById(R.id.add_btn).setOnClickListener(view -> affirmativeAction());
    }

    @Override
    public void onBackPressed() {
        ExtraUtils.refreshListView(getApplicationContext(), appWidgetId);
        super.onBackPressed();
    }

    private void addItem(final String text) {
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.crushr_add_item, mContainerView, false);

        ((TextView) newView.findViewById(R.id.crushr_task)).setText(text);

        newView.findViewById(R.id.crushr_delete).setOnClickListener(view -> {
            mContainerView.removeView(newView);
            tasks.remove(text);
            BaseUtils.removeItem(getApplicationContext(), text, appWidgetId);
        });

        tasks.add(text);
        newTask.setText("");

        mContainerView.addView(newView, 0);
    }

    private void affirmativeAction() {
        String task = newTask.getText().toString().trim();
        if(task.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_task_error), Toast.LENGTH_SHORT).show();
        } else {
            BaseUtils.addItem(getApplicationContext(), task, appWidgetId);
            addItem(task);
        }
    }
}
