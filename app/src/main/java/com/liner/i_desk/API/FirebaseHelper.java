package com.liner.i_desk.API;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.Utils.FileUtils;
import com.liner.i_desk.Utils.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {
    public static void getUserModel(String userID, final IFirebaseHelperListener helperListener){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference currentUserReference = firebaseDatabase.getReference().child("Users").child(userID);
        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                helperListener.onSuccess(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                helperListener.onFail(databaseError.getMessage());
            }
        });
    }





    public static void getUserModel(final IFirebaseHelperListener helperListener){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference currentUserReference = firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                helperListener.onSuccess(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                helperListener.onFail(databaseError.getMessage());
            }
        });
    }

    public static void setUserModel(String useruID, User userModel, final IFirebaseHelperListener helperListener){
        getUserDatabase(useruID).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(helperListener != null) {
                    if (task.isSuccessful()) {
                        helperListener.onSuccess(task);
                    } else {
                        helperListener.onFail("Failed");
                    }
                }
            }
        });
    }

    public static void setUserValue(String useruID, String key, Object object, final IFirebaseHelperListener helperListener){
        getUserDatabase(useruID).child(key).setValue(object).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    helperListener.onSuccess(task);
                } else {
                    helperListener.onFail("Failed");
                }
            }
        });
    }


    public static void checkUsersDatabaseExistValue(final Object valueObj, final String databaseKey, final IFirebaseDatabaseValueListener listener){
        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference("Users");
        Query query = usersDatabase.orderByChild(databaseKey).equalTo(String.valueOf(valueObj));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean founded = false;
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    if(String.valueOf(valueObj).equals(String.valueOf(item.child(databaseKey).getValue())) ){
                        founded = true;
                        break;
                    }
                }
                if(!founded) {
                    listener.onValueNotFound();
                } else {
                    listener.onValueExists();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static void uploadFile(File file, String uploadPath, final IFirebaseUploadFileListener firebaseUploadListnener){
        final StorageReference path = FirebaseStorage.getInstance().getReference(uploadPath);
        final UploadTask uploadTask = path.putBytes(FileUtils.getFileByteArray(file));
        firebaseUploadListnener.onUploadStart();
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            firebaseUploadListnener.onUploadFinish(uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            firebaseUploadListnener.onUploadFailed();
                        }
                    });
                } else {
                    firebaseUploadListnener.onUploadFailed();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseUploadListnener.onUploadFailed();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                firebaseUploadListnener.onUpload( Math.round(TextUtils.getPercent(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount())), taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount(), "");
            }
        });
    }

    private static int uploadCountFiles = 0;
    public static void uploadFile(final List<File> fileList, String uploadPath, final IFirebaseUploadFilesListener firebaseUploadFilesListener){
        int listSize = fileList.size();
        final List<String> urls = new ArrayList<>();
        uploadCountFiles = 0;
        for(final File file:fileList){
            uploadFile(file, uploadPath+File.separator+file.getName(), new IFirebaseUploadFileListener() {
                @Override
                public void onUploadStart() {
                    firebaseUploadFilesListener.onUploadStart();
                }

                @Override
                public void onUploadFinish(String fileURL) {
                    uploadCountFiles ++;
                    urls.add(fileURL);
                    if(uploadCountFiles == fileList.size()){
                        firebaseUploadFilesListener.onUploadFinish(urls);
                    }
                }

                @Override
                public void onUploadFailed() {
                    firebaseUploadFilesListener.onUploadFailed();
                }

                @Override
                public void onUpload(int percent, long transferred, long total, String filename) {
                    firebaseUploadFilesListener.onUpload(percent, transferred, total, file.getName());
                }
            });
        }
    }

    public static void uploadFile(byte[] bytes, String uploadPath, final IFirebaseUploadFileListener firebaseUploadListnener){
        final StorageReference path = FirebaseStorage.getInstance().getReference(uploadPath);
        final UploadTask uploadTask = path.putBytes(bytes);
        firebaseUploadListnener.onUploadStart();
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            firebaseUploadListnener.onUploadFinish(uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            firebaseUploadListnener.onUploadFailed();
                        }
                    });
                } else {
                    firebaseUploadListnener.onUploadFailed();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseUploadListnener.onUploadFailed();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                firebaseUploadListnener.onUpload( Math.round(TextUtils.getPercent(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount())), taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount(), "");
            }
        });
    }


    public interface IFirebaseUploadFileListener {
        void onUploadStart();
        void onUploadFinish(String fileURL);
        void onUploadFailed();
        void onUpload(int percent, long transferred, long total, String filename);
    }

    public interface IFirebaseUploadFilesListener {
        void onUploadStart();
        void onUploadFinish(List<String> urls);
        void onUploadFailed();
        void onUpload(int percent, long transferred, long total, String filename);
    }

    public interface IFirebaseDatabaseValueListener {
        void onValueExists();
        void onValueNotFound();
    }

    public interface IFirebaseHelperListener <T>{
        void onSuccess(T result);
        void onFail(String reason);
    }

    public static DatabaseReference getUserDatabase(String userID){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase.getReference().child("Users").child(userID);
    }
    public static DatabaseReference getUserDatabase(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


}
