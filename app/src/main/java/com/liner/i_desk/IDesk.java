package com.liner.i_desk;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;

public class IDesk extends Application {

    private static IDesk context;

    public static IDesk getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000)
                        .readTimeout(15_000)
                ))
                .commit();
        context = this;
    }

}
