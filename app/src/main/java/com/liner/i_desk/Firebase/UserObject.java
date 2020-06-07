package com.liner.i_desk.Firebase;

import java.io.Serializable;
import java.util.List;

public class UserObject implements Serializable {
    public String userID;
    private UserType userType;
    private String userDeviceID;
    private String userName;
    private String userEmail;
    private String userProfilePhotoURL;
    private String userAboutText;
    private String userStatusText;
    private UserStatus userStatus;
    private long userRegisteredAt;
    private long userLastOnlineAt;
    private boolean userRegisterFinished;
    private List<String> userRequests;
    private List<String> userMessages;
    private List<String> userFiles;

    /**
     * Конструктор для Firebase должен быть пустой.
     */
    public UserObject() {
    }

    /**
     * Конструктор для билдера обьекта
     * Принимает на вход следующие значения
     *
     * @param userID               Уникальный ИД пользователя
     * @param userDeviceID         Уникальный ИД устройства пользователя
     * @param userName             Имя пользователя
     * @param userEmail            Email пользователя
     * @param userProfilePhotoURL  Ссылка на фото профиля пользователя
     * @param userAboutText        Информация о пользователе
     * @param userStatusText       Текстовый статус пользователя, который он может менять
     * @param userStatus           Статус пользователя для системы
     * @param userRegisteredAt     Время когда пользователь был зарегистрирован в миллисекундах
     * @param userLastOnlineAt     Время когда пользователь был в сети и\или использовал приложение в миллисекундах
     * @param userRegisterFinished Завершена ли регистрация
     * @param userRequests         Список заявок пользователя
     * @param userMessages         Список сообщений пользователя
     * @param userFiles            Список файлов пользователя
     */
    public UserObject(String userID,
                      UserType userType,
                      String userDeviceID,
                      String userName,
                      String userEmail,
                      String userProfilePhotoURL,
                      String userAboutText,
                      String userStatusText,
                      UserStatus userStatus,
                      long userRegisteredAt,
                      long userLastOnlineAt,
                      boolean userRegisterFinished,
                      List<String> userRequests,
                      List<String> userMessages,
                      List<String> userFiles) {
        this.userID = userID;
        this.userType = userType;
        this.userDeviceID = userDeviceID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userProfilePhotoURL = userProfilePhotoURL;
        this.userAboutText = userAboutText;
        this.userStatusText = userStatusText;
        this.userStatus = userStatus;
        this.userRegisteredAt = userRegisteredAt;
        this.userLastOnlineAt = userLastOnlineAt;
        this.userRegisterFinished = userRegisterFinished;
        this.userRequests = userRequests;
        this.userMessages = userMessages;
        this.userFiles = userFiles;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getUserDeviceID() {
        return userDeviceID;
    }

    public void setUserDeviceID(String userDeviceID) {
        this.userDeviceID = userDeviceID;
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

    public String getUserProfilePhotoURL() {
        return userProfilePhotoURL;
    }

    public void setUserProfilePhotoURL(String userProfilePhotoURL) {
        this.userProfilePhotoURL = userProfilePhotoURL;
    }

    public String getUserAboutText() {
        return userAboutText;
    }

    public void setUserAboutText(String userAboutText) {
        this.userAboutText = userAboutText;
    }

    public String getUserStatusText() {
        return userStatusText;
    }

    public void setUserStatusText(String userStatusText) {
        this.userStatusText = userStatusText;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public long getUserRegisteredAt() {
        return userRegisteredAt;
    }

    public void setUserRegisteredAt(long userRegisteredAt) {
        this.userRegisteredAt = userRegisteredAt;
    }

    public long getUserLastOnlineAt() {
        return userLastOnlineAt;
    }

    public void setUserLastOnlineAt(long userLastOnlineAt) {
        this.userLastOnlineAt = userLastOnlineAt;
    }

    public boolean isUserRegisterFinished() {
        return userRegisterFinished;
    }

    public void setUserRegisterFinished(boolean userRegisterFinished) {
        this.userRegisterFinished = userRegisterFinished;
    }

    public List<String> getUserRequests() {
        return userRequests;
    }

    public void setUserRequests(List<String> userRequests) {
        this.userRequests = userRequests;
    }

    public List<String> getUserMessages() {
        return userMessages;
    }

    public void setUserMessages(List<String> userMessages) {
        this.userMessages = userMessages;
    }

    public List<String> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(List<String> userFiles) {
        this.userFiles = userFiles;
    }

    /**
     * Стандартные get'еры и set'еры
     */


    @Override
    public String toString() {
        return "UserObject{" +
                "userID='" + userID + '\'' +
                ", userType=" + userType +
                ", userDeviceID='" + userDeviceID + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userProfilePhotoURL='" + userProfilePhotoURL + '\'' +
                ", userAboutText='" + userAboutText + '\'' +
                ", userStatusText='" + userStatusText + '\'' +
                ", userStatus=" + userStatus +
                ", userRegisteredAt=" + userRegisteredAt +
                ", userLastOnlineAt=" + userLastOnlineAt +
                ", userRegisterFinished=" + userRegisterFinished +
                ", userRequests=" + userRequests +
                ", userMessages=" + userMessages +
                ", userFiles=" + userFiles +
                '}';
    }

    /**
     * Тип аккаунта
     */
    public enum UserType {
        CLIENT,
        SERVICE,
        ADMIN
    }

    /**
     * Статус пользователя
     */
    public enum UserStatus {
        OFFLINE,
        ONLINE,
        HIDDEN
    }
}
