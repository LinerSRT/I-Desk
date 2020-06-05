package com.liner.i_desk;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.liner.i_desk.Utils.Server.Time;


public class DEBUG extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        long currentTime = Time.getTime();
        long startTime = Time.getTime(2020, 6,5,2,5);
        long endTime = Time.getTime(2020, 6,5,2,15);
        Log.d("TAGTAG", "currentTime "+currentTime);
        Log.d("TAGTAG", "startTime "+startTime);
        Log.d("TAGTAG", "endTime "+endTime);
        Log.d("TAGTAG", "calculate "+calculate(currentTime, startTime, endTime));
        Log.d("TAGTAG", "calculate "+Time.getPercent(startTime, endTime));


    }

    private float calculate(long currentTime, long startTime, long endTime){
        return ((Float.parseFloat(String.valueOf((currentTime-startTime)))/Float.parseFloat(String.valueOf(endTime-startTime))) * 100f);
    }

}
