package com.liner.i_desk.UI.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.UI.SplashActivity;
import com.liner.i_desk.Utils.Animations.ViewAnimator;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends FirebaseFragment implements EditRegexTextView.IEditTextListener {
    private FirebaseActivity firebaseActivity;
    private ImageView profileUserPhoto;
    private TextView profileUserName;
    private TextView profileUserType;
    private TextView profileUserAbout;
    private TextView profileUserAdditionalInfo;
    private CircleImageView profileUserPhoto2;
    private LinearLayout profileUserChangePhoto;
    private LinearLayout profileUserChangeAbout;
    private LinearLayout profileUserChangeAdditionalInfo;
    private TextView profileUserSignOut;
    private IndeterminateBottomSheetDialog actionProgressDialog;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_fragment, container, false);
        profileUserPhoto = view.findViewById(R.id.profileUserPhoto);
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
        actionProgressDialog = new IndeterminateBottomSheetDialog(getActivity());
        actionProgressDialog.setDialogTitle("Подождите");
        actionProgressDialog.setDialogText("Идет обработка");
        profileAboutActionText.setTextListener(this);
        profileAdditionalActionText.setTextListener(this);
        loadUserData();

        profileUserChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {
                        actionProgressDialog.create();
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(getContext(), UserProfileFragment.this);
                    }
                });


            }
        });

        profileUserChangeAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {

                        profileAboutActionLayout.toggle(true);
                        profileAdditionalActionLayout.collapse(true);
                    }
                });
            }
        });

        profileUserChangeAdditionalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {

                        profileAdditionalActionLayout.toggle(true);
                        profileAboutActionLayout.collapse(true);
                    }
                });
            }
        });


        profileAboutActionSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {
                        actionProgressDialog.create();
                        FirebaseHelper.setUserValue(firebaseActivity.firebaseUser.getUid(), "userAboutText", profileAboutActionText.getText().toString().trim(), new FirebaseHelper.IFirebaseHelperListener() {
                            @Override
                            public void onSuccess(Object result) {
                                actionProgressDialog.dismiss(true);
                                profileAboutActionText.setText(firebaseActivity.user.getUserAboutText());
                                showUpdateDataDialog();
                                profileAboutActionLayout.collapse(true);
                            }

                            @Override
                            public void onFail(String reason) {
                                actionProgressDialog.dismiss(true);
                                showErrorDialog();
                            }
                        });
                    }
                });

            }
        });

        profileAdditionalActionSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {
                        actionProgressDialog.create();
                        FirebaseHelper.setUserValue(firebaseActivity.firebaseUser.getUid(), "userAdditionalInformationText", profileAdditionalActionText.getText().toString().trim(), new FirebaseHelper.IFirebaseHelperListener() {
                            @Override
                            public void onSuccess(Object result) {
                                actionProgressDialog.dismiss(true);
                                showUpdateDataDialog();
                                profileAdditionalActionText.setText(firebaseActivity.user.getUserAdditionalInformationText());
                                profileAdditionalActionLayout.collapse(true);
                            }

                            @Override
                            public void onFail(String reason) {
                                actionProgressDialog.dismiss(true);
                                showErrorDialog();
                            }
                        });
                    }
                });

            }
        });

        profileUserSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                    @Override
                    public void done() {
                        final SimpleBottomSheetDialog confirmDialog = new SimpleBottomSheetDialog(getActivity());
                        confirmDialog.setDialogTitle("Выход");
                        confirmDialog.setDialogText("Вы действительно хотите выйти из своего аккаунта?");
                        confirmDialog.setDialogDoneBtnText("Выйти");
                        confirmDialog.setDialogCancelBtnText("Отмена");
                        confirmDialog.setCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                                    @Override
                                    public void done() {

                                        confirmDialog.dismiss(true);
                                    }
                                });
                            }
                        });
                        confirmDialog.setDoneClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
                                    @Override
                                    public void done() {

                                        firebaseActivity.firebaseAuth.signOut();
                                        getContext().startActivity(new Intent(getContext(), SplashActivity.class));
                                        getActivity().finish();
                                    }
                                });
                            }
                        });
                        confirmDialog.create();
                    }
                });

            }
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == -1) {
                Picasso.get().load(Objects.requireNonNull(CropImage.getActivityResult(data)).getUri()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        actionProgressDialog.dismiss(true);
                        final ProgressBottomSheetDialog uploadDialog = new ProgressBottomSheetDialog(getActivity());
                        uploadDialog.setDialogTitle("Загрузка");
                        uploadDialog.setDialogText("Пожалуйста подождите, Ваше фото загружается");
                        uploadDialog.create();
                        final StorageReference path = firebaseActivity.storageReference.child("user_images").child(Objects.requireNonNull(firebaseActivity.firebaseAuth.getCurrentUser()).getUid()).child("profile_photos").child(firebaseActivity.user.getUserName() + "_" + TextUtils.generateRandomString(10) + ".jpg");
                        final UploadTask uploadTask = path.putBytes(ImageUtils.getDrawableByteArray(bitmap));
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FirebaseHelper.setUserValue(firebaseActivity.firebaseAuth.getCurrentUser().getUid(), "userPhotoURL", uri.toString(), new FirebaseHelper.IFirebaseHelperListener() {
                                                @Override
                                                public void onSuccess(Object result) {
                                                    uploadDialog.dismiss(true);
                                                    profileUserPhoto.setImageBitmap(bitmap);
                                                    profileUserPhoto2.setImageBitmap(bitmap);
                                                    final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(getActivity());
                                                    simpleBottomSheetDialog.setDialogTitle("Фото обновлено");
                                                    simpleBottomSheetDialog.setDialogText("Ваша фотография была успешно обновлена");
                                                    simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                                    simpleBottomSheetDialog.create();
                                                }

                                                @Override
                                                public void onFail(String reason) {
                                                    uploadDialog.dismiss(true);
                                                    showUploadPhotoErrorDialog();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            uploadDialog.dismiss(true);
                                            showUploadPhotoErrorDialog();
                                        }
                                    });


                                } else {
                                    uploadDialog.dismiss(true);
                                    showUploadPhotoErrorDialog();
                                }
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                uploadDialog.setProgress(Math.round(TextUtils.getPercent(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount())));
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
                actionProgressDialog.dismiss(true);
            }
        }
    }

    private void loadUserData() {
        Picasso.get().load(firebaseActivity.user.getUserPhotoURL()).into(profileUserPhoto);
        Picasso.get().load(firebaseActivity.user.getUserPhotoURL()).into(profileUserPhoto2);
        profileUserName.setText(firebaseActivity.user.getUserName());
        profileUserType.setText((firebaseActivity.user.isClientAccount()) ? "Заявитель" : "Исполнитель");
        profileUserName.setText(firebaseActivity.user.getUserName());
        profileUserAbout.setText(firebaseActivity.user.getUserAboutText());
        profileUserAdditionalInfo.setText(firebaseActivity.user.getUserAdditionalInformationText());
        profileAboutActionText.setText(firebaseActivity.user.getUserAboutText());
        profileAdditionalActionText.setText(firebaseActivity.user.getUserAdditionalInformationText());
    }

    private void showUploadPhotoErrorDialog() {
        final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(getActivity());
        simpleBottomSheetDialog.setDialogTitle("Внимание!");
        simpleBottomSheetDialog.setDialogText("Невозможно загрузить фото, попробуйте выбрать другое");
        simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
        simpleBottomSheetDialog.create();
    }

    private void showUpdateDataDialog() {
        final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(getActivity());
        simpleBottomSheetDialog.setDialogTitle("Обновлено");
        simpleBottomSheetDialog.setDialogText("Ваши данные были успешно обновлены");
        simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
        simpleBottomSheetDialog.create();
    }

    private void showErrorDialog() {
        final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(getActivity());
        simpleBottomSheetDialog.setDialogTitle("Внимание!");
        simpleBottomSheetDialog.setDialogText("Произошла ошибка, попробуйте позже");
        simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
        simpleBottomSheetDialog.create();
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
