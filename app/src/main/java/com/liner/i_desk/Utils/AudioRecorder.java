package com.liner.i_desk.Utils;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class AudioRecorder implements MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener{
    private static MediaRecorder audioRecorder;
    private Context context;
    private String outName;

    public AudioRecorder(Context context) {
        this.context = context;
        //this.outName = TextUtils.generateRandomString(10);
    }

    public void start() {
        start(context.getFilesDir().getAbsolutePath()+"/" + outName + ".mp3");
    }

    public void start(String uriOutputFile) {
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        audioRecorder.setAudioEncodingBitRate(256);
        audioRecorder.setAudioChannels(1);
        audioRecorder.setAudioSamplingRate(44100);
        audioRecorder.setOutputFile(uriOutputFile);
        audioRecorder.setOnInfoListener(this);
        audioRecorder.setOnErrorListener(this);
        try {
            audioRecorder.prepare();
            audioRecorder.start();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (audioRecorder != null) {
            audioRecorder.stop();
            audioRecorder.release();
            audioRecorder = null;
        }
    }

    public void pause() {
        if (audioRecorder != null) {
            audioRecorder.release();
            audioRecorder = null;
        }
    }

    public int getAmplitude(){
        if (audioRecorder != null) {
           return audioRecorder.getMaxAmplitude();
        }
        return 0;
    }

    @Override
    public void onError(MediaRecorder mediaRecorder, int i, int i1) {

    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {

    }

    public File getResult(){
        return new File(context.getFilesDir().getAbsolutePath()+"/" + outName + ".mp3");
    }

    public void deleteResult() {
        File resultFile = new File(context.getFilesDir().getAbsolutePath()+"/" + outName + ".mp3");
        if(resultFile.exists())
            resultFile.delete();
    }

}