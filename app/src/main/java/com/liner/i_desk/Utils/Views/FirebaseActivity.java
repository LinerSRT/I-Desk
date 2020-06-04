package com.liner.i_desk.Utils.Views;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class FirebaseActivity extends FragmentActivity {
    public FirebaseAuth firebaseAuth;
    public FirebaseDatabase firebaseDatabase;
    public StorageReference storageReference;
    public DatabaseReference usersDatabase;
    public FirebaseUser firebaseUser;
    public boolean hideKeyboard = true;
    private User user;
    private FirebaseExtender userListener;
    private List<Request> requestList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        usersDatabase = firebaseDatabase.getReference().child("Users");
        userListener = new FirebaseExtender<User>(usersDatabase.limitToLast(100)) {
            @Override
            public void onItemAdded(String key, User item, int pos) {
                processUserList(item);
                if (item.getRequestList() != null) {
                    processRequestList(item.getRequestList());
                }
                onSomethingChanged();
            }

            @Override
            public void onItemChanged(String key, User oldItem, User newItem, int pos) {
                processUserList(newItem);
                if (newItem.getRequestList() != null) {
                    processRequestList(newItem.getRequestList());
                }
//                if (newItem.getUserUID().equals(firebaseUser.getUid())) {
//                    user = newItem;
//                    onUserChanged(key, oldItem, newItem, pos);
//                } else {
//                    onChanged(key, oldItem, newItem, pos);
//                }
                onSomethingChanged();

            }

            @Override
            public void onItemRemoved(String key, User item, int pos) {
                processUserList(item);
                if (item.getRequestList() != null) {
                    processRequestList(item.getRequestList());
                }
//                if (item.getUserUID().equals(firebaseUser.getUid())) {
//                    user = item;
//                    onUserRemoved(key, item, pos);
//                } else {
//                    onRemoved(key, item, pos);
//                }
                onSomethingChanged();
            }

            @Override
            public void onItemMoved(String key, User item, int pos, int newPos) {

            }
        };
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        userListener.destroy();
        super.onDestroy();
    }

    public void updateUserOnlineTimeStamp() {
        usersDatabase.child(firebaseUser.getUid()).child("lastOnlineTimestamp").setValue(System.currentTimeMillis());
    }


    public List<Request> getRequestList() {
        if (user != null) {
            if (user.getUserAccountType() == User.Type.SERVICE)
                return requestList;
            List<Request> outList = new ArrayList<>();
            for (Request request : requestList) {
                if (request.getRequestCreatorID().equals(user.getUserUID()))
                    outList.add(request);
            }
            return outList;
        }
        return new ArrayList<>();
    }

    public Request getRequestByID(String requestID){
        for(Request request:requestList){
            if(request.getRequestID().equals(requestID)){
                return request;
            }
        }
        return null;
    }

    public List<Request> getRequestListByCreatorID(String creatorID){
        List<Request> out = new ArrayList<>();
        for(Request request:requestList){
            if(request.getRequestCreatorID().equals(creatorID)){
                out.add(request);
            }
        }
        return out;
    }


    public void setUser(User user) {
        usersDatabase.child(user.getUserUID()).setValue(user);
    }


    public List<User> getUserList() {
        return userList;
    }

    public User getCurrentUser() {
        return getUserByUID(firebaseUser.getUid());
    }

    public User getUserByUID(String userUID){
        for (User user : userList) {
            if (user.getUserUID().equals(userUID))
                return user;
        }
        return this.user;
    }

    private void processRequestList(List<Request> requests) {
        if (requestList == null || requestList.isEmpty()) {
            requestList = requests;
            return;
        }
        switch (user.getUserAccountType()) {
            case SERVICE:
                for (Request newRequest : requests) {
                    if (isRequestsContainID(newRequest.getRequestID())) {
                        requestList.set(getRequestsIndexOf(newRequest.getRequestID()), newRequest);
                    } else {
                        requestList.add(newRequest);
                    }
                }
                break;
            case CLIENT:
                for (Request newRequest : requests) {
                    if (isRequestsContainID(newRequest.getRequestID())) {
                        if (newRequest.getRequestCreatorID().equals(user.getUserUID())) {
                            requestList.set(getRequestsIndexOf(newRequest.getRequestID()), newRequest);
                        }
                    } else {
                        requestList.add(newRequest);
                    }
                }
                break;
        }
    }

    private void processUserList(User item) {
        if (item.getUserUID().equals(firebaseUser.getUid()))
            this.user = item;

        if (userList.isEmpty()) {
            userList.add(item);
            return;
        }
        for (User existUser : userList) {
            if (existUser.getUserUID().equals(item.getUserUID())) {
                userList.set(userList.indexOf(existUser), item);
                return;
            }
        }
        userList.add(item);
    }

    private boolean isRequestsContainID(String requestID) {
        for (Request request : requestList) {
            if (request.getRequestID().equals(requestID))
                return true;
        }
        return false;
    }

    private int getRequestsIndexOf(String requestID) {
        int index = 0;
        for (Request request : requestList) {
            if (request.getRequestID().equals(requestID)) {
                return index;
            }
            index++;
        }
        return index;
    }


    public abstract void onSomethingChanged();


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
