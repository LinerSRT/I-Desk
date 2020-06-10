package com.liner.i_desk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;
import com.liner.bottomdialogs.BaseDialog;
import com.liner.bottomdialogs.BaseDialogBuilder;
import com.liner.bottomdialogs.ImagePickerDialog;
import com.liner.bottomdialogs.IndeterminateDialog;
import com.liner.bottomdialogs.ProgressDialog;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.FireActivity;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.utils.ImageUtils;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.i_desk.Firebase.UserObject;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;


public class ActivityCreateProfile extends FireActivity {
    private TextFieldBoxes createProfileNickNameFiledBox;
    private CircleImageView createProfilePhoto;
    private UserObject userObject;

    private String userNickName = "", userAbout = "";
    private IndeterminateDialog.Builder progressBottomSheetDialog;
    private ProgressDialog.Builder uploadPhotoDialog;
    private ImagePickerDialog.Builder imagePickerDialog;


    private BaseDialog errorDialog;
    private BaseDialog finishDialog;
    private BaseDialog exitWarnDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile_layout);
        if (getIntent().getSerializableExtra("userObject") == null)
            signOut();
        userObject = (UserObject) getIntent().getSerializableExtra("userObject");
        createProfileNickNameFiledBox = findViewById(R.id.createProfileNickNameFiledBox);
        TextFieldBoxes createProfileAboutFiledBox = findViewById(R.id.createProfileAboutFiledBox);
        MaterialButton createProfileChoosePhoto = findViewById(R.id.createProfileChoosePhoto);
        MaterialButton createProfileNextStep = findViewById(R.id.createProfileNextStep);
        createProfilePhoto = findViewById(R.id.createProfilePhoto);
        createProfileNickNameFiledBox = findViewById(R.id.createProfileNickNameFiledBox);
        createProfileNickNameFiledBox = findViewById(R.id.createProfileNickNameFiledBox);
        createProfileNickNameFiledBox = findViewById(R.id.createProfileNickNameFiledBox);
        createProfileNickNameFiledBox = findViewById(R.id.createProfileNickNameFiledBox);
        progressBottomSheetDialog = new IndeterminateDialog.Builder(this)
                .setDialogText("Завершение регистрации").setTitleText("Подождите...").build();

        errorDialog = BaseDialogBuilder.buildFast(this,
                "Ошибка", "Что-то пошло не так. Попробуйте еще раз",
                null,
                "Ок",
                BaseDialogBuilder.Type.ERROR,
                null,
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorDialog.closeDialog();
            }
        });
        finishDialog = BaseDialogBuilder.buildFast(this,
                "Готово",
                (Constants.USERS_REQUIRE_EMAIL_VERIFICATION)?"Регистрация завершена успешно. Подтвердите свой Email для дальнейшей работы":"Регистрация завершена успешно. Теперь вы можете использовать свой аккаунт для входа",
                null,
                "Ок",
                BaseDialogBuilder.Type.INFO,
                null,
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorDialog.closeDialog();
                if(Constants.USERS_REQUIRE_EMAIL_VERIFICATION) {
                    signOut();
                } else {
                    startActivity(new Intent(ActivityCreateProfile.this, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
            }
        });

        uploadPhotoDialog = new ProgressDialog.Builder(this)
                .setDismissTouchOutside(false)
                .setTitleText("Подождите")
                .setDialogText("Ваше фото загружается")
                .build();
        createProfileNickNameFiledBox.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                userNickName = theNewText;
            }
        });
        createProfileAboutFiledBox.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                userAbout = theNewText;
            }
        });
        createProfileNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextUtils.hideKeyboard(ActivityCreateProfile.this);
                if (!createProfileNickNameFiledBox.isOnError()) {
                    progressBottomSheetDialog.show();
                    FirebaseValue.uploadByteArray(ImageUtils.getDrawableByteArray(createProfilePhoto.getDrawable()), new File(imagePickerDialog.getDialog().getImagePath()).getName(), new FirebaseValue.FileUploadListener() {
                        @Override
                        public void onStart() {
                            progressBottomSheetDialog.close();
                            uploadPhotoDialog.show();
                        }

                        @Override
                        public void onProgress(long transferredBytes, long totalBytes) {
                            uploadPhotoDialog.getDialog().setProgress(Math.round((Float.parseFloat(String.valueOf(transferredBytes))/Float.parseFloat(String.valueOf(totalBytes)))*100));
                        }

                        @Override
                        public void onFail(String reason) {
                            uploadPhotoDialog.close();
                            errorDialog.showDialog();
                        }

                        @Override
                        public void onFinish(FileObject fileObject) {
                            if (!TextUtils.isTextEmpty(userNickName))
                                userObject.setUserAboutText(userAbout);
                            if(userObject.getUserFiles() == null)
                                userObject.setUserFiles(new ArrayList<String>());
                            userObject.getUserFiles().add(fileObject.getFileID());
                            userObject.setUserName(userNickName);
                            userObject.setUserRegisteredAt(Time.getTime());
                            userObject.setUserLastOnlineAt(Time.getTime());
                            userObject.setUserStatus(UserObject.UserStatus.ONLINE);
                            userObject.setUserProfilePhotoURL(fileObject.getFileURL());
                            userObject.setUserRegisterFinished(true);
                            FirebaseValue.setUser(Firebase.getUserUID(), userObject);
                            uploadPhotoDialog.close();
                            finishDialog.showDialog();
                        }
                    });
                }
            }
        });
        createProfileChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextUtils.hideKeyboard(ActivityCreateProfile.this);
                imagePickerDialog = new ImagePickerDialog.Builder(ActivityCreateProfile.this)
                        .setDismissTouchOutside(true)
                        .setTitleText("Выберите фото")
                        .setImagePickerListener(new ImagePickerDialog.ImagePickerListener() {
                            @Override
                            public void onPicked(final String filepath) {
                                Picasso.get().load(new File(filepath)).into(createProfilePhoto);
                            }

                            @Override
                            public void onDismissed() {

                            }
                        })
                        .setDone("Готово", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                imagePickerDialog.close();
                            }
                        }).build();
                imagePickerDialog.show();

            }
        });
    }


    @Override
    public void onBackPressed() {
        errorDialog = BaseDialogBuilder.buildFast(this,
                "Выйти?", "Вы действительно хотите выйти? Процесс создания аккаунта еще не завершен!",
                "Остаться",
                "Выйти",
                BaseDialogBuilder.Type.WARNING,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorDialog.closeDialog();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorDialog.closeDialog();
                        finish();
                    }
                });
        exitWarnDialog.showDialog();
    }
}
