package com.liner.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatImageView;

import com.liner.utils.ViewUtils;

public class GradientImageView extends AppCompatImageView {
    private Paint gradientPaint;

    public GradientImageView(Context context) {
        super(context);
        init(context, null);
    }

    public GradientImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.GradientImageView, 0, 0);
        int gradientHeight = typedArray.getInteger(R.styleable.GradientImageView_giv_gradient_height, 0);
        float gradientColorAlpha = typedArray.getFloat(R.styleable.GradientImageView_giv_gradient_color_alpha, 1f);
        int gradientColor = typedArray.getColor(R.styleable.GradientImageView_giv_gradient_color, Color.TRANSPARENT);
        typedArray.recycle();
        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, ViewUtils.dpToPx(gradientHeight), gradientColor, adjustAlpha(gradientColor, gradientColorAlpha), Shader.TileMode.CLAMP);
        gradientPaint.setShader(linearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), gradientPaint);
    }
}
