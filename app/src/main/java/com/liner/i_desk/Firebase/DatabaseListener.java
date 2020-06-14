package com.liner.i_desk.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.liner.i_desk.Constants;

import java.util.List;

public abstract class DatabaseListener implements DatabaseAbstractListener {
    private FirebaseListener<UserObject> userObjectFirebaseListener;
    private FirebaseListener<RequestObject> requestObjectFirebaseListener;
    private FirebaseListener<MessageObject> messageObjectFirebaseListener;
    private FirebaseListener<FileObject> fileObjectFirebaseListener;
    private UserObject currentUser;
    private boolean listening = false;



    protected DatabaseListener() {
        userObjectFirebaseListener = new FirebaseListener<UserObject>(Constants.USERS_DATABASE_KEY) {
            @Override
            public void onItemAdded(String key, UserObject item, int pos, DatabaseReference reference) {
                if(item.getUserID().equals(Firebase.getUserUID()))
                    currentUser = item;
                onUserAdded(item, pos);
            }

            @Override
            public void onItemChanged(String key, UserObject item, int pos, DatabaseReference reference) {
                onUserChanged(item, pos);
            }

            @Override
            public void onItemRemoved(String key, UserObject item, int pos, DatabaseReference reference) {

            }

            @Override
            public void onItemMoved(String key, UserObject item, int pos, int newPos) {

            }
        };
        requestObjectFirebaseListener = new FirebaseListener<RequestObject>(Constants.REQUESTS_DATABASE_KEY) {
            @Override
            public void onItemAdded(String key, RequestObject item, int pos, DatabaseReference reference) {
                onRequestAdded(item, pos);
            }

            @Override
            public void onItemChanged(String key, RequestObject item, int pos, DatabaseReference reference) {
                onRequestChanged(item, pos);
            }

            @Override
            public void onItemRemoved(String key, RequestObject item, int pos, DatabaseReference reference) {
                onRequestDeleted(item, pos);
            }

            @Override
            public void onItemMoved(String key, RequestObject item, int pos, int newPos) {

            }
        };
        messageObjectFirebaseListener = new FirebaseListener<MessageObject>(Constants.MESSAGES_DATABASE_KEY) {
            @Override
            public void onItemAdded(String key, MessageObject item, int pos, DatabaseReference reference) {
                onMessageAdded(item, pos);
            }

            @Override
            public void onItemChanged(String key, MessageObject item, int pos, DatabaseReference reference) {
                onMessageChanged(item, pos);
            }

            @Override
            public void onItemRemoved(String key, MessageObject item, int pos, DatabaseReference reference) {
                onMessageDeleted(item, pos);
            }

            @Override
            public void onItemMoved(String key, MessageObject item, int pos, int newPos) {

            }
        };
        fileObjectFirebaseListener = new FirebaseListener<FileObject>(Constants.FILES_DATABASE_KEY) {
            @Override
            public void onItemAdded(String key, FileObject item, int pos, DatabaseReference reference) {
                onFileAdded(item, pos);
            }

            @Override
            public void onItemChanged(String key, FileObject item, int pos, DatabaseReference reference) {
                onFileChanged(item, pos);
            }

            @Override
            public void onItemRemoved(String key, FileObject item, int pos, DatabaseReference reference) {
                onFileDeleted(item, pos);
            }

            @Override
            public void onItemMoved(String key, FileObject item, int pos, int newPos) {

            }
        };
    }


    public void startListening() {
        listening = true;
        if(userObjectFirebaseListener != null)
            userObjectFirebaseListener.start();
        if(requestObjectFirebaseListener != null)
            requestObjectFirebaseListener.start();
        if(messageObjectFirebaseListener != null)
            messageObjectFirebaseListener.start();
        if(fileObjectFirebaseListener != null)
            fileObjectFirebaseListener.start();
    }
    public void stopListening() {
        listening = false;
        if(userObjectFirebaseListener != null)
            userObjectFirebaseListener.destroy();
        if(requestObjectFirebaseListener != null)
            requestObjectFirebaseListener.destroy();
        if(messageObjectFirebaseListener != null)
            messageObjectFirebaseListener.destroy();
        if(fileObjectFirebaseListener != null)
            fileObjectFirebaseListener.destroy();
    }

    public List<UserObject> getUsers(){
        return userObjectFirebaseListener.getItems();
    }
    public List<RequestObject> getRequests(){
        return requestObjectFirebaseListener.getItems();
    }
    public List<MessageObject> getMessages(){
        return messageObjectFirebaseListener.getItems();
    }
    public List<FileObject> getFiles(){
        return fileObjectFirebaseListener.getItems();
    }

    public UserObject getCurrentUser() {
        return currentUser;
    }

    public FirebaseListener<FileObject> getFileObjectFirebaseListener() {
        return fileObjectFirebaseListener;
    }

    public FirebaseListener<MessageObject> getMessageObjectFirebaseListener() {
        return messageObjectFirebaseListener;
    }

    public FirebaseListener<RequestObject> getRequestObjectFirebaseListener() {
        return requestObjectFirebaseListener;
    }

    public FirebaseListener<UserObject> getUserObjectFirebaseListener() {
        return userObjectFirebaseListener;
    }

    public boolean isListening() {
        return listening;
    }

    @Override
    public void onUserAdded(UserObject userObject, int position) {

    }

    @Override
    public void onUserChanged(UserObject userObject, int position) {

    }

    @Override
    public void onRequestAdded(RequestObject requestObject, int position) {

    }

    @Override
    public void onRequestChanged(RequestObject requestObject, int position) {

    }

    @Override
    public void onRequestDeleted(RequestObject requestObject, int position) {

    }

    @Override
    public void onMessageAdded(MessageObject messageObject, int position) {

    }

    @Override
    public void onMessageChanged(MessageObject messageObject, int position) {

    }

    @Override
    public void onMessageDeleted(MessageObject messageObject, int position) {

    }

    @Override
    public void onFileAdded(FileObject fileObject, int position) {

    }

    @Override
    public void onFileChanged(FileObject fileObject, int position) {

    }

    @Override
    public void onFileDeleted(FileObject fileObject, int position) {

    }
}
