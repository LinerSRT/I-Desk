package com.liner.i_desk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faltenreich.skeletonlayout.SkeletonLayout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.liner.bottomdialogs.BaseDialog;
import com.liner.bottomdialogs.BaseDialogBuilder;
import com.liner.bottomdialogs.ImagePickerDialog;
import com.liner.bottomdialogs.ProgressDialog;
import com.liner.i_desk.Adapters.ProfileInformationAdapter;
import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.FireActivity;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.MessageObject;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.utils.FileUtils;
import com.liner.utils.ImageUtils;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.views.BlurredImageView;
import com.liner.views.ExpandLayout;
import com.liner.views.FileListLayoutView;
import com.liner.views.YSTextView;
import com.roacult.backdrop.BackdropLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import spencerstudios.com.bungeelib.Bungee;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class ActivityUserProfile extends FireActivity {
    private BackdropLayout backdropLayout;
    private LinearLayout profileLayoutToRequestList;
    private LinearLayout profileLayoutToAddRequest;
    private LinearLayout userAboutLayout;
    private CircleImageView profileLayoutUserPhoto;
    private YSTextView profileLayoutUserName;
    private YSTextView profileLayoutUserType;
    private YSTextView profileLayoutUserAbout;
    private ExpandLayout profileLayoutUserAboutExpandLayout;
    private TextFieldBoxes profileLayoutUserAboutEditBox;
    private ExtendedEditText profileLayoutUserAboutEditText;
    private RecyclerView profileLayoutInformationRecycler;

    private ProfileInformationAdapter informationAdapter;
    private MaterialButton profileLayoutSignOut;
    private SkeletonLayout includedFront;

    private ImagePickerDialog.Builder imagePickerDialog;
    private ProgressDialog.Builder uploadPhotoDialog;
    private BlurredImageView profileLayoutBlurredUserPhoto;


    private DatabaseListener databaseListener;
    private List<ProfileInformationAdapter.InformationHolder> informationHolderList = new ArrayList<>();

    private String userAboutText = "";
    private int messageCreated = 0;
    private int requestsCreated = 0;
    private long storageUsed = 0;
    private UserObject user;


    private int loadRetryCount = 0;
    private Handler loadHandler = new Handler();
    private Runnable onLoadFinished = new Runnable() {
        @Override
        public void run() {
            if (user == null) {
                loadRetryCount++;
                if (loadRetryCount > 5) {

                    BaseDialog baseDialog = BaseDialogBuilder.buildFast(ActivityUserProfile.this,
                            "Упс...",
                            "Похоже что загрузка продолжается слишком долго, попробовать загрузить еще раз?",
                            "Нет",
                            "Попробовать",
                            BaseDialogBuilder.Type.WARNING,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    recreate();
                                }
                            });
                    baseDialog.showDialog();
                } else {
                    includedFront.showSkeleton();
                    loadHandler.postDelayed(onLoadFinished, 1500);
                }
                return;
            }
            updateUserData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_layout);
        backdropLayout = findViewById(R.id.profileBackdropLayout);
        includedFront = findViewById(R.id.includedFront);
        profileLayoutToRequestList = findViewById(R.id.profileLayoutToRequestList);
        profileLayoutBlurredUserPhoto = findViewById(R.id.profileLayoutBlurredUserPhoto);
        profileLayoutToAddRequest = findViewById(R.id.profileLayoutToAddRequest);
        userAboutLayout = findViewById(R.id.userAboutLayout);
        profileLayoutInformationRecycler = findViewById(R.id.profileLayoutInformationRecycler);
        profileLayoutUserPhoto = findViewById(R.id.profileLayoutUserPhoto);
        profileLayoutUserName = findViewById(R.id.profileLayoutUserName);
        profileLayoutUserType = findViewById(R.id.profileLayoutUserType);
        profileLayoutUserAbout = findViewById(R.id.profileLayoutUserAbout);
        profileLayoutUserAboutExpandLayout = findViewById(R.id.profileLayoutUserAboutExpandLayout);
        profileLayoutUserAboutEditBox = findViewById(R.id.profileLayoutUserAboutEditBox);
        profileLayoutUserAboutEditText = findViewById(R.id.profileLayoutUserAboutEditText);
        profileLayoutSignOut = findViewById(R.id.profileLayoutSignOut);






        informationAdapter = new ProfileInformationAdapter(informationHolderList);
        profileLayoutInformationRecycler.setLayoutManager(new LinearLayoutManager(ActivityUserProfile.this));
        profileLayoutInformationRecycler.setHasFixedSize(true);
        profileLayoutInformationRecycler.setAdapter(informationAdapter);
        profileLayoutUserAboutExpandLayout.setVisibility(View.VISIBLE);






        databaseListener = new DatabaseListener() {
            @Override
            public void onUserAdded(UserObject userObject) {
                super.onUserAdded(userObject);
                if (userObject.getUserID().equals(Firebase.getUserUID())) {
                    user = userObject;
                    updateUserData();
                }
            }

            @Override
            public void onUserChanged(UserObject userObject) {
                super.onUserChanged(userObject);
                if (userObject.getUserID().equals(Firebase.getUserUID())) {
                    user = userObject;
                    updateUserData();
                }
            }

            @Override
            public void onMessageAdded(MessageObject messageObject) {
                super.onMessageAdded(messageObject);
                if (messageObject.getCreatorID().equals(Firebase.getUserUID())) {
                    messageCreated++;
                    updateUserData();
                }
            }

            @Override
            public void onRequestAdded(RequestObject requestObject) {
                super.onRequestAdded(requestObject);
                if (requestObject.getRequestCreatorID().equals(Firebase.getUserUID())) {
                    requestsCreated++;
                    updateUserData();
                }

            }

            @Override
            public void onFileAdded(FileObject fileObject) {
                super.onFileAdded(fileObject);
                if (fileObject.getFileCreatorID().equals(Firebase.getUserUID())) {
                    storageUsed += fileObject.getFileSizeInBytes();
                    //fileListLayoutView.addFile(new FileListLayoutView.FileItem(fileObject.getFileID(), fileObject.getFileName(), fileObject.getFileType(), fileObject.getFileSizeInBytes()));
                    updateUserData();

                }

            }

            @Override
            public void onFileDeleted(FileObject fileObject) {
                super.onFileDeleted(fileObject);
                if (fileObject.getFileCreatorID().equals(Firebase.getUserUID())) {
                    storageUsed -= fileObject.getFileSizeInBytes();
                    //fileListLayoutView.removeFile(fileObject.getFileName());
                    updateUserData();
                }
            }
        };
        databaseListener.startListening();
        includedFront.showSkeleton();
        loadHandler.post(onLoadFinished);
        uploadPhotoDialog = new ProgressDialog.Builder(this)
                .setDismissTouchOutside(false)
                .setTitleText("Подождите")
                .setDialogText("Ваше фото загружается")
                .build();
        profileLayoutSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        userAboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileLayoutUserAboutExpandLayout.toggle(true);
            }
        });

        profileLayoutUserAboutEditBox.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(final String theNewText, boolean isError) {
                userAboutText = theNewText;
                if (theNewText.length() > 0 && theNewText.length() < 150) {
                    profileLayoutUserAboutEditBox.removeError();
                    profileLayoutUserAboutEditBox.getEndIconImageButton().setColorFilter(getResources().getColor(R.color.color_primary_dark), PorterDuff.Mode.SRC_IN);
                }
            }
        });

        profileLayoutUserAboutEditBox.getEndIconImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userAboutText.length() <= 0) {
                    profileLayoutUserAboutEditBox.getEndIconImageButton().setColorFilter(getResources().getColor(R.color.color_disable), PorterDuff.Mode.SRC_IN);
                    profileLayoutUserAboutEditBox.setError("Поле для ввода пустое", true);
                }
                if (!profileLayoutUserAboutEditBox.isOnError()) {
                    FirebaseValue.setUserValue(Firebase.getUserUID(), "userAboutText", userAboutText);
                    profileLayoutUserAboutEditText.setText("");
                    profileLayoutUserAboutExpandLayout.collapse(true);
                    profileLayoutUserAboutEditBox.getEndIconImageButton().setColorFilter(getResources().getColor(R.color.color_disable), PorterDuff.Mode.SRC_IN);
                    TextUtils.hideKeyboard(ActivityUserProfile.this);
                }
            }
        });


        profileLayoutToRequestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backdropLayout.close();
                startActivity(new Intent(ActivityUserProfile.this, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
                finish();
            }
        });


        profileLayoutToAddRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //backdropLayout.close();
                //startActivity(new Intent(ActivityUserProfile.this, ActivityMain.class));
                //finish();
            }
        });

        profileLayoutUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickerDialog = new ImagePickerDialog.Builder(ActivityUserProfile.this)
                        .setDismissTouchOutside(false)
                        .setTitleText("Выберите фото")
                        .setImagePickerListener(new ImagePickerDialog.ImagePickerListener() {
                            @Override
                            public void onPicked(final String filepath) {
                                Picasso.get().load(new File(filepath)).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        profileLayoutUserPhoto.setImageBitmap(bitmap);
                                        profileLayoutBlurredUserPhoto.setBlurBitmap(bitmap, 0.4f, 7);
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                            }

                            @Override
                            public void onDismissed() {

                            }
                        })
                        .setDone("Готово", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                imagePickerDialog.close();
                                FirebaseValue.getUser(Firebase.getUserUID(), new FirebaseValue.ValueListener() {
                                    @Override
                                    public void onSuccess(Object object, DatabaseReference databaseReference) {
                                        final UserObject userObject = (UserObject) object;
                                        FirebaseValue.uploadByteArray(ImageUtils.getDrawableByteArray(profileLayoutUserPhoto.getDrawable()), new File(imagePickerDialog.getDialog().getImagePath()).getName(), new FirebaseValue.FileUploadListener() {
                                            @Override
                                            public void onStart() {
                                                uploadPhotoDialog.show();
                                            }

                                            @Override
                                            public void onProgress(long transferredBytes, long totalBytes) {
                                                uploadPhotoDialog.getDialog().setProgress(Math.round((Float.parseFloat(String.valueOf(transferredBytes)) / Float.parseFloat(String.valueOf(totalBytes))) * 100));
                                            }

                                            @Override
                                            public void onFail(String reason) {
                                                uploadPhotoDialog.close();
                                            }

                                            @Override
                                            public void onFinish(FileObject fileObject) {
                                                if (userObject.getUserFiles() == null)
                                                    userObject.setUserFiles(new ArrayList<String>());
                                                userObject.getUserFiles().add(fileObject.getFileID());
                                                userObject.setUserProfilePhotoURL(fileObject.getFileURL());
                                                FirebaseValue.setUser(Firebase.getUserUID(), userObject);
                                                uploadPhotoDialog.close();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(String errorMessage) {

                                    }
                                });

                            }
                        }).build();
                imagePickerDialog.show();
            }
        });
    }

    private void updateUserData(){
        if(user != null) {
            informationHolderList.clear();
            Picasso.get().load(user.getUserProfilePhotoURL()).placeholder(R.drawable.temp_user_photo).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    profileLayoutUserPhoto.setImageBitmap(bitmap);
                    profileLayoutBlurredUserPhoto.setBlurBitmap(bitmap, 0.4f, 7);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            profileLayoutUserAbout.setText(user.getUserAboutText());
            profileLayoutUserName.setText(user.getUserName());
            switch (user.getUserType()) {
                case SERVICE:
                    profileLayoutUserType.setText("Исполнитель");
                    break;
                case CLIENT:
                    profileLayoutUserType.setText("Заявитель");
                    break;
                case ADMIN:
                    profileLayoutUserType.setText("Администратор");
                    break;
            }
            if(informationHolderList.isEmpty()) {
                informationHolderList.add(new ProfileInformationAdapter.InformationHolder("Регистрация", Time.getHumanReadableTime(user.getUserRegisteredAt(), "dd.MM.yyyy")));
                informationHolderList.add(new ProfileInformationAdapter.InformationHolder("Последнее посещение", Time.getHumanReadableTime(user.getUserLastOnlineAt(), "dd.MM.yyyy HH:mm")));
                informationHolderList.add(new ProfileInformationAdapter.InformationHolder("Сообщений написано", messageCreated));
                informationHolderList.add(new ProfileInformationAdapter.InformationHolder("Заявок создано", requestsCreated));
                informationHolderList.add(new ProfileInformationAdapter.InformationHolder("Лимит хранилища", FileUtils.humanReadableByteCount(storageUsed) + " из " + FileUtils.humanReadableByteCount(Constants.USER_STORAGE_SIZE)));
                informationAdapter.notifyDataSetChanged();
            }
            includedFront.showOriginal();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        databaseListener.startListening();
    }

    @Override
    protected void onPause() {
        databaseListener.stopListening();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseListener.stopListening();
        Bungee.slideDown(this);
    }
}
