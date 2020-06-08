package com.liner.i_desk.Firebase;

public interface DatabaseAbstractListener{
    void onUserAdded(UserObject userObject);
    void onUserChanged(UserObject userObject);
    void onRequestAdded(RequestObject requestObject);
    void onRequestChanged(RequestObject requestObject);
    void onRequestDeleted(RequestObject requestObject);
    void onMessageAdded(MessageObject messageObject);
    void onMessageChanged(MessageObject messageObject);
    void onMessageDeleted(MessageObject messageObject);
    void onFileAdded(FileObject fileObject);
    void onFileChanged(FileObject fileObject);
    void onFileDeleted(FileObject fileObject);
}