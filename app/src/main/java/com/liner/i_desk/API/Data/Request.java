package com.liner.i_desk.API.Data;

import java.util.List;

public class Request {
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
    private List<RequestFile> requestFiles;
    private List<RequestComment> requestCommentList;


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

    public List<RequestFile> getRequestFiles() {
        return requestFiles;
    }

    public void setRequestFiles(List<RequestFile> requestFiles) {
        this.requestFiles = requestFiles;
    }

    public List<RequestComment> getRequestCommentList() {
        return requestCommentList;
    }

    public void setRequestCommentList(List<RequestComment> requestCommentList) {
        this.requestCommentList = requestCommentList;
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
                ", requestCommentList=" + requestCommentList +
                '}';
    }

    public class RequestComment{
        private String commentID;
        private String commentCreatorID;
        private String commentCreationTime;
        private String commentText;
        private List<RequestFile> commentFiles;

        public RequestComment() {
        }

        public String getCommentID() {
            return commentID;
        }

        public void setCommentID(String commentID) {
            this.commentID = commentID;
        }

        public String getCommentCreatorID() {
            return commentCreatorID;
        }

        public void setCommentCreatorID(String commentCreatorID) {
            this.commentCreatorID = commentCreatorID;
        }

        public String getCommentCreationTime() {
            return commentCreationTime;
        }

        public void setCommentCreationTime(String commentCreationTime) {
            this.commentCreationTime = commentCreationTime;
        }

        public String getCommentText() {
            return commentText;
        }

        public void setCommentText(String commentText) {
            this.commentText = commentText;
        }

        public List<RequestFile> getCommentFiles() {
            return commentFiles;
        }

        public void setCommentFiles(List<RequestFile> commentFiles) {
            this.commentFiles = commentFiles;
        }

        @Override
        public String toString() {
            return "RequestComment{" +
                    "commentID='" + commentID + '\'' +
                    ", commentCreatorID='" + commentCreatorID + '\'' +
                    ", commentCreationTime='" + commentCreationTime + '\'' +
                    ", commentText='" + commentText + '\'' +
                    ", commentFiles=" + commentFiles +
                    '}';
        }
    }
    public static class RequestFile{
        private String fileUploadTime;
        private String filePath;
        private String fileID;
        private String fileOwnerID;

        public RequestFile(){

        }

        public String getFileUploadTime() {
            return fileUploadTime;
        }

        public void setFileUploadTime(String fileUploadTime) {
            this.fileUploadTime = fileUploadTime;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFileID() {
            return fileID;
        }

        public void setFileID(String fileID) {
            this.fileID = fileID;
        }

        public String getFileOwnerID() {
            return fileOwnerID;
        }

        public void setFileOwnerID(String fileOwnerID) {
            this.fileOwnerID = fileOwnerID;
        }

        @Override
        public String toString() {
            return "RequestFile{" +
                    "fileUploadTime='" + fileUploadTime + '\'' +
                    ", filePath='" + filePath + '\'' +
                    ", fileID='" + fileID + '\'' +
                    ", fileOwnerID='" + fileOwnerID + '\'' +
                    '}';
        }
    }
    public static class RequestCheckList{
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
