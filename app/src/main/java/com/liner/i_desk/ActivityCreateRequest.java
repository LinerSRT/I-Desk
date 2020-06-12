package com.liner.i_desk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.i_desk.CreateRequest.RequestChecklistStep;
import com.liner.i_desk.CreateRequest.RequestDeadlineStep;
import com.liner.i_desk.CreateRequest.RequestDeviceTextStep;
import com.liner.i_desk.CreateRequest.RequestFileStep;
import com.liner.i_desk.CreateRequest.RequestPriorityStep;
import com.liner.i_desk.CreateRequest.RequestTextStep;
import com.liner.i_desk.CreateRequest.RequestTitleStep;
import com.liner.i_desk.CreateRequest.RequestTypeStep;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.FireActivity;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.Storage.FirebaseUploadTask;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.views.verticalstepperform.VerticalStepperFormView;
import com.liner.views.verticalstepperform.listener.StepperFormListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ActivityCreateRequest extends FireActivity implements StepperFormListener {
    private RequestTypeStep requestTypeStep;
    private RequestPriorityStep requestPriorityStep;
    private RequestTitleStep requestTitleStep;
    private RequestTextStep requestTextStep;
    private RequestDeviceTextStep requestDeviceTextStep;
    private RequestDeadlineStep requestDeadlineStep;
    private RequestFileStep requestFileStep;
    private RequestChecklistStep requestChecklistStep;
    private BaseDialog cancelConfirm;
    private VerticalStepperFormView verticalStepperForm;
    private BaseDialog uploadFilesDialog;
    private BaseDialog processingDialog;
    private BaseDialog errorDialog;
    private BaseDialog finishDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        requestTypeStep = new RequestTypeStep("Тип заявки", "Выберите тип заявки");
        requestPriorityStep = new RequestPriorityStep("Приоритет заявки", "Укажите приоритет");
        requestTitleStep = new RequestTitleStep(ActivityCreateRequest.this, "Заголовок заявки", "Укажите кратко заголовок");
        requestTextStep = new RequestTextStep(ActivityCreateRequest.this, "Текст заявки", "Укажите кратко суть заявки");
        requestDeviceTextStep = new RequestDeviceTextStep(ActivityCreateRequest.this, "Описание оборудования", "Укажите оборудование");
        requestDeadlineStep = new RequestDeadlineStep("Деделайн", "Укажите время дедлайна");
        requestFileStep = new RequestFileStep(ActivityCreateRequest.this, "Прикрепить файлы", "Прикрепите файлы (необязательно)");
        requestChecklistStep = new RequestChecklistStep(ActivityCreateRequest.this, "Список задач", "Добавьте пункты (необязательно)");

        verticalStepperForm = findViewById(R.id.stepper_form);
        verticalStepperForm
                .setup(ActivityCreateRequest.this,
                        requestTypeStep,
                        requestPriorityStep,
                        requestTitleStep,
                        requestTextStep,
                        requestDeviceTextStep,
                        requestDeadlineStep,
                        requestFileStep,
                        requestChecklistStep
                )
                .init();
        uploadFilesDialog = BaseDialogBuilder.buildFast(
                this,
                "Загрузка файлов",
                "Пожалуйста подождите, ваши файлы загружаются на сервер",
                null,
                null,
                BaseDialogBuilder.Type.INDETERMINATE,
                null,
                null
        );
        processingDialog = BaseDialogBuilder.buildFast(
                this,
                "Создание заявки",
                "Пожалуйста подождите, ваша заявка создается",
                null,
                null,
                BaseDialogBuilder.Type.INDETERMINATE,
                null,
                null
        );
        errorDialog = BaseDialogBuilder.buildFast(
                this,
                "Произошла ошибка",
                "Во время создания заявки произошла ошибка. Попробуйте позже",
                null,
                "Ок",
                BaseDialogBuilder.Type.ERROR,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorDialog.closeDialog();
                    }
                }
        );
        finishDialog = BaseDialogBuilder.buildFast(
                this,
                "Заявка создана",
                "Ваша заявка была успешно создана",
                null,
                "Ок",
                BaseDialogBuilder.Type.INFO,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorDialog.closeDialog();
                        finish();
                    }
                }
        );

    }

    @Override
    public void onCompletedForm() {
        processingDialog.showDialog();
        final RequestObject newRequest = new RequestObject();
        newRequest.setRequestID(TextUtils.getUniqueString());
        newRequest.setRequestType(requestTypeStep.getResult());
        newRequest.setRequestPriority(requestPriorityStep.getResult());
        newRequest.setRequestStatus(RequestObject.RequestStatus.PENDING);
        newRequest.setRequestTitle(requestTitleStep.getResult());
        newRequest.setRequestText(requestTextStep.getResult());
        newRequest.setRequestUserDeviceText(requestDeviceTextStep.getResult());
        newRequest.setRequestChecks(requestChecklistStep.getResult());
        newRequest.setRequestMessages(new HashMap<String, String>());
        newRequest.setRequestFiles(new HashMap<String, String>());
        newRequest.setRequestCreatedAt(Time.getTime());
        newRequest.setRequestDeadlineAt(requestDeadlineStep.getResult());
        newRequest.setRequestCreatorID(Firebase.getUserUID());
        newRequest.setRequestCreatorLastOnlineTime(Time.getTime());
        if (!requestFileStep.getResult().isEmpty()) {
            uploadFilesDialog.showDialog();
            new FirebaseUploadTask().with(ActivityCreateRequest.this)
                    .userUID(Firebase.getUserUID())
                    .fileList(requestFileStep.getResult())
                    .uploadFiles(new TaskListener<List<FileObject>>() {
                        @Override
                        public void onStart(String fileUID) {

                        }

                        @Override
                        public void onProgress(long transferredBytes, long totalBytes) {

                        }

                        @Override
                        public void onFinish(List<FileObject> result, String fileUID) {
                            for(FileObject fileObject:result){
                                newRequest.getRequestFiles().put(fileObject.getFileID(), fileObject.getFileCreatorID());
                            }
                            FirebaseValue.getUser(Firebase.getUserUID(), new FirebaseValue.ValueListener() {
                                @Override
                                public void onSuccess(Object object, DatabaseReference databaseReference) {
                                    UserObject userObject = (UserObject) object;
                                    if (userObject.getUserRequests() == null)
                                        userObject.setUserRequests(new ArrayList<String>());
                                    newRequest.setRequestCreatorName(userObject.getUserName());
                                    newRequest.setRequestCreatorPhotoURL(userObject.getUserProfilePhotoURL());
                                    userObject.getUserRequests().add(newRequest.getRequestID());
                                    Objects.requireNonNull(Firebase.getRequestsDatabase()).child(newRequest.getRequestID()).setValue(newRequest);
                                    FirebaseValue.setUser(Firebase.getUserUID(), userObject);
                                    uploadFilesDialog.closeDialog();
                                    processingDialog.closeDialog();
                                    finishDialog.showDialog();
                                }

                                @Override
                                public void onFail(String errorMessage) {
                                    uploadFilesDialog.closeDialog();
                                    errorDialog.showDialog();
                                }
                            });
                        }

                        @Override
                        public void onFailed(Exception reason) {
                            uploadFilesDialog.closeDialog();
                            errorDialog.showDialog();
                        }
                    });
        } else {
            FirebaseValue.getUser(Firebase.getUserUID(), new FirebaseValue.ValueListener() {
                @Override
                public void onSuccess(Object object, DatabaseReference databaseReference) {
                    UserObject userObject = (UserObject) object;
                    newRequest.setRequestCreatorName(userObject.getUserName());
                    newRequest.setRequestCreatorPhotoURL(userObject.getUserProfilePhotoURL());
                    if (userObject.getUserRequests() == null)
                        userObject.setUserRequests(new ArrayList<String>());
                    userObject.getUserRequests().add(newRequest.getRequestID());
                    Objects.requireNonNull(Firebase.getRequestsDatabase()).child(newRequest.getRequestID()).setValue(newRequest);
                    FirebaseValue.setUser(Firebase.getUserUID(), userObject);
                    processingDialog.closeDialog();
                    finishDialog.showDialog();
                }

                @Override
                public void onFail(String errorMessage) {
                    uploadFilesDialog.closeDialog();
                    errorDialog.showDialog();
                }
            });
        }
    }

    @Override
    public void onCancelledForm() {
        cancelConfirm = BaseDialogBuilder.buildFast(this,
                "Отмена заявки",
                "Вы действительно хотите отменить заявку?",
                "Отменить",
                "Нет",
                BaseDialogBuilder.Type.QUESTION,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelConfirm.closeDialog();
                    }
                });
        cancelConfirm.showDialog();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        requestFileStep.submitPicker(requestCode, resultCode, data);
    }
}
