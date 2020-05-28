package com.liner.i_desk.API.Data;

import java.util.List;

public class Request {
    public enum Type {
        Incident,
        Service,
        Consultation
    }
    private String requestID;
    private String requestUserID;
    private long requestTime;
    private Type requestType;
    private long requestDeadline;
    private int requestPriority;
    private String requestName;
    private String requestText;
    private List<Comment> commentList;


    public Request() {
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public Type getRequestType() {
        return requestType;
    }

    public void setRequestType(Type requestType) {
        this.requestType = requestType;
    }

    public long getRequestDeadline() {
        return requestDeadline;
    }

    public void setRequestDeadline(long requestDeadline) {
        this.requestDeadline = requestDeadline;
    }

    public int getRequestPriority() {
        return requestPriority;
    }

    public void setRequestPriority(int requestPriority) {
        this.requestPriority = requestPriority;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestUserID() {
        return requestUserID;
    }

    public void setRequestUserID(String requestUserID) {
        this.requestUserID = requestUserID;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestID='" + requestID + '\'' +
                ", requestUserID='" + requestUserID + '\'' +
                ", requestTime=" + requestTime +
                ", requestType=" + requestType +
                ", requestDeadline=" + requestDeadline +
                ", requestPriority=" + requestPriority +
                ", requestName='" + requestName + '\'' +
                ", requestText='" + requestText + '\'' +
                ", commentList=" + commentList +
                '}';
    }
}
