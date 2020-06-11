package com.liner.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.liner.utils.FileUtils;

import java.io.IOException;

public class RequestCheckView extends BaseItem {
    private RadioButton requestCheckButton;
    private YSEditText requestCheckText;
    private ImageButton requestCheckEditButton;
    private ImageButton requestDeleteButton;

    public RequestCheckView(Context context) {
        super(context);
    }

    public RequestCheckView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.request_check_view, this);
    }

    @Override
    protected void onFindViewById() {
        requestCheckButton = findViewById(R.id.requestCheckButton);
        requestCheckText = findViewById(R.id.requestCheckText);
        requestCheckEditButton = findViewById(R.id.requestCheckEditButton);
        requestDeleteButton = findViewById(R.id.requestDeleteButton);
        hideView();
    }

    public ImageButton getRequestCheckEditButton() {
        return requestCheckEditButton;
    }

    public ImageButton getRequestDeleteButton() {
        return requestDeleteButton;
    }

    public RadioButton getRequestCheckButton() {
        return requestCheckButton;
    }

    public YSEditText getRequestCheckText() {
        return requestCheckText;
    }


}
