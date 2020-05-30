package com.liner.i_desk.Utils.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.i_desk.Adapters.FilePickerAdapter;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;

import java.io.File;

@SuppressLint("ViewConstructor")
public class FilePickerBottomSheetDialog extends BaseBottomSheet implements FilePickerAdapter.IPickerListener {
    private @NonNull
    Activity activity;
    private File file;
    private RecyclerView filePickerDialogRecyclerView;
    private TextView filePickerDialogTitle;
    private TextView filePickerDialogStepBack;
    private FilePickerAdapter filePickerAdapter;
    private TextView filePickerDialogCancel;
    private TextView filePickerDialogDone;
    private String titleText;
    private String cancelText;
    private String doneText;
    private OnClickListener cancelClickListener;
    private OnClickListener doneClickListener;

    private FilePickerBottomSheetDialog(Builder builder) {
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
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.filepicker_dialog_layout, this, false);
        filePickerDialogTitle = view.findViewById(R.id.filePickerDialogTitle);
        filePickerDialogRecyclerView = view.findViewById(R.id.filePickerDialogRecyclerView);
        filePickerDialogCancel = view.findViewById(R.id.filePickerDialogCancel);
        filePickerDialogDone = view.findViewById(R.id.filePickerDialogDone);
        filePickerDialogStepBack = view.findViewById(R.id.filePickerDialogStepBack);
        return view;
    }

    public void create() {
        filePickerAdapter = new FilePickerAdapter(getContext(), this);
        filePickerDialogRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        filePickerDialogRecyclerView.setAdapter(filePickerAdapter);
        filePickerDialogStepBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePickerAdapter.stepBack();
            }
        });
        filePickerDialogDone.setTextColor(Color.DKGRAY);
        filePickerDialogDone.setEnabled(false);
        if (cancelClickListener == null) {
            filePickerDialogCancel.setVisibility(GONE);
        } else {
            filePickerDialogCancel.setText(cancelText);
            filePickerDialogCancel.setOnClickListener(cancelClickListener);
        }
        if (doneClickListener == null) {
            this.doneClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss(true);
                }
            };
        }
        filePickerDialogTitle.setText(titleText);
        filePickerDialogDone.setText(doneText);
        filePickerDialogDone.setOnClickListener(doneClickListener);
        show(true);

    }

    public void close() {
        dismiss(true);
    }

    @Override
    public void onCantStepBack() {
        filePickerDialogStepBack.setVisibility(GONE);
    }

    @Override
    public void onCanStepBack() {
        filePickerDialogStepBack.setVisibility(VISIBLE);
    }

    @Override
    public void onFileSelected(File file) {
        filePickerDialogDone.setTextColor(ColorUtils.getThemeColor(activity, R.attr.colorPrimaryDark));
        filePickerDialogDone.setEnabled(true);
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public static class Builder {
        private FilePickerBottomSheetDialog dialog;
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
            dialog = new FilePickerBottomSheetDialog(this);
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
        public FilePickerBottomSheetDialog getDialog(){
            return dialog;
        }
    }
}