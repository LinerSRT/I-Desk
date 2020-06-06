package com.liner.i_desk.Utils.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;
import com.liner.i_desk.Utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SuppressLint("ViewConstructor")
public class DatePickerBottomSheetDialog extends BaseBottomSheet {
    private TextView datePickerDialogTitle;
    private TextView datePickerDialogCancel;
    private ExtendedTextButton datePickerDialogDone;
    private String titleText;
    private String cancelText;
    private String doneText;
    private OnClickListener cancelClickListener;
    private OnClickListener doneClickListener;
    private Date currentTime;



    private DatePickerBottomSheetDialog(Builder builder) {
        super(builder.activity, new Config.Builder(builder.activity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(builder.activity, R.attr.backgroundColor))
                .dismissOnTouchOutside(builder.dismissOnTouchOutside)
                .build());
        this.titleText = builder.titleText;
        this.cancelText = builder.cancelText;
        this.doneText = builder.doneText;
        this.currentTime = builder.currentTime;
        this.cancelClickListener = builder.cancelClickListener;
        this.doneClickListener = builder.doneClickListener;
    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.datepicker_dialog_layout, this, false);
        datePickerDialogTitle = view.findViewById(R.id.datePickerDialogTitle);
        datePickerDialogCancel = view.findViewById(R.id.datePickerDialogCancel);
        datePickerDialogDone = view.findViewById(R.id.datePickerDialogDone);
        return view;
    }

    public String getPickedDate() {
        Calendar calendar = Calendar.getInstance();
        return TimeUtils.getTime(calendar, TimeUtils.Type.LOCAL);
    }

    public void close() {
        dismiss(true);
    }

    public void create() {
        if (cancelClickListener == null) {
            datePickerDialogCancel.setVisibility(GONE);
        } else {
            datePickerDialogCancel.setText(cancelText);
            datePickerDialogCancel.setOnClickListener(cancelClickListener);
        }
        if (doneClickListener == null) {
            this.doneClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss(true);
                }
            };
        }
        datePickerDialogTitle.setText(titleText);
        datePickerDialogDone.setText(doneText);
        datePickerDialogDone.setOnClickListener(doneClickListener);
        show(true);
    }

    public static class Builder {
        private DatePickerBottomSheetDialog dialog;
        private @NonNull
        Activity activity;
        private boolean dismissOnTouchOutside = false;
        private boolean showHourPicker = false;
        private String titleText;
        private String cancelText;
        private String doneText;
        private OnClickListener cancelClickListener;
        private OnClickListener doneClickListener;
        private Date currentTime;


        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        public Builder setDismissTouchOutside(boolean value) {
            this.dismissOnTouchOutside = value;
            return this;
        }

        public Builder setShowHourPicker(boolean value) {
            this.showHourPicker = value;
            return this;
        }

        public Builder setTitleText(String value) {
            this.titleText = value;
            return this;
        }
        public Builder setCurrentTime(Date value) {
            this.currentTime = value;
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
            dialog = new DatePickerBottomSheetDialog(this);
            return this;
        }

        public void show() {
            if (dialog != null)
                dialog.create();
        }

        public void close() {
            if (dialog != null)
                dialog.close();
        }

        public DatePickerBottomSheetDialog getDialog() {
            return dialog;
        }
    }
}