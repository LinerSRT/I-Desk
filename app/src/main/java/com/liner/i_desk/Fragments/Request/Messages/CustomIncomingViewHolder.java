package com.liner.i_desk.Fragments.Request.Messages;

import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.MessageObject;
import com.liner.i_desk.Firebase.Storage.FirebaseFileManager;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.i_desk.R;
import com.liner.utils.FileUtils;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.File;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;


public class CustomIncomingViewHolder extends MessagesListAdapter.IncomingMessageViewHolder<MessageObject> {
    private VoicePlayerView voicePlayerView;
    private FileMessageView fileMessageView;
    private VideoMessageView videoMessageView;
    private ImageMessageView imageMessageView;
    private TextView userNickName;

    public CustomIncomingViewHolder(View itemView) {
        super(itemView);
        imageMessageView = itemView.findViewById(R.id.incomingImageMessageView);
        fileMessageView = itemView.findViewById(R.id.incomingMessageView);
        videoMessageView = itemView.findViewById(R.id.incomingVideoMessageView);
        voicePlayerView = itemView.findViewById(R.id.incomingVoicePlayerView);
        userNickName = itemView.findViewById(R.id.messageUserName);

    }

    @Override
    public void onBind(MessageObject messageObject) {
        super.onBind(messageObject);
        userNickName.setText(messageObject.getUser().getName());
        if (!messageObject.isRead()) {
            messageObject.setRead(true);
            //todo update message
        }
        if (messageObject.getMessageFiles() != null) {
            for (String key : messageObject.getMessageFiles().keySet()) {
                FirebaseValue.getFile(key, new FirebaseValue.ValueListener() {
                    @Override
                    public void onSuccess(Object object, DatabaseReference databaseReference) {
                        FileObject fileObject = (FileObject) object;
                        fileMessageView.setup(fileObject);
                        if (FileUtils.VideoFormat.SUPPORTED_LIST.contains(fileObject.getFileType())) {
                            videoMessageView.setup(fileObject);
                        } else {
                            videoMessageView.hideView();
                        }
                        if (FileUtils.ImageFormat.SUPPORTED_LIST.contains(fileObject.getFileType())) {
                            imageMessageView.setup(fileObject);
                        } else {
                            imageMessageView.hideView();
                        }
                        if (FileUtils.AudioFormat.SUPPORTED_LIST.contains(fileObject.getFileType())) {
                            new FirebaseFileManager().download(fileObject, new TaskListener<File>() {
                                @Override
                                public void onStart(String fileUID) {

                                }

                                @Override
                                public void onProgress(long transferredBytes, long totalBytes) {

                                }

                                @Override
                                public void onFinish(File result, String fileUID) {
                                    voicePlayerView.setAudio(result.getAbsolutePath());
                                    voicePlayerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onFailed(Exception reason) {

                                }
                            });
                        } else {
                            voicePlayerView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail(String errorMessage) {

                    }
                });
            }
        }
    }
}