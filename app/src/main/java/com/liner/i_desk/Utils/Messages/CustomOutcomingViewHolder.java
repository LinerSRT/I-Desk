package com.liner.i_desk.Utils.Messages;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.IDesk;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.FileUtils;
import com.liner.i_desk.Utils.Views.VideoPlayerView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.File;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;


public class CustomOutcomingViewHolder extends MessagesListAdapter.OutcomingMessageViewHolder<Request.RequestComment> {
    private ImageView imageView;
    private VideoPlayerView videoPlayerView;
    private VoicePlayerView voicePlayerView;

    public CustomOutcomingViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.outcomingImagesHolder);
        videoPlayerView = itemView.findViewById(R.id.outcomingVideoPlayerView);
        voicePlayerView = itemView.findViewById(R.id.outcomingVoicePlayerView);
    }

    @Override
    public void onBind(Request.RequestComment message) {
        super.onBind(message);
        imageView.setVisibility(View.GONE);
        videoPlayerView.setVisibility(View.GONE);
        voicePlayerView.setVisibility(View.GONE);
        if (message.getFileDataList() != null && !message.getFileDataList().isEmpty()) {
            for (Request.FileData fileData : message.getFileDataList()) {
                if(FileUtils.ImageFormat.SUPPORTED_LIST.contains(fileData.getContentType())){
                    voicePlayerView.setVisibility(View.GONE);
                    videoPlayerView.setVisibility(View.GONE);
                    if(!videoPlayerView.isReleased()){
                        videoPlayerView.release();
                    }
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(fileData.getDownloadURL()).into(imageView);
                } else if (FileUtils.VideoFormat.SUPPORTED_LIST.contains(fileData.getContentType())){
                    voicePlayerView.setVisibility(View.GONE);
                    videoPlayerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    if(videoPlayerView.isReleased())
                        videoPlayerView.setVideoURL(fileData.getDownloadURL(), false);
                } else if (FileUtils.AudioFormat.SUPPORTED_LIST.contains(fileData.getContentType())){
                    FileLoader.with(IDesk.getContext())
                            .load(fileData.getDownloadURL(),true)
                            .fromDirectory(fileData.getFileName(), FileLoader.DIR_INTERNAL)
                            .asFile(new FileRequestListener<File>() {
                                @Override
                                public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                    voicePlayerView.setAudio(response.getDownloadedFile().getAbsolutePath());
                                    voicePlayerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError(FileLoadRequest request, Throwable t) {
                                }
                            });
                    videoPlayerView.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                } else if (FileUtils.FileFormat.SUPPORTED_LIST.contains(fileData.getContentType())){

                } else if (FileUtils.TextFormat.SUPPORTED_LIST.contains(fileData.getContentType())){

                }
            }
        }
    }




}