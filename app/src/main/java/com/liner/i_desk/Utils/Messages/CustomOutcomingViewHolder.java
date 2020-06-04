package com.liner.i_desk.Utils.Messages;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.liner.i_desk.API.Data.Message;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.IDesk;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;
import com.liner.i_desk.Utils.FileUtils;
import com.liner.i_desk.Utils.Views.VideoPlayerView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.File;
import java.util.Objects;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;


public class CustomOutcomingViewHolder extends MessagesListAdapter.OutcomingMessageViewHolder<Message> {
    private VoicePlayerView voicePlayerView;
    private FileMessageView fileMessageView;
    private VideoMessageView videoMessageView;
    private ImageMessageView imageMessageView;
    private TextView messageTimeView;

    public CustomOutcomingViewHolder(View itemView) {
        super(itemView);
        imageMessageView = itemView.findViewById(R.id.outcomingImageMessageView);
        voicePlayerView = itemView.findViewById(R.id.outcomingVoicePlayerView);
        fileMessageView = itemView.findViewById(R.id.outcomingMessageView);
        videoMessageView = itemView.findViewById(R.id.outcomingVideoMessageView);
        messageTimeView = itemView.findViewById(R.id.messageTime);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        videoMessageView.hideView();
        fileMessageView.hideView();
        imageMessageView.hideView();

        if (message.isMessageReaded()) {
            Drawable drawable = IDesk.getContext().getDrawable(R.drawable.message_readed);
            messageTimeView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        } else {
            Drawable drawable = IDesk.getContext().getDrawable(R.drawable.message_sended);
            messageTimeView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }

        if (message.getFileDataList() != null && !message.getFileDataList().isEmpty()) {
            for (Request.FileData fileData : message.getFileDataList()) {
                fileMessageView.setup(fileData);
                if(FileUtils.VideoFormat.SUPPORTED_LIST.contains(fileData.getContentType())){
                    videoMessageView.setup(fileData);
                }
                if(FileUtils.ImageFormat.SUPPORTED_LIST.contains(fileData.getContentType())){
                    imageMessageView.setup(fileData);
                }
//                if(FileUtils.ImageFormat.SUPPORTED_LIST.contains(fileData.getContentType())){
//                    voicePlayerView.setVisibility(View.GONE);
//                    videoPlayerView.setVisibility(View.GONE);
//                    if(!videoPlayerView.isReleased()){
//                        videoPlayerView.release();
//                    }
//                    imageView.setVisibility(View.VISIBLE);
//                    Picasso.get().load(fileData.getDownloadURL()).into(imageView);
//                } else if (FileUtils.VideoFormat.SUPPORTED_LIST.contains(fileData.getContentType())){
//                    voicePlayerView.setVisibility(View.GONE);
//                    videoPlayerView.setVisibility(View.VISIBLE);
//                    imageView.setVisibility(View.GONE);
//                    if(videoPlayerView.isReleased())
//                        videoPlayerView.setVideoURL(fileData.getDownloadURL(), false);
//                } else if (FileUtils.AudioFormat.SUPPORTED_LIST.contains(fileData.getContentType())){
////                    FileLoader.with(IDesk.getContext())
////                            .load(fileData.getDownloadURL(),true)
////                            .fromDirectory(fileData.getFileName(), FileLoader.DIR_INTERNAL)
////                            .asFile(new FileRequestListener<File>() {
////                                @Override
////                                public void onLoad(FileLoadRequest request, FileResponse<File> response) {
////                                    voicePlayerView.setAudio(response.getDownloadedFile().getAbsolutePath());
////                                    voicePlayerView.setVisibility(View.VISIBLE);
////                                }
////
////                                @Override
////                                public void onError(FileLoadRequest request, Throwable t) {
////                                }
////                            });
//                    videoPlayerView.setVisibility(View.GONE);
//                    imageView.setVisibility(View.GONE);
//                } else if (FileUtils.FileFormat.SUPPORTED_LIST.contains(fileData.getContentType())){
//
//                } else if (FileUtils.TextFormat.SUPPORTED_LIST.contains(fileData.getContentType())){
//
//                }
            }
        }
    }




}