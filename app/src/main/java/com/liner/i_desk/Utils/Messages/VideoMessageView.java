package com.liner.i_desk.Utils.Messages;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.R;
import com.liner.views.VideoPlayerView;

public class VideoMessageView extends BaseMessageItem{
    private FileObject fileObject;
    private VideoPlayerView videoPlayerView;
    private FrameLayout videoLayout;

    public VideoMessageView(Context context) {
        super(context);
    }

    public VideoMessageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.message_videotype_layout, this);
    }

    @Override
    protected void onFindViewById() {
        videoPlayerView = findViewById(R.id.messageVideoTypeView);
        videoLayout = findViewById(R.id.messageVideoTypeLayout);
        videoLayout.setVisibility(GONE);
    }

    public void setup(FileObject fileObject) {
        this.fileObject = fileObject;
        showView();
        videoLayout.setVisibility(VISIBLE);
        videoPlayerView.setVideoURL(fileObject.getFileURL(), false);

    }
}
