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
    private List<Request.RequestComment> requestCommentList;
    private AudioRecorder audioRecorder;
    private MessagesListAdapter<Request.RequestComment> messagesListAdapter;
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
    private Button requestDetailDeleteRequest;

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
        requestDetailDeleteRequest = findViewById(R.id.requestDetailDeleteRequest);

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









        requestCommentList = request.getRequestCommentList();
        if (requestCommentList == null) {
            requestCommentList = new ArrayList<>();
            request.setRequestCommentList(requestCommentList);
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
                messagesListAdapter.clear();
                if(request.getRequestCommentList() != null && !request.getRequestCommentList().isEmpty()){
                    for(Request.RequestComment comment:request.getRequestCommentList()){
                        messagesListAdapter.addToStart(comment, false);
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

    private void createComment(final String creatorName, final String creatorID, final String requestID, final String commentText, List<File> commentFiles){
        final ProgressBottomSheetDialog uploadFileDialog = ViewUtils.createProgressDialog(this, "Загрузка файлов", "Подождите, идет загрузка");
        if(commentFiles.size() != 0){
            uploadFileDialog.create();
        }
        FirebaseHelper.uploadFileList(commentFiles, "user_files/" + request.getRequestCreatorID() + "/requests/" + requestID, new FirebaseHelper.UploadListListener() {
            @Override
            public void onFileUploading(int percent, long transferred, long total, String filename) {
                uploadFileDialog.setProgress(percent, total, transferred, filename);
            }

            @Override
            public void onFilesUploaded(List<Request.FileData> fileDataList) {
                uploadFileDialog.close();
                Request.RequestComment comment = new Request.RequestComment();
                comment.setCommentCreationTime(TimeUtils.getCurrentTime(TimeUtils.Type.SERVER));
                comment.setCommentCreatorName(creatorName);
                comment.setCommentCreatorID(creatorID);
                comment.setCommentCreatorPhotoURL(user.getUserPhotoURL());
                comment.setCommentText(commentText);
                comment.setFileDataList(fileDataList);
                comment.setCommentID(TextUtils.generateRandomString(20));
                FirebaseHelper.addComment(requestID, comment);
                fileList.clear();
            }

            @Override
            public void onFileUploadFail(String reason) {

            }

            @Override
            public void onListEmpty() {
                Request.RequestComment comment = new Request.RequestComment();
                comment.setCommentCreationTime(TimeUtils.getCurrentTime(TimeUtils.Type.SERVER));
                comment.setCommentCreatorName(creatorName);
                comment.setCommentCreatorID(creatorID);
                comment.setCommentCreatorPhotoURL(user.getUserPhotoURL());
                comment.setCommentText(commentText);
                comment.setFileDataList(new ArrayList<Request.FileData>());
                comment.setCommentID(TextUtils.generateRandomString(20));
                FirebaseHelper.addComment(requestID, comment);
            }
        });
    }
}
