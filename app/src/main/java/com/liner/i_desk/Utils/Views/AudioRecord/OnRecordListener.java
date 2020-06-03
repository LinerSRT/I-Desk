package com.liner.i_desk.Utils.Views.AudioRecord;

public interface OnRecordListener {
    void onStart();
    void onCancel();
    void onFinish(long recordTime);
}
