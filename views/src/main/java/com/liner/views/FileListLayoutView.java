package com.liner.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.liner.bottomdialogs.SimpleDialog;
import com.liner.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class FileListLayoutView extends BaseItem {
    private RecyclerView fileRecycler;
    private List<FileItem> fileItemList = new ArrayList<>();
    private FileAdapter fileAdapter;
    private Activity activity;

    private OnDeleteListener onDeleteListener;

    public FileListLayoutView(Context context) {
        super(context);
    }

    public FileListLayoutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onFindViewById() {
        fileRecycler = findViewById(R.id.fileLayoutRecycler);
        fileAdapter = new FileAdapter();
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(fileRecycler);
        fileRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        fileRecycler.setAdapter(fileAdapter);
    }


    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.file_layout_view, this);
    }

    public void addFile(FileItem FileItem) {
        fileItemList.add(FileItem);
        fileAdapter.notifyItemInserted(fileItemList.size() - 1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fileRecycler.smoothScrollToPosition(fileItemList.size() - 1);
            }
        }, 50);
    }

    public void removeFile(int position) {
        fileItemList.remove(position);
        fileAdapter.notifyItemRemoved(position);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!fileItemList.isEmpty())
                    fileRecycler.smoothScrollToPosition(fileItemList.size() - 1);
            }
        }, 50);
    }

    public void removeFile(String filename) {
        int position = -1;
        for (FileItem fileItem : fileItemList) {
            position++;
            if (fileItem.getFileName().equals(filename))
                break;
        }
        if (position != -1)
            removeFile(position);
    }

    public List<FileItem> getFileItemList() {
        return fileItemList;
    }

    public interface OnDeleteListener {
        void onDelete(String fileID, String filename);
    }

    public static class FileItem {
        private String fileID;
        private String fileName;
        private String fileType;
        private long fileSize;

        public FileItem(String fileID, String fileName, String fileType, long fileSize) {
            this.fileID = fileID;
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileSize = fileSize;
        }

        public String getFileID() {
            return fileID;
        }

        public void setFileID(String fileID) {
            this.fileID = fileID;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }
    }

    public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {
        @NonNull
        @Override
        public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item_holder, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FileHolder holder, int position) {
            FileItem item = fileItemList.get(position);
            holder.fileView.setFileSize(FileUtils.humanReadableByteCount(item.getFileSize()));
            holder.fileView.setFileName(item.getFileName());
            holder.fileView.setFileType(item.getFileType());
            holder.fileView.showView(AnimationUtils.loadAnimation(getContext(), R.anim.item_in), new OvershootInterpolator());
        }

        @Override
        public int getItemCount() {
            return fileItemList.size();
        }


        private class FileHolder extends RecyclerView.ViewHolder {
            private FileView fileView;

            public FileHolder(@NonNull View itemView) {
                super(itemView);
                fileView = itemView.findViewById(R.id.fileView);
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (activity != null && onDeleteListener != null) {
                            final SimpleDialog.Builder deleteDialog = new SimpleDialog.Builder(activity);
                            deleteDialog.setTitleText("Удалить?")
                                    .setDialogText("Удалить файл из облачного хралилища? Это действие невозможно будет отменить, а так же доступ к файлу будет удален!")
                                    .setDone("Удалить", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            deleteDialog.close();
                                            onDeleteListener.onDelete(fileItemList.get(getAdapterPosition()).getFileID(),fileItemList.get(getAdapterPosition()).getFileName());
                                        }
                                    })
                                    .setCancel("Отмена", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            deleteDialog.close();
                                        }
                                    }).build();
                            deleteDialog.show();
                        }
                        return false;
                    }
                });
            }
        }
    }
}
