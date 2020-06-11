package com.liner.i_desk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.Storage.FirebaseDownloadTask;
import com.liner.i_desk.Firebase.Storage.FirebaseUploadTask;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.kbeanie.multipicker.api.Picker.PICK_FILE;


public class TestingActivity extends AppCompatActivity {

    private MaterialButton test, testDownload,testList;
    private File file;
    private List<File> fileList;
    private FilePicker filePicker;

    private long totalSize;
    private long transferedSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        filePicker = new FilePicker(this);
        filePicker.setFilePickerCallback(new FilePickerCallback() {
            @Override
            public void onFilesChosen(List<ChosenFile> list) {
                file = new File(list.get(0).getOriginalPath());
                fileList = new ArrayList<>();
                for(ChosenFile chosenFile:list)
                    fileList.add(new File(chosenFile.getOriginalPath()));
            }

            @Override
            public void onError(String s) {

            }
        });
        filePicker.allowMultiple();
        test = findViewById(R.id.test);
        testDownload = findViewById(R.id.testDownload);
        testList = findViewById(R.id.testList);

        testDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FirebaseDownloadTask().with(TestingActivity.this)
                        .fileName("uK-zLfHUvP-15.jpg")
                        .fileURL("https://firebasestorage.googleapis.com/v0/b/i-desk.appspot.com/o/E4TlwZVD0hg6DOl8v1Gp7agENRW2%2FuK-zLfHUvP-15.jpg?alt=media&token=16296c0c-9b1b-4c89-b1c8-41bd00bf189a")
                        .userUID(Firebase.getUserUID())
                        .download(new TaskListener<File>() {
                            @Override
                            public void onStart(String fileUID) {
                                Log.e("FBUTask", "Start downloading...");
                            }

                            @Override
                            public void onProgress(long transferredBytes, long totalBytes) {
                                Log.e("FBUTask", "Downloading {"+Math.round((((float)transferredBytes/(float)totalBytes)*100))+"} ["+FileUtils.humanReadableByteCount(transferredBytes)+"/"+FileUtils.humanReadableByteCount(totalBytes)+"]");
                            }

                            @Override
                            public void onFinish(File result, String fileUID) {
                                Log.e("FBUTask", "Download finished "+result.toString());
                            }

                            @Override
                            public void onFailed(Exception reason) {
                                Log.e("FBUTask", "Download failed! "+reason.getMessage());
                            }
                        });
            }
        });



        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(file != null){
                    new FirebaseUploadTask().with(TestingActivity.this)
                            .file(file)
                            .userUID(Firebase.getUserUID())
                            .uploadFile(new TaskListener<FileObject>() {
                                @Override
                                public void onStart(String fileUID) {
                                    Log.e("FBUTask", "Start uploading...");
                                }

                                @Override
                                public void onProgress(long transferredBytes, long totalBytes) {
                                    Log.e("FBUTask", "Uploading {"+Math.round((((float)transferredBytes/(float)totalBytes)*100))+"} ["+transferredBytes+"/"+totalBytes+"]");
                                }

                                @Override
                                public void onFinish(FileObject result, String fileUID) {
                                    Log.e("FBUTask", "Upload finished!");
                                    Log.e("FBUTask", "Result "+result.toString());
                                }

                                @Override
                                public void onFailed(Exception reason) {
                                    Log.e("FBUTask", "Upload failed!");
                                }
                            });

                } else {
                    filePicker.pickFile();
                }
            }
        });
        testList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fileList != null){
                    totalSize = FileUtils.getListFilesSize(fileList);
                    transferedSize = 0;
                    new FirebaseUploadTask().with(TestingActivity.this)
                            .fileList(fileList)
                            .userUID(Firebase.getUserUID())
                            .uploadFiles(new TaskListener<List<FileObject>>() {
                                @Override
                                public void onStart(String fileUID) {

                                    Log.e("FBUTask", "Start uploading...");
                                }

                                @Override
                                public void onProgress(long transferredBytes, long totalBytes) {
                                    transferedSize += transferredBytes;
                                    Log.e("FBUTask", "Uploading {"+Math.round((((float)transferedSize/(float)totalSize)*100))+"} ["+FileUtils.humanReadableByteCount(transferedSize)+"/"+FileUtils.humanReadableByteCount(totalSize)+"]");
                                    Log.e("FBUTask", "{"+Math.round((((float)transferredBytes/(float)totalBytes)*100))+"} ["+transferredBytes+"/"+totalBytes+"]");

                                }

                                @Override
                                public void onFinish(List<FileObject> result, String fileUID) {

                                    Log.e("FBUTask", "Upload finished!");
                                    Log.e("FBUTask", "Result "+result.toString());
                                }

                                @Override
                                public void onFailed(Exception reason) {

                                    Log.e("FBUTask", "Upload failed!");
                                }
                            });
                } else {
                    filePicker.pickFile();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE){
            filePicker.submit(data);
        }
    }
}
