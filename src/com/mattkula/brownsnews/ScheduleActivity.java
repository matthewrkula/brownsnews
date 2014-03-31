package com.mattkula.brownsnews;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

/**
 * Created by matt on 3/30/14.
 */
public class ScheduleActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TextView(this));
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Sentinel-Bold.ttf"));

        getActionBar().setTitle("Schedule");
    }
}
