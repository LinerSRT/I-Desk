package com.liner.i_desk.Utils.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;

@SuppressLint("ViewConstructor")
public class EditBottomSheetDialog extends BaseBottomSheet {
    private @NonNull
    Activity activity;
    private TextView editDialogTitle;
    private EditRegexTextView editDialogView;
    private TextView editDialogCancel;
    private TextView editDialogDone;
    private String titleText;
    private String cancelText;
    private String doneText;
    private OnClickListener cancelClickListener;
    private OnClickListener doneClickListener;


    private EditBottomSheetDialog(Builder builder) {
        super(builder.activity, new Config.Builder(builder.activity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(builder.activity, R.attr.backgroundColor))
                .dismissOnTouchOutside(builder.dismissOnTouchOutside)
                .build());
        this.activity = builder.activity;
        this.titleText = builder.titleText;
        this.cancelText = builder.cancelText;
        this.doneText = builder.doneText;
        this.cancelClickListener = builder.cancelClickListener;
        this.doneClickListener = builder.doneClickListener;
    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.edit_dialog_layout, this, false);
        editDialogTitle = view.findViewById(R.id.editDialogTitle);
        editDialogView = view.findViewById(R.id.editDialogView);
        editDialogCancel = view.findViewById(R.id.editDialogCancel);
        editDialogDone = view.findViewById(R.id.editDialogDone);
        editDialogDone.setTextColor(Color.DKGRAY);
        editDialogDone.setEnabled(false);
        return view;
    }

    public String getDialogText() {
        return editDialogView.getText().toString().trim();
    }

    public void create() {
        editDialogView.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                editDialogDone.setTextColor(ColorUtils.getThemeColor(activity, R.attr.colorPrimaryDark));
                editDialogDone.setEnabled(true);
            }

            @Override
            public void onNotValid() {
                editDialogDone.setTextColor(Color.DKGRAY);
                editDialogDone.setEnabled(false);

            }
        });

        if (cancelClickListener == null) {
            editDialogCancel.setVisibility(GONE);
        } else {
            editDialogCancel.setText(cancelText);
            editDialogCancel.setOnClickListener(cancelClickListener);
        }
        if (doneClickListener == null) {
            this.doneClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss(true);
                }
            };
        }
        editDialogTitle.setText(titleText);
        editDialogDone.setText(doneText);
        editDialogDone.setOnClickListener(doneClickListener);
        show(true);

    }

    public void close() {
        dismiss(true);
    }

    public static class Builder {
        private EditBottomSheetDialog dialog;
        private @NonNull
        Activity activity;
        private boolean dismissOnTouchOutside = false;
        private String titleText;
        private String cancelText;
        private String doneText;
        private OnClickListener cancelClickListener;
        private OnClickListener doneClickListener;

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

        public Builder setDone(String text, View.OnClickListener clickListener) {
            this.doneText = text;
            this.doneClickListener = clickListener;
            return this;
        }

        public Builder setCancel(String text, View.OnClickListener clickListener) {
            this.cancelText = text;
            this.cancelClickListener = clickListener;
            return this;
        }

        public Builder build() {
            dialog = new EditBottomSheetDialog(this);
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

        public EditBottomSheetDialog getDialog(){
            return dialog;
        }
    }

}