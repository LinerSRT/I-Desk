package com.liner.i_desk.Utils.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.liner.i_desk.R;
import com.ycuwq.datepicker.date.DayPicker;
import com.ycuwq.datepicker.date.MonthPicker;
import com.ycuwq.datepicker.date.YearPicker;
import com.ycuwq.datepicker.time.HourPicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateHourPicker extends LinearLayout implements YearPicker.OnYearSelectedListener,
        MonthPicker.OnMonthSelectedListener, DayPicker.OnDaySelectedListener, HourPicker.OnHourSelectedListener {

    private YearPicker yearPicker;
    private MonthPicker monthPicker;
    private DayPicker dayPicker;
    private HourPickerView hourPicker;
    private Long mMaxDate;
    private Long mMinDate;


    private onDateTimeSelectListener onDateSelected;

    public DateHourPicker(Context context) {
        this(context, null);
    }

    public DateHourPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateHourPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.date_hour_layout_picker, this);
        initChild();
        initAttrs(context, attrs);
        yearPicker.setBackgroundDrawable(getBackground());
        monthPicker.setBackgroundDrawable(getBackground());
        dayPicker.setBackgroundDrawable(getBackground());
        hourPicker.setBackgroundDrawable(getBackground());
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DatePicker);
        int textSize = a.getDimensionPixelSize(R.styleable.DatePicker_itemTextSize,
                getResources().getDimensionPixelSize(R.dimen.WheelItemTextSize));
        int textColor = a.getColor(R.styleable.DatePicker_itemTextColor,
                Color.BLACK);
        boolean isTextGradual = a.getBoolean(R.styleable.DatePicker_textGradual, true);
        boolean isCyclic = a.getBoolean(R.styleable.DatePicker_wheelCyclic, false);
        int halfVisibleItemCount = a.getInteger(R.styleable.DatePicker_halfVisibleItemCount, 2);
        int selectedItemTextColor = a.getColor(R.styleable.DatePicker_selectedTextColor,
                getResources().getColor(R.color.com_ycuwq_datepicker_selectedTextColor));
        int selectedItemTextSize = a.getDimensionPixelSize(R.styleable.DatePicker_selectedTextSize,
                getResources().getDimensionPixelSize(R.dimen.WheelSelectedItemTextSize));
        int itemWidthSpace = a.getDimensionPixelSize(R.styleable.DatePicker_itemWidthSpace,
                getResources().getDimensionPixelOffset(R.dimen.WheelItemWidthSpace));
        int itemHeightSpace = a.getDimensionPixelSize(R.styleable.DatePicker_itemHeightSpace,
                getResources().getDimensionPixelOffset(R.dimen.WheelItemHeightSpace));
        boolean isZoomInSelectedItem = a.getBoolean(R.styleable.DatePicker_zoomInSelectedItem, true);
        boolean isShowCurtain = a.getBoolean(R.styleable.DatePicker_wheelCurtain, true);
        int curtainColor = a.getColor(R.styleable.DatePicker_wheelCurtainColor, Color.WHITE);
        boolean isShowCurtainBorder = a.getBoolean(R.styleable.DatePicker_wheelCurtainBorder, true);
        int curtainBorderColor = a.getColor(R.styleable.DatePicker_wheelCurtainBorderColor,
                getResources().getColor(R.color.com_ycuwq_datepicker_divider));
        a.recycle();

        setTextSize(textSize);
        setTextColor(textColor);
        setTextGradual(isTextGradual);
        setCyclic(isCyclic);
        setHalfVisibleItemCount(halfVisibleItemCount);
        setSelectedItemTextColor(selectedItemTextColor);
        setSelectedItemTextSize(selectedItemTextSize);
        setItemWidthSpace(itemWidthSpace);
        setItemHeightSpace(itemHeightSpace);
        setZoomInSelectedItem(isZoomInSelectedItem);
        setShowCurtain(isShowCurtain);
        setCurtainColor(curtainColor);
        setShowCurtainBorder(isShowCurtainBorder);
        setCurtainBorderColor(curtainBorderColor);
    }
    private void initChild() {
        yearPicker = findViewById(R.id.dateYearPicker_layout);
        yearPicker.setOnYearSelectedListener(this);
        monthPicker = findViewById(R.id.dateMonthPicker_layout);
        monthPicker.setOnMonthSelectedListener(this);
        dayPicker = findViewById(R.id.dateDayPicker_layout);
        dayPicker.setOnDaySelectedListener(this);
        hourPicker = findViewById(R.id.dateHourPicker_layout);
        hourPicker.setOnHourSelectedListener(this);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        if (yearPicker != null && monthPicker != null && dayPicker != null && hourPicker != null) {
            yearPicker.setBackgroundColor(color);
            monthPicker.setBackgroundColor(color);
            dayPicker.setBackgroundColor(color);
            hourPicker.setBackgroundColor(color);
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        if (yearPicker != null && monthPicker != null && dayPicker != null && hourPicker != null) {
            yearPicker.setBackgroundResource(resid);
            monthPicker.setBackgroundResource(resid);
            dayPicker.setBackgroundResource(resid);
            hourPicker.setBackgroundResource(resid);
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        if (yearPicker != null && monthPicker != null && dayPicker != null && hourPicker != null) {
            yearPicker.setBackgroundDrawable(background);
            monthPicker.setBackgroundDrawable(background);
            dayPicker.setBackgroundDrawable(background);
            hourPicker.setBackgroundDrawable(background);
        }
    }

    private void onDateSelected() {
        if (onDateSelected != null) {
            onDateSelected.onSelected(getYear(), getMonth(), getDay(), getHour(), getDateLong());
        }
    }


    @Override
    public void onMonthSelected(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mMinDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(getDateLong());
        if(calendar.compareTo(calendar2) == 1){
            hourPicker.setMinimumHour(mMinDate);
        } else {
            hourPicker.setMinimumHourInt(0);
        }
        dayPicker.setMonth(getYear(), month);
        onDateSelected();
    }

    @Override
    public void onDaySelected(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mMinDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(getDateLong());
        if(calendar.compareTo(calendar2) == 1){
            hourPicker.setMinimumHour(mMaxDate);
        } else {
            hourPicker.setMinimumHourInt(0);
        }
        onDateSelected();
    }


    @Override
    public void onYearSelected(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mMinDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(getDateLong());
        if(calendar.compareTo(calendar2) == 1){
            hourPicker.setMinimumHour(mMaxDate);
        } else {
            hourPicker.setMinimumHourInt(0);
        }
        monthPicker.setYear(year);
        int month = getMonth();
        dayPicker.setMonth(year, month);
        onDateSelected();
    }



    @Override
    public void onHourSelected(int hour) {
        onDateSelected();
    }


    public void setDate(int year, int month, int day, int hour) {
        setDate(year, month, day, hour,true);
    }


    public void setDate(int year, int month, int day, int hour, boolean smoothScroll) {
        yearPicker.setSelectedYear(year, smoothScroll);
        monthPicker.setSelectedMonth(month, smoothScroll);
        dayPicker.setSelectedDay(day, smoothScroll);
        hourPicker.setSelectedHour(hour, smoothScroll);
    }

    public void setMaxDate(long date) {
        setCyclic(false);
        mMaxDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        yearPicker.setEndYear(calendar.get(Calendar.YEAR));
        monthPicker.setMaxDate(date);
        dayPicker.setMaxDate(date);
        monthPicker.setYear(yearPicker.getSelectedYear());
        dayPicker.setMonth(yearPicker.getSelectedYear(), monthPicker.getSelectedMonth());
    }

    public void setMinDate(long date) {
        setCyclic(false);
        mMinDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        yearPicker.setStartYear(calendar.get(Calendar.YEAR));
        monthPicker.setMinDate(date);
        dayPicker.setMinDate(date);
        hourPicker.setMinimumHour(date);
        monthPicker.setYear(yearPicker.getSelectedYear());
        dayPicker.setMonth(yearPicker.getSelectedYear(), monthPicker.getSelectedMonth());
    }

    public String getDate() {
        DateFormat format = SimpleDateFormat.getDateInstance();
        return getDate(format);
    }

    public String getDate(DateFormat dateFormat) {
        int year, month, day, hour;
        year = getYear();
        month = getMonth();
        day = getDay();
        hour = getHour();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, 0);
        return dateFormat.format(calendar.getTime());
    }

    public long getDateLong(){
        int year, month, day, hour;
        year = getYear();
        month = getMonth();
        day = getDay();
        hour = getHour();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, 0);
        return calendar.getTime().getTime();
    }

    public long getDateLong(int year, int month, int day, int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, 0);
        return calendar.getTime().getTime();
    }

    public int getYear() {
        return yearPicker.getSelectedYear();
    }

    public int getMonth() {
        return monthPicker.getSelectedMonth();
    }


    public int getDay() {
        return dayPicker.getSelectedDay();
    }

    public int getHour(){
        return hourPicker.getSelectedHour();
    }

    public void setTextColor(int textColor) {
        dayPicker.setTextColor(textColor);
        monthPicker.setTextColor(textColor);
        yearPicker.setTextColor(textColor);
        hourPicker.setTextColor(textColor);
    }

    public void setTextSize(int textSize) {
        dayPicker.setTextSize(textSize);
        monthPicker.setTextSize(textSize);
        yearPicker.setTextSize(textSize);
        hourPicker.setTextSize(textSize);
    }


    public void setSelectedItemTextColor(int selectedItemTextColor) {
        dayPicker.setSelectedItemTextColor(selectedItemTextColor);
        monthPicker.setSelectedItemTextColor(selectedItemTextColor);
        yearPicker.setSelectedItemTextColor(selectedItemTextColor);
        hourPicker.setSelectedItemTextColor(selectedItemTextColor);
    }


    public void setSelectedItemTextSize(int selectedItemTextSize) {
        dayPicker.setSelectedItemTextSize(selectedItemTextSize);
        monthPicker.setSelectedItemTextSize(selectedItemTextSize);
        yearPicker.setSelectedItemTextSize(selectedItemTextSize);
        hourPicker.setSelectedItemTextSize(selectedItemTextSize);
    }



    public void setHalfVisibleItemCount(int halfVisibleItemCount) {
        dayPicker.setHalfVisibleItemCount(halfVisibleItemCount);
        monthPicker.setHalfVisibleItemCount(halfVisibleItemCount);
        yearPicker.setHalfVisibleItemCount(halfVisibleItemCount);
        hourPicker.setHalfVisibleItemCount(halfVisibleItemCount);
    }

    public void setItemWidthSpace(int itemWidthSpace) {
        dayPicker.setItemWidthSpace(itemWidthSpace);
        monthPicker.setItemWidthSpace(itemWidthSpace);
        yearPicker.setItemWidthSpace(itemWidthSpace);
        hourPicker.setItemWidthSpace(itemWidthSpace);
    }


    public void setItemHeightSpace(int itemHeightSpace) {
        dayPicker.setItemHeightSpace(itemHeightSpace);
        monthPicker.setItemHeightSpace(itemHeightSpace);
        yearPicker.setItemHeightSpace(itemHeightSpace);
        hourPicker.setItemHeightSpace(itemHeightSpace);
    }


    public void setZoomInSelectedItem(boolean zoomInSelectedItem) {
        dayPicker.setZoomInSelectedItem(zoomInSelectedItem);
        monthPicker.setZoomInSelectedItem(zoomInSelectedItem);
        yearPicker.setZoomInSelectedItem(zoomInSelectedItem);
        hourPicker.setZoomInSelectedItem(zoomInSelectedItem);
    }


    public void setCyclic(boolean cyclic) {
        dayPicker.setCyclic(cyclic);
        monthPicker.setCyclic(cyclic);
        yearPicker.setCyclic(cyclic);
        hourPicker.setCyclic(cyclic);
    }


    public void setTextGradual(boolean textGradual) {
        dayPicker.setTextGradual(textGradual);
        monthPicker.setTextGradual(textGradual);
        yearPicker.setTextGradual(textGradual);
        hourPicker.setTextGradual(textGradual);
    }


    public void setShowCurtain(boolean showCurtain) {
        dayPicker.setShowCurtain(showCurtain);
        monthPicker.setShowCurtain(showCurtain);
        yearPicker.setShowCurtain(showCurtain);
        hourPicker.setShowCurtain(showCurtain);
    }

    public void setCurtainColor(int curtainColor) {
        dayPicker.setCurtainColor(curtainColor);
        monthPicker.setCurtainColor(curtainColor);
        yearPicker.setCurtainColor(curtainColor);
        hourPicker.setCurtainColor(curtainColor);
    }

    public void setShowCurtainBorder(boolean showCurtainBorder) {
        dayPicker.setShowCurtainBorder(showCurtainBorder);
        monthPicker.setShowCurtainBorder(showCurtainBorder);
        yearPicker.setShowCurtainBorder(showCurtainBorder);
        hourPicker.setShowCurtainBorder(showCurtainBorder);
    }


    public void setCurtainBorderColor(int curtainBorderColor) {
        dayPicker.setCurtainBorderColor(curtainBorderColor);
        monthPicker.setCurtainBorderColor(curtainBorderColor);
        yearPicker.setCurtainBorderColor(curtainBorderColor);
        hourPicker.setCurtainBorderColor(curtainBorderColor);
    }

    public void setIndicatorText(String yearText, String monthText, String dayText, String hourText) {
        yearPicker.setIndicatorText(yearText);
        monthPicker.setIndicatorText(monthText);
        dayPicker.setIndicatorText(dayText);
        hourPicker.setIndicatorText(hourText);
    }




    public interface onDateTimeSelectListener{
        void onSelected(int year, int month, int day, int hour, long time);
    }


    public void setOnDateSelected(onDateTimeSelectListener onDateSelected) {
        this.onDateSelected = onDateSelected;
    }
}
