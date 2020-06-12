package com.liner.i_desk.CreateRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.liner.i_desk.R;
import com.liner.views.RequestCheckListLayoutView;

import java.util.HashMap;

import ernestoyaquello.com.verticalstepperform.Step;

@SuppressLint("InflateParams")
public class RequestChecklistStep extends Step<String> {
    private RequestCheckListLayoutView requestCheckListLayoutView;
    private Button requestCheckListStepAdd;
    private Activity activity;

    public RequestChecklistStep(Activity activity, String stepTitle) {
        super(stepTitle);
        this.activity = activity;
    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.request_checklist_step, null, false);
        requestCheckListLayoutView = view.findViewById(R.id.requestCheckListStepView);
        requestCheckListStepAdd = view.findViewById(R.id.requestCheckListStepAdd);
        requestCheckListLayoutView.setActivity(activity);
        requestCheckListStepAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCheckListLayoutView.addItem("Пустой элемент");
            }
        });
        return view;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(true, "");
    }

    @Override
    public String getStepData() {
        return "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return "Создано "+requestCheckListLayoutView.getRequestCheckList().size()+" объект(а)";
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

    }


    public HashMap<String, String> getResult(){
        HashMap<String, String> requestChecks = new HashMap<>();
        for(String check:requestCheckListLayoutView.getRequestCheckList())
            requestChecks.put("false", check);
        return requestChecks;
    }
}