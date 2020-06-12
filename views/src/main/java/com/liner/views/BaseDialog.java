package com.liner.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.BaseConfig;
import com.liner.utils.ViewUtils;

import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class BaseDialog extends BaseBottomSheet {
    private TextView dialogTitle;
    private FrameLayout dialogCustomView;
    private ProgressBar indeterminateProgress;
    private TextView dialogText;
    private TextView dialogCancel;
    private TextView dialogDone;
    private String dialogTitleText;
    private String dialogTextText;
    private String dialogDoneText;
    private String dialogCancelText;
    private View dialogView;
    private OnClickListener dialogCancelListener = null;
    private OnClickListener dialogDoneListener = null;
    private BaseDialogBuilder.Type dialogType;
    private String[] selectionList;
    private BaseDialogSelectionListener selectionListener;


    public BaseDialog(@NonNull Activity hostActivity, @NonNull BaseConfig config, BaseDialogBuilder builder) {
        super(hostActivity, config);
        this.dialogTitleText = builder.dialogTitleText;
        this.dialogTextText = builder.dialogTextText;
        this.dialogDoneText = builder.dialogDoneText;
        this.dialogCancelText = builder.dialogCancelText;
        this.dialogView = builder.dialogView;
        this.dialogType = builder.dialogType;
        this.selectionList = builder.selectionList;
        this.selectionListener = builder.selectionListener;
    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.base_dialog, this, false);
        dialogTitle = view.findViewById(R.id.baseDialogTitle);
        dialogCustomView = view.findViewById(R.id.baseDialogCustomView);
        dialogText = view.findViewById(R.id.baseDialogText);
        dialogCancel = view.findViewById(R.id.baseDialogCancel);
        dialogDone = view.findViewById(R.id.baseDialogDone);
        indeterminateProgress = view.findViewById(R.id.indeterminateProgress);
        return view;
    }

    public void showDialog() {
        indeterminateProgress.setIndeterminate(true);
        indeterminateProgress.setVisibility(GONE);
        switch (dialogType) {
            case ERROR:
                dialogTitle.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.error_icon), null, null, null);
                break;
            case INFO:
                dialogTitle.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.info_icon), null, null, null);
                break;
            case WARNING:
                dialogTitle.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.warning_icon), null, null, null);
                break;
            case QUESTION:
                dialogTitle.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.question_icon), null, null, null);
                break;
            case PROGRESS:
                ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
                progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.primary_dark), PorterDuff.Mode.SRC_IN);
                progressBar.setMax(100);
                progressBar.setProgress(0);
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                progressBar.setLayoutParams(layoutParams);
                dialogCustomView.addView(progressBar);
                hideActionButtons();
                break;
            case INDETERMINATE:
                indeterminateProgress.setVisibility(VISIBLE);
                hideActionButtons();
                break;
            case SINGLE_CHOOSE:
                ListView listView = new ListView(getContext());
                SelectionAdapter selectionAdapter = new SelectionAdapter(getContext(), selectionList);
                listView.setAdapter(selectionAdapter);
                dialogCustomView.addView(listView);
                hideActionButtons();
                break;
        }

        if (dialogView != null) {
            dialogCustomView.removeAllViews();
            dialogCustomView.addView(dialogView);
        }
        if (dialogCancelListener == null) {
            dialogCancel.setVisibility(GONE);
        } else {
            dialogCancel.setText(dialogCancelText);
            dialogCancel.setOnClickListener(dialogCancelListener);
        }

        dialogDone.setText(dialogDoneText);
        if (dialogDoneListener == null) {
            dialogCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss(true);
                }
            });
        } else {
            dialogDone.setOnClickListener(dialogDoneListener);
        }
        dialogText.setText(dialogTextText);
        dialogTitle.setText(dialogTitleText);
        show(true);
    }






    public void closeDialog() {
        if(dialogCustomView.getChildCount() > 0)
            dialogCustomView.removeAllViews();
        dismiss(false);
    }

    public ProgressBar getProgressBar() {
        if (dialogType == BaseDialogBuilder.Type.PROGRESS || dialogType == BaseDialogBuilder.Type.INDETERMINATE)
            return (ProgressBar) dialogCustomView.getChildAt(0);
        else return null;
    }

    public View getDialogView() {
        return dialogCustomView.getChildAt(0);
    }

    public void setDialogCancel(String text, OnClickListener dialogCancelListener) {
        this.dialogCancelText = text;
        this.dialogCancelListener = dialogCancelListener;
    }

    public void setDialogDone(String text, OnClickListener dialogDoneListener) {
        this.dialogDoneText = text;
        this.dialogDoneListener = dialogDoneListener;
    }

    public void hideActionButtons() {
        dialogCancel.setVisibility(GONE);
        dialogDone.setVisibility(GONE);
    }

    public void showActionButtons() {
        if (dialogCancelListener != null)
            dialogCancel.setVisibility(VISIBLE);
        dialogDone.setVisibility(VISIBLE);
    }

    public void setDialogTitleText(String dialogTitleText) {
        this.dialogTitleText = dialogTitleText;
    }

    public void setDialogTextText(String dialogTextText) {
        this.dialogTextText = dialogTextText;
    }

    public void setDialogDoneText(String dialogDoneText) {
        this.dialogDoneText = dialogDoneText;
    }

    public void setDialogCancelText(String dialogCancelText) {
        this.dialogCancelText = dialogCancelText;
    }

    public void setDialogType(BaseDialogBuilder.Type dialogType) {
        this.dialogType = dialogType;
    }







    public class SelectionAdapter extends ArrayAdapter<String> {
        public SelectionAdapter(Context context, String[] items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.base_dialog_choise_layout, parent, false);
            }
            convertView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(selectionListener != null)
                        selectionListener.onItemClick(position);
                    closeDialog();
                }
            });
            YSTextView chooseItem = convertView.findViewById(R.id.selectionText);
            chooseItem.setText(getItem(position));
            return convertView;
        }
    }

    public interface BaseDialogSelectionListener{
        void onItemClick(int position);
    }


}