package com.liner.i_desk.Utils.Views;

import android.content.Context;
import android.util.AttributeSet;

import com.ycuwq.datepicker.WheelPicker;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HourPickerView extends WheelPicker<Integer> {
    private int minimumHour = 0;
    private int selectedHour = minimumHour;
    private com.ycuwq.datepicker.time.HourPicker.OnHourSelectedListener mOnHourSelectedListener;

    public HourPickerView(Context context) {
        this(context, null);
    }

    public HourPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HourPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setItemMaximumWidthText("00");
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits(2);
        setDataFormat(numberFormat);
        updateHour();
        setOnWheelChangeListener(new OnWheelChangeListener<Integer>() {
            @Override
            public void onWheelSelected(Integer item, int position) {
                selectedHour = item;
                if (mOnHourSelectedListener != null) {
                    mOnHourSelectedListener.onHourSelected(item);
                }
            }
        });
    }

    private void updateHour() {
        List<Integer> list = new ArrayList<>();
        for (int i = minimumHour; i < 24; i++) {
            list.add(i);
        }
        setDataList(list);
    }

    public void setSelectedHour(int hour) {
        setSelectedHour(hour, true);
    }

    public void setSelectedHour(int hour, boolean smootScroll) {
        setCurrentPosition(hour, smootScroll);
    }

    public void setMinimumHour(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        this.minimumHour = calendar.get(Calendar.HOUR_OF_DAY);
        this.selectedHour = minimumHour;
        updateHour();
    }

    public void setMinimumHourInt(int hour){
        this.minimumHour = hour;
        this.selectedHour = minimumHour;
        updateHour();
    }

    public int getMinimumHour() {
        return minimumHour;
    }

    public int getSelectedHour() {
        return selectedHour;
    }

    public void setOnHourSelectedListener(com.ycuwq.datepicker.time.HourPicker.OnHourSelectedListener onHourSelectedListener) {
        mOnHourSelectedListener = onHourSelectedListener;
    }

    public interface OnHourSelectedListener {
        void onHourSelected(int hour);
    }
}