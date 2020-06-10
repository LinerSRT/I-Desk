package com.liner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.liner.utils.FileUtils;

import java.io.IOException;

public class RequestCheckView extends BaseItem {
    private ImageView fileTypeIcon;
    private YSTextView fileName;
    private YSTextView fileSize;
    private ImageButton deleteIcon;

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
        fileTypeIcon = findViewById(R.id.fileTypeView);
        fileName = findViewById(R.id.fileName);
        fileSize = findViewById(R.id.fileSize);
        deleteIcon = findViewById(R.id.deleteIcon);
        hideView();
    }


    public void setFileName(String text) {
        fileName.setText(text);
    }

    public void setFileSize(String text) {
        fileSize.setText(text);
    }

    public void setFileType(String fileType){
        try {
            fileTypeIcon.setImageDrawable(FileUtils.getFileIcon(getContext(), fileType));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageButton getDeleteButton(){
        return deleteIcon;
    }
}
