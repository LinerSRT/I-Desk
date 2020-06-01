package com.liner.i_desk.Utils.Views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liner.i_desk.Utils.BroadcastManager;

public abstract class FirebaseFragment extends Fragment implements BroadcastManager.BroadcastListener{
    public FirebaseActivity firebaseActivity;
    public BroadcastManager broadcastManager;
    public String FIREBASE_ACTION = "com.liner.i_desk.FIREBASE_CHANGED";
    public String FIREBASE_ACTION_USER = "com.liner.i_desk.FIREBASE_ACTION_USER";
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        firebaseActivity = (FirebaseActivity) getActivity();
        broadcastManager = new BroadcastManager(getContext());
        broadcastManager.registerListener(FIREBASE_ACTION, this);
        super.onCreate(savedInstanceState);

    }


    public abstract void onFirebaseChanged();
    public abstract void onUserOptained();

    @Override
    public void onReceiveLocal(Intent intent) {
        if(intent.getAction().equals(FIREBASE_ACTION))
            onFirebaseChanged();
        if(intent.getAction().equals(FIREBASE_ACTION_USER))
            onUserOptained();
    }
}
