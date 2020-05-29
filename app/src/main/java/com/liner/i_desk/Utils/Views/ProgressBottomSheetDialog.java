package com.liner.i_desk.Utils.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;

@SuppressLint("ViewConstructor")
public class ProgressBottomSheetDialog extends BaseBottomSheet {
    private TextView progressDialogTitle;
    private TextView progressDialogText;
    private ProgressBar progressBar;

    public ProgressBottomSheetDialog(@NonNull Activity hostActivity) {
        super(hostActivity, new Config.Builder(hostActivity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(hostActivity, R.attr.backgroundColor))
                .dismissOnTouchOutside(false)
                .build());

    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout, this, false);
        progressDialogTitle = view.findViewById(R.id.progressDialogTitle);
        progressDialogText = view.findViewById(R.id.progressDialogText);
        progressBar = view.findViewById(R.id.progressDialogBar);
        return view;
    }


    public void setDialogTitle(String dialogTitle) {
        progressDialogTitle.setText(dialogTitle);
    }

    public void setDialogText(String dialogText) {
        progressDialogText.setText(dialogText);
    }

    public void setProgress(int value){
        progressBar.setProgress(value);
    }

    public void create(){
        show(true);
    }
}