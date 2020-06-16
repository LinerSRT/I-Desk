package com.liner.i_desk.Fragments.Request.Messages;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.R;
import com.liner.views.BaseItem;
import com.squareup.picasso.Picasso;

public class ImageMessageView extends BaseItem {
    private FileObject fileObject;
    private ImageView messageImage;

    public ImageMessageView(Context context) {
        super(context);
    }

    public ImageMessageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.message_imagetype_layout, this);
    }

    @Override
    protected void onFindViewById() {
        messageImage = findViewById(R.id.messageImageTypeView);
    }

    public void setup(FileObject fileObject) {
        this.fileObject = fileObject;
        showView();
        Picasso.get().load(fileObject.getFileURL()).into(messageImage);
    }
}
