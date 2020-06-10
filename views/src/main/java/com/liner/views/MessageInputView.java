package com.liner.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.liner.utils.TextUtils;

public class MessageInputView extends BaseItem implements ExpandLayout.OnExpandCallback, TextWatcher {
    private LinearLayout messageLayout;
    private ExpandLayout messageExpandLayout;
    private AttachmentLayoutView messageAttachmentLayoutView;
    private ImageButton messageAttachmentButton;
    private EditText messageText;
    private ImageButton messageSendButton;
    private YSTextView messageAttachmentVideoButton;
    private YSTextView messageAttachmentImageButton;
    private YSTextView messageAttachmentAudioButton;
    private YSTextView messageAttachmentFileButton;
    private MessageInputCallback messageInputCallback;


    public MessageInputView(Context context) {
        super(context);
    }

    public MessageInputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFindViewById() {
        messageLayout = findViewById(R.id.messageInputLayout);
        messageExpandLayout = findViewById(R.id.messageInputAttachmentExpandLayout);
        messageAttachmentLayoutView = findViewById(R.id.messageInputAttachmentLayout);
        messageAttachmentButton = findViewById(R.id.messageInputAttachmentButton);
        messageText = findViewById(R.id.messageInputEditText);
        messageSendButton = findViewById(R.id.messageInputSendButton);
        messageAttachmentVideoButton = findViewById(R.id.messageInputAttachmentVideo);
        messageAttachmentImageButton = findViewById(R.id.messageInputAttachmentImage);
        messageAttachmentAudioButton = findViewById(R.id.messageInputAttachmentAudio);
        messageAttachmentFileButton = findViewById(R.id.messageInputAttachmentFile);
        messageText.addTextChangedListener(this);
        messageExpandLayout.setOnExpandCallback(this);
        messageAttachmentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(messageInputCallback != null)
                    messageInputCallback.onAttachmentLayoutOpen();
                messageExpandLayout.toggle();
            }
        });
        messageSendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(messageText.getText().toString().length() > 0)
                    if(messageInputCallback != null)
                        messageInputCallback.onMessageSend(messageText.getText().toString());
            }
        });
        messageAttachmentVideoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(messageInputCallback != null)
                    messageInputCallback.onVideoAttach();
            }
        });
        messageAttachmentImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(messageInputCallback != null)
                    messageInputCallback.onImageAttach();
            }
        });
        messageAttachmentAudioButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(messageInputCallback != null)
                    messageInputCallback.onAudioAttach();
            }
        });
        messageAttachmentFileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(messageInputCallback != null)
                    messageInputCallback.onFileAttach();
            }
        });
    }

    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.message_input_layout, this);
    }

    @Override
    public void onExpanded(ExpandLayout expandLayout) {

    }

    @Override
    public void onCollapsed(ExpandLayout expandLayout) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(messageExpandLayout.isExpanded())
            messageExpandLayout.collapse();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void setMessageInputCallback(MessageInputCallback messageInputCallback) {
        this.messageInputCallback = messageInputCallback;
    }

    public void addAttachment(AttachmentLayoutView.AttachmentItem attachmentItem){
        messageAttachmentLayoutView.addAttachment(attachmentItem);
    }

    public void collapseAttachmentLayout(){
        messageExpandLayout.collapse();
    }


    public interface MessageInputCallback{
        void onAttachmentLayoutOpen();
        void onVideoAttach();
        void onImageAttach();
        void onAudioAttach();
        void onFileAttach();
        void onMessageSend(String message);
    }
}
