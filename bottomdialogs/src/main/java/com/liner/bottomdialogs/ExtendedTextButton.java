package com.liner.bottomdialogs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;


public class ExtendedTextButton extends AppCompatTextView {
    private int enabledColor = -1;
    private int disabledColor = -1;

    public ExtendedTextButton(Context context) {
        super(context);
        init(context);
    }

    public ExtendedTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        enabledColor = getCurrentTextColor();
        disabledColor = context.getResources().getColor(R.color.color_disable);
    }


    @Override
    public void setEnabled(boolean enabled) {
        setTextColor((enabled)?enabledColor:disabledColor);
        super.setEnabled(enabled);
    }
}
