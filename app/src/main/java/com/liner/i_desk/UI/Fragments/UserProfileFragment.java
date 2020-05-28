package com.liner.i_desk.UI.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.liner.i_desk.API.Data.User;
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
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends FirebaseFragment {
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
        loadUserData();

        profileUserChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionProgressDialog.create();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getContext(), UserProfileFragment.this);

            }
        });


        profileAboutActionText.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                profileAboutActionSubmit.setEnabled(true);
            }

            @Override
            public void onNotValid() {
                profileAboutActionSubmit.setEnabled(false);

            }
        });
        profileAdditionalActionText.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                profileAdditionalActionSubmit.setEnabled(true);
            }

            @Override
            public void onNotValid() {
                profileAdditionalActionSubmit.setEnabled(false);
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
                actionProgressDialog.create();
                FirebaseHelper.setUserValue(firebaseActivity.firebaseUser.getUid(), "userAboutText", profileAboutActionText.getText().toString().trim(), new FirebaseHelper.IFirebaseHelperListener() {
                    @Override
                    public void onSuccess(Object result) {
                        actionProgressDialog.dismiss(true);
                        final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(getActivity());
                        simpleBottomSheetDialog.setDialogTitle("");
                        simpleBottomSheetDialog.setDialogText("Данные успешно обновлены");
                        simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                        simpleBottomSheetDialog.create();
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

        profileAdditionalActionSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionProgressDialog.create();
                FirebaseHelper.setUserValue(firebaseActivity.firebaseUser.getUid(), "userAdditionalInformationText", profileAdditionalActionText.getText().toString().trim(), new FirebaseHelper.IFirebaseHelperListener() {
                    @Override
                    public void onSuccess(Object result) {
                        actionProgressDialog.dismiss(true);
                        final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(getActivity());
                        simpleBottomSheetDialog.setDialogTitle("");
                        simpleBottomSheetDialog.setDialogText("Данные успешно обновлены");
                        simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                        simpleBottomSheetDialog.create();
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



        profileUserSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SimpleBottomSheetDialog confirmDialog = new SimpleBottomSheetDialog(getActivity());
                confirmDialog.setDialogTitle("Выход");
                confirmDialog.setDialogText("Вы действительно хотите выйти из своего аккаунта?");
                confirmDialog.setDialogDoneBtnText("Выйти");
                confirmDialog.setDialogCancelBtnText("Отмена");
                confirmDialog.setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        confirmDialog.dismiss(true);
                    }
                });
                confirmDialog.setDoneClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firebaseActivity.firebaseAuth.signOut();
                        getContext().startActivity(new Intent(getContext(), SplashActivity.class));
                        getActivity().finish();
                    }
                });
                confirmDialog.create();
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
                                                    actionProgressDialog.dismiss(true);
                                                    final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(getActivity());
                                                    simpleBottomSheetDialog.setDialogTitle("");
                                                    simpleBottomSheetDialog.setDialogText("Фото было успешно обновлено");
                                                    simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                                    simpleBottomSheetDialog.create();
                                                }

                                                @Override
                                                public void onFail(String reason) {
                                                    actionProgressDialog.dismiss(true);
                                                    showUploadPhotoErrorDialog();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            actionProgressDialog.dismiss(true);
                                            showUploadPhotoErrorDialog();
                                        }
                                    });


                                } else {
                                    actionProgressDialog.dismiss(true);
                                    showUploadPhotoErrorDialog();
                                }
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
                Picasso.get().load(Objects.requireNonNull(CropImage.getActivityResult(data)).getUri()).into(profileUserPhoto);
                Picasso.get().load(Objects.requireNonNull(CropImage.getActivityResult(data)).getUri()).into(profileUserPhoto2);

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
    }

    private void showUploadPhotoErrorDialog() {
        final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(getActivity());
        simpleBottomSheetDialog.setDialogTitle("Внимание!");
        simpleBottomSheetDialog.setDialogText("Невозможно загрузить фото, попробуйте выбрать другое");
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
}
