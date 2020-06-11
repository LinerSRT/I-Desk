package com.liner.i_desk.CreateRequest;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.R;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import ernestoyaquello.com.verticalstepperform.Step;

@SuppressLint("InflateParams")
public class RequestTypeStep extends Step<String> {
    private SwipeSelector swipeSelector;

    public RequestTypeStep(String stepTitle) {
        super(stepTitle);
    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.request_type_step, null, false);
        swipeSelector = view.findViewById(R.id.requestTypeSelectorStep);
        swipeSelector.setItems(
                new SwipeItem(RequestObject.RequestType.CONSULTATION, "Консультация", "Простоя заявка для консультации по простым вопросам"),
                new SwipeItem(RequestObject.RequestType.SERVICE, "Сервис", "Заявка на сервисное обслуживание"),
                new SwipeItem(RequestObject.RequestType.INCIDENT, "Инцидент", "Заявка влючающая в себя срочное решение возникшей проблемы")
        );
        return view;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(true, "");
    }

    @Override
    public String getStepData() {
        return String.valueOf(swipeSelector.getSelectedItem().value);
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        switch (getStepData()){
            case "CONSULTATION":
                return "Консультация";
            case "SERVICE":
                return "Сервис";
            case "INCIDENT":
                return "Инцидент";
        }
        return "";
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
        switch (stepData){
            case "CONSULTATION":
                swipeSelector.selectItemAt(0, true);
                break;
            case "SERVICE":
                swipeSelector.selectItemAt(1, true);
                break;
            case "INCIDENT":
                swipeSelector.selectItemAt(2, true);
                break;
        }
    }

    public RequestObject.RequestType getResult(){
        return (RequestObject.RequestType) swipeSelector.getSelectedItem().value;
    }
}