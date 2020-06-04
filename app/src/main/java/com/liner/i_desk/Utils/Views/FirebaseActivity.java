package com.liner.i_desk.Utils.Views;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.Utils.BroadcastManager;

import java.util.Objects;

public abstract class FirebaseActivity extends FragmentActivity {
    public FirebaseActivity firebaseActivity;
    public FirebaseAuth firebaseAuth;
    public FirebaseDatabase firebaseDatabase;
    public StorageReference storageReference;
    public DatabaseReference usersDatabase;
    public FirebaseUser firebaseUser;
    public User user;
    public BroadcastManager broadcastManager;
    public String FIREBASE_ACTION_USER_ADDED = "com.liner.i_desk.FIREBASE_ACTION_USER_ADDED";
    public String FIREBASE_ACTION_DATABSE_CHANGED = "com.liner.i_desk.FIREBASE_ACTION_DATABSE_CHANGED";
    public String FIREBASE_ACTION_USER_CHANGED = "com.liner.i_desk.FIREBASE_ACTION_USER_CHANGED";
    public String FIREBASE_ACTION_USER_DELETED = "com.liner.i_desk.FIREBASE_ACTION_USER_DELETED";


    public boolean hideKeyboard = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        firebaseActivity = this;
        broadcastManager = new BroadcastManager(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        usersDatabase = firebaseDatabase.getReference().child("Users");
        new FirebaseExtender<User>(usersDatabase) {
            @Override
            public void onItemAdded(String key, User item, int pos) {
                if(item.getUserUID().equals(firebaseUser.getUid())){
                    user = item;
                    onUserReceived(key, item, pos);
                    broadcastManager.sendLocal(FIREBASE_ACTION_USER_ADDED);
                } else {
                    onReceived(key, item, pos);
                }
            }

            @Override
            public void onItemChanged(String key, User oldItem, User newItem, int pos) {
                if(newItem.getUserUID().equals(firebaseUser.getUid())){
                    user = newItem;
                    onUserChanged(key, oldItem, newItem, pos);
                    broadcastManager.sendLocal(FIREBASE_ACTION_USER_CHANGED);
                } else {
                    onChanged(key, oldItem, newItem, pos);
                }
            }

            @Override
            public void onItemRemoved(String key, User item, int pos) {
                if(item.getUserUID().equals(firebaseUser.getUid())){
                    user = item;
                    onUserRemoved(key, item, pos);
                    broadcastManager.sendLocal(FIREBASE_ACTION_USER_DELETED);
                } else {
                    onRemoved(key, item, pos);
                }
            }

            @Override
            public void onItemMoved(String key, User item, int pos, int newPos) {

            }
        };


//        usersDatabase.keepSynced(true);
//        FirebaseHelper.getUserModel(new FirebaseHelper.IFirebaseHelperListener() {
//            @Override
//            public void onSuccess(Object result) {
//                user = (User) result;
//                FirebaseHelper.getRequests(user, new FirebaseHelper.IFirebaseHelperListener() {
//                    @Override
//                    public void onSuccess(Object result) {
//                        broadcastManager.sendLocal(FIREBASE_ACTION_USER);
//                        onUserObtained(user);
//                    }
//
//                    @Override
//                    public void onFail(String reason) {
//                        broadcastManager.sendLocal(FIREBASE_ACTION_USER);
//                        onUserObtained(user);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFail(String reason) {
//                user = null;
//            }
//        });
//        usersDatabase.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if (dataSnapshot.getKey().equals(firebaseUser.getUid())) {
//                    user = dataSnapshot.getValue(User.class);
//                }
//                onFirebaseChanged();
//                broadcastManager.sendLocal(FIREBASE_ACTION);
//                updateUserList();
//            }
//
//            @Override
//            public void onChildChanged(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
//                if (dataSnapshot.getKey().equals(firebaseUser.getUid())) {
//                    user = dataSnapshot.getValue(User.class);
//                }
//                onFirebaseChanged();
//                broadcastManager.sendLocal(FIREBASE_ACTION);
//                updateUserList();
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getKey().equals(firebaseUser.getUid())) {
//                    user = dataSnapshot.getValue(User.class);
//                }
//                onFirebaseChanged();
//                broadcastManager.sendLocal(FIREBASE_ACTION);
//                updateUserList();
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if (dataSnapshot.getKey().equals(firebaseUser.getUid())) {
//                    user = dataSnapshot.getValue(User.class);
//                }
//                onFirebaseChanged();
//                broadcastManager.sendLocal(FIREBASE_ACTION);
//                updateUserList();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        super.onCreate(savedInstanceState);
    }


    public abstract void onUserReceived(String key, User user, int pos);
    public abstract void onUserChanged(String key, User oldValue, User newValue, int pos);
    public abstract void onUserRemoved(String key, User user, int pos);
    public abstract void onReceived(String key, User user, int pos);
    public abstract void onChanged(String key, User oldValue, User newValue, int pos);
    public abstract void onRemoved(String key, User user, int pos);




//    public void updateUserList() {
//        FirebaseHelper.getRequests(user, new FirebaseHelper.IFirebaseHelperListener() {
//            @Override
//            public void onSuccess(Object result) {
//                broadcastManager.sendLocal(FIREBASE_ACTION_USER);
//                onUserObtained(user);
//            }
//
//            @Override
//            public void onFail(String reason) {
//                broadcastManager.sendLocal(FIREBASE_ACTION_USER);
//                onUserObtained(user);
//            }
//        });
//    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (hideKeyboard)
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        return super.dispatchTouchEvent(ev);
    }
}
