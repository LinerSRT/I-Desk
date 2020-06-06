package com.liner.i_desk.Utils.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.liner.i_desk.Constants;

import java.util.Objects;

public class Firebase {
    public static boolean isUserLoginned() {
        if (Constants.USERS_REQUIRE_EMAIL_VERIFICATION) {
            return FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous() && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();
        } else {
            return FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous();
        }
    }
    public static String getUserUID(){
        if(!isUserLoginned())
            return null;
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


    public static DatabaseReference getUsersDatabase(){
        if(!isUserLoginned())
            return null;
        return FirebaseDatabase.getInstance().getReference(Constants.USERS_DATABASE_KEY);
    }


    public static DatabaseReference getRequestsDatabase(){
        if(!isUserLoginned())
            return null;
        return FirebaseDatabase.getInstance().getReference(Constants.REQUESTS_DATABASE_KEY);
    }

    public static DatabaseReference getMessagesDatabase(){
        if(!isUserLoginned())
            return null;
        return FirebaseDatabase.getInstance().getReference(Constants.MESSAGES_DATABASE_KEY);
    }

    public static DatabaseReference getFilesDatabase(){
        if(!isUserLoginned())
            return null;
        return FirebaseDatabase.getInstance().getReference(Constants.FILES_DATABASE_KEY);
    }
}
