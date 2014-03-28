package com.mattkula.brownsnews.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by matt on 3/27/14.
 */
public class LoadingView extends ImageView {

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context) {
        super(context);
        init();
    }

    private void init(){
        setScaleX(0);
        setScaleY(0);
    }

    public void show(){
        setScaleX(0);
        setScaleY(0);

        this.animate()
                .scaleX(1)
                .scaleY(1);
    }

    public void dismiss(){
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadingView.this.animate()
                        .scaleX(0)
                        .scaleY(0);
            }
        }, 500);
    }
}
