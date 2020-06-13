package com.liner.i_desk.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.liner.i_desk.CreateRequest.RequestChecklistStep;
import com.liner.i_desk.CreateRequest.RequestDeadlineStep;
import com.liner.i_desk.CreateRequest.RequestDeviceTextStep;
import com.liner.i_desk.CreateRequest.RequestFileStep;
import com.liner.i_desk.CreateRequest.RequestPriorityStep;
import com.liner.i_desk.CreateRequest.RequestTextStep;
import com.liner.i_desk.CreateRequest.RequestTitleStep;
import com.liner.i_desk.CreateRequest.RequestTypeStep;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.Storage.FirebaseUploadTask;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.i_desk.R;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.verticalstepperform.Builder;
import com.liner.views.verticalstepperform.Step;
import com.liner.views.verticalstepperform.VerticalStepperFormView;
import com.liner.views.verticalstepperform.listener.StepperFormListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CreateRequestFragment extends Fragment implements StepperFormListener {
    public RequestTypeStep requestTypeStep;
    public RequestPriorityStep requestPriorityStep;
    public RequestTitleStep requestTitleStep;
    public RequestTextStep requestTextStep;
    public RequestDeviceTextStep requestDeviceTextStep;
    public RequestDeadlineStep requestDeadlineStep;
    public RequestFileStep requestFileStep;
    public RequestChecklistStep requestChecklistStep;
    public BaseDialog cancelConfirm;
    public VerticalStepperFormView verticalStepperForm;
    public BaseDialog uploadFilesDialog;
    public BaseDialog processingDialog;
    public BaseDialog errorDialog;
    public BaseDialog finishDialog;
    public CloseCallback closeCallback;
    public Builder builder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_request, container, false);
        verticalStepperForm = view.findViewById(R.id.stepper_form);
        new InitFragment().execute();

        return view;
    }

    private class InitFragment extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            requestTypeStep = new RequestTypeStep("Тип заявки", "Выберите тип заявки");
            requestPriorityStep = new RequestPriorityStep("Приоритет заявки", "Укажите приоритет");
            requestTitleStep = new RequestTitleStep(getActivity(), "Заголовок заявки", "Укажите кратко заголовок");
            requestTextStep = new RequestTextStep(getActivity(), "Текст заявки", "Укажите кратко суть заявки");
            requestDeviceTextStep = new RequestDeviceTextStep(getActivity(), "Описание оборудования", "Укажите оборудование");
            requestDeadlineStep = new RequestDeadlineStep("Деделайн", "Укажите время дедлайна");
            requestFileStep = new RequestFileStep(getActivity(), "Прикрепить файлы", "Прикрепите файлы (необязательно)");
            requestChecklistStep = new RequestChecklistStep(getActivity(), "Список задач", "Добавьте пункты (необязательно)");
            List<Step> stepList = new ArrayList<>();
            stepList.add(requestTypeStep);
            stepList.add(requestPriorityStep);
            stepList.add(requestTitleStep);
            stepList.add(requestDeviceTextStep);
            stepList.add(requestDeadlineStep);
            stepList.add(requestFileStep);
            stepList.add(requestChecklistStep);
            builder = new Builder(verticalStepperForm, CreateRequestFragment.this, stepList.toArray(new Step[0]));
            uploadFilesDialog = BaseDialogBuilder.buildFast(
                    getActivity(),
                    "Загрузка файлов",
                    "Пожалуйста подождите, ваши файлы загружаются на сервер",
                    null,
                    null,
                    BaseDialogBuilder.Type.INDETERMINATE,
                    null,
                    null
            );
            processingDialog = BaseDialogBuilder.buildFast(
                    getActivity(),
                    "Создание заявки",
                    "Пожалуйста подождите, ваша заявка создается",
                    null,
                    null,
                    BaseDialogBuilder.Type.INDETERMINATE,
                    null,
                    null
            );
            errorDialog = BaseDialogBuilder.buildFast(
                    getActivity(),
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
                    getActivity(),
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
                            if (closeCallback != null)
                                closeCallback.onClose();
                        }
                    }
            );
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            builder.init();
        }
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
            new FirebaseUploadTask().with(getActivity())
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
                            for (FileObject fileObject : result) {
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
        cancelConfirm = BaseDialogBuilder.buildFast(getActivity(),
                "Отмена заявки",
                "Вы действительно хотите отменить заявку?",
                "Отменить",
                "Нет",
                BaseDialogBuilder.Type.QUESTION,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelConfirm.closeDialog();
                        if (closeCallback != null)
                            closeCallback.onClose();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelConfirm.closeDialog();
                        verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
                    }
                });
        cancelConfirm.showDialog();
    }

    public void setCloseCallback(CloseCallback closeCallback) {
        this.closeCallback = closeCallback;
    }

    public interface CloseCallback {
        void onClose();
    }
}
