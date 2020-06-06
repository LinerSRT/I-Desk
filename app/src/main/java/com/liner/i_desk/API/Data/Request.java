package com.liner.i_desk.API.Data;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class Request implements Serializable {
    public enum Type {
        INCIDENT,
        SERVICE,
        CONSULTATION
    }
    public enum Priority{
        LOW,
        MEDIUM,
        HIGH
    }
    public enum Status{
        OPENNED,
        WORK,
        CLOSED
    }
    private String requestID;
    private String requestCreatorID;
    private Type requestType;
    private Priority requestPriority;
    private Status requestStatus;
    private String requestCreationTime;
    private String requestDeadlineTime;
    private String requestTitle;
    private String requestUserDeviceDescription;
    private String requestShortDescription;
    private List<RequestCheckList> requestCheckList;
    private List<FileData> requestFiles;
    private List<Message> messageList;


    public Request() {
    }


    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestCreatorID() {
        return requestCreatorID;
    }

    public void setRequestCreatorID(String requestCreatorID) {
        this.requestCreatorID = requestCreatorID;
    }

    public Type getRequestType() {
        return requestType;
    }

    public void setRequestType(Type requestType) {
        this.requestType = requestType;
    }

    public Priority getRequestPriority() {
        return requestPriority;
    }

    public void setRequestPriority(Priority requestPriority) {
        this.requestPriority = requestPriority;
    }

    public Status getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Status requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestCreationTime() {
        return requestCreationTime;
    }

    public void setRequestCreationTime(String requestCreationTime) {
        this.requestCreationTime = requestCreationTime;
    }

    public String getRequestDeadlineTime() {
        return requestDeadlineTime;
    }

    public void setRequestDeadlineTime(String requestDeadlineTime) {
        this.requestDeadlineTime = requestDeadlineTime;
    }

    public String getRequestTitle() {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public String getRequestUserDeviceDescription() {
        return requestUserDeviceDescription;
    }

    public void setRequestUserDeviceDescription(String requestUserDeviceDescription) {
        this.requestUserDeviceDescription = requestUserDeviceDescription;
    }

    public String getRequestShortDescription() {
        return requestShortDescription;
    }

    public void setRequestShortDescription(String requestShortDescription) {
        this.requestShortDescription = requestShortDescription;
    }

    public List<RequestCheckList> getRequestCheckList() {
        return requestCheckList;
    }

    public void setRequestCheckList(List<RequestCheckList> requestCheckList) {
        this.requestCheckList = requestCheckList;
    }

    public List<FileData> getRequestFiles() {
        return requestFiles;
    }

    public void setRequestFiles(List<FileData> requestFiles) {
        this.requestFiles = requestFiles;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }


    @Override
    public String toString() {
        return "Request{" +
                "requestID='" + requestID + '\'' +
                ", requestCreatorID='" + requestCreatorID + '\'' +
                ", requestType=" + requestType +
                ", requestPriority=" + requestPriority +
                ", requestStatus=" + requestStatus +
                ", requestCreationTime='" + requestCreationTime + '\'' +
                ", requestDeadlineTime='" + requestDeadlineTime + '\'' +
                ", requestTitle='" + requestTitle + '\'' +
                ", requestUserDeviceDescription='" + requestUserDeviceDescription + '\'' +
                ", requestShortDescription='" + requestShortDescription + '\'' +
                ", requestCheckList=" + requestCheckList +
                ", requestFiles=" + requestFiles +
                ", messageList=" + messageList +
                '}';
    }


    public static class FileData implements Serializable{
        private String downloadURL;
        private String fileName;
        private String filePath;
        private String contentType;
        private String contentEncoding;
        private long fileByteSize;
        private long fileCreationTime;
        private File file;


        public FileData() {
        }

        public FileData(String downloadURL,
                        String fileName,
                        String filePath,
                        String contentType,
                        String contentEncoding,
                        long fileByteSize,
                        long fileCreationTime) {
            this.downloadURL = downloadURL;
            this.fileName = fileName;
            this.filePath = filePath;
            this.contentType = contentType;
            this.contentEncoding = contentEncoding;
            this.fileByteSize = fileByteSize;
            this.fileCreationTime = fileCreationTime;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getDownloadURL() {
            return downloadURL;
        }

        public void setDownloadURL(String downloadURL) {
            this.downloadURL = downloadURL;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentEncoding() {
            return contentEncoding;
        }

        public void setContentEncoding(String contentEncoding) {
            this.contentEncoding = contentEncoding;
        }

        public long getFileByteSize() {
            return fileByteSize;
        }

        public void setFileByteSize(long fileByteSize) {
            this.fileByteSize = fileByteSize;
        }

        public long getFileCreationTime() {
            return fileCreationTime;
        }

        public void setFileCreationTime(long fileCreationTime) {
            this.fileCreationTime = fileCreationTime;
        }

        @Override
        public String toString() {
            return "FileData{" +
                    "downloadURL='" + downloadURL + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", filePath='" + filePath + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", contentEncoding='" + contentEncoding + '\'' +
                    ", fileByteSize=" + fileByteSize +
                    ", fileCreationTime=" + fileCreationTime +
                    '}';
        }
    }


    public static class RequestCheckList implements Serializable{
        private String checkListText;
        private boolean checkFinished;

        public RequestCheckList() {
        }

        public RequestCheckList(String checkListText, boolean checkFinished) {
            this.checkListText = checkListText;
            this.checkFinished = checkFinished;
        }

        public String getCheckListText() {
            return checkListText;
        }

        public void setCheckListText(String checkListText) {
            this.checkListText = checkListText;
        }

        public boolean isCheckFinished() {
            return checkFinished;
        }

        public void setCheckFinished(boolean checkFinished) {
            this.checkFinished = checkFinished;
        }

        @Override
        public String toString() {
            return "RequestCheckList{" +
                    "checkListText='" + checkListText + '\'' +
                    ", checkFinished=" + checkFinished +
                    '}';
        }
    }
}
