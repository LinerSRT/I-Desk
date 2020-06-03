package com.liner.i_desk.Utils.Messages;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public abstract class BaseMessageItem extends LinearLayout {
    protected Context context;
    protected LayoutInflater inflater;

    public BaseMessageItem(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.context = context;
        onInflate();
        onFindViewById();
    }

    public BaseMessageItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        this.context = context;
        onInflate();
        onFindViewById();
    }

    protected abstract void onFindViewById();
    protected abstract void onInflate();

    protected void hideView(){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        animation.setDuration(400);
        startAnimation(animation);
        setVisibility(View.GONE);
    }
    protected void showView(){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animation.setDuration(400);
        startAnimation(animation);
        setVisibility(View.VISIBLE);
    }

}