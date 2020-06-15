package com.liner.views.MediaPicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liner.utils.FileUtils;
import com.liner.views.R;
import com.liner.views.YSTextView;

import java.io.IOException;
import java.util.List;

public class MediaPickerAdapter extends RecyclerView.Adapter<MediaPickerAdapter.ViewHolder> {
    private List<MediaPickerFile> mediaPickerFileList;
    private Context context;
    private AdapterCallback adapterCallback;

    public MediaPickerAdapter(List<MediaPickerFile> mediaPickerFileList, Context context) {
        this.mediaPickerFileList = mediaPickerFileList;
        this.context = context;
    }

    public void setAdapterCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.media_picker_selector, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaPickerFile mediaPickerFile = mediaPickerFileList.get(position);
        if(mediaPickerFile.getFile().isFile()){
            holder.mediaPickerFileInfo.setText(FileUtils.humanReadableByteCount(mediaPickerFile.getFile().length()));
            try {
                holder.mediaPickerFileItem.setImageDrawable(FileUtils.getFileIcon(context, mediaPickerFile.getFile()));
            } catch (IOException e) {
                e.printStackTrace();
                holder.mediaPickerFileItem.setImageResource(R.drawable.file_icon);
            }


        } else {
            holder.mediaPickerFileItem.setImageResource(R.drawable.folder_icon);
            holder.mediaPickerFileInfo.setText(mediaPickerFile.getFile().getPath());
        }
        holder.mediaPickerFileName.setText(mediaPickerFile.getFile().getName());
        holder.mediaPickerFileSelection.setVisibility((mediaPickerFile.isSelected())?View.VISIBLE:View.GONE);
    }

    @Override
    public int getItemCount() {
        return mediaPickerFileList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        YSTextView mediaPickerFileName;
        YSTextView mediaPickerFileInfo;
        ImageView mediaPickerFileItem;
        ImageView mediaPickerFileSelection;

        public ViewHolder(View itemView) {
            super(itemView);
            mediaPickerFileName = itemView.findViewById(R.id.mediaPickerFileName);
            mediaPickerFileInfo = itemView.findViewById(R.id.mediaPickerFileInfo);
            mediaPickerFileItem = itemView.findViewById(R.id.mediaPickerFileItem);
            mediaPickerFileSelection = itemView.findViewById(R.id.mediaPickerFileSelection);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(adapterCallback != null)
                        adapterCallback.onItemClick(getAdapterPosition(), mediaPickerFileList.get(getAdapterPosition()));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(adapterCallback != null)
                        adapterCallback.onItemLongClick(getAdapterPosition(), mediaPickerFileList.get(getAdapterPosition()));
                    return true;
                }
            });
        }
    }

    public interface AdapterCallback{
        void onItemClick(int position, MediaPickerFile mediaPickerFile);
        void onItemLongClick(int position, MediaPickerFile mediaPickerFile);
    }
}
