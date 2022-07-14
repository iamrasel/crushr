package rasel.neo.crushr;

import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SingleTaskDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.single_task_dialog);

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.50);
        getWindow().setLayout(width, height);

        final String task;
        final int appWidgetId;
        if(getIntent().hasExtra("rasel.neo.crushr.OPEN")) {
            task = getIntent().getExtras().getString("text");
            appWidgetId = getIntent().getExtras().getInt("id");
        } else {
            task = getIntent().getExtras().getString(CrushrProvider.EXTRA_WORD);
            appWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        }

        EditText message = findViewById(R.id.message);
        message.setMovementMethod(new ScrollingMovementMethod());
        message.setText(task);

        findViewById(R.id.copy_btn).setOnClickListener(v -> {
            ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("task", task);
            clipBoard.setPrimaryClip(clipData);

            Toast.makeText(getApplicationContext(), getString(R.string.text_copied_toast), Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.share_btn).setOnClickListener(v -> {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/*");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, task);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        });

        findViewById(R.id.tweet_btn).setOnClickListener(v -> {
            Intent tweetIntent;
            if(ExtraUtils.packageExist(getApplicationContext(), getString(R.string.twitter_package))) {
                tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.setClassName(getString(R.string.twitter_package), getString(R.string.twitter_class));
                tweetIntent.setType("text/*");
                tweetIntent.putExtra(android.content.Intent.EXTRA_TEXT, task);
            } else {
                Uri uri = Uri.parse(getString(R.string.twitter_url) + task);
                tweetIntent = new Intent(Intent.ACTION_VIEW, uri);
            }
            startActivity(tweetIntent);
        });

        findViewById(R.id.pin_btn).setOnClickListener(v ->
                NotificationReceiver.createNotification(getApplicationContext(), task, appWidgetId));

        findViewById(R.id.edit_btn).setOnClickListener(view -> {
            String editedTask = message.getText().toString().trim();
            if(!editedTask.isEmpty()) {
                if(ExtraUtils.notificationExist(getApplicationContext())) {
                    BaseUtils.removeItem(getApplicationContext(), task, appWidgetId);
                    NotificationReceiver.createNotification(getApplicationContext(), editedTask, appWidgetId);
                } else {
                    BaseUtils.removeItem(getApplicationContext(), task, appWidgetId);
                }
                BaseUtils.addItem(getApplicationContext(), editedTask, appWidgetId);
                ExtraUtils.refreshListView(getApplicationContext(), appWidgetId);
            }
            finish();
        });

        findViewById(R.id.remove_btn).setOnClickListener(view -> {
            BaseUtils.removeItem(getApplicationContext(), task, appWidgetId);
            ExtraUtils.refreshListView(getApplicationContext(), appWidgetId);
            finish();
        });
    }
}
