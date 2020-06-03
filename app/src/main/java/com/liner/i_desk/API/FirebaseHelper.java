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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.Utils.FileUtils;
import com.liner.i_desk.Utils.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FirebaseHelper {
    private static int uploadCountFiles = 0;

    public static void getUserModel(String userID, final IFirebaseHelperListener helperListener) {
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

    public static void getUserModel(final IFirebaseHelperListener helperListener) {
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



    public static void getRequestCommentList(final String requestID, final IFirebaseHelperListener helperListener) {
        getUserDatabase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getRequestList() != null)
                for(Request request:user.getRequestList()){
                    if(request.getRequestID().equals(requestID)){
                        helperListener.onSuccess(request.getRequestCommentList());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void setRequestCommentList(final String userID, final String requestID, final List<Request.RequestComment> commentList, final IFirebaseHelperListener helperListener) {
        getUserDatabase(userID).child("requestList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    for (DataSnapshot requestData : child.getChildren())
                        if (requestData.getKey().equals("requestID") && requestData.getValue().equals(requestID)) {
                            DatabaseReference reference = child.getRef().child("requestCommentList");
                            reference.setValue(commentList);
                            helperListener.onSuccess(commentList);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void addComment(final String userID, final String requestID, final Request.RequestComment comment){
        getUserDatabase(userID).child("requestList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    for (DataSnapshot requestData : child.getChildren())
                        if (requestData.getKey().equals("requestID") && requestData.getValue().equals(requestID)) {
                            DatabaseReference reference = child.getRef().child("requestCommentList");
                            List<Request.RequestComment> list = new ArrayList<>();
                            for(DataSnapshot comment:child.child("requestCommentList").getChildren()){
                                list.add(comment.getValue(Request.RequestComment.class));
                            }
                            list.add(comment);
                            reference.setValue(list);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void setUserModel(String userID, User userModel, final IFirebaseHelperListener helperListener) {
        getUserDatabase(userID).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (helperListener != null) {
                    if (task.isSuccessful()) {
                        helperListener.onSuccess(task);
                    } else {
                        helperListener.onFail("Failed");
                    }
                }
            }
        });
    }

    public static void setUserValue(String userID, String key, Object object, final IFirebaseHelperListener helperListener) {
        getUserDatabase(userID).child(key).setValue(object).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    helperListener.onSuccess(task);
                } else {
                    helperListener.onFail("Failed");
                }
            }
        });
    }

    public static void checkUsersDatabaseExistValue(final Object valueObj, final String databaseKey, final IFirebaseDatabaseValueListener listener) {
        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference("Users");
        Query query = usersDatabase.orderByChild(databaseKey).equalTo(String.valueOf(valueObj));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean founded = false;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    if (String.valueOf(valueObj).equals(String.valueOf(item.child(databaseKey).getValue()))) {
                        founded = true;
                        break;
                    }
                }
                if (!founded) {
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




    public static DatabaseReference getUserDatabase(String userID) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase.getReference().child("Users").child(userID);
    }
    public static DatabaseReference getUsersDatabase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase.getReference().child("Users");
    }

    public static DatabaseReference getUserDatabase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


    public interface IFirebaseDatabaseValueListener {
        void onValueExists();

        void onValueNotFound();
    }

    public interface IFirebaseHelperListener<T> {
        void onSuccess(T result);

        void onFail(String reason);
    }









    public static void uploadFile(final File file, String uploadPath, final UploadListener uploadListener) {
        final StorageReference path = FirebaseStorage.getInstance().getReference(uploadPath);
        final UploadTask uploadTask = path.putBytes(FileUtils.getFileByteArray(file));
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
                                public void onSuccess(StorageMetadata storageMetadata) {
                                    uploadListener.onFileUploaded(new Request.FileData(fileURL, storageMetadata.getName(), storageMetadata.getPath(),
                                            storageMetadata.getContentType(), storageMetadata.getContentEncoding(), storageMetadata.getSizeBytes(), storageMetadata.getCreationTimeMillis()));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    uploadListener.onFileUploadFail(e.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadListener.onFileUploadFail(e.getMessage());
                        }
                    });
                } else {
                    uploadListener.onFileUploadFail(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadListener.onFileUploadFail(e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                uploadListener.onFileUploading(Math.round(TextUtils.getPercent(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount())), taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount(), file.getName());
            }
        });
    }

    public static void uploadByteArray(byte[] bytes, final String filename, String uploadPath, final UploadListener uploadListener) {
        final StorageReference path = FirebaseStorage.getInstance().getReference(uploadPath);
        final UploadTask uploadTask = path.putBytes(bytes);
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
                                public void onSuccess(StorageMetadata storageMetadata) {
                                    uploadListener.onFileUploaded(new Request.FileData(fileURL, storageMetadata.getName(), storageMetadata.getPath(),
                                            storageMetadata.getContentType(), storageMetadata.getContentEncoding(), storageMetadata.getSizeBytes(), storageMetadata.getCreationTimeMillis()));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    uploadListener.onFileUploadFail(e.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadListener.onFileUploadFail(e.getMessage());
                        }
                    });
                } else {
                    uploadListener.onFileUploadFail(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadListener.onFileUploadFail(e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                uploadListener.onFileUploading(Math.round(TextUtils.getPercent(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount())), taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount(), filename);
            }
        });
    }

    public static void uploadFileList(List<File> fileList, String uploadPath, final UploadListListener uploadListListener) {
        final List<Request.FileData> fileDataList = new ArrayList<>();
        uploadCountFiles = 0;
        if(!fileList.isEmpty()) {
            for (final File file : fileList) {
                uploadFile(file, uploadPath + File.separator + file.getName(), new FirebaseHelper.UploadListener() {
                    @Override
                    public void onFileUploading(int percent, long transferred, long total, String filename) {
                        uploadListListener.onFileUploading(percent, transferred, total, filename);
                    }

                    @Override
                    public void onFileUploaded(Request.FileData fileData) {
                        uploadCountFiles++;
                        fileDataList.add(fileData);
                        if (uploadCountFiles == fileDataList.size()) {
                            uploadListListener.onFilesUploaded(fileDataList);
                        }
                    }

                    @Override
                    public void onFileUploadFail(String reason) {
                        uploadListListener.onFileUploadFail(reason);
                    }
                });
            }
        } else {
            uploadListListener.onListEmpty();
        }
    }


    public interface UploadListener{
        void onFileUploading(int percent, long transferred, long total, String filename);
        void onFileUploaded(Request.FileData fileData);
        void onFileUploadFail(String reason);
    }
    public interface UploadListListener{
        void onFileUploading(int percent, long transferred, long total, String filename);
        void onFilesUploaded(List<Request.FileData> fileDataList);
        void onFileUploadFail(String reason);
        void onListEmpty();
    }

}
