package com.liner.i_desk;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.liner.views.AttachmentLayoutView;
import com.liner.views.ExpandLayout;
import com.liner.views.MessageInputView;
import com.stfalcon.chatkit.messages.MessageInput;

import java.util.ArrayList;
import java.util.List;


public class TestingActivity extends AppCompatActivity {
    private AttachmentLayoutView attachmentLayoutView;
    private MessageInput messageInput;
    private ExpandLayout expandableLayout;
    private ImageView photoPick;

    private MessageInputView messageInputView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

    }

}
