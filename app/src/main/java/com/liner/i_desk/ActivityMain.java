package com.liner.i_desk;


import android.os.Bundle;

import com.liner.i_desk.Utils.Firebase.FireActivity;

public class ActivityMain extends FireActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
    }
}
