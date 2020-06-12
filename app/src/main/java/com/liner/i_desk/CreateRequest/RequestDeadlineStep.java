package com.liner.i_desk.CreateRequest;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.liner.i_desk.R;
import com.liner.utils.Time;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.liner.views.verticalstepperform.Step;

@SuppressLint("InflateParams")
public class RequestDeadlineStep extends Step<String> {
    private SingleDateAndTimePicker requestDeadlineStepPicker;
    private Date selectedDate;

    public RequestDeadlineStep(String title, String subtitle) {
        super(title, subtitle);
    }

    public RequestDeadlineStep(String stepTitle) {
        super(stepTitle);
    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.request_deadline_step, null, false);
        requestDeadlineStepPicker = view.findViewById(R.id.requestDeadlineStepPicker);
        selectedDate = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        requestDeadlineStepPicker.setMinDate(selectedDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        requestDeadlineStepPicker.selectDate(calendar);
        requestDeadlineStepPicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                selectedDate = date;
            }
        });

        return view;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(true);
    }

    @Override
    public String getStepData() {
        return String.valueOf(selectedDate.getTime());
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return Time.getHumanReadableTime(Long.parseLong(getStepData()), "yyyy.MM.dd HH:mm");
    }

    @Override
    protected void onStepOpened(boolean animated) {

    }

    @Override
    protected void onStepClosed(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }

    @Override
    public void restoreStepData(String stepData) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(Long.parseLong(stepData)));
        requestDeadlineStepPicker.selectDate(calendar);
    }

    public long getResult(){
        return selectedDate.getTime();
    }
}