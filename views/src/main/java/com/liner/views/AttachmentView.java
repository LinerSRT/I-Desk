package com.liner.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class AttachmentView extends BaseItem {
    private ImageView attachmentThumb;
    private YSMarqueTextView attachmentName;


    public AttachmentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFindViewById() {
        attachmentName = findViewById(R.id.attachmentName);
        attachmentThumb = findViewById(R.id.attachmentThumb);
    }

    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.attachment_item, this);
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName.setText(attachmentName);
        requestLayout();
    }


    public void setDismissClickListener(View.OnClickListener listener){
        attachmentName.setOnClickListener(listener);
    }

    public void setAttachmentThumb(Bitmap bitmap) {
        this.attachmentThumb.setImageBitmap(bitmap);
    }
}
