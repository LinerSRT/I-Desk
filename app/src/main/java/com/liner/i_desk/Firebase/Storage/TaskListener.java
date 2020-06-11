package com.liner.i_desk.Firebase.Storage;

public interface TaskListener <T>{
    void onStart(String fileUID);
    void onProgress(long transferredBytes, long totalBytes);
    void onFinish(T result, String fileUID);
    void onFailed(Exception reason);
}