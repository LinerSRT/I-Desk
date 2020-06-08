package com.liner.bottomdialogs.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liner.bottomdialogs.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.ViewHolder> {
    private List<ItemHolder> images;
    private OnItemSelected onItemSelected;

    public ImagePickerAdapter(List<ItemHolder> images, OnItemSelected onItemSelected) {
        this.images = images;
        this.onItemSelected = onItemSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImagePickerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.imagepicker_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ItemHolder itemHolder = images.get(position);
        Picasso.get().load(new File(itemHolder.getImagePath())).noFade().into(holder.imageView);
        holder.checkBox.setChecked(itemHolder.isSelected());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(ItemHolder item:images)
                    item.setSelected(false);
                itemHolder.setSelected(true);
                notifyDataSetChanged();
                onItemSelected.onSelected(itemHolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private CheckBox checkBox;

        ViewHolder(final View view) {
            super(view);
            imageView = view.findViewById(R.id.imagePickerItemView);
            checkBox = view.findViewById(R.id.imagePickerCheckBox);
            checkBox.setEnabled(false);

        }

    }

    public static class ItemHolder{
        private String imagePath;
        private boolean selected;

        public ItemHolder(String imagePath, boolean selected) {
            this.imagePath = imagePath;
            this.selected = selected;
        }

        public String getImagePath() {
            return imagePath;
        }

        boolean isSelected() {
            return selected;
        }

        void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    public interface OnItemSelected{
        void onSelected(ItemHolder itemHolder);
    }

}
