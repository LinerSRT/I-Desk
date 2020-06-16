package com.liner.i_desk.Firebase;

import com.nostra13.universalimageloader.utils.L;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class RequestObject implements Serializable {
    private String requestID;
    private RequestType requestType;
    private RequestPriority requestPriority;
    private RequestStatus requestStatus;
    private RequestClose requestClose;
    private String requestTitle;
    private String requestText;
    private String requestUserDeviceText;
    private List<CheckObject> requestChecks;
    private HashMap<String, String> requestMessages;
    private HashMap<String, String> requestFiles;
    private long requestCreatedAt;
    private long requestDeadlineAt;
    private String requestCreatorID;
    private String requestCreatorName;
    private String requestCreatorPhotoURL;
    private long requestCreatorLastOnlineTime;
    private String requestAcceptorID;
    private String requestAcceptorName;
    private String requestAcceptorPhotoURL;
    private long requestAcceptorLastOnlineTime;
    private boolean requestRated = false;
    private int requestRate = 0;

    /**
     * Конструктор для Firebase должен быть пустой.
     */
    public RequestObject() {
    }

    /**
     * Конструктор для билдера обьекта
     * Принимает на вход следующие значения
     *
     * @param requestID                     ИД заявки для сохранения уникального пути в БД Firebase
     * @param requestType                   Тип заявки
     * @param requestPriority               Приоритет заявки
     * @param requestStatus                 Статус заявки
     * @param requestTitle                  Заголовок заявки
     * @param requestText                   Текст заявки
     * @param requestUserDeviceText         Описание устройств
     * @param requestChecks                 Чеклист заяки
     * @param requestMessages               Сообщения заявки. Где первое значение ИД сообщения, а второе ИД пользователя создавшего сообщение
     * @param requestFiles                  Список файлов которые могут быть отправлены вместе с сообщением. Где первое значение ИД файла в БД Firebase, а второе ИД пользователя который загрузил файл
     * @param requestCreatedAt              Время создания заявки в миллисекундах
     * @param requestDeadlineAt             Время дедлайна заявки в миллисекундах
     * @param requestCreatorID              ИД пользователя создавшего заявку, необходимо для дополнительной идентификации при получении списка заявок из БД Firebase
     * @param requestCreatorName            Имя пользователя создавшего заявку
     * @param requestCreatorPhotoURL        Ссылка на фото профиля пользователя создавшего заявку
     * @param requestCreatorLastOnlineTime  Время когда пользователь создавший заявку был в сети и\или использовал приложение в миллисекундах
     * @param requestAcceptorID             ИД пользователя, с типом аккаунта "Сервисный", который принял заявку в обработку
     * @param requestAcceptorName           Имя пользователя, с типом аккаунта "Сервисный", который принял заявку в обработку
     * @param requestAcceptorPhotoURL       Ссылка на фото профиля пользователя, с типом аккаунта "Сервисный", который принял заявку в обработку
     * @param requestAcceptorLastOnlineTime Время когда пользователь, с типом аккаунта "Сервисный", был в сети и\или использовал приложение в миллисекундах
     */
    public RequestObject(String requestID,
                         RequestType requestType,
                         RequestPriority requestPriority,
                         RequestStatus requestStatus,
                         String requestTitle,
                         String requestText,
                         String requestUserDeviceText,
                         List<CheckObject> requestChecks,
                         HashMap<String, String> requestMessages,
                         HashMap<String, String> requestFiles,
                         long requestCreatedAt,
                         long requestDeadlineAt,
                         String requestCreatorID,
                         String requestCreatorName,
                         String requestCreatorPhotoURL,
                         long requestCreatorLastOnlineTime,
                         String requestAcceptorID,
                         String requestAcceptorName,
                         String requestAcceptorPhotoURL,
                         long requestAcceptorLastOnlineTime) {
        this.requestID = requestID;
        this.requestType = requestType;
        this.requestPriority = requestPriority;
        this.requestStatus = requestStatus;
        this.requestTitle = requestTitle;
        this.requestText = requestText;
        this.requestUserDeviceText = requestUserDeviceText;
        this.requestChecks = requestChecks;
        this.requestMessages = requestMessages;
        this.requestFiles = requestFiles;
        this.requestCreatedAt = requestCreatedAt;
        this.requestDeadlineAt = requestDeadlineAt;
        this.requestCreatorID = requestCreatorID;
        this.requestCreatorName = requestCreatorName;
        this.requestCreatorPhotoURL = requestCreatorPhotoURL;
        this.requestCreatorLastOnlineTime = requestCreatorLastOnlineTime;
        this.requestAcceptorID = requestAcceptorID;
        this.requestAcceptorName = requestAcceptorName;
        this.requestAcceptorPhotoURL = requestAcceptorPhotoURL;
        this.requestAcceptorLastOnlineTime = requestAcceptorLastOnlineTime;
    }

    /**
     * Стандартные get'еры и set'еры
     */

    public boolean isRequestRated() {
        return requestRated;
    }

    public void setRequestRated(boolean requestRated) {
        this.requestRated = requestRated;
    }

    public int getRequestRate() {
        return requestRate;
    }

    public void setRequestRate(int requestRate) {
        this.requestRate = requestRate;
    }

    public RequestClose getRequestClose() {
        return requestClose;
    }

    public void setRequestClose(RequestClose requestClose) {
        this.requestClose = requestClose;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public RequestPriority getRequestPriority() {
        return requestPriority;
    }

    public void setRequestPriority(RequestPriority requestPriority) {
        this.requestPriority = requestPriority;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestTitle() {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getRequestUserDeviceText() {
        return requestUserDeviceText;
    }

    public void setRequestUserDeviceText(String requestUserDeviceText) {
        this.requestUserDeviceText = requestUserDeviceText;
    }

    public List<CheckObject> getRequestChecks() {
        return requestChecks;
    }

    public void setRequestChecks(List<CheckObject> requestChecks) {
        this.requestChecks = requestChecks;
    }

    public HashMap<String, String> getRequestMessages() {
        return requestMessages;
    }

    public void setRequestMessages(HashMap<String, String> requestMessages) {
        this.requestMessages = requestMessages;
    }

    public HashMap<String, String> getRequestFiles() {
        return requestFiles;
    }

    public void setRequestFiles(HashMap<String, String> requestFiles) {
        this.requestFiles = requestFiles;
    }

    public long getRequestCreatedAt() {
        return requestCreatedAt;
    }

    public void setRequestCreatedAt(long requestCreatedAt) {
        this.requestCreatedAt = requestCreatedAt;
    }

    public long getRequestDeadlineAt() {
        return requestDeadlineAt;
    }

    public void setRequestDeadlineAt(long requestDeadlineAt) {
        this.requestDeadlineAt = requestDeadlineAt;
    }

    public String getRequestCreatorID() {
        return requestCreatorID;
    }

    public void setRequestCreatorID(String requestCreatorID) {
        this.requestCreatorID = requestCreatorID;
    }

    public String getRequestCreatorName() {
        return requestCreatorName;
    }

    public void setRequestCreatorName(String requestCreatorName) {
        this.requestCreatorName = requestCreatorName;
    }

    public String getRequestCreatorPhotoURL() {
        return requestCreatorPhotoURL;
    }

    public void setRequestCreatorPhotoURL(String requestCreatorPhotoURL) {
        this.requestCreatorPhotoURL = requestCreatorPhotoURL;
    }

    public long getRequestCreatorLastOnlineTime() {
        return requestCreatorLastOnlineTime;
    }

    public void setRequestCreatorLastOnlineTime(long requestCreatorLastOnlineTime) {
        this.requestCreatorLastOnlineTime = requestCreatorLastOnlineTime;
    }

    public String getRequestAcceptorID() {
        return requestAcceptorID;
    }

    public void setRequestAcceptorID(String requestAcceptorID) {
        this.requestAcceptorID = requestAcceptorID;
    }

    public String getRequestAcceptorName() {
        return requestAcceptorName;
    }

    public void setRequestAcceptorName(String requestAcceptorName) {
        this.requestAcceptorName = requestAcceptorName;
    }

    public String getRequestAcceptorPhotoURL() {
        return requestAcceptorPhotoURL;
    }

    public void setRequestAcceptorPhotoURL(String requestAcceptorPhotoURL) {
        this.requestAcceptorPhotoURL = requestAcceptorPhotoURL;
    }

    public long getRequestAcceptorLastOnlineTime() {
        return requestAcceptorLastOnlineTime;
    }

    public void setRequestAcceptorLastOnlineTime(long requestAcceptorLastOnlineTime) {
        this.requestAcceptorLastOnlineTime = requestAcceptorLastOnlineTime;
    }

    @Override
    public String toString() {
        return "RequestObject{" +
                "requestID='" + requestID + '\'' +
                ", requestType=" + requestType +
                ", requestPriority=" + requestPriority +
                ", requestStatus=" + requestStatus +
                ", requestTitle='" + requestTitle + '\'' +
                ", requestText='" + requestText + '\'' +
                ", requestUserDeviceText='" + requestUserDeviceText + '\'' +
                ", requestChecks=" + requestChecks +
                ", requestMessages=" + requestMessages +
                ", requestFiles=" + requestFiles +
                ", requestCreatedAt=" + requestCreatedAt +
                ", requestDeadlineAt=" + requestDeadlineAt +
                ", requestCreatorID='" + requestCreatorID + '\'' +
                ", requestCreatorName='" + requestCreatorName + '\'' +
                ", requestCreatorPhotoURL='" + requestCreatorPhotoURL + '\'' +
                ", requestCreatorLastOnlineTime=" + requestCreatorLastOnlineTime +
                ", requestAcceptorID='" + requestAcceptorID + '\'' +
                ", requestAcceptorName='" + requestAcceptorName + '\'' +
                ", requestAcceptorPhotoURL='" + requestAcceptorPhotoURL + '\'' +
                ", requestAcceptorLastOnlineTime=" + requestAcceptorLastOnlineTime +
                ", requestRated=" + requestRated +
                ", requestRate=" + requestRate +
                '}';
    }

    /**
     * Тип заявки
     */
    public enum RequestType {
        INCIDENT,
        SERVICE,
        CONSULTATION
    }

    /**
     * Статус заяки
     */
    public enum RequestStatus {
        PENDING,
        PROCESSING,
        CLOSE_REQUEST,
        CLOSED
    }

    /**
     * Важность заявки
     */
    public enum RequestPriority {
        VERY_LOW,
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH
    }

    public enum RequestClose {
        SEND,
        ACCEPTED,
        DENIED
    }
}
