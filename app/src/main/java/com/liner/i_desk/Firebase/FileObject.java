package com.liner.i_desk.Firebase;

import java.io.Serializable;

public class FileObject implements Serializable {
    private String fileID;
    private String fileName;
    private String fileURL;
    private String fileType;
    private String fileCreatorID;
    private long fileCreatedAt;
    private long fileSizeInBytes;

    /**
     * Конструктор для Firebase должен быть пустой.
     */
    public FileObject() {
    }

    /**
     * Конструктор для билдера обьекта
     * Принимает на вход следующие значения
     *
     * @param fileID ИД файла для сохранения уникального пути в БД Firebase
     * @param fileName Имя файла которое включает в себя так же расширение файла
     * @param fileURL Ссылка на файл из облачного хранилища
     * @param fileType MIME тип файла
     * @param fileCreatorID ИД пользователя загрузившего файл
     * @param fileCreatedAt Время когда файл был загружен на облако в миллисекундах
     * @param fileSizeInBytes Время когда пользователь был в сети и\или использовал приложение в миллисекундах
     */
    public FileObject(String fileID,
                      String fileName,
                      String fileURL,
                      String fileType,
                      String fileCreatorID,
                      long fileCreatedAt,
                      long fileSizeInBytes) {
        this.fileID = fileID;
        this.fileName = fileName;
        this.fileURL = fileURL;
        this.fileType = fileType;
        this.fileCreatorID = fileCreatorID;
        this.fileCreatedAt = fileCreatedAt;
        this.fileSizeInBytes = fileSizeInBytes;
    }

    /**
     * Стандартные get'еры и set'еры
     */
    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileCreatorID() {
        return fileCreatorID;
    }

    public void setFileCreatorID(String fileCreatorID) {
        this.fileCreatorID = fileCreatorID;
    }

    public long getFileCreatedAt() {
        return fileCreatedAt;
    }

    public void setFileCreatedAt(long fileCreatedAt) {
        this.fileCreatedAt = fileCreatedAt;
    }

    public long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public void setFileSizeInBytes(long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    @Override
    public String toString() {
        return "FileObject{" +
                "fileID='" + fileID + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileURL='" + fileURL + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileCreatorID='" + fileCreatorID + '\'' +
                ", fileCreatedAt=" + fileCreatedAt +
                ", fileSizeInBytes=" + fileSizeInBytes +
                '}';
    }
}
