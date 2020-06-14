package com.liner.i_desk.Firebase;

public interface DatabaseAbstractListener{
    void onUserAdded(UserObject userObject, int position);
    void onUserChanged(UserObject userObject, int position);
    void onRequestAdded(RequestObject requestObject, int position);
    void onRequestChanged(RequestObject requestObject, int position);
    void onRequestDeleted(RequestObject requestObject, int position);
    void onMessageAdded(MessageObject messageObject, int position);
    void onMessageChanged(MessageObject messageObject, int position);
    void onMessageDeleted(MessageObject messageObject, int position);
    void onFileAdded(FileObject fileObject, int position);
    void onFileChanged(FileObject fileObject, int position);
    void onFileDeleted(FileObject fileObject, int position);
}