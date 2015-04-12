package com.mattkula.brownsnews.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;

public class ViewUtils {

    public static void updateActionBarFont(Activity activity) {
        int titleId = activity.getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTextview = (TextView)activity.findViewById(titleId);
        actionBarTextview.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Sentinel-Bold.ttf"));
    }

}
