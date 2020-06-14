

package com.liner.views.bottomsheetcore;

import android.app.Activity;


import androidx.annotation.NonNull;

import com.liner.views.bottomsheetcore.config.BaseConfig;


public abstract class BaseBottomSheet extends BottomSheetContainer {

    public BaseBottomSheet(@NonNull Activity hostActivity, @NonNull BaseConfig config) {
        super(hostActivity, config);
    }
}