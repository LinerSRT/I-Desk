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
public class RequestTitleStep extends Step<String> {
    private Activity activity;
    private ExtendedEditText requestTitleStepEditText;
    public RequestTitleStep(Activity activity, String stepTitle) {
        super(stepTitle);
        this.activity = activity;
    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.request_title_step, null, false);
        TextFieldBoxes requestTitleStepBox = view.findViewById(R.id.requestTitleStepBox);
        requestTitleStepEditText = view.findViewById(R.id.requestTitleStepEditText);
        requestTitleStepBox.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
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
        return requestTitleStepEditText.getText().toString();
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return getStepData();
    }

    @Override
    protected void onStepOpened(boolean animated) {
        requestTitleStepEditText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    protected void onStepClosed(boolean animated) {
        InputMethodManager inputMethodManager =(InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(requestTitleStepEditText.getWindowToken(), 0);
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }

    @Override
    public void restoreStepData(String stepData) {
        requestTitleStepEditText.setText(stepData);
    }

    public String getResult(){
        return requestTitleStepEditText.getText().toString();
    }
}