package com.liner.i_desk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.R;
import com.liner.utils.FileUtils;
import com.liner.views.FileView;

import java.util.List;

public class ProfileFileAdapter extends RecyclerView.Adapter<ProfileFileAdapter.ViewHolder> {
    private List<FileObject> holderList;

    public ProfileFileAdapter(List<FileObject> holderList) {
        this.holderList = holderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_file_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileObject item = holderList.get(position);
        holder.fileView.setFileName(item.getFileName());
        holder.fileView.setFileSize(FileUtils.humanReadableByteCount(item.getFileSizeInBytes()));
        holder.fileView.setFileType(item.getFileType());
        holder.fileView.showView();
    }

    @Override
    public int getItemCount() {
        return holderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private FileView fileView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileView = itemView.findViewById(R.id.profileFileView);
        }
    }
}
