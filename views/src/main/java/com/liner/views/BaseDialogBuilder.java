package com.liner.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.BaseConfig;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.utils.ViewUtils;

@SuppressLint("ViewConstructor")
public class BaseDialogBuilder {
    public Activity activity;
    public Config config;
    public boolean dismissOnTouchOutside = false;
    public float dimAmount = 0.2f;
    public int animationDuration = 500;
    public Interpolator animationInterpolator = new OvershootInterpolator();
    public int backgroundColor = Color.TRANSPARENT;
    public float cornerRadius = ViewUtils.dpToPx(8);
    public String dialogTitleText = "";
    public String dialogTextText = "";
    public String dialogDoneText = "Ок";
    public String dialogCancelText = "";
    public View dialogView = null;
    public Type dialogType = Type.INFO;
    public BaseDialog.BaseDialogSelectionListener selectionListener;
    public String[] selectionList = new String[]{};


    public enum Type{
        INFO,
        WARNING,
        ERROR,
        QUESTION,
        PROGRESS,
        INDETERMINATE,
        SINGLE_CHOOSE
    }

    public BaseDialogBuilder(Activity activity) {
        this.activity = activity;
    }

    public BaseDialogBuilder setDialogType(Type dialogType) {
        this.dialogType = dialogType;
        return this;
    }

    public BaseDialogBuilder setDismissOnTouchOutside(boolean dismissOnTouchOutside) {
        this.dismissOnTouchOutside = dismissOnTouchOutside;
        return this;
    }

    public BaseDialogBuilder setSelectionListener(BaseDialog.BaseDialogSelectionListener selectionListener) {
        this.selectionListener = selectionListener;
        return this;
    }

    public BaseDialogBuilder setSelectionList(String[] selectionList) {
        this.selectionList = selectionList;
        return this;
    }

    public BaseDialogBuilder setDialogTitle(String title){
        BaseDialogBuilder.this.dialogTitleText = title;
        return this;
    }

    public BaseDialogBuilder setDialogText(String dialogText) {
        BaseDialogBuilder.this.dialogTextText = dialogText;
        return this;
    }

    public BaseDialogBuilder setDialogCustomView(View view) {
        BaseDialogBuilder.this.dialogView = view;
        return this;
    }

    public BaseDialogBuilder setDimAmount(float dimAmount) {
        BaseDialogBuilder.this.dimAmount = dimAmount;
        return this;
    }

    public BaseDialogBuilder setAnimationDuration(int animationDuration) {
        BaseDialogBuilder.this.animationDuration = animationDuration;
        return this;
    }

    public BaseDialogBuilder setAnimationInterpolator(Interpolator animationInterpolator) {
        BaseDialogBuilder.this.animationInterpolator = animationInterpolator;
        return this;
    }

    public BaseDialogBuilder setBackgroundColor(int backgroundColor) {
        BaseDialogBuilder.this.backgroundColor = backgroundColor;
        return this;
    }

    public BaseDialogBuilder setCornerRadius(float cornerRadius) {
        BaseDialogBuilder.this.cornerRadius = cornerRadius;
        return this;
    }

    public BaseDialog build() {
        this.config = new Config.Builder(activity)
                .dismissOnTouchOutside(dismissOnTouchOutside)
                .dimAmount(dimAmount)
                .sheetAnimationDuration(animationDuration)
                .sheetAnimationInterpolator(animationInterpolator)
                .sheetBackgroundColor(backgroundColor)
                .sheetCornerRadius(cornerRadius)
                .build();
        return new BaseDialog(activity, config, this);
    }

    public static BaseDialog buildFast(Activity activity,
                                       String title,
                                       String text,
                                       String cancelText,
                                       String doneText,
                                       BaseDialogBuilder.Type dialogType,
                                       View.OnClickListener cancelListener,
                                       View.OnClickListener doneListener){
        final BaseDialog baseDialog = new BaseDialogBuilder(activity)
                .setDialogText(text)
                .setDialogTitle(title)
                .setDialogType(dialogType)
                .build();
        baseDialog.setDialogCancel(cancelText, cancelListener);
        baseDialog.setDialogDone(doneText, doneListener);
        return baseDialog;
    }
}