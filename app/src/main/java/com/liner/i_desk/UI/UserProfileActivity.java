package com.liner.i_desk.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ImageUtils;
import com.liner.i_desk.Utils.TextUtils;
import com.liner.i_desk.Utils.Views.EditRegexTextView;
import com.liner.i_desk.Utils.Views.FirebaseActivity;
import com.liner.i_desk.Utils.Views.IndeterminateBottomSheetDialog;
import com.liner.i_desk.Utils.Views.ProgressBottomSheetDialog;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends FirebaseActivity implements EditRegexTextView.IEditTextListener{
    private TextView profileUserName;
    private TextView profileUserType;
    private TextView profileUserAbout;
    private TextView profileUserAdditionalInfo;
    private TextView profileActionBack;
    private CircleImageView profileUserPhoto2;
    private LinearLayout profileUserChangePhoto;
    private LinearLayout profileUserChangeAbout;
    private LinearLayout profileUserChangeAdditionalInfo;
    private TextView profileUserSignOut;
    private IndeterminateBottomSheetDialog.Builder actionProgressDialog;

    private ExpandableLayout profileAboutActionLayout;
    private ExpandableLayout profileAdditionalActionLayout;

    private EditRegexTextView profileAboutActionText;
    private EditRegexTextView profileAdditionalActionText;

    private Button profileAboutActionSubmit;
    private Button profileAdditionalActionSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profileUserName = findViewById(R.id.profileUserName);
        profileUserType = findViewById(R.id.profileUserType);
        profileUserAbout = findViewById(R.id.profileUserAbout);
        profileUserAdditionalInfo = findViewById(R.id.profileUserAdditionalInfo);
        profileUserPhoto2 = findViewById(R.id.profileUserPhoto2);
        profileUserChangePhoto = findViewById(R.id.profileUserChangePhoto);
        profileUserChangeAbout = findViewById(R.id.profileUserChangeAbout);
        profileUserChangeAdditionalInfo = findViewById(R.id.profileUserChangeAdditionalInfo);
        profileUserSignOut = findViewById(R.id.profileUserSignOut);
        profileAboutActionLayout = findViewById(R.id.profileAboutActionLayout);
        profileAdditionalActionLayout = findViewById(R.id.profileAdditionalActionLayout);
        profileAboutActionText = findViewById(R.id.profileAboutActionText);
        profileAdditionalActionText = findViewById(R.id.profileAdditionalActionText);
        profileAboutActionSubmit = findViewById(R.id.profileAboutActionSubmit);
        profileAdditionalActionSubmit = findViewById(R.id.profileAdditionalActionSubmit);
        profileActionBack = findViewById(R.id.profileActionBack);
        profileActionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
                finish();
            }
        });
        actionProgressDialog = new IndeterminateBottomSheetDialog.Builder(this);
        actionProgressDialog
                .setTitleText("Подождите")
                .setDialogText("Идет обработка")
                .build();
        profileAboutActionText.setTextListener(this);
        profileAdditionalActionText.setTextListener(this);
        loadUserData();

        profileUserChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionProgressDialog.show();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(UserProfileActivity.this);


            }
        });

        profileUserChangeAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profileAboutActionLayout.toggle(true);
                profileAdditionalActionLayout.collapse(true);
            }
        });

        profileUserChangeAdditionalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profileAdditionalActionLayout.toggle(true);
                profileAboutActionLayout.collapse(true);
            }
        });


        profileAboutActionSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionProgressDialog.show();
                FirebaseHelper.setUserValue(firebaseUser.getUid(), "userAboutText", profileAboutActionText.getText().toString().trim(), new FirebaseHelper.IFirebaseHelperListener() {
                    @Override
                    public void onSuccess(Object result) {
                        actionProgressDialog.close();
                        profileAboutActionText.setText(getCurrentUser().getUserAboutText());
                        showUpdateDataDialog();
                        profileAboutActionLayout.collapse(true);
                    }

                    @Override
                    public void onFail(String reason) {
                        actionProgressDialog.close();
                        showErrorDialog();
                    }
                });

            }
        });

        profileAdditionalActionSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionProgressDialog.show();
                FirebaseHelper.setUserValue(firebaseUser.getUid(), "userAdditionalInformationText", profileAdditionalActionText.getText().toString().trim(), new FirebaseHelper.IFirebaseHelperListener() {
                    @Override
                    public void onSuccess(Object result) {
                        actionProgressDialog.close();
                        showUpdateDataDialog();
                        profileAdditionalActionText.setText(getCurrentUser().getUserAdditionalInformationText());
                        profileAdditionalActionLayout.collapse(true);
                    }

                    @Override
                    public void onFail(String reason) {
                        actionProgressDialog.close();
                        showErrorDialog();
                    }
                });

            }
        });

        profileUserSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SimpleBottomSheetDialog.Builder confirmDialog = new SimpleBottomSheetDialog.Builder(UserProfileActivity.this);
                confirmDialog.setTitleText("Выход")
                        .setDialogText("Вы действительно хотите выйти из своего аккаунта?")
                        .setCancel("Отмена", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                confirmDialog.close();
                            }
                        })
                        .setDone("Выйти", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                confirmDialog.close();
                                firebaseAuth.signOut();
                                startActivity(new Intent(UserProfileActivity.this, SplashActivity.class));
                                finish();
                            }
                        }).build();
                confirmDialog.show();
            }
        });




    }

    @Override
    public void onSomethingChanged() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == -1) {
                if(data == null) {
                    actionProgressDialog.close();
                    showUploadPhotoErrorDialog();
                    return;
                }

                Picasso.get().load(Objects.requireNonNull(CropImage.getActivityResult(data)).getUri()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        actionProgressDialog.close();
                        final ProgressBottomSheetDialog.Builder uploadDialog = new ProgressBottomSheetDialog.Builder(UserProfileActivity.this);
                        uploadDialog.setTitleText("Загрузка").setDialogText("Пожалуйста подождите, Ваше фото загружается").build();
                        uploadDialog.show();
                        FirebaseHelper.uploadByteArray(ImageUtils.getDrawableByteArray(bitmap), File.separator + getCurrentUser().getUserName() + "_" + TextUtils.generateRandomString(10) + ".jpg",
                                "user_images" + File.separator + firebaseUser.getUid() + File.separator + "profile_photos",
                                new FirebaseHelper.UploadListener() {
                                    @Override
                                    public void onFileUploading(int percent, long transferred, long total, String filename) {
                                        uploadDialog.getDialog().setProgress(percent);
                                    }

                                    @Override
                                    public void onFileUploaded(Request.FileData fileData) {
                                        uploadDialog.close();
                                        profileUserPhoto2.setImageBitmap(bitmap);
                                        FirebaseHelper.setUserValue(firebaseUser.getUid(), "userPhotoURL", fileData.getDownloadURL(), new FirebaseHelper.IFirebaseHelperListener() {
                                            @Override
                                            public void onSuccess(Object result) {

                                                final  SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(UserProfileActivity.this);
                                                simpleDialog.setTitleText("Фото обновлено")
                                                        .setDialogText("Ваша фотография была успешно обновлена")
                                                        .setDone("Готово", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                simpleDialog.close();
                                                            }
                                                        }).build();
                                                simpleDialog.show();
                                            }

                                            @Override
                                            public void onFail(String reason) {
                                                uploadDialog.close();
                                                showUploadPhotoErrorDialog();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFileUploadFail(String reason) {

                                    }
                                });
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        showUploadPhotoErrorDialog();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showUploadPhotoErrorDialog();
            } else if (resultCode == 0) {
                actionProgressDialog.close();
            }
        }
    }

    private void loadUserData() {
        try {
            Picasso.get().load(getCurrentUser().getUserPhotoURL()).into(profileUserPhoto2);
            profileUserName.setText(getCurrentUser().getUserName());
            switch (getCurrentUser().getUserAccountType()){
                case SERVICE:
                    profileUserType.setText("Исполнитель");
                    break;
                case CLIENT:
                    profileUserType.setText("Заявитель");
                    break;
            }
            profileUserName.setText(getCurrentUser().getUserName());
            profileUserAbout.setText(getCurrentUser().getUserAboutText());
            profileUserAdditionalInfo.setText(getCurrentUser().getUserAdditionalInformationText());
            profileAboutActionText.setText(getCurrentUser().getUserAboutText());
            profileAdditionalActionText.setText(getCurrentUser().getUserAdditionalInformationText());
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    private void showUploadPhotoErrorDialog() {
        final  SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(UserProfileActivity.this);
        simpleDialog.setTitleText("Внимание!")
                .setDialogText("Невозможно загрузить фото, попробуйте выбрать другое")
                .setDone("Ок", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        simpleDialog.close();
                    }
                }).build();
        simpleDialog.show();
    }

    private void showUpdateDataDialog() {
        final  SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(UserProfileActivity.this);
        simpleDialog.setTitleText("Обновлено")
                .setDialogText("Ваши данные были успешно обновлены")
                .setDone("Ок", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        simpleDialog.close();
                    }
                }).build();
        simpleDialog.show();
    }

    private void showErrorDialog() {
        final  SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(UserProfileActivity.this);
        simpleDialog.setTitleText("Внимание!")
                .setDialogText("Произошла ошибка, попробуйте позже")
                .setDone("Ок", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        simpleDialog.close();
                    }
                }).build();
        simpleDialog.show();
    }

    @Override
    public void onValid(String result) {
        profileAdditionalActionSubmit.setEnabled(profileAdditionalActionText.isFieldCorrect());
        profileAboutActionSubmit.setEnabled(profileAboutActionText.isFieldCorrect());
    }

    @Override
    public void onNotValid() {
        profileAdditionalActionSubmit.setEnabled(profileAdditionalActionText.isFieldCorrect());
        profileAboutActionSubmit.setEnabled(profileAboutActionText.isFieldCorrect());
    }
}
