package com.liner.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.liner.utils.ImageUtils;

public class BlurredImageView extends AppCompatImageView {
    private float SCALE = 0.2f;
    private int RADIUS = 10;

    public BlurredImageView(Context context) {
        super(context);
        setImageBitmap(ImageUtils.fastblur(ImageUtils.drawableToBitmap(getDrawable()), SCALE, RADIUS));
    }

    public BlurredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageBitmap(ImageUtils.fastblur(ImageUtils.drawableToBitmap(getDrawable()), SCALE, RADIUS));
    }

    public BlurredImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageBitmap(ImageUtils.fastblur(ImageUtils.drawableToBitmap(getDrawable()), SCALE, RADIUS));
    }


    public void setBlurDrawable(Drawable drawable, float scale, int radius){
        setImageBitmap(ImageUtils.fastblur(ImageUtils.drawableToBitmap(drawable), scale, radius));
    }

    public void setBlurDrawable(Drawable drawable){
        setImageBitmap(ImageUtils.fastblur(ImageUtils.drawableToBitmap(drawable), SCALE, RADIUS));
    }

    public void setBlurBitmap(Bitmap bitmap, float scale, int radius){
        setImageBitmap(ImageUtils.fastblur(bitmap, scale, radius));
    }

    public void setBlurBitmap(Bitmap bitmap){
        setImageBitmap(ImageUtils.fastblur(bitmap, SCALE, RADIUS));
    }

    public void setBlurResource(int resID, float scale, int radius){
        setBlurDrawable(getContext().getResources().getDrawable(resID), scale, radius);
    }

    public void setBlurResource(int resID){
        setBlurDrawable(getContext().getResources().getDrawable(resID));
    }
}
