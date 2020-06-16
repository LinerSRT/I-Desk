package com.liner.i_desk.Firebase;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class MessageObject implements Serializable, IMessage, MessageContentType {
    private String messageID;
    private String messageText;
    private HashMap<String, String> messageFiles;
    private long messageCreatedAt;
    private long messageReadedAt;
    private MessageStatus messageStatus;
    private String creatorName;
    private String creatorPhotoURL;
    private String creatorID;
    private boolean isRead;

    /**
     * Конструктор для Firebase должен быть пустой.
     */
    public MessageObject() {
    }

    /**
     * Конструктор для билдера обьекта
     * Принимает на вход следующие значения
     *
     * @param messageID        ИД сообщения для сохранения уникального пути в БД Firebase
     * @param messageText      Текст сообщения
     * @param messageFiles     Список файлов которые могут быть отправлены вместе с сообщением. Где первое значение ИД файла в БД Firebase, а второе ИД пользователя который загрузил файл
     * @param messageCreatedAt Время создания сообщения в миллисекундах
     * @param messageReadedAt  Время когда сообщение было прочитано в миллисекундах
     * @param messageStatus    Статус сообщения
     * @param creatorName      Имя пользователя создавшего сообщение
     * @param creatorPhotoURL  Ссылка на фото профиля пользователя собздавшего сообщение
     * @param creatorID        ИД пользователя создавшего сообщение, необходимо для дополнительной идентификации при получении списка сообщений из БД Firebase
     */
    public MessageObject(String messageID,
                         String messageText,
                         HashMap<String, String> messageFiles,
                         long messageCreatedAt,
                         long messageReadedAt,
                         MessageStatus messageStatus,
                         String creatorName,
                         String creatorPhotoURL,
                         String creatorID) {
        this.messageID = messageID;
        this.messageText = messageText;
        this.messageFiles = messageFiles;
        this.messageCreatedAt = messageCreatedAt;
        this.messageReadedAt = messageReadedAt;
        this.messageStatus = messageStatus;
        this.creatorName = creatorName;
        this.creatorPhotoURL = creatorPhotoURL;
        this.creatorID = creatorID;
    }

    @Override
    public String getId() {
        return messageID;
    }

    @Override
    public String getText() {
        return messageText;
    }

    @Override
    public IUser getUser() {
        return new IUser() {
            @Override
            public String getId() {
                return creatorID;
            }

            @Override
            public String getName() {
                return creatorName;
            }

            @Override
            public String getAvatar() {
                return creatorPhotoURL;
            }
        };
    }

    @Override
    public Date getCreatedAt() {
        return new Date(messageCreatedAt);
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Стандартные get'еры и set'еры
     */


    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public HashMap<String, String> getMessageFiles() {
        return messageFiles;
    }

    public void setMessageFiles(HashMap<String, String> messageFiles) {
        this.messageFiles = messageFiles;
    }

    public long getMessageCreatedAt() {
        return messageCreatedAt;
    }

    public void setMessageCreatedAt(long messageCreatedAt) {
        this.messageCreatedAt = messageCreatedAt;
    }

    public long getMessageReadedAt() {
        return messageReadedAt;
    }

    public void setMessageReadedAt(long messageReadedAt) {
        this.messageReadedAt = messageReadedAt;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorPhotoURL() {
        return creatorPhotoURL;
    }

    public void setCreatorPhotoURL(String creatorPhotoURL) {
        this.creatorPhotoURL = creatorPhotoURL;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "messageID='" + messageID + '\'' +
                ", messageText='" + messageText + '\'' +
                ", messageFiles=" + messageFiles +
                ", messageCreatedAt=" + messageCreatedAt +
                ", messageReadedAt=" + messageReadedAt +
                ", messageStatus=" + messageStatus +
                ", creatorName='" + creatorName + '\'' +
                ", creatorPhotoURL='" + creatorPhotoURL + '\'' +
                ", creatorID='" + creatorID + '\'' +
                '}';
    }

    /**
     * Статус сообщения
     */
    public enum MessageStatus {
        SENDING,
        SENDED,
        READED,
        SEND_FAIL
    }
}
