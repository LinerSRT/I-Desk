package com.liner.views.MediaPicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liner.views.R;
import com.liner.views.YSTextView;

import java.io.File;
import java.util.List;

public class MediaPickerPathAdapter extends RecyclerView.Adapter<MediaPickerPathAdapter.ViewHolder> {
    private List<File> mediaPickerFileList;
    private Context context;
    private AdapterCallback adapterCallback;

    public MediaPickerPathAdapter(List<File> mediaPickerFileList, Context context) {
        this.mediaPickerFileList = mediaPickerFileList;
        this.context = context;
    }

    public void setAdapterCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.media_picker_path_selector, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File mediaPickerFile = mediaPickerFileList.get(position);
        if (position == 0) {
            holder.mediaPickerFilePath.setText("Хранилище");
        } else {
            holder.mediaPickerFilePath.setText(mediaPickerFile.getName());
        }
    }

    @Override
    public int getItemCount() {
        return mediaPickerFileList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        YSTextView mediaPickerFilePath;

        public ViewHolder(View itemView) {
            super(itemView);
            mediaPickerFilePath = itemView.findViewById(R.id.mediaPickerFilePath);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(adapterCallback != null)
                        adapterCallback.onItemClick(getAdapterPosition(), mediaPickerFileList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface AdapterCallback{
        void onItemClick(int position, File file);
    }
}
