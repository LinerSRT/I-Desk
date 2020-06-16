package com.liner.i_desk;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.UserObject;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class IDesk extends Application {

    private static IDesk context;

    public static IDesk getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000)
                        .readTimeout(15_000)
                ))
                .commit();
        if(Firebase.isUserLoginned()){
            Objects.requireNonNull(Firebase.getUsersDatabase()).child(Objects.requireNonNull(Firebase.getUserUID())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.child("userStatus").getRef().onDisconnect().setValue(UserObject.UserStatus.OFFLINE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        context = this;

    }

}
