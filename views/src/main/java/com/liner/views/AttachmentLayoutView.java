package com.liner.views;

import android.content.Context;
import android.graphics.Bitmap;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AttachmentLayoutView extends BaseItem {
    private RecyclerView attachmentRecycler;
    private List<AttachmentItem> attachmentItemList = new ArrayList<>();
    private AttachmentAdapter attachmentAdapter;
    private onAttachmentListener onAttachmentListener;

    public AttachmentLayoutView(Context context) {
        super(context);
    }

    public AttachmentLayoutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFindViewById() {
        attachmentRecycler = findViewById(R.id.attachmentLayoutRecycler);
        attachmentAdapter = new AttachmentAdapter();
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(attachmentRecycler);
        attachmentRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        attachmentRecycler.setAdapter(attachmentAdapter);
    }


    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.attachment_layout_view, this);
    }

    public void addAttachment(AttachmentItem attachmentItem) {
        attachmentItemList.add(attachmentItem);
        attachmentAdapter.notifyItemInserted(attachmentItemList.size() - 1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                attachmentRecycler.smoothScrollToPosition(attachmentItemList.size() - 1);
            }
        }, 50);
    }

    public void clearAttachments(){
        attachmentItemList.clear();
        attachmentAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                attachmentRecycler.smoothScrollToPosition(0);
            }
        }, 50);
    }

    public List<AttachmentItem> getAttachmentItemList() {
        return attachmentItemList;
    }

    public void setOnAttachmentListener(AttachmentLayoutView.onAttachmentListener onAttachmentListener) {
        this.onAttachmentListener = onAttachmentListener;
    }

    public interface onAttachmentListener {
        void onAttachmentRemoved(int position);
        void onAttachemntClicked(int position, AttachmentItem attachmentItem);
    }

    public static class AttachmentItem {
        private Bitmap attachThumb;
        private String attachFileName;
        private File file;

        public AttachmentItem(Bitmap attachThumb, String attachFileName, File file) {
            this.attachThumb = attachThumb;
            this.attachFileName = attachFileName;
            this.file = file;
        }

        public Bitmap getAttachThumb() {
            return attachThumb;
        }

        public void setAttachThumb(Bitmap attachThumb) {
            this.attachThumb = attachThumb;
        }

        public String getAttachFileName() {
            return attachFileName;
        }

        public void setAttachFileName(String attachFileName) {
            this.attachFileName = attachFileName;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }

    private class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.AttachmentHolder> {
        @NonNull
        @Override
        public AttachmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AttachmentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_item_holder, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AttachmentHolder holder, int position) {
            AttachmentItem item = attachmentItemList.get(position);
            holder.attachmentView.setAttachmentThumb(item.getAttachThumb());
            holder.attachmentView.setAttachmentName(item.getAttachFileName());
            holder.attachmentView.showView(AnimationUtils.loadAnimation(getContext(), R.anim.item_in), new OvershootInterpolator());
        }

        @Override
        public int getItemCount() {
            return attachmentItemList.size();
        }

        private class AttachmentHolder extends RecyclerView.ViewHolder {
            private AttachmentView attachmentView;

            public AttachmentHolder(@NonNull View itemView) {
                super(itemView);
                attachmentView = itemView.findViewById(R.id.attachmentItem);
                attachmentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onAttachmentListener != null)
                            onAttachmentListener.onAttachemntClicked(getAdapterPosition(), attachmentItemList.get(getAdapterPosition()));
                    }
                });
                attachmentView.setDismissClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attachmentView.showView(AnimationUtils.loadAnimation(getContext(), R.anim.item_out), new OvershootInterpolator());
                        int position = getAdapterPosition();
                        attachmentItemList.remove(position);
                        notifyItemRemoved(position);
                        attachmentRecycler.scrollToPosition(position - 1);
                        if (onAttachmentListener != null)
                            onAttachmentListener.onAttachmentRemoved(position);
                    }
                });
            }
        }
    }
}
