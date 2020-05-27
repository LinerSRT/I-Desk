package com.liner.i_desk.Utils;

import android.content.Context;
import android.util.TypedValue;

import androidx.annotation.ColorInt;

import com.liner.i_desk.R;

public class ColorUtils {
    @ColorInt
    public static int getThemeColor(Context context, int resource) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(resource, typedValue, true);
        return context.getResources().getColor(typedValue.resourceId);
    }
}
