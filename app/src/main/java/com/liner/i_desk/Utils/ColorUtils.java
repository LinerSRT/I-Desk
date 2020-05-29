package com.liner.i_desk.Utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.annotation.ColorInt;

public class ColorUtils {
    @ColorInt
    public static int getThemeColor(Context context, int resource) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(resource, typedValue, true);
        return context.getResources().getColor(typedValue.resourceId);
    }

    @ColorInt
    public static int interpolateColor(@ColorInt int startColor, @ColorInt int endColor, float proportion) {
        if (proportion > 1 || proportion < 0)
            proportion = proportion / 100;
        float[] HSVa = new float[3];
        float[] HSVb = new float[3];
        float[] HSVOut = new float[3];
        Color.colorToHSV(startColor, HSVa);
        Color.colorToHSV(endColor, HSVb);
        for (int i = 0; i < 3; i++) {
            HSVOut[i] = interpolate(HSVa[i], HSVb[i], proportion);
        }
        return Color.HSVToColor((int) interpolate(Color.alpha(startColor), Color.alpha(endColor), proportion), HSVOut);
    }

    private static float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }
}
