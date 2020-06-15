package com.liner.views.MediaPicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liner.utils.FileUtils;
import com.liner.views.R;
import com.liner.views.YSMarqueTextView;
import com.liner.views.YSTextView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class MediaPickerImageAdapter extends RecyclerView.Adapter<MediaPickerImageAdapter.ViewHolder> {
    private List<MediaPickerFile> mediaPickerFileList;
    private Context context;
    private AdapterCallback adapterCallback;

    public MediaPickerImageAdapter(List<MediaPickerFile> mediaPickerFileList, Context context) {
        this.mediaPickerFileList = mediaPickerFileList;
        this.context = context;
    }

    public void setAdapterCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.media_picker_image_selector, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaPickerFile mediaPickerFile = mediaPickerFileList.get(position);
        Picasso.get().load(mediaPickerFile.getFile()).into(holder.mediaPickerImageItem);
        holder.mediaPickerImageFilename.setText(mediaPickerFile.getFile().getName());
        holder.mediaPickerImageSelection.setVisibility((mediaPickerFile.isSelected())?View.VISIBLE:View.GONE);
    }

    @Override
    public int getItemCount() {
        return mediaPickerFileList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaPickerImageItem;
        ImageView mediaPickerImageSelection;
        YSMarqueTextView mediaPickerImageFilename;

        public ViewHolder(final View itemView) {
            super(itemView);
            mediaPickerImageItem = itemView.findViewById(R.id.mediaPickerImageItem);
            mediaPickerImageSelection = itemView.findViewById(R.id.mediaPickerImageSelection);
            mediaPickerImageFilename = itemView.findViewById(R.id.mediaPickerImageFilename);
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

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            itemView.animate()
                                    .scaleX(3f)
                                    .scaleY(3f)
                                    .setInterpolator(new OvershootInterpolator())
                                    .setDuration(300)
                                    .start();
                            break;
                        case MotionEvent.ACTION_UP:
                            itemView.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setInterpolator(new AccelerateInterpolator())
                                    .setDuration(300)
                                    .start();
                            break;
                    }


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
