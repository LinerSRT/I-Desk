package com.liner.i_desk;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class DEBUG extends AppCompatActivity {
//    private FirebaseAuth firebaseAuth;
//    private FirebaseListener<User> userFirebaseListener;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

//        userFirebaseListener = new FirebaseListener<User>(Constants.USERS_DATABASE_KEY) {
//
//            @Override
//            public void onItemAdded(String key, User item, int pos, DatabaseReference reference) {
//
//            }
//
//            @Override
//            public void onItemChanged(String key, User item, int pos, DatabaseReference reference) {
//                for (int i = 0; i < 5; i++) {
//                    reference.child("test"+i).setValue(i);
//                }
//                userFirebaseListener.destroy();
//            }
//
//            @Override
//            public void onItemRemoved(String key, User item, int pos, DatabaseReference reference) {
//
//            }
//
//            @Override
//            public void onItemMoved(String key, User item, int pos, int newPos) {
//
//            }
//        };
////        userFirebaseListener.start();
//
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
////                UserObject userObject = new UserObject(
////                        Constants.GENERATE_UNIQUE_ID(),
////                        Constants.GENERATE_UNIQUE_ID(),
////                        "Test",
////                        "serinity324@gmail.com",
////                        null,
////                        "About text",
////                        "Im registered, yay!",
////                        UserObject.UserStatus.ONLINE,
////                        Time.getTime(),
////                        Time.getTime(),
////                        new HashMap<String, String>(),
////                        new HashMap<String, String>(),
////                        new HashMap<String, String>()
////                );
////                LoginRegisterFirebase.RegisterUser(DEBUG.this, userObject, "123456qq", new LoginRegisterFirebase.OnFirebaseActionListener() {
////                    @Override
////                    public void onSuccess() {
////
////                    }
////
////                    @Override
////                    public void onFailed() {
////
////                    }
////                });
////                HashMap<String, String> checks = new HashMap<>();
////                checks.put("true", "Test check1");
////                checks.put("false", "Test check2");
////                RequestObject request = new RequestObject(
////                        Constants.GENERATE_UNIQUE_ID(),
////                        RequestObject.RequestType.CONSULTATION,
////                        RequestObject.RequestPriority.MEDIUM,
////                        RequestObject.RequestStatus.PENDING,
////                        "Test request",
////                        "Test description",
////                        "Test device description",
////                        checks,
////                        System.currentTimeMillis(),
////                        System.currentTimeMillis(),
////                        firebaseAuth.getCurrentUser().getUid(),
////                        firebaseAuth.getCurrentUser().getDisplayName(),
////                        firebaseAuth.getCurrentUser().getPhotoUrl().toString(),
////                        System.currentTimeMillis(),
////                        "",
////                        "",
////                        "",
////                        System.currentTimeMillis()
////                );
//
////                FirebaseValue.setRequest(request.getRequestID(), request);
////                FirebaseValue.setUserValue(firebaseAuth.getCurrentUser().getUid(), "lastOnlineTimestamp", System.currentTimeMillis());
//                //handler.postDelayed(this, 5000);
//            }
//        };
//        handler.postDelayed(runnable, 5000);

    }




}
