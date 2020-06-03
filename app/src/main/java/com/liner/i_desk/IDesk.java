package com.liner.i_desk;

import android.app.Application;

public class IDesk extends Application {

    private static IDesk mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static IDesk getContext() {
        return mContext;
    }

}
