package com.liner.i_desk.API.Data;

public class User {
    private String userUID;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userPhotoURL;
    private String deviceToken;
    private long userLastOnlineTimeStamp;
    private boolean isClientAccount = true;

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

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
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

    @Override
    public String toString() {
        return "User{" +
                "userUID='" + userUID + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userPhotoURL='" + userPhotoURL + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", userLastOnlineTimeStamp=" + userLastOnlineTimeStamp +
                ", isClientAccount=" + isClientAccount +
                '}';
    }
}
