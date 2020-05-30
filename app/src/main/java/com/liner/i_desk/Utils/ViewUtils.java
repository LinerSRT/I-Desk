package com.liner.i_desk.Utils;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.liner.i_desk.Utils.Views.ProgressBottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ViewUtils {
    private static List<ProgressBottomSheetDialog> progressBottomSheetDialogs = new ArrayList<>();

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(float px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToSp(float px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static int pxToSp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static int dpToSp(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }
    public static int dpToSp(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }


    public static void setMargins (View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }

    public static ProgressBottomSheetDialog createProgressDialog(Activity activity, String title, String text){
        return new ProgressBottomSheetDialog.Builder(activity)
                .setTitleText(title)
                .setDialogText(text)
                .build().getDialog();
    }
}
