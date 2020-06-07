package com.liner.i_desk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.faltenreich.skeletonlayout.SkeletonLayout;
import com.google.firebase.database.DatabaseReference;
import com.liner.i_desk.Firebase.FireActivity;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseListener;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.MessageObject;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.views.BlurredImageView;
import com.liner.views.YSTextView;
import com.roacult.backdrop.BackdropLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.cachapa.expandablelayout.ExpandableLayout;

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
    private ExpandableLayout profileLayoutUserAboutExpandLayout;
    private TextFieldBoxes profileLayoutUserAboutEditBox;
    private ExtendedEditText profileLayoutUserAboutEditText;
    private YSTextView profileLayoutRegistrationDate;
    private YSTextView profileLayoutLastOnlineDate;
    private YSTextView profileLayoutMessageWrited;
    private YSTextView profileLayoutRequestsCreated;
    private Button profileLayoutSignOut;
    private SkeletonLayout includedFront;

    private BlurredImageView profileLayoutBlurredUserPhoto;

    private FirebaseListener<UserObject> userObjectFirebaseListener;
    private FirebaseListener<MessageObject> messageObjectFirebaseListener;
    private FirebaseListener<RequestObject> requestObjectFirebaseListener;
    private UserObject userObject;


    private String userAboutText = "";
    private int messageCreated = 0;
    private int requestsCreated = 0;

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
        profileLayoutUserPhoto = findViewById(R.id.profileLayoutUserPhoto);
        profileLayoutUserName = findViewById(R.id.profileLayoutUserName);
        profileLayoutUserType = findViewById(R.id.profileLayoutUserType);
        profileLayoutUserAbout = findViewById(R.id.profileLayoutUserAbout);
        profileLayoutUserAboutExpandLayout = findViewById(R.id.profileLayoutUserAboutExpandLayout);
        profileLayoutUserAboutEditBox = findViewById(R.id.profileLayoutUserAboutEditBox);
        profileLayoutUserAboutEditText = findViewById(R.id.profileLayoutUserAboutEditText);
        profileLayoutRegistrationDate = findViewById(R.id.profileLayoutRegistrationDate);
        profileLayoutLastOnlineDate = findViewById(R.id.profileLayoutLastOnlineDate);
        profileLayoutMessageWrited = findViewById(R.id.profileLayoutMessageWrited);
        profileLayoutRequestsCreated = findViewById(R.id.profileLayoutRequestsCreated);
        profileLayoutSignOut = findViewById(R.id.profileLayoutSignOut);
        includedFront.showSkeleton();


        messageObjectFirebaseListener = new FirebaseListener<MessageObject>(Constants.MESSAGES_DATABASE_KEY) {
            @Override
            public void onItemAdded(String key, MessageObject item, int pos, DatabaseReference reference) {
                if (item.getCreatorID().equals(Firebase.getUserUID()))
                    messageCreated++;
            }

            @Override
            public void onItemChanged(String key, MessageObject item, int pos, DatabaseReference reference) {

            }

            @Override
            public void onItemRemoved(String key, MessageObject item, int pos, DatabaseReference reference) {

            }

            @Override
            public void onItemMoved(String key, MessageObject item, int pos, int newPos) {

            }
        };
        messageObjectFirebaseListener.start();
        requestObjectFirebaseListener = new FirebaseListener<RequestObject>(Constants.REQUESTS_DATABASE_KEY) {
            @Override
            public void onItemAdded(String key, RequestObject item, int pos, DatabaseReference reference) {
                if (item.getRequestCreatorID().equals(Firebase.getUserUID()))
                    requestsCreated++;
            }

            @Override
            public void onItemChanged(String key, RequestObject item, int pos, DatabaseReference reference) {

            }

            @Override
            public void onItemRemoved(String key, RequestObject item, int pos, DatabaseReference reference) {

            }

            @Override
            public void onItemMoved(String key, RequestObject item, int pos, int newPos) {

            }
        };
        requestObjectFirebaseListener.start();


        userObjectFirebaseListener = new FirebaseListener<UserObject>(Constants.USERS_DATABASE_KEY) {
            @Override
            public void onItemAdded(String key, UserObject item, int pos, DatabaseReference reference) {
                if (item.getUserID().equals(Firebase.getUserUID())) {
                    userObject = item;
                    fillUserData(userObject);
                }

            }

            @Override
            public void onItemChanged(String key, UserObject item, int pos, DatabaseReference reference) {
                if (item.getUserID().equals(Firebase.getUserUID())) {
                    userObject = item;
                    fillUserData(userObject);
                }
            }

            @Override
            public void onItemRemoved(String key, UserObject item, int pos, DatabaseReference reference) {
                if (item.getUserID().equals(Firebase.getUserUID()))
                    finish();
            }

            @Override
            public void onItemMoved(String key, UserObject item, int pos, int newPos) {

            }
        };
        userObjectFirebaseListener.start();
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
                startActivity(new Intent(ActivityUserProfile.this, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
                Bungee.slideUp(ActivityUserProfile.this);
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
    }

    private void fillUserData(final UserObject userObject) {
        Picasso.get().load(userObject.getUserProfilePhotoURL()).placeholder(R.drawable.temp_user_photo).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                profileLayoutBlurredUserPhoto.setBlurBitmap(bitmap, 0.4f, 7);
                profileLayoutUserPhoto.setImageBitmap(bitmap);
                profileLayoutUserAbout.setText(userObject.getUserAboutText());
                profileLayoutRegistrationDate.setText(Time.getHumanReadableTime(userObject.getUserRegisteredAt(), "dd.MM.yyyy"));
                profileLayoutLastOnlineDate.setText(Time.getHumanReadableTime(userObject.getUserLastOnlineAt(), "dd.MM.yyyy HH:mm"));
                profileLayoutMessageWrited.setText(String.valueOf(messageCreated));
                profileLayoutRequestsCreated.setText(String.valueOf(requestsCreated));
                includedFront.showOriginal();
                profileLayoutUserAboutExpandLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        profileLayoutUserName.setText(userObject.getUserName());
        switch (userObject.getUserType()) {
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userObjectFirebaseListener.destroy();
        messageObjectFirebaseListener.destroy();
        requestObjectFirebaseListener.destroy();
        Bungee.slideDown(this);
    }
}
