package com.liner.bottomdialogs;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.BaseConfig;

public class AttachmentDialog extends BaseBottomSheet {

    public AttachmentDialog(@NonNull Activity hostActivity, @NonNull BaseConfig config) {
        super(hostActivity, config);
    }

    @NonNull
    @Override
    protected View onCreateSheetContentView(@NonNull Context context) {
        return null;
    }
}
