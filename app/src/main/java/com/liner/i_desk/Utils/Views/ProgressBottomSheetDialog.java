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
    private TextView progressDialogFileStatus;
    private ProgressBar progressBar;
    private String titleText;
    private String dialogText;

    private ProgressBottomSheetDialog(Builder builder) {
        super(builder.activity, new Config.Builder(builder.activity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(builder.activity, R.attr.backgroundColor))
                .dismissOnTouchOutside(builder.dismissOnTouchOutside)
                .build());
        this.titleText = builder.titleText;
        this.dialogText = builder.dialogText;
    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout, this, false);
        progressDialogTitle = view.findViewById(R.id.progressDialogTitle);
        progressDialogText = view.findViewById(R.id.progressDialogText);
        progressDialogFileStatus = view.findViewById(R.id.progressDialogFileStatus);
        progressBar = view.findViewById(R.id.progressDialogBar);
        return view;
    }

    public void setProgress(int value) {
        progressDialogFileStatus.setVisibility(GONE);
        progressBar.setProgress(value);
    }

    @SuppressLint("SetTextI18n")
    public void setProgress(int value, long total, long transferred, String filename) {
        progressDialogFileStatus.setVisibility(VISIBLE);
        progressDialogFileStatus.setText(filename + " (" + Math.round(transferred / 1000f) + " кБ из " + Math.round(total / 1000f) + " кБ)");
        progressBar.setProgress(value);
    }

    public void close() {
        dismiss(true);
    }

    public void create() {
        progressDialogTitle.setText(titleText);
        progressDialogText.setText(dialogText);
        show(true);
    }

    public static class Builder {
        private ProgressBottomSheetDialog dialog;
        private @NonNull
        Activity activity;
        private boolean dismissOnTouchOutside = false;
        private String titleText;
        private String dialogText;

        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        public Builder setDismissTouchOutside(boolean value) {
            this.dismissOnTouchOutside = value;
            return this;
        }

        public Builder setTitleText(String value) {
            this.titleText = value;
            return this;
        }

        public Builder setDialogText(String value) {
            this.dialogText = value;
            return this;
        }


        public Builder build() {
            dialog = new ProgressBottomSheetDialog(this);
            return this;
        }


        public void show(){
            if(dialog != null)
                dialog.create();
        }

        public void close(){
            if(dialog != null)
                dialog.close();
        }
        public ProgressBottomSheetDialog getDialog(){
            return dialog;
        }
    }
}