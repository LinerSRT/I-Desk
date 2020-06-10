package com.liner.i_desk.CreateRequest;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.liner.i_desk.R;

import ernestoyaquello.com.verticalstepperform.Step;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

@SuppressLint("InflateParams")
public class RequestTextStep extends Step<String> {
    private ExtendedEditText requestTextStepEditText;
    public RequestTextStep(String stepTitle) {
        super(stepTitle);
    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.request_text_step, null, false);
        TextFieldBoxes requestTextStepBox = view.findViewById(R.id.requestTextStepBox);
        requestTextStepEditText = view.findViewById(R.id.requestTextStepEditText);
        requestTextStepBox.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
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
        return requestTextStepEditText.getText().toString();
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return getStepData();
    }

    @Override
    protected void onStepOpened(boolean animated) {
        requestTextStepEditText.requestFocus();

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
        requestTextStepEditText.setText(stepData);
    }
}