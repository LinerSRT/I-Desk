package com.liner.i_desk.UI.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.UI.MainActivity;
import com.liner.i_desk.UI.SplashActivity;
import com.liner.i_desk.Utils.ImageUtils;
import com.liner.i_desk.Utils.TextUtils;
import com.liner.i_desk.Utils.Views.EditRegexTextView;
import com.liner.i_desk.Utils.Views.FirebaseActivity;
import com.liner.i_desk.Utils.Views.FirebaseFragment;
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

public class UserProfileFragment extends FirebaseFragment implements EditRegexTextView.IEditTextListener {
    private FirebaseActivity firebaseActivity;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseActivity = (FirebaseActivity) getActivity();
    }

    @Override
    public void onFirebaseChanged() {
        loadUserData();
    }

    @Override
    public void onUserObtained() {
        loadUserData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_fragment, container, false);
        profileUserName = view.findViewById(R.id.profileUserName);
        profileUserType = view.findViewById(R.id.profileUserType);
        profileUserAbout = view.findViewById(R.id.profileUserAbout);
        profileUserAdditionalInfo = view.findViewById(R.id.profileUserAdditionalInfo);
        profileUserPhoto2 = view.findViewById(R.id.profileUserPhoto2);
        profileUserChangePhoto = view.findViewById(R.id.profileUserChangePhoto);
        profileUserChangeAbout = view.findViewById(R.id.profileUserChangeAbout);
        profileUserChangeAdditionalInfo = view.findViewById(R.id.profileUserChangeAdditionalInfo);
        profileUserSignOut = view.findViewById(R.id.profileUserSignOut);
        profileAboutActionLayout = view.findViewById(R.id.profileAboutActionLayout);
        profileAdditionalActionLayout = view.findViewById(R.id.profileAdditionalActionLayout);
        profileAboutActionText = view.findViewById(R.id.profileAboutActionText);
        profileAdditionalActionText = view.findViewById(R.id.profileAdditionalActionText);
        profileAboutActionSubmit = view.findViewById(R.id.profileAboutActionSubmit);
        profileAdditionalActionSubmit = view.findViewById(R.id.profileAdditionalActionSubmit);
        profileActionBack = view.findViewById(R.id.profileActionBack);
        profileActionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.extendedViewPager.setCurrentItem(0);
            }
        });
        actionProgressDialog = new IndeterminateBottomSheetDialog.Builder(getActivity());
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
                        .start(getContext(), UserProfileFragment.this);


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
                FirebaseHelper.setUserValue(firebaseActivity.firebaseUser.getUid(), "userAboutText", profileAboutActionText.getText().toString().trim(), new FirebaseHelper.IFirebaseHelperListener() {
                    @Override
                    public void onSuccess(Object result) {
                        actionProgressDialog.close();
                        profileAboutActionText.setText(firebaseActivity.user.getUserAboutText());
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
                FirebaseHelper.setUserValue(firebaseActivity.firebaseUser.getUid(), "userAdditionalInformationText", profileAdditionalActionText.getText().toString().trim(), new FirebaseHelper.IFirebaseHelperListener() {
                    @Override
                    public void onSuccess(Object result) {
                        actionProgressDialog.close();
                        showUpdateDataDialog();
                        profileAdditionalActionText.setText(firebaseActivity.user.getUserAdditionalInformationText());
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

                final SimpleBottomSheetDialog.Builder confirmDialog = new SimpleBottomSheetDialog.Builder(getActivity());
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
                                firebaseActivity.firebaseAuth.signOut();
                                getContext().startActivity(new Intent(getContext(), SplashActivity.class));
                                getActivity().finish();
                            }
                        }).build();
                confirmDialog.show();
            }
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                        final ProgressBottomSheetDialog.Builder uploadDialog = new ProgressBottomSheetDialog.Builder(getActivity());
                        uploadDialog.setTitleText("Загрузка").setDialogText("Пожалуйста подождите, Ваше фото загружается").build();
                        uploadDialog.show();
                        FirebaseHelper.uploadByteArray(ImageUtils.getDrawableByteArray(bitmap), File.separator + firebaseActivity.user.getUserName() + "_" + TextUtils.generateRandomString(10) + ".jpg",
                                "user_images" + File.separator + firebaseActivity.firebaseUser.getUid() + File.separator + "profile_photos",
                                new FirebaseHelper.UploadListener() {
                                    @Override
                                    public void onFileUploading(int percent, long transferred, long total, String filename) {
                                        uploadDialog.getDialog().setProgress(percent);
                                    }

                                    @Override
                                    public void onFileUploaded(Request.FileData fileData) {
                                        uploadDialog.close();
                                        profileUserPhoto2.setImageBitmap(bitmap);
                                        FirebaseHelper.setUserValue(firebaseActivity.firebaseUser.getUid(), "userPhotoURL", fileData.getDownloadURL(), new FirebaseHelper.IFirebaseHelperListener() {
                                            @Override
                                            public void onSuccess(Object result) {

                                                final  SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(getActivity());
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
            Picasso.get().load(firebaseActivity.user.getUserPhotoURL()).into(profileUserPhoto2);
            profileUserName.setText(firebaseActivity.user.getUserName());
            switch (firebaseActivity.user.getUserAccountType()){
                case SERVICE:
                    profileUserType.setText("Исполнитель");
                    break;
                case CLIENT:
                    profileUserType.setText("Заявитель");
                    break;
            }
            profileUserName.setText(firebaseActivity.user.getUserName());
            profileUserAbout.setText(firebaseActivity.user.getUserAboutText());
            profileUserAdditionalInfo.setText(firebaseActivity.user.getUserAdditionalInformationText());
            profileAboutActionText.setText(firebaseActivity.user.getUserAboutText());
            profileAdditionalActionText.setText(firebaseActivity.user.getUserAdditionalInformationText());
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    private void showUploadPhotoErrorDialog() {
        final  SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(getActivity());
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
        final  SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(getActivity());
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
        final  SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(getActivity());
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
