package com.liner.i_desk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.liner.i_desk.CreateRequest.RequestDeadlineStep;
import com.liner.i_desk.CreateRequest.RequestDeviceTextStep;
import com.liner.i_desk.CreateRequest.RequestFileStep;
import com.liner.i_desk.CreateRequest.RequestPriorityStep;
import com.liner.i_desk.CreateRequest.RequestTextStep;
import com.liner.i_desk.CreateRequest.RequestTitleStep;
import com.liner.i_desk.CreateRequest.RequestTypeStep;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class ActivityCreateRequest extends AppCompatActivity implements StepperFormListener {
    private RequestTypeStep requestTypeStep;
    private RequestPriorityStep requestPriorityStep;
    private RequestTitleStep requestTitleStep;
    private RequestTextStep requestTextStep;
    private RequestDeviceTextStep requestDeviceTextStep;
    private RequestDeadlineStep requestDeadlineStep;
    private RequestFileStep requestFileStep;


    private VerticalStepperFormView verticalStepperForm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        requestTypeStep = new RequestTypeStep("Тип заявки");
        requestPriorityStep = new RequestPriorityStep("Приоритет заявки");
        requestTitleStep = new RequestTitleStep("Заголовок заявки");
        requestTextStep = new RequestTextStep("Текст заявки");
        requestDeviceTextStep = new RequestDeviceTextStep(this,"Описание оборудования");
        requestDeadlineStep = new RequestDeadlineStep("Деделайн");
        requestFileStep = new RequestFileStep(this,"Прикрепить файлы");

        verticalStepperForm = findViewById(R.id.stepper_form);
        verticalStepperForm
                .setup(this,
                        requestTypeStep,
                        requestPriorityStep,
                        requestTitleStep,
                        requestTextStep,
                        requestDeviceTextStep,
                        requestDeadlineStep,
                        requestFileStep
                )
                .lastStepNextButtonText("Создать заявку")
                .init();
    }

    @Override
    public void onCompletedForm() {

    }

    @Override
    public void onCancelledForm() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        requestFileStep.submitPicker(requestCode, resultCode, data);
    }
}
