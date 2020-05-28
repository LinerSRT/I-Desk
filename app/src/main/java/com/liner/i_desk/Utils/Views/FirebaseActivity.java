package com.liner.i_desk.Utils.Views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.Utils.BroadcastManager;

public abstract class FirebaseActivity extends FragmentActivity {
    private FirebaseActivity firebaseActivity;
    public FirebaseAuth firebaseAuth;
    public FirebaseDatabase firebaseDatabase;
    public StorageReference storageReference;
    public DatabaseReference usersDatabase;
    public FirebaseUser firebaseUser;
    public User user;
    public BroadcastManager broadcastManager;
    public String FIREBASE_ACTION = "com.liner.i_desk.FIREBASE_CHANGED";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        firebaseActivity = this;
        broadcastManager = new BroadcastManager(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        usersDatabase = firebaseDatabase.getReference().child("Users");
        usersDatabase.keepSynced(true);
        FirebaseHelper.getUserModel(new FirebaseHelper.IFirebaseHelperListener() {
            @Override
            public void onSuccess(Object result) {
                user = (User) result;
                onUserObtained(user);
            }

            @Override
            public void onFail(String reason) {
                user = null;
            }
        });
        usersDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(firebaseUser.getUid())){
                    user = dataSnapshot.getValue(User.class);
                    onFirebaseChanged(user);
                    broadcastManager.sendLocal(FIREBASE_ACTION);
                }
            }

            @Override
            public void onChildChanged(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(firebaseUser.getUid())){
                    user = dataSnapshot.getValue(User.class);
                    onFirebaseChanged(user);
                    broadcastManager.sendLocal(FIREBASE_ACTION);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getKey().equals(firebaseUser.getUid())){
                    user = dataSnapshot.getValue(User.class);
                    onFirebaseChanged(user);
                    broadcastManager.sendLocal(FIREBASE_ACTION);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onCreate(savedInstanceState);
    }

    public abstract void onFirebaseChanged(User user);
    public abstract void onUserObtained(User user);
}
