package com.mattkula.brownsnews;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.mattkula.brownsnews.background.UpdateManager;

/**
 * Created by matt on 4/2/14.
 */
public class SettingsActivity extends Activity {

    CheckBox readShown;
    CheckBox notificationsEnabled;
    Spinner notificationDuration;

    int selectedIndex = 0;

    int[] integers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Sentinel-Bold.ttf"));
        getActionBar().setTitle("Settings");

        integers = getResources().getIntArray(R.array.notification_duration_value);
        selectedIndex = getSelectedIndex();
        notificationsEnabled = (CheckBox)findViewById(R.id.checkbox_notifications_enabled);
        notificationsEnabled.setChecked(Prefs.getValueForKey(this, Prefs.TAG_NOTIFICATION_ENABLED, true));
        notificationDuration = (Spinner)findViewById(R.id.spinner_notification_interval);
        notificationDuration.setSelection(selectedIndex);
        notificationDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedIndex = notificationDuration.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        readShown = (CheckBox)findViewById(R.id.checkbox_read_shown);
        readShown.setChecked(Prefs.getValueForKey(this, Prefs.TAG_READ_SHOWN, false));
    }

    public int getSelectedIndex(){
        int savedInterval = (int)Prefs.getValueForKey(this, Prefs.TAG_NOTIFICATION_INTERVAL, 3600);

        for(int i=0; i < integers.length; i++){
            if(integers[i] == savedInterval){
                return i;
            }
        }
        throw new IllegalStateException("Duration not found for settings.");
    }

    private void save(){
        setResult(RESULT_OK);
        Prefs.setValueForKey(this, Prefs.TAG_READ_SHOWN, readShown.isChecked());
        Prefs.setValueForKey(this, Prefs.TAG_NOTIFICATION_ENABLED, notificationsEnabled.isChecked());
        Prefs.setValueForKey(this, Prefs.TAG_NOTIFICATION_INTERVAL, integers[selectedIndex]);
        UpdateManager.rescheduleUpdates(this);
    }

    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.select_sources, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.select_done:
                save();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
