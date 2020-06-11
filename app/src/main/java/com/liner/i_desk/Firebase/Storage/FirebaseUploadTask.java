package com.liner.i_desk.Firebase.Storage;

import android.content.Context;

import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.Firebase;

import java.io.File;
import java.util.List;

public class FirebaseUploadTask {
    public Context context;
    public String fileName;
    public String userUID = Firebase.getUserUID();
    public byte[] data = new byte[4096];
    public List<File> fileList;
    public File file;

    public FirebaseUploadTask with(Context context) {
        this.context = context;
        return this;
    }

    public FirebaseUploadTask bytes(byte[] bytes) {
        this.data = bytes;
        return this;
    }

    public FirebaseUploadTask fileList(List<File> fileList) {
        this.fileList = fileList;
        return this;
    }

    public FirebaseUploadTask file(File file) {
        this.file = file;
        this.fileName = file.getName();
        return this;
    }

    public FirebaseUploadTask filename(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public FirebaseUploadTask userUID(String userUID) {
        this.userUID = userUID;
        return this;
    }


    public void uploadBytes(TaskListener<FileObject> listener){
        new FirebaseFileManager(this).uploadBytes(listener);
    }

    public void uploadFile(TaskListener<FileObject> listener){
        new FirebaseFileManager(this).uploadFile(listener);
    }

    public void uploadFiles(TaskListener<List<FileObject>> listener){
        new FirebaseFileManager(this).uploadFiles(listener);
    }
}