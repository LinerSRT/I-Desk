package com.liner.i_desk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.MediaPicker;
import com.kbeanie.multipicker.api.callbacks.MediaPickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.entity.ChosenVideo;
import com.liner.i_desk.Firebase.Storage.FirebaseUploadTask;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.FireActivity;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.utils.ImageUtils;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.utils.ViewUtils;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

import static com.kbeanie.multipicker.api.Picker.PICK_MEDIA;


public class ActivityCreateProfile extends FireActivity {
    private TextFieldBoxes createProfileNickNameFiledBox;
    private CircleImageView createProfilePhoto;
    private UserObject userObject;

    private String userNickName = "", userAbout = "";


    private BaseDialog errorDialog;
    private BaseDialog finishDialog;
    private BaseDialog exitWarnDialog;
    private BaseDialog processingDialog;
    private BaseDialog uploadingDialog;
    private MediaPicker mediaPicker;

    private BaseDialog accountTypeSelectionDialog;
    private String[] accountNames = new String[]{"Заявитель","Исполнитель"};
    private UserObject.UserType accountType = UserObject.UserType.CLIENT;

    private String userPhotoURL;

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

        accountTypeSelectionDialog = new BaseDialogBuilder(this)
                .setDialogTitle("Тип аккаунта")
                .setDialogText("Выберите тип вашего аккаунта")
                .setDialogType(BaseDialogBuilder.Type.SINGLE_CHOOSE)
                .setSelectionList(accountNames)
                .setSelectionListener(new BaseDialog.BaseDialogSelectionListener() {
                    @Override
                    public void onItemClick(int position) {
                        accountType = (position == 0)? UserObject.UserType.CLIENT: UserObject.UserType.SERVICE;
                        FirebaseValue.getUser(Firebase.getUserUID(), new FirebaseValue.ValueListener() {
                            @Override
                            public void onSuccess(Object object, DatabaseReference databaseReference) {
                                UserObject userObject = (UserObject) object;
                                if (!TextUtils.isTextEmpty(userNickName))
                                    userObject.setUserAboutText(userAbout);
                                userObject.setUserType(accountType);
                                userObject.setUserName(userNickName);
                                userObject.setUserProfilePhotoURL(userPhotoURL);
                                userObject.setUserRegisteredAt(Time.getTime());
                                userObject.setUserLastOnlineAt(Time.getTime());
                                userObject.setUserStatus(UserObject.UserStatus.ONLINE);
                                userObject.setUserRegisterFinished(true);
                                FirebaseValue.setUser(Firebase.getUserUID(), userObject);
                                processingDialog.closeDialog();
                                finishDialog.showDialog();
                            }

                            @Override
                            public void onFail(String errorMessage) {
                                processingDialog.closeDialog();
                                errorDialog.showDialog();
                            }
                        });
                    }
                })
                .build();

        processingDialog = BaseDialogBuilder.buildFast(
                this,
                "Завершение регистрации",
                "Подождите...",
                null,
                null,
                BaseDialogBuilder.Type.INDETERMINATE,
                null,
                null
        );
        uploadingDialog = BaseDialogBuilder.buildFast(
                this,
                "Загрузка фото",
                "Ваше фото загружается на сервер, подождите",
                null,
                null,
                BaseDialogBuilder.Type.PROGRESS,
                null,
                null
        );
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



        mediaPicker = new MediaPicker(this);
        mediaPicker.setMediaPickerCallback(new MediaPickerCallback() {
            @Override
            public void onMediaChosen(final List<ChosenImage> list, List<ChosenVideo> list1) {
                new FirebaseUploadTask().with(ActivityCreateProfile.this)
                        .file(new File(list.get(0).getOriginalPath()))
                        .userUID(Firebase.getUserUID())
                        .uploadFile(new TaskListener<FileObject>() {
                            @Override
                            public void onStart(String fileUID) {
                                uploadingDialog.showDialog();
                            }

                            @Override
                            public void onProgress(long transferredBytes, long totalBytes) {
                                uploadingDialog.getProgressBar().setProgress(Math.round((((float)transferredBytes/(float)totalBytes)*100)));
                            }

                            @Override
                            public void onFinish(FileObject result, String fileUID) {
                                Picasso.get().load(new File(list.get(0).getOriginalPath())).into(createProfilePhoto);
                                userPhotoURL = result.getFileURL();
                                uploadingDialog.closeDialog();
                            }

                            @Override
                            public void onFailed(Exception reason) {
                                uploadingDialog.closeDialog();
                                errorDialog.showDialog();
                            }
                        });
            }

            @Override
            public void onError(String s) {

            }
        });
        mediaPicker.onError("Выберите одно изображение");


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
                    processingDialog.showDialog();
                    accountTypeSelectionDialog.showDialog();
                }
            }
        });
        createProfileChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextUtils.hideKeyboard(ActivityCreateProfile.this);
                mediaPicker.pickMedia();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_MEDIA){
            if(resultCode == RESULT_OK){
                mediaPicker.submit(data);
            } else if(resultCode == RESULT_CANCELED){
                errorDialog.setDialogTextText("Вы не выбрали фото");
                errorDialog.setDialogType(BaseDialogBuilder.Type.WARNING);
                errorDialog.showDialog();
            } else {
                errorDialog.setDialogTextText("Что-то пошло не так. Попробуйте еще раз");
                errorDialog.setDialogType(BaseDialogBuilder.Type.ERROR);
                errorDialog.showDialog();
            }
        }
    }
}
