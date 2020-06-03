package com.liner.i_desk.Utils.Messages;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.FileUtils;
import com.liner.i_desk.Utils.TextUtils;
import com.liner.i_desk.Utils.Views.CircleProgressBar;
import com.liner.i_desk.Utils.Views.VideoPlayerView;
import com.liner.i_desk.Utils.Views.VideoSurfaceView;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;

import java.io.File;
import java.io.IOException;

public class VideoMessageView extends BaseMessageItem{
    private Request.FileData fileData;
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

    public void setup(Request.FileData data) {
        this.fileData = data;
        showView();
        Log.d("TAGTAG", "SETUP VIDEO + "+fileData.toString());
        videoLayout.setVisibility(VISIBLE);
        videoPlayerView.setVideoURL(fileData.getDownloadURL(), false);

    }
}
