package com.liner.i_desk.CreateRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.liner.i_desk.R;

import ernestoyaquello.com.verticalstepperform.Step;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

@SuppressLint("InflateParams")
public class RequestDeviceTextStep extends Step<String> {
    private ExtendedEditText requestDeviceTextStepEditText;
    private Activity activity;
    public RequestDeviceTextStep(Activity activity, String stepTitle) {
        super(stepTitle);
        this.activity = activity;
    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.request_device_text_step, null, false);
        TextFieldBoxes requestDeviceTextStepBox = view.findViewById(R.id.requestDeviceTextStepBox);
        requestDeviceTextStepEditText = view.findViewById(R.id.requestDeviceTextStepEditText);
        requestDeviceTextStepBox.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                markAsCompletedOrUncompleted(true);
            }
        });
        return view;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        if (stepData.length() < 10) {
            return new IsDataValid(false, "Введите корректные данные");
        } else {
            return new IsDataValid(true);
        }
    }

    @Override
    public String getStepData() {
        return requestDeviceTextStepEditText.getText().toString();
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return getStepData();
    }

    @Override
    protected void onStepOpened(boolean animated) {
        requestDeviceTextStepEditText.requestFocus();
    }

    @Override
    protected void onStepClosed(boolean animated) {
        InputMethodManager inputMethodManager =(InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(requestDeviceTextStepEditText.getWindowToken(), 0);
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }

    @Override
    public void restoreStepData(String stepData) {
        requestDeviceTextStepEditText.setText(stepData);
    }
}