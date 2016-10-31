package com.humbleai.humblenotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class SettingsActivity extends AppCompatActivity {

    private boolean isUpdated = false;
    private String activityType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAppCaption));
        toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.colorDescription));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        activityType = extras.getString("type");

        CheckBox checkMinTwoNotebooks = (CheckBox) findViewById(R.id.action_settings_min_two_notebooks);
        CheckBox checkMinTwoNotes = (CheckBox) findViewById(R.id.action_settings_min_two_notes);
        CheckBox checkSmallFontsNotebooks = (CheckBox) findViewById(R.id.action_settings_small_font_notebooks);
        CheckBox checkSmallFontsNotes = (CheckBox) findViewById(R.id.action_settings_small_font_notes);

        SharedPreferences settings = getSharedPreferences(ScrollingActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        checkMinTwoNotebooks.setChecked(settings.getBoolean("action_settings_min_two_notebooks", false));
        checkMinTwoNotes.setChecked(settings.getBoolean("action_settings_min_two_notes", false));
        checkSmallFontsNotebooks.setChecked(settings.getBoolean("action_settings_small_font_notebooks", false));
        checkSmallFontsNotes.setChecked(settings.getBoolean("action_settings_small_font_notes", false));

        editor.apply();


        checkMinTwoNotebooks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences(ScrollingActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("action_settings_min_two_notebooks", isChecked);
                editor.apply();
                if (activityType.equals("sets")) isUpdated = true;
            }
        });

        checkMinTwoNotes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences(ScrollingActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("action_settings_min_two_notes", isChecked);
                editor.apply();
                if (activityType.equals("notes")) isUpdated = true;
            }
        });

        checkSmallFontsNotebooks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences(ScrollingActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("action_settings_small_font_notebooks", isChecked);
                editor.apply();
                if (activityType.equals("sets")) isUpdated = true;
            }
        });

        checkSmallFontsNotes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences(ScrollingActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("action_settings_small_font_notes", isChecked);
                editor.apply();
                if (activityType.equals("notes")) isUpdated = true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("isUpdated", isUpdated);
        setResult(RESULT_OK, intent);

        super.finish();
    }
}
