package com.liner.i_desk.Firebase;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.liner.i_desk.ActivityMain;
import com.liner.i_desk.R;
import com.liner.utils.ImageUtils;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.utils.ViewUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MessagingService extends Service {
    public static Intent serviceIntent = null;
    private DatabaseListener databaseListener;
    private UserObject userObject;

    public MessagingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        if(!Firebase.isUserLoginned()){
            stopSelf();
        }
        databaseListener = new DatabaseListener() {
            @Override
            public void onUserAdded(UserObject user, int position) {
                super.onUserAdded(userObject, position);
                userObject = getCurrentUser();
            }

            @Override
            public void onRequestAdded(RequestObject requestObject, int position) {
                super.onRequestAdded(requestObject, position);
                if (userObject != null) {
                    long currentTime = Time.getTime();
                    long requestCreationTime = requestObject.getRequestCreatedAt();
                    long triggerTime = TimeUnit.MINUTES.toMillis(10);
                    switch (userObject.getUserType()) {
                        case SERVICE:
                        case ADMIN:
                            if (currentTime - requestCreationTime <= triggerTime) {
                                makeNotification(requestObject, NotificationType.NEW_REQUEST);
                            }
                            break;
                        case CLIENT:
                            if(requestObject.getRequestCreatorID().equals(Firebase.getUserUID())){
                                if (currentTime - requestCreationTime <= triggerTime) {
                                    makeNotification(requestObject, NotificationType.NEW_REQUEST);
                                }
                            }
                            break;
                    }
                }

            }

            @Override
            public void onRequestChanged(RequestObject requestObject, int position) {
                super.onRequestChanged(requestObject, position);

            }

            @Override
            public void onRequestDeleted(RequestObject requestObject, int position) {
                super.onRequestDeleted(requestObject, position);
            }

            @Override
            public void onMessageAdded(MessageObject messageObject, int position) {
                super.onMessageAdded(messageObject, position);
            }

            @Override
            public void onMessageChanged(MessageObject messageObject, int position) {
                super.onMessageChanged(messageObject, position);
            }

            @Override
            public void onMessageDeleted(MessageObject messageObject, int position) {
                super.onMessageDeleted(messageObject, position);
            }
        };
        databaseListener.startListening();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceIntent = null;
        databaseListener.stopListening();
        if(Firebase.isUserLoginned())
            restartService();
    }


    protected void restartService() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(alarmManager).set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void makeNotification(final RequestObject requestObject, final NotificationType notificationType) {
        if(requestObject.getRequestCreatorPhotoURL() != null) {
            Picasso.get().load(requestObject.getRequestCreatorPhotoURL()).resize(ViewUtils.dpToPx(64), ViewUtils.dpToPx(64)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    sendNotification(requestObject, notificationType, bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    sendNotification(requestObject, notificationType, ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.temp_user_photo)));
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            sendNotification(requestObject, notificationType, ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.temp_user_photo)));
        }
    }

    private void sendNotification(final RequestObject requestObject, final NotificationType notificationType, Bitmap bitmap){
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = TextUtils.getUniqueString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Уведомления", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Уведомления I-Desk");
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        switch (notificationType){
            case NEW_REQUEST:
                if(requestObject.getRequestCreatorID().equals(Firebase.getUserUID())){
                    notificationBuilder.setContentTitle("Ваша заявка создана");
                    notificationBuilder.setContentText("Ваша заявка была успешно создана!");
                } else {
                    notificationBuilder.setContentTitle("Создана новая заявка");
                    notificationBuilder.setContentText("Пользователь "+requestObject.getRequestCreatorName()+" создал новую заявку!");
                }
                notificationBuilder.setSmallIcon(R.drawable.requests_icon);
                break;
            case NEW_MESSAGE:
                break;
            case EDIT_REQUEST:
                if(requestObject.getRequestCreatorID().equals(Firebase.getUserUID())){
                    notificationBuilder.setContentTitle("Ваша заявка обновлена!");
                    notificationBuilder.setContentText("Состояние вашей заявки обновлено, проверьте изменения!");
                } else {
                    notificationBuilder.setContentTitle("Заявка обновлена!");
                    notificationBuilder.setContentText("Состояние заявки "+requestObject.getRequestCreatorName()+" обновлено");
                }
                notificationBuilder.setSmallIcon(R.drawable.requests_icon);
                break;
        }
        if(bitmap != null){
            notificationBuilder.setLargeIcon(bitmap);
        }
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(requestObject.getRequestText()));
        notificationBuilder.setColorized(true);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSound);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).play();
        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }

    private enum NotificationType {
        NEW_REQUEST,
        EDIT_REQUEST,
        NEW_MESSAGE
    }

}
