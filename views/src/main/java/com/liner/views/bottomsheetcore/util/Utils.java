

package com.liner.views.bottomsheetcore.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.NonNull;


public final class Utils {


    public static final int API_VERSION = Build.VERSION.SDK_INT;

    public static final boolean IS_AT_LEAST_JELLY_BEAN = (API_VERSION >= Build.VERSION_CODES.JELLY_BEAN);
    public static final boolean IS_AT_LEAST_KITKAT = (API_VERSION >= Build.VERSION_CODES.KITKAT);
    public static final boolean IS_AT_LEAST_LOLLIPOP = (API_VERSION >= Build.VERSION_CODES.LOLLIPOP);
    public static final boolean IS_AT_LEAST_MARSHMALLOW = (API_VERSION >= Build.VERSION_CODES.M);
    public static final boolean IS_AT_LEAST_NOUGAT = (API_VERSION >= Build.VERSION_CODES.N);
    public static final boolean IS_AT_LEAST_OREO = (API_VERSION >= Build.VERSION_CODES.O);




    
    public static int getStatusBarSize(@NonNull Context context) {
        Preconditions.nonNull(context);

        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(
            "status_bar_height",
            "dimen",
            "android"
        );

        return resources.getDimensionPixelSize(resourceId);
    }




    
    public static int getNavigationBarSize(@NonNull Context context) {
        Preconditions.nonNull(context);

        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(
            "navigation_bar_height",
            "dimen",
            "android"
        );

        return resources.getDimensionPixelSize(resourceId);
    }




}
