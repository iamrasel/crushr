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

public class SecondaryColorDialog extends AppCompatActivity {

    private RadioGroup mFirstSecondaryGroup;
    private RadioGroup mSecondSecondaryGroup;
    private EditText secondaryInputBox;
    private boolean isChecking = true;
    private int appWidgetId;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.secondary_color_dialog);
        appWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);
        getWindow().setLayout(width, height);

        mFirstSecondaryGroup = findViewById(R.id.secondary_color_selector_first_row);
        mSecondSecondaryGroup = findViewById(R.id.secondary_color_selector_second_row);
        secondaryInputBox = findViewById(R.id.input_secondary);

        mFirstSecondaryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1 && isChecking) {
                isChecking = false;
                mSecondSecondaryGroup.clearCheck();
            }
            isChecking = true;
        });
        mSecondSecondaryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1 && isChecking) {
                isChecking = false;
                mFirstSecondaryGroup.clearCheck();
            }
            isChecking = true;
        });

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(CrushrProvider.SHARED_PREF_TAG, Context.MODE_PRIVATE);
        int secondaryColor = prefs.getInt(CrushrProvider.SHARED_PREF_SECONDARY_COLOR + appWidgetId, ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_1));
        String convertedArgb = ExtraUtils.intColorToArgbString(secondaryColor);
        secondaryInputBox.setText(convertedArgb);
        (findViewById(R.id.preview_secondary)).setBackgroundColor(secondaryColor);

        if (Arrays.asList(getString(R.color.secondary_color_1), getString(R.color.secondary_color_2), getString(R.color.secondary_color_3),
                        getString(R.color.secondary_color_4), getString(R.color.secondary_color_5), getString(R.color.secondary_color_6),
                        getString(R.color.secondary_color_7), getString(R.color.secondary_color_8), getString(R.color.secondary_color_9),
                        getString(R.color.secondary_color_10), getString(R.color.secondary_color_11), getString(R.color.secondary_color_12))
                .contains(convertedArgb)) {
            loadColorSelections(secondaryColor);
        } else {
            mFirstSecondaryGroup.clearCheck();
            mSecondSecondaryGroup.clearCheck();
        }

        secondaryInputBox.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String secondaryColorStr = secondaryInputBox.getText().toString().trim();
                View previewSecondary = findViewById(R.id.preview_secondary);
                try{
                    if(secondaryColorStr.length() < 2) {
                        previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
                    } else if((secondaryColorStr.length() == 7) || (secondaryColorStr.length() == 9)) {
                        previewSecondary.setBackgroundColor(Color.parseColor(secondaryColorStr));
                    } else if(secondaryColorStr.equalsIgnoreCase("#fff")) {
                        previewSecondary.setBackgroundColor(Color.parseColor(secondaryColorStr + "fff"));
                    } else if(secondaryColorStr.equalsIgnoreCase("#000")) {
                        previewSecondary.setBackgroundColor(Color.parseColor(secondaryColorStr + "000"));
                    } else if((2 <= secondaryColorStr.length() && secondaryColorStr.length() < 7)) {
                        previewSecondary.setBackgroundColor(Color.parseColor(String.format("%1$-" + 7 + "s", secondaryColorStr).replace(' ', '0')));
                    } else if(secondaryColorStr.length() == 8) {
                        previewSecondary.setBackgroundColor(Color.parseColor(secondaryColorStr.substring(0, 7)));
                    } else if(secondaryColorStr.length() > 9) {
                        previewSecondary.setBackgroundColor(Color.parseColor(secondaryColorStr.substring(0, 9)));
                    }
                } catch(NumberFormatException ignored) {}
            }
        });

        findViewById(R.id.input_cancel).setOnClickListener(v -> finish());

        findViewById(R.id.input_ok).setOnClickListener(v -> {
            String secondaryColorStr = secondaryInputBox.getText().toString().trim();
            try{
                if(secondaryColorStr.length() < 2) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_change_toast), Toast.LENGTH_SHORT).show();
                } else if((secondaryColorStr.length() == 7) || (secondaryColorStr.length() == 9)) {
                    BaseUtils.setSecondaryColor(getApplicationContext(), Color.parseColor(secondaryColorStr), appWidgetId);
                } else if(secondaryColorStr.equalsIgnoreCase("#fff")) {
                    BaseUtils.setSecondaryColor(getApplicationContext(), Color.parseColor(secondaryColorStr + "fff"), appWidgetId);
                } else if(secondaryColorStr.equalsIgnoreCase("#000")) {
                    BaseUtils.setSecondaryColor(getApplicationContext(), Color.parseColor(secondaryColorStr + "000"), appWidgetId);
                } else if((2 <= secondaryColorStr.length() && secondaryColorStr.length() < 7)) {
                    BaseUtils.setSecondaryColor(getApplicationContext(), Color.parseColor(String.format("%1$-" + 7 + "s", secondaryColorStr).replace(' ', '0')), appWidgetId);
                } else if(secondaryColorStr.length() == 8) {
                    BaseUtils.setSecondaryColor(getApplicationContext(), Color.parseColor(secondaryColorStr.substring(0, 7)), appWidgetId);
                } else if(secondaryColorStr.length() > 9) {
                    BaseUtils.setSecondaryColor(getApplicationContext(), Color.parseColor(secondaryColorStr.substring(0, 9)), appWidgetId);
                }
            } catch(NumberFormatException ignored) {}
            finish();
        });
    }

    private void loadColorSelections(int pColor) {
        if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_1)) {
            ((RadioButton) findViewById(R.id.secondary_color_1)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_2)) {
            ((RadioButton) findViewById(R.id.secondary_color_2)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_3)) {
            ((RadioButton) findViewById(R.id.secondary_color_3)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_4)) {
            ((RadioButton) findViewById(R.id.secondary_color_4)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_5)) {
            ((RadioButton) findViewById(R.id.secondary_color_5)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_6)) {
            ((RadioButton) findViewById(R.id.secondary_color_6)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_7)) {
            ((RadioButton) findViewById(R.id.secondary_color_7)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_8)) {
            ((RadioButton) findViewById(R.id.secondary_color_8)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_9)) {
            ((RadioButton) findViewById(R.id.secondary_color_9)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_10)) {
            ((RadioButton) findViewById(R.id.secondary_color_10)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_11)) {
            ((RadioButton) findViewById(R.id.secondary_color_11)).setChecked(true);
        } else if (pColor == ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_12)) {
            ((RadioButton) findViewById(R.id.secondary_color_12)).setChecked(true);
        }
    }

    @SuppressLint({"NonConstantResourceId", "ResourceType"})
    public void onSecondaryButtonClicked(View view) {
        View previewSecondary = findViewById(R.id.preview_secondary);
        switch(view.getId()) {
            case R.id.secondary_color_1:
                secondaryInputBox.setText(getString(R.color.secondary_color_1));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_1));
                break;
            case R.id.secondary_color_2:
                secondaryInputBox.setText(getString(R.color.secondary_color_2));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_2));
                break;
            case R.id.secondary_color_3:
                secondaryInputBox.setText(getString(R.color.secondary_color_3));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_3));
                break;
            case R.id.secondary_color_4:
                secondaryInputBox.setText(getString(R.color.secondary_color_4));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_4));
                break;
            case R.id.secondary_color_5:
                secondaryInputBox.setText(getString(R.color.secondary_color_5));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_5));
                break;
            case R.id.secondary_color_6:
                secondaryInputBox.setText(getString(R.color.secondary_color_6));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_6));
                break;
            case R.id.secondary_color_7:
                secondaryInputBox.setText(getString(R.color.secondary_color_7));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_7));
                break;
            case R.id.secondary_color_8:
                secondaryInputBox.setText(getString(R.color.secondary_color_8));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_8));
                break;
            case R.id.secondary_color_9:
                secondaryInputBox.setText(getString(R.color.secondary_color_9));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_9));
                break;
            case R.id.secondary_color_10:
                secondaryInputBox.setText(getString(R.color.secondary_color_10));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_10));
                break;
            case R.id.secondary_color_11:
                secondaryInputBox.setText(getString(R.color.secondary_color_11));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_11));
                break;
            case R.id.secondary_color_12:
                secondaryInputBox.setText(getString(R.color.secondary_color_12));
                previewSecondary.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_color_12));
                break;
        }
    }
}
