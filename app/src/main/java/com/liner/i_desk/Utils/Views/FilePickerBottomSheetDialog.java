package com.liner.i_desk.Utils.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.i_desk.Adapters.FilePickerAdapter;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.Animations.ViewAnimator;
import com.liner.i_desk.Utils.ColorUtils;

import java.io.File;

@SuppressLint("ViewConstructor")
public class FilePickerBottomSheetDialog extends BaseBottomSheet implements FilePickerAdapter.IPickerListener {
    private File file;
    private LinearLayout filePickerDialogButtonLayout;
    private TextView filePickerDialogTitle;
    private TextView filePickerDialogStepBack;
    private RecyclerView filePickerDialogRecyclerView;
    private FilePickerAdapter filePickerAdapter;
    private TextView filePickerDialogCancel;
    private TextView filePickerDialogDone;
    private OnClickListener cancelClickListener;
    private OnClickListener doneClickListener;

    public FilePickerBottomSheetDialog(@NonNull Activity hostActivity) {
        super(hostActivity, new Config.Builder(hostActivity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(hostActivity, R.attr.backgroundColor))
                .dismissOnTouchOutside(false)
                .build());

    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.filepicker_dialog_layout, this, false);
        filePickerDialogButtonLayout = view.findViewById(R.id.filePickerDialogButtonLayout);
        filePickerDialogTitle = view.findViewById(R.id.filePickerDialogTitle);
        filePickerDialogRecyclerView = view.findViewById(R.id.filePickerDialogRecyclerView);
        filePickerDialogCancel = view.findViewById(R.id.filePickerDialogCancel);
        filePickerDialogDone = view.findViewById(R.id.filePickerDialogDone);
        filePickerDialogStepBack = view.findViewById(R.id.filePickerDialogStepBack);
        filePickerAdapter = new FilePickerAdapter(getContext(), this);
        filePickerDialogRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        filePickerDialogRecyclerView.setAdapter(filePickerAdapter);
        filePickerDialogStepBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {
                        filePickerAdapter.stepBack();
                    }
                });
            }
        });
        return view;
    }


    public void setDialogTitle(String dialogTitle) {
        filePickerDialogTitle.setText(dialogTitle);
    }


    public void setDialogDoneBtnText(String dialogDoneBtnText) {
        filePickerDialogDone.setText(dialogDoneBtnText);
    }

    public void setDialogCancelBtnText(String dialogCancelBtnText) {
        filePickerDialogCancel.setText(dialogCancelBtnText);
    }

    public void setCancelClickListener(OnClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
        filePickerDialogCancel.setOnClickListener(cancelClickListener);
    }

    public void setDoneClickListener(OnClickListener doneClickListener) {
        this.doneClickListener = doneClickListener;
        filePickerDialogDone.setOnClickListener(doneClickListener);
    }


    public void create() {

        if (cancelClickListener == null) {
            filePickerDialogCancel.setVisibility(GONE);
        }
        if (doneClickListener == null) {
            setDoneClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                        @Override
                        public void done() {
                            dismiss(true);
                        }
                    });
                }
            });
        }
        show(true);

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
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}