package rasel.neo.crushr;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ConfigActivity extends AppCompatActivity {

    private int appWidgetId;
    private boolean shouldExecuteOnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.crushr_config);
        appWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        shouldExecuteOnResume = false;

        // load previews
        loadPreviews();

        findViewById(R.id.github_btn).setOnClickListener(v -> {
            Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_url)));
            startActivity(openLink);
        });

        findViewById(R.id.primary_color_dialog).setOnClickListener(v -> {
            Intent primaryIntent = new Intent(getApplicationContext(), PrimaryColorDialog.class);
            primaryIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivity(primaryIntent);
        });

        findViewById(R.id.secondary_color_dialog).setOnClickListener(v -> {
            Intent secondaryIntent = new Intent(getApplicationContext(), SecondaryColorDialog.class);
            secondaryIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivity(secondaryIntent);
        });

        findViewById(R.id.btn_reset).setOnClickListener(v -> {
            BaseUtils.setPrimaryColor(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.primary_color_1), appWidgetId);
            BaseUtils.setSecondaryColor(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_1), appWidgetId);
            loadPreviews();
        });

        findViewById(R.id.configs_apply).setOnClickListener(v -> {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            CrushrProvider.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        });
    }

    private void loadPreviews() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(CrushrProvider.SHARED_PREF_TAG, Context.MODE_PRIVATE);
        int primaryColor = prefs.getInt(CrushrProvider.SHARED_PREF_PRIMARY_COLOR + appWidgetId, ContextCompat.getColor(getApplicationContext(), R.color.primary_color_1));
        int secondaryColor = prefs.getInt(CrushrProvider.SHARED_PREF_SECONDARY_COLOR + appWidgetId, ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_1));

        GradientDrawable borderPrimary = new GradientDrawable();
        borderPrimary.setShape(GradientDrawable.OVAL);
        borderPrimary.setColor(primaryColor);
        borderPrimary.setStroke(1, 0xFF111111);
        (findViewById(R.id.primary_color_preview)).setBackground(borderPrimary);

        GradientDrawable borderSecondary = new GradientDrawable();
        borderSecondary.setShape(GradientDrawable.OVAL);
        borderSecondary.setColor(secondaryColor);
        borderSecondary.setStroke(1, 0xFF111111);
        (findViewById(R.id.secondary_color_preview)).setBackground(borderSecondary);
    }

    protected void onResume() {
        super.onResume();
        if(shouldExecuteOnResume){
            loadPreviews();
        } else {
            shouldExecuteOnResume = true;
        }
    }
}
