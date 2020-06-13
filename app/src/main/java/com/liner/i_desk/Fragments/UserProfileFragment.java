package com.liner.i_desk.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
    private int messagesCount = 0;
    private int requestsCount = 0;


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
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        databaseListener = new DatabaseListener() {
            @Override
            public void onUserAdded(UserObject userObject) {
                super.onUserAdded(userObject);
                if(userObject.getUserID().equals(Firebase.getUserUID())){
                    currentUser = userObject;
                    updateUserData();
                }
            }

            @Override
            public void onUserChanged(UserObject userObject) {
                super.onUserChanged(userObject);
                if(userObject.getUserID().equals(Firebase.getUserUID())){
                    currentUser = userObject;
                    updateUserData();
                }
            }

            @Override
            public void onRequestAdded(RequestObject requestObject) {
                super.onRequestAdded(requestObject);
                if(requestObject.getRequestCreatorID().equals(Firebase.getUserUID()))
                    requestsCount ++;
            }

            @Override
            public void onMessageAdded(MessageObject messageObject) {
                super.onMessageAdded(messageObject);
                if(messageObject.getCreatorID().equals(Firebase.getUserUID()))
                    messagesCount ++;
            }
        };
        databaseListener.startListening();
    }

    @Override
    public void onDetach() {
        if (databaseListener.isListening())
            databaseListener.stopListening();
        super.onDetach();
    }

    private void updateUserData(){
        Picasso.get().load(currentUser.getUserProfilePhotoURL()).into(userPhoto);
        userName.setText(currentUser.getUserName());
        userStatus.setText(currentUser.getUserStatusText());
        userRegisterAt.setText(Time.getHumanReadableTime(currentUser.getUserRegisteredAt(), "dd.MM.yyyy"));
        userMessageCount.setText(String.valueOf(messagesCount));
        userRequestCount.setText(String.valueOf(requestsCount));
        userEmail.setText(currentUser.getUserEmail());
        userAboutText.setText(currentUser.getUserAboutText());
    }
}
