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
public class RequestPriorityStep extends Step<String> {
    private SwipeSelector swipeSelector;

    public RequestPriorityStep(String stepTitle) {
        super(stepTitle);
    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.request_priority_step, null, false);
        swipeSelector = view.findViewById(R.id.requestPrioritySelectorStep);
        swipeSelector.setItems(
                new SwipeItem(RequestObject.RequestPriority.VERY_LOW, "Очень низкий", "Для заявок которые не требуют срочного решения"),
                new SwipeItem(RequestObject.RequestPriority.LOW, "Низкий", "Для заявок которые не требуют срочного решения, но должны быть выполнены в срок"),
                new SwipeItem(RequestObject.RequestPriority.MEDIUM, "Средний", "Для стандартных заявок"),
                new SwipeItem(RequestObject.RequestPriority.HIGH, "Высокий", "Для заявок которые требуют более быстрого выполнения"),
                new SwipeItem(RequestObject.RequestPriority.VERY_HIGH, "Очень высокий", "Для заявок которые требуют немедленного решения")
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
            case "VERY_LOW":
                return "Очень низкий";
            case "LOW":
                return "Низкий";
            case "MEDIUM":
                return "Средний";
            case "HIGH":
                return "Высокий";
            case "VERY_HIGH":
                return "Очень высокий";
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
            case "VERY_LOW":
                swipeSelector.selectItemAt(0, true);
                break;
            case "LOW":
                swipeSelector.selectItemAt(1, true);
                break;
            case "MEDIUM":
                swipeSelector.selectItemAt(2, true);
                break;
            case "HIGH":
                swipeSelector.selectItemAt(2, true);
                break;
            case "VERY_HIGH":
                swipeSelector.selectItemAt(2, true);
                break;
        }
    }

    public RequestObject.RequestPriority getResult(){
        return (RequestObject.RequestPriority) swipeSelector.getSelectedItem().value;
    }
}