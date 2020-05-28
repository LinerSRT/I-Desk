package com.liner.i_desk.Utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Objects;

public class BroadcastManager {
    private Context context;
    private BroadcastListener listener;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(listener != null)
                listener.onReceiveLocal(intent);
        }
    };
    public interface BroadcastListener{
        void onReceiveLocal(Intent intent);
    }


    public void registerListener(String action, BroadcastListener listener) {
        LocalBroadcastManager.getInstance(Objects.requireNonNull(context)).registerReceiver(broadcastReceiver, new IntentFilter(action));
        this.listener = listener;
    }

    public void registerListener(String[] action, BroadcastListener listener) {
        IntentFilter filter = new IntentFilter();
        for(String item:action){
            filter.addAction(item);
        }
        LocalBroadcastManager.getInstance(Objects.requireNonNull(context)).registerReceiver(broadcastReceiver, new IntentFilter(filter));
        this.listener = listener;
    }

    public BroadcastManager(Context context){
        this.context = context;
    }


    public void sendLocal(String action){
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendLocalExtra(Intent intent){
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}