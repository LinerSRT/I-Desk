package com.liner.i_desk;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.Utils.AudioRecorder;
import com.liner.i_desk.Utils.Views.FirebaseActivity;


public class DEBUG extends FirebaseActivity {
    private AudioRecorder audioRecorder;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        audioRecorder = new AudioRecorder(this);

        ImageView test = findViewById(R.id.startRecordTEST);


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



















    @Override
    public void onFirebaseChanged() {

    }

    @Override
    public void onUserObtained(User user) {


    }
}
