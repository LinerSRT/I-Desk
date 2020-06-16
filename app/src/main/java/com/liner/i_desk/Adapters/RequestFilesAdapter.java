package com.liner.i_desk.Adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.github.abdularis.buttonprogress.DownloadButtonProgress;
import com.google.firebase.database.DatabaseReference;
import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.Storage.FirebaseFileManager;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.i_desk.R;
import com.liner.utils.FileUtils;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.YSMarqueTextView;
import com.liner.views.YSTextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RequestFilesAdapter extends RecyclerView.Adapter<RequestFilesAdapter.FilesViewHolder> {
    private Activity activity;
    public DatabaseListener databaseListener;
    private List<FileObject> fileObjectList;
    private RequestObject requestObject;
    private FirebaseFileManager firebaseFileManager;
    private File file;
    private BaseDialog deleteWarnDialog;


    public void setRequestObject(RequestObject requestObject) {
        this.requestObject = requestObject;
    }

    public void addFile(FileObject fileObject){
        fileObjectList.add(fileObject);
        notifyItemInserted(fileObjectList.size()-1);
    }

    public RequestFilesAdapter(Activity activity, final RequestObject requestObject) {
        this.activity = activity;
        this.requestObject = requestObject;
        this.fileObjectList = new ArrayList<>();
        deleteWarnDialog = BaseDialogBuilder.buildFast(activity,
                "Вы действительно хотите удалить данный файл из облачного хранилища?",
                "ВНИМАНИЕ! Это действие нельзя будет отменить!",
                "Да",
                "Отмена",
                BaseDialogBuilder.Type.WARNING,
                null,
                null
        );
        databaseListener = new DatabaseListener() {
            @Override
            public void onFileAdded(final FileObject fileObject, int position, final String key) {
                super.onFileAdded(fileObject, position, key);
                if (!contain(fileObject)) {
                    HashMap<String, String> requestFiles = requestObject.getRequestFiles();
                    if(requestFiles.containsKey(key)) {
                        if (fileObject.getFileCreatorID().equals(requestObject.getRequestCreatorID()) || fileObject.getFileCreatorID().equals(requestObject.getRequestAcceptorID())) {
                            fileObjectList.add(fileObject);
                            if (fileObjectList.size() <= 0) {
                                notifyItemInserted(0);
                            } else {
                                notifyItemInserted(fileObjectList.size() - 1);
                            }
                        }
                    }
                }

            }

            @Override
            public void onFileChanged(FileObject fileObject, int position, String key) {
                super.onFileChanged(fileObject, position, key);
                if (contain(fileObject)) {
                    int index = getIndex(fileObject);
                    fileObjectList.set(index, fileObject);
                    notifyItemChanged(index);
                }


            }

            @Override
            public void onFileDeleted(FileObject fileObject, int position, String key) {
                super.onFileDeleted(fileObject, position, key);
                if(contain(fileObject)) {
                    int index = getIndex(fileObject);
                    fileObjectList.remove(index);
                    notifyItemRemoved(index);
                }

            }
        };
    }



    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilesViewHolder(LayoutInflater.from(activity).inflate(R.layout.files_view_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FilesViewHolder holder, int position) {
        final FileObject fileObject = fileObjectList.get(position);
        try {
            holder.fileHolderIcon.setImageDrawable(FileUtils.getFileIcon(activity, fileObject.getFileType()));
        } catch (IOException e) {
            e.printStackTrace();
            holder.fileHolderIcon.setImageResource(R.drawable.file_icon);
        }
        holder.fileHolderName.setText(fileObject.getFileName());
        holder.fileHolderSize.setText(FileUtils.humanReadableByteCount(fileObject.getFileSizeInBytes()));
        firebaseFileManager = new FirebaseFileManager();
        holder.fileHolderDownload.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                firebaseFileManager.download(fileObject, new TaskListener<File>() {
                    @Override
                    public void onStart(String fileUID) {
                        holder.fileHolderDownload.setDeterminate();
                        file = null;
                    }

                    @Override
                    public void onProgress(long transferredBytes, long totalBytes) {
                        holder.fileHolderDownload.setCurrentProgress(Math.round(((float)transferredBytes/(float)totalBytes)*100));
                    }

                    @Override
                    public void onFinish(File result, String fileUID) {
                        holder.fileHolderDownload.setFinish();
                        file = result;
                    }

                    @Override
                    public void onFailed(Exception reason) {
                        holder.fileHolderDownload.setIdle();
                        file = null;
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {
                firebaseFileManager.cancel();
                holder.fileHolderDownload.setIdle();
                file = null;

            }

            @Override
            public void onFinishButtonClick(View view) {
                if(file != null)
                    FileUtils.openFile(activity, file, FileUtils.getMimeType(file.getAbsolutePath()));
            }
        });
        holder.fileHolderDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteWarnDialog.setDialogDone("Нет", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteWarnDialog.closeDialog();
                    }
                });
                deleteWarnDialog.setDialogCancel("Да", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseValue.deleteFile(fileObject);
                        FirebaseValue.getRequest(requestObject.getRequestID(), new FirebaseValue.ValueListener() {
                            @Override
                            public void onSuccess(Object object, DatabaseReference databaseReference) {
                                RequestObject item = (RequestObject) object;
                                item.getRequestFiles().remove(fileObject.getFileID());
                                FirebaseValue.setRequest(requestObject.getRequestID(), item);
                                requestObject = item;
                            }

                            @Override
                            public void onFail(String errorMessage) {

                            }
                        });
                        deleteWarnDialog.closeDialog();
                    }
                });
                deleteWarnDialog.showDialog();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileObjectList.size();
    }

    public boolean contain(FileObject item) {
        if (fileObjectList.size() <= 0)
            return false;
        for (FileObject requestObject : fileObjectList) {
            if (requestObject.getFileID().equals(item.getFileID())) {
                return true;
            }
        }
        return false;
    }

    public int getIndex(FileObject item) {
        int index = 0;
        if (fileObjectList.size() <= 0)
            return index;

        for (FileObject requestObject : fileObjectList) {
            if (requestObject.getFileID().equals(item.getFileID())) {
                return index;
            }
            index++;
        }
        return index;
    }

    public void onStart() {
        databaseListener.startListening();
    }

    public void onDestroy() {
        databaseListener.stopListening();
    }

    class FilesViewHolder extends RecyclerView.ViewHolder {
        private ImageView fileHolderIcon;
        private YSMarqueTextView fileHolderName;
        private YSTextView fileHolderSize;
        private DownloadButtonProgress fileHolderDownload;
        private ImageButton fileHolderDelete;

        public FilesViewHolder(@NonNull View view) {
            super(view);
            fileHolderIcon = view.findViewById(R.id.fileHolderIcon);
            fileHolderName = view.findViewById(R.id.fileHolderName);
            fileHolderSize = view.findViewById(R.id.fileHolderSize);
            fileHolderDownload = view.findViewById(R.id.fileHolderDownload);
            fileHolderDelete = view.findViewById(R.id.fileHolderDelete);
        }
    }


}
