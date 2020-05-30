package com.liner.i_desk.Utils.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.Animations.ViewAnimator;
import com.liner.i_desk.Utils.ColorUtils;

@SuppressLint("ViewConstructor")
public class EditBottomSheetDialog extends BaseBottomSheet {
    private Context context;
    private LinearLayout editDialogButtonLayout;
    private TextView editDialogTitle;
    private EditRegexTextView editDialogView;
    private TextView editDialogCancel;
    private TextView editDialogDone;
    private OnClickListener cancelClickListener;
    private OnClickListener doneClickListener;

    public EditBottomSheetDialog(@NonNull Activity hostActivity) {
        super(hostActivity, new Config.Builder(hostActivity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(hostActivity, R.attr.backgroundColor))
                .dismissOnTouchOutside(false)
                .build());
        this.context = hostActivity;
    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.edit_dialog_layout, this, false);
        editDialogButtonLayout = view.findViewById(R.id.editDialogButtonLayout);
        editDialogTitle = view.findViewById(R.id.editDialogTitle);
        editDialogView = view.findViewById(R.id.editDialogView);
        editDialogCancel = view.findViewById(R.id.editDialogCancel);
        editDialogDone = view.findViewById(R.id.editDialogDone);
        editDialogDone.setTextColor(Color.DKGRAY);
        editDialogDone.setEnabled(false);
        return view;
    }


    public void setDialogTitle(String dialogTitle) {
        editDialogTitle.setText(dialogTitle);
    }

    public void setDialogDoneBtnText(String dialogDoneBtnText) {
        editDialogDone.setText(dialogDoneBtnText);
    }

    public void setDialogCancelBtnText(String dialogCancelBtnText) {
        editDialogCancel.setText(dialogCancelBtnText);
    }

    public void setCancelClickListener(OnClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
        editDialogCancel.setOnClickListener(cancelClickListener);
    }

    public void setDoneClickListener(OnClickListener doneClickListener) {
        this.doneClickListener = doneClickListener;
        editDialogDone.setOnClickListener(doneClickListener);
    }

    public String getDialogText(){
        return editDialogView.getText().toString().trim();
    }


    public void create(){
        editDialogView.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                editDialogDone.setTextColor(ColorUtils.getThemeColor(context, R.attr.colorPrimaryDark));
                editDialogDone.setEnabled(true);
            }

            @Override
            public void onNotValid() {
                editDialogDone.setTextColor(Color.DKGRAY);
                editDialogDone.setEnabled(false);

            }
        });
        if(cancelClickListener == null){
            editDialogCancel.setVisibility(GONE);
        }
        if(doneClickListener == null){
            setDoneClickListener(new OnClickListener(){
                @Override
                public void onClick(View view) {
                    new ViewAnimator(editDialogDone).animateAction(200, new ViewAnimator.AnimatorListener() {
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
}