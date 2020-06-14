package com.liner.i_desk.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.MessageObject;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.i_desk.R;
import com.liner.utils.Time;
import com.liner.views.YSTextView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {
    private DatabaseListener databaseListener;
    private CircleImageView userPhoto;
    private YSTextView userName;
    private YSTextView userStatus;
    private YSTextView userRegisterAt;
    private YSTextView userMessageCount;
    private YSTextView userRequestCount;
    private YSTextView userEmail;
    private YSTextView userAboutText;


    private UserObject currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        userPhoto = view.findViewById(R.id.userPhoto);
        userName = view.findViewById(R.id.userName);
        userStatus = view.findViewById(R.id.userStatus);
        userRegisterAt = view.findViewById(R.id.userRegisterAt);
        userMessageCount = view.findViewById(R.id.userMessageCount);
        userRequestCount = view.findViewById(R.id.userRequestCount);
        userEmail = view.findViewById(R.id.userEmail);
        userAboutText = view.findViewById(R.id.userAboutText);
        userRequestCount.setText("0");
        userMessageCount.setText("0");
        databaseListener = new DatabaseListener() {
            @Override
            public void onUserAdded(UserObject userObject, int position) {
                super.onUserAdded(userObject, position);
                if (userObject.getUserID().equals(Firebase.getUserUID())) {
                    currentUser = userObject;
                    updateUserData();
                }
            }

            @Override
            public void onUserChanged(UserObject userObject, int position) {
                super.onUserChanged(userObject, position);
                if (userObject.getUserID().equals(Firebase.getUserUID())) {
                    currentUser = userObject;
                    updateUserData();
                }
            }

            @Override
            public void onRequestAdded(RequestObject requestObject, int position) {
                super.onRequestAdded(requestObject, position);
                if (requestObject.getRequestCreatorID().equals(Firebase.getUserUID()))
                    userRequestCount.setText(String.valueOf(Integer.parseInt(userRequestCount.getText().toString().trim())+1));
            }

            @Override
            public void onMessageAdded(MessageObject messageObject, int position) {
                super.onMessageAdded(messageObject, position);
                if (messageObject.getCreatorID().equals(Firebase.getUserUID()))
                    userMessageCount.setText(String.valueOf(Integer.parseInt(userMessageCount.getText().toString().trim())+1));
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
        Picasso.get().load(currentUser.getUserProfilePhotoURL()).networkPolicy(NetworkPolicy.OFFLINE).into(userPhoto);
        userName.setText(currentUser.getUserName());
        userStatus.setText(currentUser.getUserStatusText());
        userRegisterAt.setText(Time.getHumanReadableTime(currentUser.getUserRegisteredAt(), "dd.MM.yyyy"));
        userEmail.setText(currentUser.getUserEmail());
        userAboutText.setText(currentUser.getUserAboutText());
    }
}
