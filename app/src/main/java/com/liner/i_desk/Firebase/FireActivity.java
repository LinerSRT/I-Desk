package com.liner.i_desk.Firebase;

import android.content.Intent;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.liner.i_desk.ActivitySplash;
import com.liner.utils.Time;

import java.util.concurrent.TimeUnit;

import static com.liner.i_desk.Constants.USER_STATUS_UPDATE_PERIOD;

public abstract class FireActivity extends AppCompatActivity {
    private Handler onlineUpdater = new Handler();
    private Runnable onlineUpdaterRunnable = new Runnable() {
        @Override
        public void run() {
            FirebaseValue.setUserValue(Firebase.getUserUID(), "userLastOnlineAt", Time.getTime());
            FirebaseValue.setUserValue(Firebase.getUserUID(), "userStatus", UserObject.UserStatus.ONLINE);
            onlineUpdater.postDelayed(onlineUpdaterRunnable, TimeUnit.MINUTES.toMillis(USER_STATUS_UPDATE_PERIOD));
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(!Firebase.isUserLoginned()){
            sendToSplash();
        }
        onlineUpdater.post(onlineUpdaterRunnable);
    }


    @Override
    protected void onDestroy() {
        FirebaseValue.setUserValue(Firebase.getUserUID(), "userStatus", UserObject.UserStatus.OFFLINE);
        onlineUpdater.removeCallbacks(onlineUpdaterRunnable);
        super.onDestroy();
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        sendToSplash();
    }
    public void sendToSplash(){
        Intent intent = new Intent(this, ActivitySplash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
