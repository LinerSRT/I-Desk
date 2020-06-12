package com.liner.i_desk.Firebase.Storage;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.utils.FileUtils;
import com.liner.utils.TextUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirebaseFileManager {
    public String fileURL;
    public List<File> fileList;
    public File file;
    private String fileName;
    private String userUID;
    private byte[] data;
    private StorageReference storageReference;
    private String referencePath;

    private int uploadedFiles = 0;

    public FirebaseFileManager(FirebaseUploadTask task) {
        this.fileName = task.fileName;
        this.userUID = task.userUID;
        this.data = task.data;
        this.fileList = task.fileList;
        this.file = task.file;
        this.referencePath = userUID + File.separator + fileName;
        storageReference = FirebaseStorage.getInstance().getReference(referencePath);
    }

    public FirebaseFileManager(FirebaseDownloadTask task) {
        this.userUID = task.userUID;
        this.fileName = task.fileName;
        this.fileURL = task.fileURL;
        this.referencePath = userUID + File.separator + fileName;
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
    }


    public void uploadFiles(final TaskListener<List<FileObject>> listener) {
        final List<FileObject> resultList = new ArrayList<>();
        listener.onStart(null);
        FirebaseValue.getUser(userUID, new FirebaseValue.ValueListener() {
            @Override
            public void onSuccess(Object object, DatabaseReference databaseReference) {
                final UserObject userObject = (UserObject) object;
                for (File item : fileList) {
                    final String fileUID = TextUtils.getUniqueString();
                    storageReference.putFile(Uri.fromFile(item)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull final Task<Uri> fileDownloadURL) {
                                        storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                                            @Override
                                            public void onComplete(@NonNull Task<StorageMetadata> fileMetadata) {
                                                FileObject fileObject = new FileObject(
                                                        fileUID,
                                                        fileName,
                                                        Objects.requireNonNull(fileDownloadURL.getResult()).toString(),
                                                        Objects.requireNonNull(fileMetadata.getResult()).getContentType(),
                                                        userUID,
                                                        fileMetadata.getResult().getCreationTimeMillis(),
                                                        fileMetadata.getResult().getSizeBytes()
                                                );
                                                Objects.requireNonNull(Firebase.getFilesDatabase()).child(fileObject.getFileID()).setValue(fileObject);
                                                if(userObject.getUserFiles() == null)
                                                    userObject.setUserFiles(new ArrayList<String>());
                                                userObject.getUserFiles().add(fileObject.getFileID());
                                                resultList.add(fileObject);
                                                uploadedFiles++;
                                                if (uploadedFiles == fileList.size()) {
                                                    FirebaseValue.setUser(userUID, userObject);
                                                    listener.onFinish(resultList, null);
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                listener.onFailed(task.getException());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onFailed(e);
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                            listener.onProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
                        }
                    });
                }
            }
            @Override
            public void onFail(String errorMessage) {
                listener.onFailed(null);
            }
        });
    }

    public void uploadFile(final TaskListener<FileObject> listener) {
        final String fileUID = TextUtils.getUniqueString();
        listener.onStart(fileUID);
        storageReference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    FirebaseValue.getUser(userUID, new FirebaseValue.ValueListener() {
                        @Override
                        public void onSuccess(Object object, DatabaseReference databaseReference) {
                            final UserObject userObject = (UserObject) object;
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull final Task<Uri> fileDownloadURL) {
                                    storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                                        @Override
                                        public void onComplete(@NonNull Task<StorageMetadata> fileMetadata) {
                                            if (userObject.getUserFiles() == null)
                                                userObject.setUserFiles(new ArrayList<String>());
                                            userObject.getUserFiles().add(fileUID);
                                            final FileObject fileObject = new FileObject(
                                                    fileUID,
                                                    fileName,
                                                    fileDownloadURL.getResult().toString(),
                                                    Objects.requireNonNull(fileMetadata.getResult()).getContentType(),
                                                    userUID,
                                                    fileMetadata.getResult().getCreationTimeMillis(),
                                                    fileMetadata.getResult().getSizeBytes()
                                            );
                                            FirebaseValue.setUser(userUID, userObject);
                                            Firebase.getFilesDatabase().child(fileUID).setValue(fileObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    listener.onFinish(fileObject, null);

                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onFail(String errorMessage) {

                        }
                    });
                } else {
                    listener.onFailed(task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailed(e);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                listener.onProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
            }
        });


    }

    public void uploadBytes(final TaskListener<FileObject> listener) {
        final String fileUID = TextUtils.getUniqueString();
        listener.onStart(fileUID);
        if (file != null) {
            storageReference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        FirebaseValue.getUser(userUID, new FirebaseValue.ValueListener() {
                            @Override
                            public void onSuccess(Object object, DatabaseReference databaseReference) {
                                final UserObject userObject = (UserObject) object;
                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull final Task<Uri> fileDownloadURL) {
                                        storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                                            @Override
                                            public void onComplete(@NonNull Task<StorageMetadata> fileMetadata) {
                                                if (userObject.getUserFiles() == null)
                                                    userObject.setUserFiles(new ArrayList<String>());
                                                userObject.getUserFiles().add(fileUID);
                                                final FileObject fileObject = new FileObject(
                                                        fileUID,
                                                        fileName,
                                                        fileDownloadURL.getResult().toString(),
                                                        Objects.requireNonNull(fileMetadata.getResult()).getContentType(),
                                                        userUID,
                                                        fileMetadata.getResult().getCreationTimeMillis(),
                                                        fileMetadata.getResult().getSizeBytes()
                                                );
                                                if(userObject.getUserFiles() == null)
                                                    userObject.setUserFiles(new ArrayList<String>());
                                                userObject.getUserFiles().add(fileObject.getFileID());
                                                FirebaseValue.setUser(userUID, userObject);
                                                Firebase.getFilesDatabase().child(fileUID).setValue(fileObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        listener.onFinish(fileObject, fileUID);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onFail(String errorMessage) {

                            }
                        });
                    } else {
                        listener.onFailed(task.getException());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.onFailed(e);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    listener.onProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
                }
            });
        } else if (fileList != null) {

            for (File item : fileList) {
                storageReference.putFile(Uri.fromFile(item)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            FirebaseValue.getUser(userUID, new FirebaseValue.ValueListener() {
                                @Override
                                public void onSuccess(Object object, DatabaseReference databaseReference) {
                                    final UserObject userObject = (UserObject) object;
                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<Uri> fileDownloadURL) {
                                            storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                                                @Override
                                                public void onComplete(@NonNull Task<StorageMetadata> fileMetadata) {
                                                    if (userObject.getUserFiles() == null)
                                                        userObject.setUserFiles(new ArrayList<String>());
                                                    userObject.getUserFiles().add(fileUID);
                                                    final FileObject fileObject = new FileObject(
                                                            fileUID,
                                                            fileName,
                                                            fileDownloadURL.getResult().toString(),
                                                            Objects.requireNonNull(fileMetadata.getResult()).getContentType(),
                                                            userUID,
                                                            fileMetadata.getResult().getCreationTimeMillis(),
                                                            fileMetadata.getResult().getSizeBytes()
                                                    );
                                                    FirebaseValue.setUser(userUID, userObject);
                                                    Firebase.getFilesDatabase().child(fileUID).setValue(fileObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            listener.onFinish(fileObject, fileUID);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onFail(String errorMessage) {

                                }
                            });
                        } else {
                            listener.onFailed(task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailed(e);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        listener.onProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
                    }
                });


            }


        } else if (data != null) {
            storageReference.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        FirebaseValue.getUser(userUID, new FirebaseValue.ValueListener() {
                            @Override
                            public void onSuccess(Object object, DatabaseReference databaseReference) {
                                final UserObject userObject = (UserObject) object;
                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull final Task<Uri> fileDownloadURL) {
                                        storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                                            @Override
                                            public void onComplete(@NonNull Task<StorageMetadata> fileMetadata) {
                                                if (userObject.getUserFiles() == null)
                                                    userObject.setUserFiles(new ArrayList<String>());
                                                userObject.getUserFiles().add(fileUID);
                                                final FileObject fileObject = new FileObject(
                                                        fileUID,
                                                        fileName,
                                                        fileDownloadURL.getResult().toString(),
                                                        Objects.requireNonNull(fileMetadata.getResult()).getContentType(),
                                                        userUID,
                                                        fileMetadata.getResult().getCreationTimeMillis(),
                                                        fileMetadata.getResult().getSizeBytes()
                                                );
                                                FirebaseValue.setUser(userUID, userObject);
                                                Firebase.getFilesDatabase().child(fileUID).setValue(fileObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        listener.onFinish(fileObject, fileUID);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onFail(String errorMessage) {

                            }
                        });
                    } else {
                        listener.onFailed(task.getException());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.onFailed(e);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    listener.onProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
                }
            });
        }
    }

    public void download(final TaskListener<File> listener) {
        listener.onStart(null);
        FileDownloader.getImpl().create(fileURL)
                .setPath(FileUtils.getDownloadDir() + fileName)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void started(BaseDownloadTask task) {
                        listener.onStart(null);
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        listener.onProgress(soFarBytes, totalBytes);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        listener.onFinish(new File(task.getPath()), null);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        listener.onFailed(null);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }

}
