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

import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.liner.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class FileListLayoutView extends BaseItem {
    private RecyclerView fileRecycler;
    private List<ChosenFile> fileItemList = new ArrayList<>();
    private FileAdapter fileAdapter;
    private Activity activity;


    public FileListLayoutView(Context context) {
        super(context);
    }

    public FileListLayoutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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

    public void addFile(ChosenFile file) {

        fileItemList.add(file);
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
    }


    public int findPositionForObject(ChosenFile file) {
        return fileItemList.indexOf(file);
    }

    public List<ChosenFile> getFileItemList() {
        return fileItemList;
    }

    public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {
        @NonNull
        @Override
        public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item_holder, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FileHolder holder, int position) {
            ChosenFile item = fileItemList.get(position);
            holder.fileView.setFileType(item.getMimeType());
            holder.fileView.setFileName(item.getDisplayName());
            holder.fileView.setFileSize(FileUtils.humanReadableByteCount(item.getSize()));
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
                fileView.getDeleteButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (activity != null) {
                            final BaseDialog baseDialog = new BaseDialogBuilder(activity)
                                    .setDialogText("Вы действительно хотите удалить файл из списка? ")
                                    .setDialogTitle("Удаление")
                                    .setDialogType(BaseDialogBuilder.Type.QUESTION)
                                    .build();
                            baseDialog.setDialogCancel("Отмена", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    baseDialog.closeDialog();
                                }
                            });
                            baseDialog.setDialogDone("Удалить", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    baseDialog.closeDialog();
                                    removeFile(getAdapterPosition());
                                }
                            });
                            baseDialog.showDialog();
                        }
                    }
                });
            }
        }
    }
}
