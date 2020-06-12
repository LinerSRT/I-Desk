package com.liner.views.irbottomnavigation;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.widget.AppCompatImageButton;

import com.liner.views.R;

public class CentreButton extends AppCompatImageButton {

    public CentreButton(Context context) {
        super(context);
        setBackground(getContext().getResources().getDrawable(R.drawable.circle_bg_without_padding));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            animateClick(AnimationUtils.loadAnimation(getContext(), R.anim.item_in));
        }
        boolean result = super.onTouchEvent(ev);
        if (!result) {
            if(ev.getAction() == MotionEvent.ACTION_UP) {
                cancelLongPress();
            }
            setPressed(false);
        }
        return result;
    }

    public void animateClick(Animation animation){
        animation.setDuration(200);
        animation.setInterpolator(new OvershootInterpolator());
        startAnimation(animation);
    }
}
