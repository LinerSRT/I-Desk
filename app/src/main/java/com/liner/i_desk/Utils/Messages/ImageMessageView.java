package com.liner.i_desk.Utils.Messages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.Views.VideoPlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageMessageView extends BaseMessageItem{
    private Request.FileData fileData;
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

    public void setup(Request.FileData data) {
        this.fileData = data;
        showView();
        Picasso.get().load(fileData.getDownloadURL()).into(messageImage);

    }
}
