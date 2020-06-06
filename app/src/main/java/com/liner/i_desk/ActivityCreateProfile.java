package com.liner.i_desk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.liner.i_desk.UI.MainActivity;
import com.liner.i_desk.Utils.Firebase.FileObject;
import com.liner.i_desk.Utils.Firebase.FireActivity;
import com.liner.i_desk.Utils.Firebase.Firebase;
import com.liner.i_desk.Utils.Firebase.FirebaseValue;
import com.liner.i_desk.Utils.Firebase.Time;
import com.liner.i_desk.Utils.Firebase.UserObject;
import com.liner.i_desk.Utils.ImageUtils;
import com.liner.i_desk.Utils.TextUtils;
import com.liner.i_desk.Utils.Views.ImagePickerBottomSheetDialog;
import com.liner.i_desk.Utils.Views.IndeterminateBottomSheetDialog;
import com.liner.i_desk.Utils.Views.ProgressBottomSheetDialog;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;


public class ActivityCreateProfile extends FireActivity {
    private TextFieldBoxes createProfileNickNameFiledBox;
    private TextFieldBoxes createProfileAboutFiledBox;
    private CircleImageView createProfilePhoto;
    private Button createProfileChoosePhoto;
    private Button createProfileNextStep;
    private SimpleBottomSheetDialog.Builder exitWarnDialog;
    private UserObject userObject;

    private String userNickName = "", userAbout = "", photoPath = null;
    private SimpleBottomSheetDialog.Builder errorDialog;
    private SimpleBottomSheetDialog.Builder finishDialog;
    private IndeterminateBottomSheetDialog.Builder progressBottomSheetDialog;
    private ProgressBottomSheetDialog.Builder uploadPhotoDialog;
    private ImagePickerBottomSheetDialog.Builder imagePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile_layout);
        if (getIntent().getSerializableExtra("userObject") == null)
            signOut();
        userObject = (UserObject) getIntent().getSerializableExtra("userObject");
        createProfileNickNameFiledBox = findViewById(R.id.createProfileNickNameFiledBox);
        createProfileAboutFiledBox = findViewById(R.id.createProfileAboutFiledBox);
        createProfileChoosePhoto = findViewById(R.id.createProfileChoosePhoto);
        createProfileNextStep = findViewById(R.id.createProfileNextStep);
        createProfilePhoto = findViewById(R.id.createProfilePhoto);
        createProfileNickNameFiledBox = findViewById(R.id.createProfileNickNameFiledBox);
        createProfileNickNameFiledBox = findViewById(R.id.createProfileNickNameFiledBox);
        createProfileNickNameFiledBox = findViewById(R.id.createProfileNickNameFiledBox);
        createProfileNickNameFiledBox = findViewById(R.id.createProfileNickNameFiledBox);
        progressBottomSheetDialog = new IndeterminateBottomSheetDialog.Builder(this)
                .setDialogText("Завершение регистрации").setTitleText("Подождите...").build();
        errorDialog = new SimpleBottomSheetDialog.Builder(this)
                .setDismissTouchOutside(false)
                .setTitleText("Ошибка")
                .setDialogText("Что-то пошло не так. Попробуйте еще раз")
                .setDone("Ок", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorDialog.close();
                    }
                }).build();
        finishDialog = new SimpleBottomSheetDialog.Builder(this)
                .setDismissTouchOutside(false)
                .setTitleText("Готово")
                .setDialogText((Constants.USERS_REQUIRE_EMAIL_VERIFICATION)?"Регистрация завершена успешно. Подтвердите свой Email для дальнейшей работы":"Регистрация завершена успешно. Теперь вы можете использовать свой аккаунт для входа")
                .setDone("Ок", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorDialog.close();
                        if(Constants.USERS_REQUIRE_EMAIL_VERIFICATION) {
                            signOut();
                        } else {
                            startActivity(new Intent(ActivityCreateProfile.this, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }

                    }
                }).build();
        uploadPhotoDialog = new ProgressBottomSheetDialog.Builder(this)
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
                            errorDialog.show();
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
                            finishDialog.show();

                        }
                    });
                }
            }
        });
        createProfileChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextUtils.hideKeyboard(ActivityCreateProfile.this);
                imagePickerDialog = new ImagePickerBottomSheetDialog.Builder(ActivityCreateProfile.this)
                        .setDismissTouchOutside(true)
                        .setTitleText("Выберите фото")
                        .setImagePickerListener(new ImagePickerBottomSheetDialog.ImagePickerListener() {
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
        exitWarnDialog = new SimpleBottomSheetDialog.Builder(this)
                .setTitleText("Выйти?")
                .setDialogText("Вы действительно хотите выйти? Процесс создания аккаунта еще не завершен. Выход из приложения на этом этапе анулирует процесс создания нового аккаунта!")
                .setCancel("Выйти", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                })
                .setDone("Остаться", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exitWarnDialog.close();
                    }
                }).build();
        exitWarnDialog.show();
    }


}
