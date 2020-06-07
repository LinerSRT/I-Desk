package com.liner.bottomdialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.bottomdialogs.Adapters.ImagePickerAdapter;
import com.liner.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint({"ViewConstructor"})
public class ImagePickerDialog extends BaseBottomSheet {
    private Activity activity;
    private TextView imagePickerDialogTitle;
    private RecyclerView imagePickerRecyclerView;
    private TextView imagePickerDialogCancel;
    private TextView imagePickerDialogDone;
    private String titleText;
    private String cancelText;
    private String doneText;
    private OnClickListener cancelClickListener;
    private OnClickListener doneClickListener;
    private ImagePickerListener imagePickerListener;
    private String imagePath;


    private ImagePickerDialog(Builder builder) {
        super(builder.activity, new Config.Builder(builder.activity)
                .sheetBackgroundColor(builder.activity.getResources().getColor(R.color.color_background))
                .dismissOnTouchOutside(builder.dismissOnTouchOutside)
                .build());
        this.activity = builder.activity;
        this.titleText = builder.titleText;
        this.cancelText = builder.cancelText;
        this.doneText = builder.doneText;
        this.imagePickerListener = builder.imagePickerListener;
        this.cancelClickListener = builder.cancelClickListener;
        this.doneClickListener = builder.doneClickListener;
    }


    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.imagepicker_dialog_layout, this, false);
        imagePickerDialogTitle = view.findViewById(R.id.imagePickerDialogTitle);
        imagePickerRecyclerView = view.findViewById(R.id.imagePickerRecycler);
        imagePickerDialogCancel = view.findViewById(R.id.imagePickerDialogCancel);
        imagePickerDialogDone = view.findViewById(R.id.imagePickerDialogDone);
        return view;
    }

    public void create() {
        List<ImagePickerAdapter.ItemHolder> holderList = new ArrayList<>();
        for (String path : ImageUtils.getAllImagesFromDevice(activity))
            holderList.add(new ImagePickerAdapter.ItemHolder(path, false));
        ImagePickerAdapter imagePickerAdapter = new ImagePickerAdapter(holderList, new ImagePickerAdapter.OnItemSelected() {
            @Override
            public void onSelected(ImagePickerAdapter.ItemHolder itemHolder) {
                imagePath = itemHolder.getImagePath();
                if (imagePickerListener != null)
                    imagePickerListener.onPicked(itemHolder.getImagePath());
            }
        });
        imagePickerRecyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        Objects.requireNonNull(imagePickerRecyclerView.getItemAnimator()).setChangeDuration(0);
        imagePickerRecyclerView.setAdapter(imagePickerAdapter);
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(imagePickerRecyclerView);
        imagePickerDialogTitle.setText(titleText);
        if (cancelClickListener == null) {
            imagePickerDialogCancel.setVisibility(GONE);
        } else {
            imagePickerDialogCancel.setText(cancelText);
            imagePickerDialogCancel.setOnClickListener(cancelClickListener);
        }
        if (doneClickListener == null) {
            this.doneClickListener = new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss(true);
                }
            };
        }
        imagePickerDialogDone.setText(doneText);
        imagePickerDialogDone.setOnClickListener(doneClickListener);
        show(true);

    }

    public void close() {
        dismiss(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus)
            if (imagePickerListener != null)
                imagePickerListener.onDismissed();
    }

    public String getImagePath() {
        return imagePath;
    }

    public interface ImagePickerListener {
        void onPicked(String filepath);

        void onDismissed();
    }

    public static class Builder {
        private @NonNull
        Activity activity;
        private ImagePickerDialog dialog;
        private boolean dismissOnTouchOutside = false;
        private String titleText;
        private String cancelText;
        private String doneText;
        private OnClickListener cancelClickListener;
        private OnClickListener doneClickListener;
        private ImagePickerListener imagePickerListener;

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

        public Builder setDone(String text, OnClickListener clickListener) {
            this.doneText = text;
            this.doneClickListener = clickListener;
            return this;
        }

        public Builder setCancel(String text, OnClickListener clickListener) {
            this.cancelText = text;
            this.cancelClickListener = clickListener;
            return this;
        }

        public Builder setImagePickerListener(ImagePickerListener listener) {
            this.imagePickerListener = listener;
            return this;
        }

        public Builder build() {
            this.dialog = new ImagePickerDialog(this);
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

        public ImagePickerDialog getDialog() {
            return dialog;
        }
    }
}