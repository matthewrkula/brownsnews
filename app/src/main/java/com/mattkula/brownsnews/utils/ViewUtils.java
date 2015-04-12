package com.mattkula.brownsnews.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class ViewUtils {

    public static void updateActionBarFont(Activity activity) {
        int titleId = activity.getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTextview = (TextView)activity.findViewById(titleId);
        actionBarTextview.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Sentinel-Bold.ttf"));
    }

    public static float pixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public static float dpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }
}
