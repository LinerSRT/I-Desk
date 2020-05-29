package com.liner.i_desk.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.Animations.ViewAnimator;
import com.liner.i_desk.Utils.TimeUtils;
import com.liner.i_desk.Utils.Views.DatePickerBottomSheetDialog;
import com.liner.i_desk.Utils.Views.FilePickerBottomSheetDialog;
import com.liner.i_desk.Utils.Views.FirebaseActivity;

public class CreateRequestActivity extends FirebaseActivity {
    private TextView closeRequestCreatingActivity;
    private TextView doneRequestCreatingActivity;
    private TextView createRequestDeadlineView;
    private TextView createRequestDeadlineText;
    private TextView createRequestFilePickerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        closeRequestCreatingActivity = findViewById(R.id.closeRequestCreatingActivity);
        doneRequestCreatingActivity = findViewById(R.id.doneRequestCreatingActivity);
        createRequestDeadlineView = findViewById(R.id.createRequestDeadlineView);
        createRequestDeadlineText = findViewById(R.id.createRequestDeadlineText);
        createRequestFilePickerView = findViewById(R.id.createRequestFilePickerView);
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
                                        Log.d("TAGTAG", "Selected "+pickerBottomSheetDialog.getFile());
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
