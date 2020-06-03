package com.liner.i_desk.Utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

@SuppressLint("StaticFieldLeak")
public class LoadVideoThumb extends AsyncTask<Void, Void, Bitmap> {
    private String URL;
    private ImageView imageView;
    private String fileName;

    public LoadVideoThumb(String URL, ImageView imageView, String fileName) {
        this.URL = URL;
        this.imageView = imageView;
        this.fileName = fileName;
    }

    @Override
    protected Bitmap doInBackground(Void... v) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(URL, new HashMap<String, String>());
            Bitmap bitmap = retriever.getFrameAtTime();
            FileOutputStream outStream;
            File file = new File(fileName);
            if(!file.exists()) {
                file.createNewFile();
            }
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outStream);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }
    }
}