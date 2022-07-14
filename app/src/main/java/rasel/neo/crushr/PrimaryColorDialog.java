package rasel.neo.crushr;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

public class PrimaryColorDialog extends AppCompatActivity {

    private RadioGroup mFirstPrimaryGroup;
    private RadioGroup mSecondPrimaryGroup;
    private EditText primaryInputBox;
    private boolean isChecking = true;
    private int appWidgetId;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.primary_color_dialog);
        appWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);
        getWindow().setLayout(width, height);

        mFirstPrimaryGroup = findViewById(R.id.primary_color_selector_first_row);
        mSecondPrimaryGroup = findViewById(R.id.primary_color_selector_second_row);
        primaryInputBox = findViewById(R.id.input_primary);

        mFirstPrimaryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1 && isChecking) {
                isChecking = false;
                mSecondPrimaryGroup.clearCheck();
            }
            isChecking = true;
        });
        mSecondPrimaryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1 && isChecking) {
                isChecking = false;
                mFirstPrimaryGroup.clearCheck();
            }
            isChecking = true;
        });

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(CrushrProvider.SHARED_PREF_TAG, Context.MODE_PRIVATE);
        int primaryColor = prefs.getInt(CrushrProvider.SHARED_PREF_PRIMARY_COLOR + appWidgetId, ContextCompat.getColor(getApplicationContext(), R.color.primary_color_1));
        String convertedArgb = ExtraUtils.intColorToArgbString(primaryColor);
        primaryInputBox.setText(convertedArgb);
        (findViewById(R.id.preview_primary)).setBackgroundColor(primaryColor);

        if (Arrays.asList(getString(R.color.primary_color_1), getString(R.color.primary_color_2), getString(R.color.primary_color_3),
                getString(R.color.primary_color_4), getString(R.color.primary_color_5), getString(R.color.primary_color_6),
                getString(R.color.primary_color_7), getString(R.color.primary_color_8), getString(R.color.primary_color_9),
                getString(R.color.primary_color_10), getString(R.color.primary_color_11), getString(R.color.primary_color_12))
                .contains(convertedArgb)) {
            loadColorSelections(primaryColor);
        } else {
            mFirstPrimaryGroup.clearCheck();
            mSecondPrimaryGroup.clearCheck();
        }

        primaryInputBox.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String primaryColorStr = primaryInputBox.getText().toString().trim();
                View previewPrimary = findViewById(R.id.preview_primary);
                try{
                    if(primaryColorStr.length() < 2) {
                        previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
                    } else if((primaryColorStr.length() == 7) || (primaryColorStr.length() == 9)) {
                        previewPrimary.setBackgroundColor(Color.parseColor(primaryColorStr));
                    } else if(primaryColorStr.equalsIgnoreCase("#fff")) {
                        previewPrimary.setBackgroundColor(Color.parseColor(primaryColorStr + "fff"));
                    } else if(primaryColorStr.equalsIgnoreCase("#000")) {
                        previewPrimary.setBackgroundColor(Color.parseColor(primaryColorStr + "000"));
                    } else if((2 <= primaryColorStr.length() && primaryColorStr.length() < 7)) {
                        previewPrimary.setBackgroundColor(Color.parseColor(String.format("%1$-" + 7 + "s", primaryColorStr).replace(' ', '0')));
                    } else if(primaryColorStr.length() == 8) {
                        previewPrimary.setBackgroundColor(Color.parseColor(primaryColorStr.substring(0, 7)));
                    } else if(primaryColorStr.length() > 9) {
                        previewPrimary.setBackgroundColor(Color.parseColor(primaryColorStr.substring(0, 9)));
                    }
                } catch(NumberFormatException ignored) {}
            }
        });

        findViewById(R.id.input_cancel).setOnClickListener(v -> finish());

        findViewById(R.id.input_ok).setOnClickListener(v -> {
            String primaryColorStr = primaryInputBox.getText().toString().trim();
            try{
                if(primaryColorStr.length() < 2) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_change_toast), Toast.LENGTH_SHORT).show();
                } else if((primaryColorStr.length() == 7) || (primaryColorStr.length() == 9)) {
                    BaseUtils.setPrimaryColor(getApplicationContext(), Color.parseColor(primaryColorStr), appWidgetId);
                } else if(primaryColorStr.equalsIgnoreCase("#fff")) {
                    BaseUtils.setPrimaryColor(getApplicationContext(), Color.parseColor(primaryColorStr + "fff"), appWidgetId);
                } else if(primaryColorStr.equalsIgnoreCase("#000")) {
                    BaseUtils.setPrimaryColor(getApplicationContext(), Color.parseColor(primaryColorStr + "000"), appWidgetId);
                } else if((2 <= primaryColorStr.length() && primaryColorStr.length() < 7)) {
                    BaseUtils.setPrimaryColor(getApplicationContext(), Color.parseColor(String.format("%1$-" + 7 + "s", primaryColorStr).replace(' ', '0')), appWidgetId);
                } else if(primaryColorStr.length() == 8) {
                    BaseUtils.setPrimaryColor(getApplicationContext(), Color.parseColor(primaryColorStr.substring(0, 7)), appWidgetId);
                } else if(primaryColorStr.length() > 9) {
                    BaseUtils.setPrimaryColor(getApplicationContext(), Color.parseColor(primaryColorStr.substring(0, 9)), appWidgetId);
                }
            } catch(NumberFormatException ignored) {}
            finish();
        });
    }

    private void loadColorSelections(int pColor) {
        if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_1)) {
            ((RadioButton) findViewById(R.id.primary_color_1)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_2)) {
            ((RadioButton) findViewById(R.id.primary_color_2)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_3)) {
            ((RadioButton) findViewById(R.id.primary_color_3)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_4)) {
            ((RadioButton) findViewById(R.id.primary_color_4)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_5)) {
            ((RadioButton) findViewById(R.id.primary_color_5)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_6)) {
            ((RadioButton) findViewById(R.id.primary_color_6)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_7)) {
            ((RadioButton) findViewById(R.id.primary_color_7)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_8)) {
            ((RadioButton) findViewById(R.id.primary_color_8)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_9)) {
            ((RadioButton) findViewById(R.id.primary_color_9)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_10)) {
            ((RadioButton) findViewById(R.id.primary_color_10)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_11)) {
            ((RadioButton) findViewById(R.id.primary_color_11)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.primary_color_12)) {
            ((RadioButton) findViewById(R.id.primary_color_12)).setChecked(true);
        }
    }

    @SuppressLint({"NonConstantResourceId", "ResourceType"})
    public void onPrimaryButtonClicked(View view) {
        View previewPrimary = findViewById(R.id.preview_primary);
        switch(view.getId()) {
            case R.id.primary_color_1:
                primaryInputBox.setText(getString(R.color.primary_color_1));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_1));
                break;
            case R.id.primary_color_2:
                primaryInputBox.setText(getString(R.color.primary_color_2));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_2));
                break;
            case R.id.primary_color_3:
                primaryInputBox.setText(getString(R.color.primary_color_3));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_3));
                break;
            case R.id.primary_color_4:
                primaryInputBox.setText(getString(R.color.primary_color_4));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_4));
                break;
            case R.id.primary_color_5:
                primaryInputBox.setText(getString(R.color.primary_color_5));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_5));
                break;
            case R.id.primary_color_6:
                primaryInputBox.setText(getString(R.color.primary_color_6));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_6));
                break;
            case R.id.primary_color_7:
                primaryInputBox.setText(getString(R.color.primary_color_7));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_7));
                break;
            case R.id.primary_color_8:
                primaryInputBox.setText(getString(R.color.primary_color_8));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_8));
                break;
            case R.id.primary_color_9:
                primaryInputBox.setText(getString(R.color.primary_color_9));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_9));
                break;
            case R.id.primary_color_10:
                primaryInputBox.setText(getString(R.color.primary_color_10));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_10));
                break;
            case R.id.primary_color_11:
                primaryInputBox.setText(getString(R.color.primary_color_11));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_11));
                break;
            case R.id.primary_color_12:
                primaryInputBox.setText(getString(R.color.primary_color_12));
                previewPrimary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_color_12));
                break;
        }
    }
}
