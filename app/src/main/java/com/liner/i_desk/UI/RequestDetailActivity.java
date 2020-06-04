package com.liner.i_desk.UI;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.liner.i_desk.API.Data.Message;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.AudioRecorder;
import com.liner.i_desk.Utils.Messages.CustomIncomingViewHolder;
import com.liner.i_desk.Utils.Messages.CustomOutcomingViewHolder;
import com.liner.i_desk.Utils.TextUtils;
import com.liner.i_desk.Utils.TimeUtils;
import com.liner.i_desk.Utils.ViewUtils;
import com.liner.i_desk.Utils.Views.AudioRecord.OnRecordListener;
import com.liner.i_desk.Utils.Views.AudioRecord.RecordButton;
import com.liner.i_desk.Utils.Views.AudioRecord.RecordView;
import com.liner.i_desk.Utils.Views.FilePickerBottomSheetDialog;
import com.liner.i_desk.Utils.Views.FirebaseActivity;
import com.liner.i_desk.Utils.Views.ProgressBottomSheetDialog;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;
import com.liner.i_desk.Utils.Views.VerticalTextView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RequestDetailActivity extends FirebaseActivity implements MessageInput.AttachmentsListener, MessageInput.InputListener{
    private List<Message> requestCommentList;
    private AudioRecorder audioRecorder;
    private MessagesListAdapter<Message> messagesListAdapter;
    private MessageInput messageInput;
    private MessagesList messagesList;
    private Request request;
    private String requestID;
    private List<File> fileList = new ArrayList<>();


    private TextView requestDetailTitle;
    private LinearLayout requestDetailTypeLayout;
    private VerticalTextView requestDetailTypeText;
    private TextView requestDetailText;
    private TextView requestDetailDeviceText;
    private TextView requestDetailCreateTime;
    private TextView requestDetailDeadline;
    private Button requestDetailCloseRequest;
    private LinearLayout chatLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        hideKeyboard = false;
        messagesList = findViewById(R.id.messagesList);
        messageInput = findViewById(R.id.detailRequestInputMessage);

        requestDetailTitle = findViewById(R.id.requestDetailTitle);
        requestDetailTypeLayout = findViewById(R.id.requestDetailTypeLayout);
        requestDetailTypeText = findViewById(R.id.requestDetailTypeText);
        requestDetailText = findViewById(R.id.requestDetailText);
        requestDetailDeviceText = findViewById(R.id.requestDetailDeviceText);
        requestDetailCreateTime = findViewById(R.id.requestDetailCreateTime);
        requestDetailDeadline = findViewById(R.id.requestDetailDeadline);
        requestDetailCloseRequest = findViewById(R.id.requestDetailCloseRequest);
        chatLayout = findViewById(R.id.chatLayout);
        request = (Request) getIntent().getSerializableExtra("requestObject");
        requestID = request.getRequestID();



        if(request == null)
            finish();









        requestDetailTitle.setText(request.getRequestTitle());
        switch (request.getRequestType()) {
            case SERVICE:
                requestDetailTypeText.setText("Сервис");
                requestDetailTypeLayout.getBackground().setColorFilter(getResources().getColor(R.color.request_service), PorterDuff.Mode.SRC_IN);
                break;
            case INCIDENT:
                requestDetailTypeText.setText("Инцидент");
                requestDetailTypeLayout.getBackground().setColorFilter(getResources().getColor(R.color.request_incident), PorterDuff.Mode.SRC_IN);
                break;
            case CONSULTATION:
                requestDetailTypeText.setText("Консультация");
                requestDetailTypeLayout.getBackground().setColorFilter(getResources().getColor(R.color.request_consultation), PorterDuff.Mode.SRC_IN);
                break;
        }
        requestDetailText.setText(request.getRequestShortDescription());
        requestDetailDeviceText.setText(request.getRequestUserDeviceDescription());
        requestDetailCreateTime.setText(TimeUtils.convertDate(request.getRequestCreationTime()));
        requestDetailDeadline.setText(TimeUtils.convertDate(request.getRequestDeadlineTime()));

        requestDetailCloseRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(RequestDetailActivity.this);
                simpleDialog.setTitleText("Закрыть заявку?")
                        .setDialogText("Это действие нельзя будет отменить! Вы действительно хотите закрыть заявку?")
                        .setCancel("Нет", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                simpleDialog.close();
                            }
                        })
                        .setDone("Да", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                simpleDialog.close();
                                request.setRequestStatus(Request.Status.CLOSED);
                                FirebaseHelper.updateRequest(requestID, request, new FirebaseHelper.IFirebaseHelperListener() {
                                    @Override
                                    public void onSuccess(Object result) {
                                        finish();
                                    }

                                    @Override
                                    public void onFail(String reason) {

                                    }
                                });
                            }
                        }).build();

                simpleDialog.show();

            }
        });







        requestCommentList = request.getMessageList();
        if (requestCommentList == null) {
            requestCommentList = new ArrayList<>();
            request.setMessageList(requestCommentList);
        }
        final RecordView recordView = findViewById(R.id.record_view);
        RecordButton recordButton = findViewById(R.id.record_button);
        recordButton.setRecordView(recordView);


        recordView.setCancelBounds(0);
        recordView.setLessThanSecondAllowed(true);
        recordView.setSlideToCancelText("Отмена");

        audioRecorder = new AudioRecorder(this);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                audioRecorder.start();
                messageInput.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                audioRecorder.stop();
                audioRecorder.deleteResult();
                messageInput.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFinish(long recordTime) {
                audioRecorder.stop();
                String time = getHumanTimeText(recordTime);
                Log.d("RecordView", "onFinish");
                Log.d("RecordTime", time);
                Log.d("RecordTime", "file: "+audioRecorder.getResult().getAbsolutePath());
                messageInput.setVisibility(View.VISIBLE);
            }
        });




        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(CustomIncomingViewHolder.class, R.layout.item_custom_incoming_message)
                .setOutcomingTextConfig(CustomOutcomingViewHolder.class, R.layout.item_custom_outcoming_message);
        messagesListAdapter = new MessagesListAdapter<>(firebaseUser.getUid(), holdersConfig, new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        });
        messagesList.setAdapter(messagesListAdapter);
        messageInput.setInputListener(this);
        messageInput.setAttachmentsListener(this);

    }


    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    @Override
    public void onAddAttachments() {
        final FilePickerBottomSheetDialog.Builder fp = new FilePickerBottomSheetDialog.Builder(RequestDetailActivity.this);
        fp.setTitleText("Прикрепить файл")
                .setCancel("Отмена", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fp.close();
                        if(fileList.size() == 0)
                            messageInput.getButton().setEnabled(false);
                    }
                })
                .setDone("Прикрепить", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fp.close();
                        fileList.add(fp.getDialog().getFile());
                        messageInput.getButton().setEnabled(true);
                    }
                }).build();
        fp.show();
    }



    @Override
    public void onFirebaseChanged() {
        FirebaseHelper.getRequestByID(requestID, new FirebaseHelper.IFirebaseHelperListener() {
            @Override
            public void onSuccess(Object result) {
                request = (Request) result;
                if(request.getRequestStatus() == Request.Status.CLOSED){
                    chatLayout.setVisibility(View.GONE);
                    requestDetailCloseRequest.setVisibility(View.GONE);
                } else {
                    if(user.getUserAccountType() == User.Type.SERVICE) {
                        requestDetailCloseRequest.setVisibility(View.VISIBLE);
                    } else {
                        requestDetailCloseRequest.setVisibility(View.GONE);
                    }
                    chatLayout.setVisibility(View.VISIBLE);
                    messagesListAdapter.clear();
                    if (request.getMessageList() != null && !request.getMessageList().isEmpty()) {
                        for (Message message : request.getMessageList()) {
                            messagesListAdapter.addToStart(message, false);
                        }
                    }
                }
            }

            @Override
            public void onFail(String reason) {

            }
        });
    }

    @Override
    public void onUserObtained(User user) {

    }


    @Override
    public boolean onSubmit(CharSequence input) {
        createComment(user.getUserName(), user.getUserUID(), request.getRequestID(), input.toString().trim(), fileList);
        return true;
    }

    private void createComment(final String creatorName, final String creatorID, final String requestID, final String messageText, List<File> messageFiles){
        final ProgressBottomSheetDialog uploadFileDialog = ViewUtils.createProgressDialog(this, "Загрузка файлов", "Подождите, идет загрузка");
        if(messageFiles.size() != 0){
            uploadFileDialog.create();
        }
        FirebaseHelper.uploadFileList(messageFiles, "user_files/" + request.getRequestCreatorID() + "/requests/" + requestID, new FirebaseHelper.UploadListListener() {
            @Override
            public void onFileUploading(int percent, long transferred, long total, String filename) {
                uploadFileDialog.setProgress(percent, total, transferred, filename);
            }

            @Override
            public void onFilesUploaded(List<Request.FileData> fileDataList) {
                uploadFileDialog.close();
                Message message = new Message();
                message.setRequestID(requestID);
                message.setMessageCreationTime(TimeUtils.getCurrentTime(TimeUtils.Type.SERVER));
                message.setMessageCreatorName(creatorName);
                message.setMessageCreatorID(creatorID);
                message.setMessageCreatorPhotoURL(user.getUserPhotoURL());
                message.setMessageText(messageText);
                message.setFileDataList(fileDataList);
                message.setMessageReaded(false);
                message.setMessageID(TextUtils.generateRandomString(20));
                FirebaseHelper.addNewMessage(requestID, message);
                fileList.clear();
            }

            @Override
            public void onFileUploadFail(String reason) {

            }

            @Override
            public void onListEmpty() {
                Message message = new Message();
                message.setRequestID(requestID);
                message.setMessageCreationTime(TimeUtils.getCurrentTime(TimeUtils.Type.SERVER));
                message.setMessageCreatorName(creatorName);
                message.setMessageCreatorID(creatorID);
                message.setMessageCreatorPhotoURL(user.getUserPhotoURL());
                message.setMessageText(messageText);
                message.setMessageReaded(false);
                message.setFileDataList(new ArrayList<Request.FileData>());
                message.setMessageID(TextUtils.generateRandomString(20));
                FirebaseHelper.addNewMessage(requestID, message);
            }
        });
    }
}
