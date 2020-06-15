package com.liner.i_desk.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.FireActivity;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.MessageObject;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.Storage.FirebaseUploadTask;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.i_desk.R;
import com.liner.utils.Time;
import com.liner.utils.ViewUtils;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.BaseDialogSelectionItem;
import com.liner.views.BlurredImageView;
import com.liner.views.YSTextView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment implements ImagePickerCallback {
    public DatabaseListener databaseListener;
    public ImageView userPhoto;
    public YSTextView userName;
    public YSTextView userStatus;
    public YSTextView userRegisterAt;
    public YSTextView userMessageCount;
    public YSTextView userRequestCount;
    public YSTextView userEmail;
    public YSTextView userAboutText;
    public UserObject currentUser;
    public MaterialButton editUserProfile;
    public MaterialButton signOut;
    public BaseDialog selectionDialog;
    public BaseDialog mediaSelectionDialog;
    public BaseDialog uploadingDialog;
    public BaseDialog errorDialog;
    public ImagePicker imagePicker;
    public CameraImagePicker cameraImagePicker;
    public String pickedPhotoPath;

    private String userID;
    private boolean forceHideUserProfileActions = false;
    public UserProfileFragment(String userID) {
        this.userID = userID;
    }
    public UserProfileFragment(String userID, boolean forceHideUserProfileActions) {
        this.userID = userID;
        this.forceHideUserProfileActions = forceHideUserProfileActions;
    }

    @Override
    public void onStart() {
        super.onStart();
        imagePicker = new ImagePicker(getActivity());
        cameraImagePicker = new CameraImagePicker(getActivity());
        imagePicker.setImagePickerCallback(this);
        cameraImagePicker.setImagePickerCallback(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        userPhoto = view.findViewById(R.id.userPhoto);
        userName = view.findViewById(R.id.userName);
        userStatus = view.findViewById(R.id.userStatus);
        userRegisterAt = view.findViewById(R.id.userRegisterAt);
        userMessageCount = view.findViewById(R.id.userMessageCount);
        userRequestCount = view.findViewById(R.id.userRequestCount);
        userEmail = view.findViewById(R.id.userEmail);
        userAboutText = view.findViewById(R.id.userAboutText);
        editUserProfile = view.findViewById(R.id.editUserProfile);
        signOut = view.findViewById(R.id.signOut);
        if(userID.equals(Firebase.getUserUID())){
            if(forceHideUserProfileActions){
                editUserProfile.setVisibility(View.GONE);
                signOut.setVisibility(View.GONE);
            } else {
                editUserProfile.setVisibility(View.VISIBLE);
                signOut.setVisibility(View.VISIBLE);
            }
            uploadingDialog = BaseDialogBuilder.buildFast(
                    getActivity(),
                    "Загрузка фото",
                    "Ваше фото загружается на сервер, подождите",
                    null,
                    null,
                    BaseDialogBuilder.Type.PROGRESS,
                    null,
                    null
            );
            errorDialog = BaseDialogBuilder.buildFast(getActivity(),
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

            List<BaseDialogSelectionItem> mediaSelectionItems = new ArrayList<>();
            mediaSelectionItems.add(new BaseDialogSelectionItem(R.drawable.image_icon, "Выбрать изображение", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaSelectionDialog.closeDialog();
                    imagePicker.pickImage();

                }
            }));
            mediaSelectionItems.add(new BaseDialogSelectionItem(R.drawable.camera_icon, "Сделать фото", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaSelectionDialog.closeDialog();
                    pickedPhotoPath = cameraImagePicker.pickImage();

                }
            }));
            mediaSelectionDialog = new BaseDialogBuilder(getActivity())
                    .setDialogTitle("Выберите вариант")
                    .setDialogText("")
                    .setSelectionList(mediaSelectionItems)
                    .setDismissOnTouchOutside(true)
                    .setDialogType(BaseDialogBuilder.Type.SINGLE_CHOOSE)
                    .build();
            List<BaseDialogSelectionItem> selectionItems = new ArrayList<>();
            selectionItems.add(new BaseDialogSelectionItem(R.drawable.user_icon, "Никнейм", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectionDialog.closeDialog();
                    new BaseDialogBuilder(getActivity())
                            .setDialogTitle("Никнейм")
                            .setDialogType(BaseDialogBuilder.Type.EDIT)
                            .setEditHelpText("Укажите ваш новый никнем")
                            .setEditMinCharacters(6)
                            .setEditListener(new BaseDialog.BaseDialogEditListener() {
                                @Override
                                public void onEditFinished(String text) {
                                    for(RequestObject requestObject:databaseListener.getRequests()){
                                        if(requestObject.getRequestAcceptorID() != null && requestObject.getRequestAcceptorID().equals(Firebase.getUserUID())){
                                            requestObject.setRequestAcceptorName(text);
                                        }
                                        if(requestObject.getRequestCreatorID().equals(Firebase.getUserUID())){
                                            requestObject.setRequestCreatorName(text);
                                        }
                                        FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
                                    }
                                    FirebaseValue.setUserValue(Firebase.getUserUID(), "userName", text);
                                }
                            }).build().showDialog();
                }
            }));
            selectionItems.add(new BaseDialogSelectionItem(R.drawable.status_icon, "Статус", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectionDialog.closeDialog();
                    new BaseDialogBuilder(getActivity())
                            .setDialogTitle("Статус")
                            .setDialogType(BaseDialogBuilder.Type.EDIT)
                            .setEditHelpText("Укажите ваш новый статус")
                            .setEditMinCharacters(4)
                            .setEditListener(new BaseDialog.BaseDialogEditListener() {
                                @Override
                                public void onEditFinished(String text) {
                                    FirebaseValue.setUserValue(Firebase.getUserUID(), "userStatusText", text);
                                }
                            }).build().showDialog();
                }
            }));
            selectionItems.add(new BaseDialogSelectionItem(R.drawable.about_icon, "О себе", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectionDialog.closeDialog();
                    new BaseDialogBuilder(getActivity())
                            .setDialogTitle("Информация о себе")
                            .setDialogType(BaseDialogBuilder.Type.EDIT)
                            .setEditHelpText("Укажите кратко информацию о себе")
                            .setEditMinCharacters(1)
                            .setEditListener(new BaseDialog.BaseDialogEditListener() {
                                @Override
                                public void onEditFinished(String text) {
                                    FirebaseValue.setUserValue(Firebase.getUserUID(), "userAboutText", text);
                                }
                            }).build().showDialog();
                }
            }));
            selectionItems.add(new BaseDialogSelectionItem(R.drawable.camera_icon, "Фото профиля", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectionDialog.closeDialog();
                    mediaSelectionDialog.showDialog();
                }
            }));
            selectionDialog = new BaseDialogBuilder(getActivity())
                    .setDialogTitle("Выберите вариант")
                    .setDialogText("Выберите что вы хотите изменить")
                    .setSelectionList(selectionItems)
                    .setDismissOnTouchOutside(true)
                    .setDialogType(BaseDialogBuilder.Type.SINGLE_CHOOSE)
                    .build();
            editUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectionDialog.showDialog();
                }
            });
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FireActivity activity = (FireActivity) getActivity();
                    activity.signOut();
                }
            });

        } else {
            editUserProfile.setVisibility(View.GONE);
            signOut.setVisibility(View.GONE);
        }
        userRequestCount.setText("0");
        userMessageCount.setText("0");
        databaseListener = new DatabaseListener() {
            @Override
            public void onUserAdded(UserObject userObject, int position) {
                super.onUserAdded(userObject, position);
                if (userObject.getUserID().equals(userID)) {
                    currentUser = userObject;
                    updateUserData();
                }
            }

            @Override
            public void onUserChanged(UserObject userObject, int position) {
                super.onUserChanged(userObject, position);
                if (userObject.getUserID().equals(userID)) {
                    currentUser = userObject;
                    updateUserData();
                }
            }

            @Override
            public void onRequestAdded(RequestObject requestObject, int position) {
                super.onRequestAdded(requestObject, position);
                if (requestObject.getRequestCreatorID().equals(Firebase.getUserUID()))
                    userRequestCount.setText(String.valueOf(Integer.parseInt(userRequestCount.getText().toString().trim()) + 1));
            }

            @Override
            public void onMessageAdded(MessageObject messageObject, int position) {
                super.onMessageAdded(messageObject, position);
                if (messageObject.getCreatorID().equals(Firebase.getUserUID()))
                    userMessageCount.setText(String.valueOf(Integer.parseInt(userMessageCount.getText().toString().trim()) + 1));
            }
        };

        databaseListener.startListening();
        return view;
    }

    @Override
    public void onDetach() {
        if (databaseListener.isListening())
            databaseListener.stopListening();
        super.onDetach();
    }

    private void updateUserData() {
        Picasso.get().load(currentUser.getUserProfilePhotoURL()).resize(ViewUtils.dpToPx(250), ViewUtils.dpToPx(250)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                userPhoto.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                userPhoto.setImageResource(R.drawable.material);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        userName.setText(currentUser.getUserName());
        userStatus.setText(currentUser.getUserStatusText());
        userRegisterAt.setText(Time.getHumanReadableTime(currentUser.getUserRegisteredAt(), "dd.MM.yyyy"));
        userEmail.setText(currentUser.getUserEmail());
        userAboutText.setText(currentUser.getUserAboutText());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (imagePicker == null) {
                imagePicker = new ImagePicker(getActivity());
                imagePicker.setImagePickerCallback(this);
            }
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                imagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (pickedPhotoPath != null)
                    new FirebaseUploadTask().with(getActivity())
                            .file(new File(pickedPhotoPath))
                            .userUID(Firebase.getUserUID())
                            .uploadFile(new TaskListener<FileObject>() {
                                @Override
                                public void onStart(String fileUID) {
                                    uploadingDialog.showDialog();
                                }

                                @Override
                                public void onProgress(long transferredBytes, long totalBytes) {
                                    uploadingDialog.getProgressBar().setProgress(Math.round((((float) transferredBytes / (float) totalBytes) * 100)));
                                }

                                @Override
                                public void onFinish(FileObject result, String fileUID) {
                                    Picasso.get().load(new File(pickedPhotoPath)).resize(ViewUtils.dpToPx(250), ViewUtils.dpToPx(250)).into(userPhoto);
                                    for(RequestObject requestObject:databaseListener.getRequests()){
                                        if(requestObject.getRequestAcceptorID() != null && requestObject.getRequestAcceptorID().equals(Firebase.getUserUID())){
                                            requestObject.setRequestAcceptorPhotoURL(result.getFileURL());
                                        }
                                        if(requestObject.getRequestCreatorID().equals(Firebase.getUserUID())){
                                            requestObject.setRequestCreatorPhotoURL(result.getFileURL());
                                        }
                                        FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
                                    }

                                    FirebaseValue.setUserValue(Firebase.getUserUID(), "userProfilePhotoURL", result.getFileURL());
                                    uploadingDialog.closeDialog();
                                }

                                @Override
                                public void onFailed(Exception reason) {
                                    uploadingDialog.closeDialog();
                                    errorDialog.showDialog();
                                }
                            });
            }
        } else {
            errorDialog.showDialog();
        }
    }

    @Override
    public void onImagesChosen(final List<ChosenImage> list) {
        new FirebaseUploadTask().with(getActivity())
                .file(new File(list.get(0).getOriginalPath()))
                .userUID(Firebase.getUserUID())
                .uploadFile(new TaskListener<FileObject>() {
                    @Override
                    public void onStart(String fileUID) {
                        uploadingDialog.showDialog();
                    }

                    @Override
                    public void onProgress(long transferredBytes, long totalBytes) {
                        uploadingDialog.getProgressBar().setProgress(Math.round((((float) transferredBytes / (float) totalBytes) * 100)));
                    }

                    @Override
                    public void onFinish(FileObject result, String fileUID) {
                        Picasso.get().load(new File(list.get(0).getOriginalPath())).resize(ViewUtils.dpToPx(250), ViewUtils.dpToPx(250)).into(userPhoto);
                        FirebaseValue.setUserValue(Firebase.getUserUID(), "userProfilePhotoURL", result.getFileURL());
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
        errorDialog.showDialog();
    }

}
