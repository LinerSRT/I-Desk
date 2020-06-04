package com.liner.i_desk;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.liner.i_desk.API.Data.Message;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.Utils.AudioRecorder;
import com.liner.i_desk.Utils.Views.FirebaseActivity;
import com.liner.i_desk.Utils.Views.FirebaseExtender;


public class DEBUG extends AppCompatActivity{
    private AudioRecorder audioRecorder;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        audioRecorder = new AudioRecorder(this);

        ImageView test = findViewById(R.id.startRecordTEST);

//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("Users")
//                .limitToLast(50);
//        FirebaseExtender<User> extender = new FirebaseExtender<User>(query) {
//            @Override
//            public void onItemAdded(String key, User item, int pos) {
//                Log.d("TAGTAG", "onItemAdded "+item.toString());
//            }
//
//            @Override
//            public void onItemChanged(String key, User oldItem, User newItem, int pos) {
//
//                Log.d("TAGTAG", "onItemChanged "+oldItem.toString());
//                Log.d("TAGTAG", "-----------------------------------");
//                Log.d("TAGTAG", "onItemChanged "+newItem.toString());
//            }
//
//            @Override
//            public void onItemRemoved(String key, User item, int pos) {
//
//                Log.d("TAGTAG", "onItemRemoved "+item.toString());
//            }
//
//            @Override
//            public void onItemMoved(String key, User item, int pos, int newPos) {
//
//                Log.d("TAGTAG", "onItemMoved "+item.toString());
//            }
//        };
        Query query2 = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requestList").child("messageList")
                .limitToLast(50);
        FirebaseExtender<Message> extender2 = new FirebaseExtender<Message>(query2) {
            @Override
            public void onItemAdded(String key, Message item, int pos) {
                Log.d("TAGTAG", "Request onItemAdded "+item.toString());
            }

            @Override
            public void onItemChanged(String key, Message oldItem, Message newItem, int pos) {

                Log.d("TAGTAG", "Request onItemChanged "+oldItem.toString());
                Log.d("TAGTAG", "-----------------------------------");
                Log.d("TAGTAG", "Request onItemChanged "+newItem.toString());
            }

            @Override
            public void onItemRemoved(String key, Message item, int pos) {

                Log.d("TAGTAG", "Request onItemRemoved "+item.toString());
            }

            @Override
            public void onItemMoved(String key, Message item, int pos, int newPos) {

                Log.d("TAGTAG", "Request onItemMoved "+item.toString());
            }
        };


        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        final Handler handler = new Handler();
        final Runnable testR = new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 10);
            }
        };

        test.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                final int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        audioRecorder.start();
                        handler.post(testR);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        Log.d("TAGTAG", "getAmplitude "+audioRecorder.getAmplitude());
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        audioRecorder.stop();
                        if(audioRecorder.getResult() != null)
                            Log.d("TAGTAG", "SSS "+audioRecorder.getResult().getAbsolutePath());
                        return true;
                }
                return false;
            }
        });

    }



}
