package com.liner.i_desk.Utils.Firebase;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.Constants;
import com.liner.i_desk.Utils.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FirebaseValue {
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




    public static void uploadByteArray(byte[] bytes, final String filenameWithExtension, final FileUploadListener fileUploadListener) {
        final String fileID = TextUtils.getUniqueString();
        final StorageReference path = FirebaseStorage.getInstance().getReference(Firebase.getUserUID()+File.separator+fileID+File.separator+filenameWithExtension);
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
}
