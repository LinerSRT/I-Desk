package com.liner.i_desk.API.Data;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Message implements Serializable, IMessage, MessageContentType {
    private String requestID;
    private String messageID;
    private String messageCreatorID;
    private String messageCreatorPhotoURL;
    private long creationTime;
    private String messageCreatorName;
    private String messageText;
    private boolean messageReaded;
    private List<Request.FileData> fileDataList;

    public Message() {
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageCreatorID() {
        return messageCreatorID;
    }

    public void setMessageCreatorID(String messageCreatorID) {
        this.messageCreatorID = messageCreatorID;
    }

    public String getMessageCreatorPhotoURL() {
        return messageCreatorPhotoURL;
    }

    public void setMessageCreatorPhotoURL(String messageCreatorPhotoURL) {
        this.messageCreatorPhotoURL = messageCreatorPhotoURL;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getMessageCreatorName() {
        return messageCreatorName;
    }

    public void setMessageCreatorName(String messageCreatorName) {
        this.messageCreatorName = messageCreatorName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isMessageReaded() {
        return messageReaded;
    }

    public void setMessageReaded(boolean messageReaded) {
        this.messageReaded = messageReaded;
    }

    public List<Request.FileData> getFileDataList() {
        return fileDataList;
    }

    public void setFileDataList(List<Request.FileData> fileDataList) {
        this.fileDataList = fileDataList;
    }

    @Override
    public IUser getUser() {
        return new IUser() {
            @Override
            public String getId() {
                return getMessageCreatorID();
            }

            @Override
            public String getName() {
                return getMessageCreatorName();
            }

            @Override
            public String getAvatar() {
                return getMessageCreatorPhotoURL();
            }
        };
    }

    @Override
    public String getId() {
        return getMessageID();
    }

    @Override
    public String getText() {
        return getMessageText();
    }

    @Override
    public Date getCreatedAt() {
        return new Date(getCreationTime());
    }
}