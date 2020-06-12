package com.liner.i_desk;

import android.os.Bundle;

import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.FireActivity;
import com.liner.views.GradientImageView;

import spencerstudios.com.bungeelib.Bungee;

public class ActivityUserProfile extends FireActivity {
    private DatabaseListener databaseListener;
    private GradientImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_layout);


//        userProfileImage = findViewById(R.id.userProfileImage);
//
//
//
//        databaseListener = new DatabaseListener(){
//            @Override
//            public void onUserChanged(UserObject userObject) {
//                super.onUserChanged(userObject);
//
//
//            }
//
//            @Override
//            public void onUserAdded(UserObject userObject) {
//                super.onUserAdded(userObject);
//                if(userObject.getUserID().equals(Firebase.getUserUID())){
//                    Picasso.get().load(userObject.getUserProfilePhotoURL()).into(userProfileImage);
//                }
//            }
//        };
//        databaseListener.startListening();



    }



    @Override
    protected void onResume() {
        super.onResume();
        //databaseListener.startListening();
    }

    @Override
    protected void onPause() {
        //databaseListener.stopListening();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       //databaseListener.stopListening();
        Bungee.slideDown(this);
    }


}
