package com.liner.i_desk.Firebase;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.liner.utils.FileUtils;
import com.liner.utils.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirebaseValue {
    private static int uploadCount = 0;
    private static long uploadSize = 0;
    private static long uploadedCount = 0;

    public static void getUserValue(String userUID, final String key, final ValueListener valueListener) {
        if (!Firebase.isUserLoginned())
            return;
        Objects.requireNonNull(Firebase.getUsersDatabase()).child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(key)) {
                    valueListener.onSuccess(dataSnapshot.child(key).getValue(), dataSnapshot.child(key).getRef());
                } else {
                    valueListener.onFail("Value not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getUser(final String userUID, final ValueListener valueListener) {
        if (!Firebase.isUserLoginned())
            return;
        Objects.requireNonNull(Firebase.getUsersDatabase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userUID)) {
                    valueListener.onSuccess(dataSnapshot.child(userUID).getValue(UserObject.class), dataSnapshot.child(userUID).getRef());
                } else {
                    valueListener.onFail("Value not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void setUserValue(String userID, String key, Object value) {
        if (!Firebase.isUserLoginned())
            return;
        Objects.requireNonNull(Firebase.getUsersDatabase()).child(userID).child(key).setValue(value);
    }

    public static void setUser(String userID, Object value) {
        if (!Firebase.isUserLoginned())
            return;
        Objects.requireNonNull(Firebase.getUsersDatabase()).child(userID).setValue(value);
    }

    public static void deleteFile(final String creatorID, final String fileID, String fileName){
        final StorageReference path = FirebaseStorage.getInstance().getReference(Firebase.getUserUID()+File.separator+fileName);
        path.delete();
        getUser(creatorID, new FirebaseValue.ValueListener(){
            @Override
            public void onFail(String errorMessage) {

            }

            @Override
            public void onSuccess(Object object, DatabaseReference databaseReference) {
                UserObject userObject = (UserObject) object;
                userObject.getUserFiles().remove(fileID);
                setUserValue(creatorID, "userFiles", userObject.getUserFiles());
                Objects.requireNonNull(Firebase.getFilesDatabase()).child(fileID).removeValue();
            }
        });
    }

    public static void deleteFile(final FileObject fileObject){
        final StorageReference path = FirebaseStorage.getInstance().getReference(Firebase.getUserUID()+File.separator+fileObject.getFileID()+File.separator+fileObject.getFileName());
        path.delete();
        getUser(fileObject.getFileCreatorID(), new FirebaseValue.ValueListener(){
            @Override
            public void onFail(String errorMessage) {

            }

            @Override
            public void onSuccess(Object object, DatabaseReference databaseReference) {
                UserObject userObject = (UserObject) object;
                userObject.getUserFiles().remove(fileObject.getFileID());
                setUserValue(fileObject.getFileCreatorID(), "userFiles", userObject.getUserFiles());
                Objects.requireNonNull(Firebase.getFilesDatabase()).child(fileObject.getFileID()).removeValue();
            }
        });
    }

    public static void uploadFileList(final List<File> fileList, final FileListUploadListener fileListUploadListener){
        final List<FileObject> fileObjects = new ArrayList<>();
        uploadCount = 0;
        uploadedCount = 0;
        uploadSize = FileUtils.getListFilesSize(fileList);
        for(final File file:fileList){
            uploadByteArray(FileUtils.getFileByteArray(file), file.getName(), new FileUploadListener() {
                @Override
                public void onStart() {
                    Log.d(getClass().getSimpleName(), "Start upload ["+file.getName()+"] finished");
                }

                @Override
                public void onProgress(long transferredBytes, long totalBytes) {
                    Log.d(getClass().getSimpleName(), "Uploading "+uploadCount+" of "+fileList.size()+" | "+file.getName()+" ["+transferredBytes+"/"+totalBytes+"]");
                    uploadCount += transferredBytes;
                    fileListUploadListener.onUploading(Math.round((uploadCount/uploadSize)*100));
                }

                @Override
                public void onFail(String reason) {
                    fileListUploadListener.onFail(reason);
                }

                @Override
                public void onFinish(FileObject fileObject) {
                    uploadCount++;
                    Log.d(getClass().getSimpleName(), "Upload ["+file.getName()+"] finished");
                    fileObjects.add(fileObject);
                    if(uploadCount == fileList.size())
                        fileListUploadListener.onFinish(fileObjects);
                }
            });
        }
    }

    public static void uploadByteArray(byte[] bytes, final String filenameWithExtension, final FileUploadListener fileUploadListener) {
        final String fileID = TextUtils.getUniqueString();
        final StorageReference path = FirebaseStorage.getInstance().getReference(Firebase.getUserUID()+File.separator+filenameWithExtension);
        final UploadTask uploadTask = path.putBytes(bytes);
        fileUploadListener.onStart();
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String fileURL = uri.toString();
                            path.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                @Override
                                public void onSuccess(final StorageMetadata storageMetadata) {
                                    FileObject fileObject = new FileObject(
                                            fileID,
                                            filenameWithExtension,
                                            fileURL,
                                            storageMetadata.getContentType(),
                                            Firebase.getUserUID(),
                                            storageMetadata.getCreationTimeMillis(),
                                            storageMetadata.getSizeBytes()
                                    );
                                    Objects.requireNonNull(Firebase.getFilesDatabase()).child(fileObject.getFileID()).setValue(fileObject);
                                    fileUploadListener.onFinish(fileObject);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    fileUploadListener.onFail(e.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            fileUploadListener.onFail(e.getMessage());
                        }
                    });
                } else {
                    fileUploadListener.onFail(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fileUploadListener.onFail(e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                fileUploadListener.onProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
            }
        });
    }









































    public interface ValueListener {
        void onSuccess(Object object, DatabaseReference databaseReference);
        void onFail(String errorMessage);
    }

    public interface FileUploadListener{
        void onStart();
        void onProgress(long transferredBytes, long totalBytes);
        void onFail(String reason);
        void onFinish(FileObject fileObject);
    }
    public interface FileListUploadListener{
        void onFinish(List<FileObject> fileObjectList);
        void onFail(String reason);
        void onUploading(int percent);
    }
}
