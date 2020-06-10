package com.liner.i_desk;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.liner.i_desk.Firebase.FireActivity;
import com.roacult.backdrop.BackdropLayout;

import spencerstudios.com.bungeelib.Bungee;

public class ActivityMain extends FireActivity {


    private LinearLayout mainLayoutToProfile;
    private LinearLayout mainLayoutToAddRequest;
    private BackdropLayout mainBackdropLayout;
    private FloatingActionButton addNewRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        mainLayoutToAddRequest = findViewById(R.id.mainLayoutToProfile);
        mainLayoutToProfile = findViewById(R.id.mainLayoutToProfile);
        mainBackdropLayout = findViewById(R.id.mainBackdropLayout);
        addNewRequestButton = findViewById(R.id.addNewRequestButton);
        addNewRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityMain.this, ActivityCreateRequest.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                Bungee.slideUp(ActivityMain.this);
            }
        });


        mainLayoutToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityMain.this, ActivityUserProfile.class));
                Bungee.slideUp(ActivityMain.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mainBackdropLayout.close();
    }
}
