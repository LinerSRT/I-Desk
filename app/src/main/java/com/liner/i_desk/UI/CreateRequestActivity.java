package com.liner.i_desk.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.Animations.ViewAnimator;
import com.liner.i_desk.Utils.TimeUtils;
import com.liner.i_desk.Utils.Views.DatePickerBottomSheetDialog;
import com.liner.i_desk.Utils.Views.EditBottomSheetDialog;
import com.liner.i_desk.Utils.Views.FilePickerBottomSheetDialog;
import com.liner.i_desk.Utils.Views.FirebaseActivity;
import com.liner.i_desk.Utils.Views.RequestCheckListView;
import com.liner.i_desk.Utils.Views.RequestTypeView;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class CreateRequestActivity extends FirebaseActivity {
    private TextView closeRequestCreatingActivity;
    private TextView doneRequestCreatingActivity;
    private TextView createRequestDeadlineView;
    private TextView createRequestDeadlineText;
    private TextView createRequestFilePickerView;
    private TextView createRequestAddCheckListItem;
    private TagContainerLayout createRequestFileTagLayout;

    private RequestTypeView createRequestTypeView;
    private RequestCheckListView createRequestRequestCheckListView;

    private List<File> fileList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        closeRequestCreatingActivity = findViewById(R.id.closeRequestCreatingActivity);
        doneRequestCreatingActivity = findViewById(R.id.doneRequestCreatingActivity);
        createRequestDeadlineView = findViewById(R.id.createRequestDeadlineView);
        createRequestDeadlineText = findViewById(R.id.createRequestDeadlineText);
        createRequestFilePickerView = findViewById(R.id.createRequestFilePickerView);
        createRequestFileTagLayout = findViewById(R.id.createRequestFileTagLayout);
        createRequestTypeView = findViewById(R.id.createRequestTypeView);
        createRequestAddCheckListItem = findViewById(R.id.createRequestAddCheckListItem);
        createRequestRequestCheckListView = findViewById(R.id.createRequestRequestCheckListView);


        createRequestRequestCheckListView.setOnItemClickListener(new RequestCheckListView.onItemClickListener() {
            @Override
            public void onItemClick(int position, final RequestCheckListView.RequestListItem requestListItem) {
                final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(CreateRequestActivity.this);
                simpleBottomSheetDialog.setDialogTitle("Изменить элемент?");
                simpleBottomSheetDialog.setDialogText("Вы действительно хотите изменить этот элемент?");
                simpleBottomSheetDialog.setDialogCancelBtnText("Отмена");
                simpleBottomSheetDialog.setDialogDoneBtnText("Изменить");
                simpleBottomSheetDialog.setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                            @Override
                            public void done() {
                                simpleBottomSheetDialog.dismiss(true);
                            }
                        });
                    }
                });
                simpleBottomSheetDialog.setDoneClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                            @Override
                            public void done() {
                                simpleBottomSheetDialog.dismiss(true);
                                final EditBottomSheetDialog editBottomSheetDialog = new EditBottomSheetDialog(CreateRequestActivity.this);
                                editBottomSheetDialog.setDialogTitle("Введите текст элемента");
                                editBottomSheetDialog.setDialogCancelBtnText("Отмена");
                                editBottomSheetDialog.setDialogDoneBtnText("Изменить");
                                editBottomSheetDialog.setCancelClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                                            @Override
                                            public void done() {
                                                editBottomSheetDialog.dismiss(true);
                                            }
                                        });
                                    }
                                });
                                editBottomSheetDialog.setDoneClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                                            @Override
                                            public void done() {
                                                requestListItem.getTextView().setText(editBottomSheetDialog.getDialogText());
                                                createRequestRequestCheckListView.updateViews();
                                                editBottomSheetDialog.dismiss(true);
                                            }
                                        });
                                    }
                                });
                                editBottomSheetDialog.create();

                            }
                        });
                    }
                });
                simpleBottomSheetDialog.create();

            }

            @Override
            public void onCloseClick(final int position) {

                final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(CreateRequestActivity.this);
                simpleBottomSheetDialog.setDialogTitle("Удалить элемент?");
                simpleBottomSheetDialog.setDialogText("Вы действительно хотите удалить этот элемент?");
                simpleBottomSheetDialog.setDialogCancelBtnText("Отмена");
                simpleBottomSheetDialog.setDialogDoneBtnText("Удалить");
                simpleBottomSheetDialog.setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                            @Override
                            public void done() {
                                simpleBottomSheetDialog.dismiss(true);
                            }
                        });
                    }
                });
                simpleBottomSheetDialog.setDoneClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                            @Override
                            public void done() {
                                simpleBottomSheetDialog.dismiss(true);
                                createRequestRequestCheckListView.getRequestListItems().remove(position);
                                createRequestRequestCheckListView.updateViews();
                            }
                        });
                    }
                });
                simpleBottomSheetDialog.create();


            }
        });

        createRequestAddCheckListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {
                        final EditBottomSheetDialog editBottomSheetDialog = new EditBottomSheetDialog(CreateRequestActivity.this);
                        editBottomSheetDialog.setDialogTitle("Текст элемента");
                        editBottomSheetDialog.setDialogCancelBtnText("Отмена");
                        editBottomSheetDialog.setDialogDoneBtnText("Добавить");
                        editBottomSheetDialog.setCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                                    @Override
                                    public void done() {
                                        editBottomSheetDialog.dismiss(true);
                                    }
                                });
                            }
                        });
                        editBottomSheetDialog.setDoneClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                                    @Override
                                    public void done() {
                                        createRequestRequestCheckListView.addItem(editBottomSheetDialog.getDialogText());
                                        editBottomSheetDialog.dismiss(true);
                                    }
                                });
                            }
                        });
                        editBottomSheetDialog.create();
                    }
                });
            }
        });

        createRequestTypeView.setOnTypeSelectedListener(new RequestTypeView.onTypeSelectedListener() {
            @Override
            public void onSelected(Request.Type type) {


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

            }
        });

        closeRequestCreatingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {
                        finish();
                    }
                });
            }
        });
        createRequestDeadlineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {
                        final DatePickerBottomSheetDialog pickerBottomSheetDialog = new DatePickerBottomSheetDialog(CreateRequestActivity.this);
                        pickerBottomSheetDialog.setDialogTitle("Выберите дату для дедлайна");
                        pickerBottomSheetDialog.setDialogDoneBtnText("Готово");
                        pickerBottomSheetDialog.setDialogCancelBtnText("Отмена");
                        pickerBottomSheetDialog.setCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                                    @Override
                                    public void done() {

                                        pickerBottomSheetDialog.dismiss(true);
                                    }
                                });
                            }
                        });
                        pickerBottomSheetDialog.setDoneClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                                    @Override
                                    public void done() {

                                        pickerBottomSheetDialog.dismiss(true);
                                        createRequestDeadlineText.setVisibility(View.VISIBLE);
                                        createRequestDeadlineText.setText(TimeUtils.convertDate(pickerBottomSheetDialog.getPickedDate(), true));
                                    }
                                });
                            }
                        });
                        pickerBottomSheetDialog.create();
                    }
                });

            }
        });
        createRequestFilePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {
                        final FilePickerBottomSheetDialog pickerBottomSheetDialog = new FilePickerBottomSheetDialog(CreateRequestActivity.this);
                        pickerBottomSheetDialog.setDialogTitle("Выберите файл");
                        pickerBottomSheetDialog.setDialogDoneBtnText("Готово");
                        pickerBottomSheetDialog.setDialogCancelBtnText("Отмена");
                        pickerBottomSheetDialog.setCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                                    @Override
                                    public void done() {
                                        pickerBottomSheetDialog.dismiss(true);
                                    }
                                });
                            }
                        });
                        pickerBottomSheetDialog.setDoneClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                                    @Override
                                    public void done() {
                                        createRequestFileTagLayout.addTag(pickerBottomSheetDialog.getFile().getName());
                                        fileList.add(pickerBottomSheetDialog.getFile());
                                        pickerBottomSheetDialog.dismiss(true);
                                    }
                                });

                            }
                        });
                        pickerBottomSheetDialog.create();
                    }
                });
            }
        });


    }

    @Override
    public void onFirebaseChanged(User user) {

    }

    @Override
    public void onUserObtained(User user) {

    }
}
