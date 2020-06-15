package com.liner.views.MediaPicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liner.utils.ImageUtils;
import com.liner.views.R;
import com.liner.views.YSMarqueTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MediaPickerImageAdapter extends RecyclerView.Adapter<MediaPickerImageAdapter.ViewHolder> {
    private List<MediaPickerFile> mediaPickerFileList;
    private Context context;
    private AdapterCallback adapterCallback;
    private List<Bitmap> bitmaps;

    public MediaPickerImageAdapter(List<MediaPickerFile> mediaPickerFileList, Context context) {
        this.mediaPickerFileList = mediaPickerFileList;
        this.context = context;
        this.bitmaps = new ArrayList<>();
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position, final List<Object> payloads) {
        final MediaPickerFile mediaPickerFile = mediaPickerFileList.get(position);
        if(!payloads.isEmpty()){
            for(Object object:payloads){
                if(object.equals("SELECTION")){
                    holder.mediaPickerImageSelection.setVisibility((mediaPickerFile.isSelected()) ? View.VISIBLE : View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPickerFile.isSelected()) {
                                holder.itemView.animate()
                                        .scaleX(0.95f)
                                        .scaleY(0.95f)
                                        .alpha(0.8f)
                                        .setInterpolator(new OvershootInterpolator())
                                        .setDuration(300)
                                        .start();
                            } else {
                                holder.itemView.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .alpha(1f)
                                        .setInterpolator(new OvershootInterpolator())
                                        .setDuration(300)
                                        .start();
                            }
                        }
                    }, 100);
                }
            }
        } else {
            onBindViewHolder(holder, position);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final MediaPickerFile mediaPickerFile = mediaPickerFileList.get(position);
        Picasso.get().load(mediaPickerFile.getFile()).into(holder.mediaPickerImageItem, new Callback() {
            @Override
            public void onSuccess() {
                bitmaps.add(ImageUtils.drawableToBitmap(holder.mediaPickerImageItem.getDrawable()));
            }

            @Override
            public void onError(Exception e) {

            }
        });
        holder.mediaPickerImageFilename.setText(mediaPickerFile.getFile().getName());
        holder.mediaPickerImageSelection.setVisibility((mediaPickerFile.isSelected()) ? View.VISIBLE : View.GONE);
        holder.mediaPickerImageFilename.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mediaPickerFileList.size();
    }


    public interface AdapterCallback {
        void onItemClick(int position, MediaPickerFile mediaPickerFile);

        void onItemLongClick(int position, ImageView imageView);
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
                    if (adapterCallback != null)
                        adapterCallback.onItemClick(getAdapterPosition(), mediaPickerFileList.get(getAdapterPosition()));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (adapterCallback != null)
                        adapterCallback.onItemLongClick(getAdapterPosition(), mediaPickerImageItem);
                    showFullPreview(bitmaps.get(getAdapterPosition()));
                    return true;
                }
            });
        }
    }

    public void showFullPreview(Bitmap bitmap){
        final Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.anim.item_in;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.image_preview);
        ImageView imageView = dialog.findViewById(R.id.mediaPickerImagePreview);
        imageView.setImageBitmap(bitmap);
        dialog.show();
    }
}
