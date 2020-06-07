package com.liner.views.AudioRecord;

public interface OnRecordListener {
    void onStart();
    void onCancel();
    void onFinish(long recordTime);
}
