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
import com.liner.i_desk.Utils.Animations.ViewAnimator;
import com.liner.i_desk.Utils.ColorUtils;
import com.liner.i_desk.Utils.TimeUtils;
import com.ycuwq.datepicker.date.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@SuppressLint("ViewConstructor")
public class DatePickerBottomSheetDialog extends BaseBottomSheet {
    private LinearLayout datePickerDialogButtonLayout;
    private TextView datePickerDialogTitle;
    private DatePicker datePickerDialogView;
    private TextView datePickerDialogCancel;
    private TextView datePickerDialogDone;
    private OnClickListener cancelClickListener;
    private OnClickListener doneClickListener;

    public DatePickerBottomSheetDialog(@NonNull Activity hostActivity) {
        super(hostActivity, new Config.Builder(hostActivity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(hostActivity, R.attr.backgroundColor))
                .dismissOnTouchOutside(false)
                .build());

    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.datepicker_dialog_layout, this, false);
        datePickerDialogButtonLayout = view.findViewById(R.id.datePickerDialogButtonLayout);
        datePickerDialogTitle = view.findViewById(R.id.datePickerDialogTitle);
        datePickerDialogView = view.findViewById(R.id.datePickerDialogView);
        datePickerDialogCancel = view.findViewById(R.id.datePickerDialogCancel);
        datePickerDialogDone = view.findViewById(R.id.datePickerDialogDone);

        return view;
    }


    public void setDialogTitle(String dialogTitle) {
        datePickerDialogTitle.setText(dialogTitle);
    }


    public void setDialogDoneBtnText(String dialogDoneBtnText) {
        datePickerDialogDone.setText(dialogDoneBtnText);
    }

    public void setDialogCancelBtnText(String dialogCancelBtnText) {
        datePickerDialogCancel.setText(dialogCancelBtnText);
    }

    public void setCancelClickListener(OnClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
        datePickerDialogCancel.setOnClickListener(cancelClickListener);
    }

    public void setDoneClickListener(OnClickListener doneClickListener) {
        this.doneClickListener = doneClickListener;
        datePickerDialogDone.setOnClickListener(doneClickListener);
    }

    public String getPickedDate() {
        return datePickerDialogView.getDate(new SimpleDateFormat(TimeUtils.SERVER_DATE_FORMAT, Locale.getDefault()));
    }


    public void create() {
        datePickerDialogView.setMaxDate(TimeUtils.getDate(TimeUtils.getTime(TimeUtils.Type.SERVER, 365, TimeUnit.DAYS)).getTime());
        datePickerDialogView.setMinDate(TimeUtils.getDate(TimeUtils.getTime(TimeUtils.Type.SERVER, -365, TimeUnit.DAYS)).getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePickerDialogView.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
        if (cancelClickListener == null) {
            datePickerDialogCancel.setVisibility(GONE);
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
        if (doneClickListener == null && cancelClickListener == null)
            datePickerDialogButtonLayout.setVisibility(GONE);

        show(true);

    }
}