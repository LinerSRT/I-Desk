package com.liner.i_desk.API.Data;

import java.util.List;

public class User {
    private String userUID;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userPhotoURL;
    private String userAboutText;
    private String userAdditionalInformationText;
    private long userLastOnlineTimeStamp;
    private boolean isClientAccount = true;
    private List<Request> requestList;

    public User() {
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

    public long getUserLastOnlineTimeStamp() {
        return userLastOnlineTimeStamp;
    }

    public void setUserLastOnlineTimeStamp(long userLastOnlineTimeStamp) {
        this.userLastOnlineTimeStamp = userLastOnlineTimeStamp;
    }

    public boolean isClientAccount() {
        return isClientAccount;
    }

    public void setClientAccount(boolean clientAccount) {
        isClientAccount = clientAccount;
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
                ", userLastOnlineTimeStamp=" + userLastOnlineTimeStamp +
                ", isClientAccount=" + isClientAccount +
                ", requestList=" + requestList +
                '}';
    }
}
