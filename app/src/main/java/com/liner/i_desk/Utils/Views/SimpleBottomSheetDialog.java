package com.liner.i_desk.Utils.Views;

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
public class SimpleBottomSheetDialog extends BaseBottomSheet {
    private LinearLayout simpleDialogButtonLayout;
    private TextView simpleDialogTitle;
    private TextView simpleDialogText;
    private TextView simpleDialogCancel;
    private TextView simpleDialogDone;
    private String titleText;
    private String dialogText;
    private String cancelText;
    private String doneText;
    private OnClickListener cancelClickListener;
    private OnClickListener doneClickListener;


    public static class Builder{
        private @NonNull Activity activity;
        private SimpleBottomSheetDialog dialog;
        private boolean dismissOnTouchOutside = false;
        private String titleText;
        private String dialogText;
        private String cancelText;
        private String doneText;
        private OnClickListener cancelClickListener;
        private OnClickListener doneClickListener;

        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        public Builder setDismissTouchOutside(boolean value){
            this.dismissOnTouchOutside = value;
            return this;
        }
        public Builder setTitleText(String value){
            this.titleText = value;
            return this;
        }
        public Builder setDialogText(String value){
            this.dialogText = value;
            return this;
        }
        public Builder setDone(String text, View.OnClickListener clickListener){
            this.doneText = text;
            this.doneClickListener = clickListener;
            return this;
        }
        public Builder setCancel(String text, View.OnClickListener clickListener){
            this.cancelText = text;
            this.cancelClickListener = clickListener;
            return this;
        }

        public Builder build(){
            this.dialog = new SimpleBottomSheetDialog(this);
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
    }
    private SimpleBottomSheetDialog(Builder builder) {
        super(builder.activity, new Config.Builder(builder.activity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(builder.activity, R.attr.backgroundColor))
                .dismissOnTouchOutside(builder.dismissOnTouchOutside)
                .build());
        this.titleText = builder.titleText;
        this.dialogText = builder.dialogText;
        this.cancelText = builder.cancelText;
        this.doneText = builder.doneText;
        this.cancelClickListener = builder.cancelClickListener;
        this.doneClickListener = builder.doneClickListener;
    }


    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_dialog_layout, this, false);
        simpleDialogButtonLayout = view.findViewById(R.id.simpleDialogButtonLayout);
        simpleDialogTitle = view.findViewById(R.id.simpleDialogTitle);
        simpleDialogText = view.findViewById(R.id.simpleDialogText);
        simpleDialogCancel = view.findViewById(R.id.simpleDialogCancel);
        simpleDialogDone = view.findViewById(R.id.simpleDialogDone);
        return view;
    }

    public void create(){
        simpleDialogTitle.setText(titleText);
        simpleDialogText.setText(dialogText);
        if(cancelClickListener == null){
            simpleDialogCancel.setVisibility(GONE);
        } else {
            simpleDialogCancel.setText(cancelText);
            simpleDialogCancel.setOnClickListener(cancelClickListener);
        }
        if(doneClickListener == null){
            this.doneClickListener = new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    dismiss(true);
                }
            };
        }
        simpleDialogDone.setText(doneText);
        simpleDialogDone.setOnClickListener(doneClickListener);
        show(true);

    }

    public void close(){
        dismiss(true);
    }

}