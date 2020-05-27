package com.liner.i_desk.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;

@SuppressLint("ViewConstructor")
public class IndeterminateBottomSheetDialog extends BaseBottomSheet {
    private TextView indeterminateDialogTitle;
    private TextView indeterminateDialogText;

    public IndeterminateBottomSheetDialog(@NonNull Activity hostActivity) {
        super(hostActivity, new Config.Builder(hostActivity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(hostActivity, R.attr.backgroundColor))
                .dismissOnTouchOutside(false)
                .build());

    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_indeterminate_dialog_layout, this, false);
        indeterminateDialogTitle = view.findViewById(R.id.indeterminateDialogTitle);
        indeterminateDialogText = view.findViewById(R.id.indeterminateDialogText);
        return view;
    }


    public void setDialogTitle(String dialogTitle) {
        indeterminateDialogTitle.setText(dialogTitle);
    }

    public void setDialogText(String dialogText) {
        indeterminateDialogText.setText(dialogText);
    }


    public void create(){
        show(true);
    }
}