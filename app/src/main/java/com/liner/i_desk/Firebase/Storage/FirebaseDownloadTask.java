package com.liner.i_desk.Firebase.Storage;

import android.content.Context;

import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.Firebase;

import java.io.File;

public class FirebaseDownloadTask {
    public Context context;
    public String fileName;
    public String fileURL;
    public String userUID = Firebase.getUserUID();

    public FirebaseDownloadTask with(Context context) {
        this.context = context;
        return this;
    }

    public FirebaseDownloadTask fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
    public FirebaseDownloadTask fileURL(String fileURL) {
        this.fileURL = fileURL;
        return this;
    }

    public FirebaseDownloadTask userUID(String userUID) {
        this.userUID = userUID;
        return this;
    }

    public void download(TaskListener<File> listener){
        new FirebaseFileManager(this).download(listener);
    }
}