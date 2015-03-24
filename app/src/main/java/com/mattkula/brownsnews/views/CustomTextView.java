package com.mattkula.brownsnews.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

    static Typeface typeface;
    static Typeface typefaceBold;

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        if (isInEditMode()) {
            return;
        }

        if(typeface == null){
            typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Sentinel-Bold.ttf");
        }
        if(typefaceBold == null){
            typefaceBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Sentinel-Bold.ttf");
        }
        setTypeface(typeface);
    }

    public void setBold(boolean setBold){
        if(setBold){
            setTypeface(typefaceBold);
        } else {
            setTypeface(typeface);
        }
    }
}
