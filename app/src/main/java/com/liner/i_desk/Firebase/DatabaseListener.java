package com.liner.i_desk.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.liner.i_desk.Constants;

public abstract class DatabaseListener implements DatabaseAbstractListener {
    private FirebaseListener<UserObject> userObjectFirebaseListener;
    private FirebaseListener<RequestObject> requestObjectFirebaseListener;
    private FirebaseListener<MessageObject> messageObjectFirebaseListener;
    private FirebaseListener<FileObject> fileObjectFirebaseListener;
    private boolean listening = false;

    protected DatabaseListener() {
        userObjectFirebaseListener = new FirebaseListener<UserObject>(Constants.USERS_DATABASE_KEY) {
            @Override
            public void onItemAdded(String key, UserObject item, int pos, DatabaseReference reference) {
                onUserAdded(item);
            }

            @Override
            public void onItemChanged(String key, UserObject item, int pos, DatabaseReference reference) {
                onUserChanged(item);
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
                onRequestAdded(item);
            }

            @Override
            public void onItemChanged(String key, RequestObject item, int pos, DatabaseReference reference) {
                onRequestChanged(item);
            }

            @Override
            public void onItemRemoved(String key, RequestObject item, int pos, DatabaseReference reference) {
                onRequestDeleted(item);
            }

            @Override
            public void onItemMoved(String key, RequestObject item, int pos, int newPos) {

            }
        };
        messageObjectFirebaseListener = new FirebaseListener<MessageObject>(Constants.MESSAGES_DATABASE_KEY) {
            @Override
            public void onItemAdded(String key, MessageObject item, int pos, DatabaseReference reference) {
                onMessageAdded(item);
            }

            @Override
            public void onItemChanged(String key, MessageObject item, int pos, DatabaseReference reference) {
                onMessageChanged(item);
            }

            @Override
            public void onItemRemoved(String key, MessageObject item, int pos, DatabaseReference reference) {
                onMessageDeleted(item);
            }

            @Override
            public void onItemMoved(String key, MessageObject item, int pos, int newPos) {

            }
        };
        fileObjectFirebaseListener = new FirebaseListener<FileObject>(Constants.FILES_DATABASE_KEY) {
            @Override
            public void onItemAdded(String key, FileObject item, int pos, DatabaseReference reference) {
                onFileAdded(item);
            }

            @Override
            public void onItemChanged(String key, FileObject item, int pos, DatabaseReference reference) {
                onFileChanged(item);
            }

            @Override
            public void onItemRemoved(String key, FileObject item, int pos, DatabaseReference reference) {
                onFileDeleted(item);
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

    public boolean isListening() {
        return listening;
    }

    @Override
    public void onUserAdded(UserObject userObject) {

    }

    @Override
    public void onUserChanged(UserObject userObject) {

    }

    @Override
    public void onRequestAdded(RequestObject requestObject) {

    }

    @Override
    public void onRequestChanged(RequestObject requestObject) {

    }

    @Override
    public void onRequestDeleted(RequestObject requestObject) {

    }

    @Override
    public void onMessageAdded(MessageObject messageObject) {

    }

    @Override
    public void onMessageChanged(MessageObject messageObject) {

    }

    @Override
    public void onMessageDeleted(MessageObject messageObject) {

    }

    @Override
    public void onFileAdded(FileObject fileObject) {

    }

    @Override
    public void onFileChanged(FileObject fileObject) {

    }

    @Override
    public void onFileDeleted(FileObject fileObject) {

    }
}
