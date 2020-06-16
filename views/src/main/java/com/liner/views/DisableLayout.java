package com.liner.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class DisableLayout extends RelativeLayout {
    private boolean enableTouch = true;

    public DisableLayout(Context context) {
        super(context);
    }

    public DisableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DisableLayout, 0, 0);
        enableTouch = typedArray.getBoolean(R.styleable.DisableLayout_dl_enabled, true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return enableTouch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return enableTouch;
    }

    public void setDisableTouch(boolean enableTouch) {
        this.enableTouch = enableTouch;
    }
}
