package com.liner.i_desk.API.Data;

import java.util.List;

public class User {
    public enum Type{
        CLIENT,
        SERVICE
    }
    private String userUID;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userPhotoURL;
    private String userAboutText;
    private String userAdditionalInformationText;
    private Type userAccountType;
    private String userLastOnlineTimeStamp;
    private List<Request> requestList;

    public User() {
    }

    public Type getUserAccountType() {
        return userAccountType;
    }

    public void setUserAccountType(Type userAccountType) {
        this.userAccountType = userAccountType;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhotoURL() {
        return userPhotoURL;
    }

    public void setUserPhotoURL(String userPhotoURL) {
        this.userPhotoURL = userPhotoURL;
    }

    public String getUserLastOnlineTimeStamp() {
        return userLastOnlineTimeStamp;
    }

    public void setUserLastOnlineTimeStamp(String userLastOnlineTimeStamp) {
        this.userLastOnlineTimeStamp = userLastOnlineTimeStamp;
    }


    public List<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
    }


    public String getUserAboutText() {
        return userAboutText;
    }

    public void setUserAboutText(String userAboutText) {
        this.userAboutText = userAboutText;
    }


    public String getUserAdditionalInformationText() {
        return userAdditionalInformationText;
    }

    public void setUserAdditionalInformationText(String userAdditionalInformationText) {
        this.userAdditionalInformationText = userAdditionalInformationText;
    }

    @Override
    public String toString() {
        return "User{" +
                "userUID='" + userUID + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userPhotoURL='" + userPhotoURL + '\'' +
                ", userAboutText='" + userAboutText + '\'' +
                ", userAdditionalInformationText='" + userAdditionalInformationText + '\'' +
                ", userAccountType=" + userAccountType +
                ", userLastOnlineTimeStamp=" + userLastOnlineTimeStamp +
                ", requestList=" + requestList +
                '}';
    }
}
