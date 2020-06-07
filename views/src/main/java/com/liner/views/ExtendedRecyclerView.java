package com.liner.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ExtendedRecyclerView extends RecyclerView {
    public ExtendedRecyclerView(@NonNull Context context) {
        super(context);
    }

    public ExtendedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean isPaddingOffsetRequired() {
        return true;
    }

    @Override
    protected int getTopPaddingOffset() {
        return -getPaddingStart();
    }

    @Override
    protected int getBottomPaddingOffset() {
        return getPaddingEnd();
    }
}
