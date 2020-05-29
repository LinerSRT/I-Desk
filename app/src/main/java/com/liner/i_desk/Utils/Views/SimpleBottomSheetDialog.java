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
import com.arthurivanets.bottomsheets.config.BaseConfig;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.Animations.ViewAnimator;
import com.liner.i_desk.Utils.ColorUtils;

@SuppressLint("ViewConstructor")
public class SimpleBottomSheetDialog extends BaseBottomSheet {
    private LinearLayout simpleDialogButtonLayout;
    private TextView simpleDialogTitle;
    private TextView simpleDialogText;
    private TextView simpleDialogCancel;
    private TextView simpleDialogDone;
    private View.OnClickListener cancelClickListener;
    private View.OnClickListener doneClickListener;

    public SimpleBottomSheetDialog(@NonNull Activity hostActivity) {
        super(hostActivity, new Config.Builder(hostActivity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(hostActivity, R.attr.backgroundColor))
                .dismissOnTouchOutside(false)
                .build());

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


    public void setDialogTitle(String dialogTitle) {
        simpleDialogTitle.setText(dialogTitle);
    }

    public void setDialogText(String dialogText) {
        simpleDialogText.setText(dialogText);
    }

    public void setDialogDoneBtnText(String dialogDoneBtnText) {
        simpleDialogDone.setText(dialogDoneBtnText);
    }

    public void setDialogCancelBtnText(String dialogCancelBtnText) {
        simpleDialogCancel.setText(dialogCancelBtnText);
    }

    public void setCancelClickListener(OnClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
        simpleDialogCancel.setOnClickListener(cancelClickListener);
    }

    public void setDoneClickListener(OnClickListener doneClickListener) {
        this.doneClickListener = doneClickListener;
        simpleDialogDone.setOnClickListener(doneClickListener);
    }

    public void create(){
        if(cancelClickListener == null){
            simpleDialogCancel.setVisibility(GONE);
        }
        if(doneClickListener == null){
            setDoneClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    new ViewAnimator(simpleDialogDone).animateAction(200, new ViewAnimator.AnimatorListener() {
                        @Override
                        public void done() {
                            dismiss(true);
                        }
                    });
                }
            });
        }
        if(doneClickListener == null && cancelClickListener == null)
            simpleDialogButtonLayout.setVisibility(GONE);

        show(true);

    }
}