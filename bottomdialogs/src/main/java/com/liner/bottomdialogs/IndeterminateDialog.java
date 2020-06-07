package com.liner.bottomdialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.Config;

@SuppressLint("ViewConstructor")
public class IndeterminateDialog extends BaseBottomSheet {
    private TextView indeterminateDialogTitle;
    private TextView indeterminateDialogText;
    private String titleText;
    private String dialogText;


    private IndeterminateDialog(Builder builder) {
        super(builder.activity, new Config.Builder(builder.activity)
                .sheetBackgroundColor(builder.activity.getResources().getColor(R.color.color_background))
                .dismissOnTouchOutside(builder.dismissOnTouchOutside)
                .build());
        this.titleText = builder.titleText;
        this.dialogText = builder.dialogText;
    }


    public static class Builder{
        private IndeterminateDialog dialog;
        private @NonNull Activity activity;
        private boolean dismissOnTouchOutside = false;
        private String titleText;
        private String dialogText;

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

        public Builder build(){
            dialog = new IndeterminateDialog(this);
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

        public IndeterminateDialog getDialog(){
            return dialog;
        }
    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_indeterminate_dialog_layout, this, false);
        indeterminateDialogTitle = view.findViewById(R.id.indeterminateDialogTitle);
        indeterminateDialogText = view.findViewById(R.id.indeterminateDialogText);
        return view;
    }



    public void close(){
        dismiss(true);
    }


    public void create(){
        indeterminateDialogTitle.setText(titleText);
        indeterminateDialogText.setText(dialogText);
        show(true);
    }
}