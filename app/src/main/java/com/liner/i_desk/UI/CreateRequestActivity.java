package com.liner.i_desk.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.liner.i_desk.API.Data.Message;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.TextUtils;
import com.liner.i_desk.Utils.ViewUtils;
import com.liner.i_desk.Utils.Views.DatePickerBottomSheetDialog;
import com.liner.i_desk.Utils.Views.EditBottomSheetDialog;
import com.liner.i_desk.Utils.Views.EditRegexTextView;
import com.liner.i_desk.Utils.Views.ExtendedTextButton;
import com.liner.i_desk.Utils.Views.FilePickerBottomSheetDialog;
import com.liner.i_desk.Utils.Views.FirebaseActivity;
import com.liner.i_desk.Utils.Views.IndeterminateBottomSheetDialog;
import com.liner.i_desk.Utils.Views.ProgressBottomSheetDialog;
import com.liner.i_desk.Utils.Views.RequestCheckListView;
import com.liner.i_desk.Utils.Views.RequestTypeView;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class CreateRequestActivity extends FirebaseActivity {
    private EditRegexTextView createRequestTitle;
    private EditRegexTextView createRequestShortDescription;
    private EditRegexTextView createRequestUserDeviceDescription;
    private TextView closeRequestCreatingActivity;
    private ExtendedTextButton doneRequestCreatingActivity;
    private TextView createRequestDeadlineView;
    private TextView createRequestDeadlineText;
    private TextView createRequestFilePickerView;
    private TextView createRequestAddCheckListItem;
    private TagContainerLayout createRequestFileTagLayout;
    private RequestTypeView createRequestTypeView;
    private RequestCheckListView createRequestCheckListView;
    private List<File> fileList = new ArrayList<>();
    private FilePickerBottomSheetDialog.Builder filePickerBottomSheetDialog;
    private DatePickerBottomSheetDialog.Builder datePickerBottomSheetDialog;
    private Request newRequest = new Request();
    private List<Request> userRequests;

    private IndeterminateBottomSheetDialog.Builder actionProgressDialog;
    private boolean requestTitleDone = false;
    private boolean requestShortDescDone = false;
    private boolean requestUserDeviceDescDone = false;
    private boolean requestDeadlineDone = false;
    private boolean requestTypeDone = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        closeRequestCreatingActivity = findViewById(R.id.closeRequestCreatingActivity);
        doneRequestCreatingActivity = findViewById(R.id.doneRequestCreatingActivity);
        createRequestTitle = findViewById(R.id.createRequestTitle);
        createRequestShortDescription = findViewById(R.id.createRequestShortDescription);
        createRequestUserDeviceDescription = findViewById(R.id.createRequestUserDeviceDescription);
        createRequestDeadlineView = findViewById(R.id.createRequestDeadlineView);
        createRequestDeadlineText = findViewById(R.id.createRequestDeadlineText);
        createRequestFilePickerView = findViewById(R.id.createRequestFilePickerView);
        createRequestFileTagLayout = findViewById(R.id.createRequestFileTagLayout);
        createRequestTypeView = findViewById(R.id.createRequestTypeView);
        createRequestAddCheckListItem = findViewById(R.id.createRequestAddCheckListItem);
        createRequestCheckListView = findViewById(R.id.createRequestCheckListView);
        doneRequestCreatingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRequest();
            }
        });


        actionProgressDialog = new IndeterminateBottomSheetDialog.Builder(this);
        actionProgressDialog
                .setTitleText("Подождите")
                .setDialogText("Идет обработка")
                .build();

        createRequestShortDescription.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                requestShortDescDone = true;
            }

            @Override
            public void onNotValid() {
                requestShortDescDone = false;
            }
        });
        createRequestTitle.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                requestTitleDone = true;
            }

            @Override
            public void onNotValid() {
                requestTitleDone = false;
            }
        });

        createRequestUserDeviceDescription.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                requestUserDeviceDescDone = true;
            }

            @Override
            public void onNotValid() {
                requestUserDeviceDescDone = false;
            }
        });

        createRequestCheckListView.setOnItemClickListener(new RequestCheckListView.onItemClickListener() {
            @Override
            public void onItemClick(int position, final RequestCheckListView.RequestListItem requestListItem) {
                final SimpleBottomSheetDialog.Builder dialogBuilder = new SimpleBottomSheetDialog.Builder(CreateRequestActivity.this);
                dialogBuilder.setTitleText("Подтверждение")
                        .setDialogText("Вы действительно хотите изменить этот элемент?")
                        .setDismissTouchOutside(false)
                        .setCancel("Отмена", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBuilder.close();
                            }
                        });
                dialogBuilder.setDone("Изменить", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.close();
                        final EditBottomSheetDialog.Builder editDialogBuilder = new EditBottomSheetDialog.Builder(CreateRequestActivity.this);
                        editDialogBuilder.setTitleText("Введите текст элемента")
                                .setCancel("Отмена", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        editDialogBuilder.close();
                                    }
                                })
                                .setDone("Изменить", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        requestListItem.getTextView().setText(editDialogBuilder.getDialog().getDialogText());
                                        createRequestCheckListView.updateViews();
                                        editDialogBuilder.close();
                                    }
                                }).build();
                        editDialogBuilder.show();

                    }
                }).build();
                dialogBuilder.show();
            }

            @Override
            public void onCloseClick(final int position) {
                final SimpleBottomSheetDialog.Builder dialogBuilder = new SimpleBottomSheetDialog.Builder(CreateRequestActivity.this);
                dialogBuilder.setTitleText("Удаление")
                        .setDialogText("Удалить элемент?")
                        .setCancel("Нет", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBuilder.close();
                            }
                        })
                        .setDone("Да", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBuilder.close();
                                createRequestCheckListView.getRequestListItems().remove(position);
                                createRequestCheckListView.updateViews();
                            }
                        }).build();
                dialogBuilder.show();
            }
        });

        createRequestAddCheckListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditBottomSheetDialog.Builder editDialogBuilder = new EditBottomSheetDialog.Builder(CreateRequestActivity.this);
                editDialogBuilder.setTitleText("Введите текст элемента")
                        .setCancel("Отмена", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editDialogBuilder.close();
                            }
                        })
                        .setDone("Добавить", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createRequestCheckListView.addItem(editDialogBuilder.getDialog().getDialogText());
                                editDialogBuilder.close();
                            }
                        }).build();
                editDialogBuilder.show();
            }
        });

        createRequestTypeView.setOnTypeSelectedListener(new RequestTypeView.onTypeSelectedListener() {
            @Override
            public void onSelected(Request.Type type) {
                requestTypeDone = true;

            }
        });
        createRequestFileTagLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(final int position, String text) {
                createRequestFileTagLayout.removeTag(position);
                fileList.remove(position);
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                createRequestFileTagLayout.removeTag(position);
                fileList.remove(position);
            }
        });

        closeRequestCreatingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        createRequestDeadlineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerBottomSheetDialog = new DatePickerBottomSheetDialog.Builder(CreateRequestActivity.this);
                datePickerBottomSheetDialog.setTitleText("Выберите дату дедлайна")
                        .setCancel("Отмена", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                datePickerBottomSheetDialog.close();
                            }
                        })
                        .setDone("Готово", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                datePickerBottomSheetDialog.close();
                                createRequestDeadlineText.setVisibility(View.VISIBLE);
                                requestDeadlineDone = true;
                                //createRequestDeadlineText.setText(Time.getHumanReadableTime(datePickerBottomSheetDialog.getDialog().getPickedDate(), "yyyy.MM.dd HH:mm"));

                            }
                        }).build();
                datePickerBottomSheetDialog.show();
            }
        });
        createRequestFilePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filePickerBottomSheetDialog = new FilePickerBottomSheetDialog.Builder(CreateRequestActivity.this);
                filePickerBottomSheetDialog.setTitleText("Выберите файл")
                        .setCancel("Отмена", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                filePickerBottomSheetDialog.close();
                            }
                        })
                        .setDone("Готово", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                filePickerBottomSheetDialog.close();
                                createRequestFileTagLayout.addTag(filePickerBottomSheetDialog.getDialog().getFile().getName());
                                fileList.add(filePickerBottomSheetDialog.getDialog().getFile());

                            }
                        }).build();
                filePickerBottomSheetDialog.show();
            }
        });


    }

    @Override
    public void onSomethingChanged() {

    }

    private void submitRequest() {
        if (!requestTitleDone) {
            showMessage("Внимание!", "Укажите заголовок заявки");
            return;
        }
        if (!requestShortDescDone) {
            showMessage("Внимание!", "Укажите краткое описание заявки");
            return;
        }
        if (!requestUserDeviceDescDone) {
            showMessage("Внимание!", "Укажите оборудование заявки");
            return;
        }
        if (!requestTypeDone) {
            showMessage("Внимание!", "Укажите тип заявки");
            return;
        }
        if (!requestDeadlineDone) {
            showMessage("Внимание!", "Укажите дедлайн заявки");
            return;
        }
        if (getCurrentUser() != null) {
            actionProgressDialog.show();
            final SimpleBottomSheetDialog.Builder confirmDialog = new SimpleBottomSheetDialog.Builder(CreateRequestActivity.this);
            confirmDialog.setTitleText("Заявка создана")
                    .setDialogText("Ваша заявка была успешно создана")
                    .setDone("Ок", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            confirmDialog.close();
                            finish();
                        }
                    }).build();

            final SimpleBottomSheetDialog.Builder errorDialog = new SimpleBottomSheetDialog.Builder(CreateRequestActivity.this);
            errorDialog.setTitleText("Ошибка")
                    .setDialogText("Что-то пошло не так, попробовать еще раз?")
                    .setCancel("Нет", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            errorDialog.close();
                            finish();
                        }
                    })
                    .setDone("", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            errorDialog.close();
                            submitRequest();
                        }
                    }).build();

            //userRequests = user.getRequestList();
            if (userRequests == null)
                userRequests = new ArrayList<>();
            //newRequest.setRequestID(TextUtils.generateRandomString(20));
            newRequest.setRequestCreatorID(firebaseUser.getUid());
            newRequest.setRequestCheckList(new ArrayList<Request.RequestCheckList>());
            newRequest.setRequestFiles(new ArrayList<Request.FileData>());
            newRequest.setRequestType(createRequestTypeView.getType());
            newRequest.setMessageList(new ArrayList<Message>());
//            newRequest.setCreateTime(Time.getTime());
//            newRequest.setDeadlineTime(datePickerBottomSheetDialog.getDialog().getPickedDate());
            newRequest.setRequestUserDeviceDescription(createRequestUserDeviceDescription.getText().toString().trim());
            newRequest.setRequestPriority(Request.Priority.MEDIUM);
            newRequest.setRequestShortDescription(createRequestShortDescription.getText().toString().trim());
            newRequest.setRequestStatus(Request.Status.OPENNED);
            newRequest.setRequestTitle(createRequestTitle.getText().toString().trim());
            for (RequestCheckListView.RequestListItem item : createRequestCheckListView.getRequestListItems()) {
                newRequest.getRequestCheckList().add(new Request.RequestCheckList(item.getTextView().getText().toString().trim(), false));
            }
            final ProgressBottomSheetDialog uploadFileDialog = ViewUtils.createProgressDialog(this, "Загрузка файлов", "Подождите, идет загрузка");
            if(!fileList.isEmpty())
                uploadFileDialog.create();
            FirebaseHelper.uploadFileList(fileList, "user_files" + File.separator + firebaseUser.getUid() + File.separator + "requests" + File.separator + newRequest.getRequestID(), new FirebaseHelper.UploadListListener() {
                @Override
                public void onFileUploading(int percent, long transferred, long total, String filename) {

                    uploadFileDialog.setProgress(percent, total, transferred, filename);
                }

                @Override
                public void onFilesUploaded(List<Request.FileData> fileDataList) {
                    uploadFileDialog.dismiss(true);
                    newRequest.setRequestFiles(fileDataList);
                    userRequests.add(newRequest);
                    FirebaseHelper.setUserValue(firebaseUser.getUid(), "requestList", userRequests, new FirebaseHelper.IFirebaseHelperListener() {
                        @Override
                        public void onSuccess(Object result) {
                            actionProgressDialog.close();
                            confirmDialog.show();
                        }

                        @Override
                        public void onFail(String reason) {
                            actionProgressDialog.close();
                            confirmDialog.close();
                            errorDialog.show();
                        }
                    });
                }

                @Override
                public void onFileUploadFail(String reason) {
                    actionProgressDialog.close();
                    confirmDialog.close();
                    errorDialog.show();
                }

                @Override
                public void onListEmpty() {
                    newRequest.setRequestFiles(new ArrayList<Request.FileData>());
                    userRequests.add(newRequest);
                    FirebaseHelper.setUserValue(firebaseUser.getUid(), "requestList", userRequests, new FirebaseHelper.IFirebaseHelperListener() {
                        @Override
                        public void onSuccess(Object result) {
                            actionProgressDialog.close();
                            confirmDialog.show();
                        }

                        @Override
                        public void onFail(String reason) {
                            actionProgressDialog.close();
                            confirmDialog.close();
                            errorDialog.show();
                        }
                    });
                }
            });

        }
    }

    private void showMessage(String title, String text) {
        final SimpleBottomSheetDialog.Builder dialog = new SimpleBottomSheetDialog.Builder(this);
        dialog.setTitleText(title)
                .setDialogText(text)
                .setDone("Ок", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.close();
                    }
                }).build();
        dialog.show();


    }
}
