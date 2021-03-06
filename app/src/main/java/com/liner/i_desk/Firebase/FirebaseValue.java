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

    public static void getFile(final String fileID, final ValueListener valueListener) {
        if (!Firebase.isUserLoginned())
            return;
        Objects.requireNonNull(Firebase.getFilesDatabase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(fileID)) {
                    valueListener.onSuccess(dataSnapshot.child(fileID).getValue(FileObject.class), dataSnapshot.child(fileID).getRef());
                } else {
                    valueListener.onFail("Value not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getRequest(final String requestID, final ValueListener valueListener) {
        if (!Firebase.isUserLoginned())
            return;
        Objects.requireNonNull(Firebase.getRequestsDatabase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(requestID)) {
                    valueListener.onSuccess(dataSnapshot.child(requestID).getValue(RequestObject.class), dataSnapshot.child(requestID).getRef());
                } else {
                    valueListener.onFail("Value not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getRequests(String userID, final ValueListener valueListener){
        final List<RequestObject> resultList = new ArrayList<>();
        getUser(userID, new ValueListener() {
            @Override
            public void onSuccess(Object object, final DatabaseReference databaseReference) {
                final UserObject userObject = (UserObject) object;
                Objects.requireNonNull(Firebase.getRequestsDatabase()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(userObject.getUserRequests().contains(dataSnapshot.getKey())){
                            resultList.add(dataSnapshot.getValue(RequestObject.class));
                        }
                        if(resultList.size() == userObject.getUserRequests().size()){
                            valueListener.onSuccess(resultList, null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onFail(String errorMessage) {
                valueListener.onFail(errorMessage);
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
    public static void setMessage(String messageID, Object value) {
        if (!Firebase.isUserLoginned())
            return;
        Objects.requireNonNull(Firebase.getMessagesDatabase()).child(messageID).setValue(value);
    }

    public static void setRequest(String requestID, Object value) {
        if (!Firebase.isUserLoginned())
            return;
        Objects.requireNonNull(Firebase.getRequestsDatabase()).child(requestID).setValue(value);
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
