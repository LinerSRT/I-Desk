package com.liner.i_desk.Utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {

    public static byte[] getFileByteArray(File file) {
        byte[] bytes = new byte[(int) file.length()];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }


    public static String getAppDownloadDir(Context context) {
        return Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/download/";
    }

    public static String getAppDownloadPhotoDir(Context context) {
        return Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/download/photo/";
    }

    public static String getAppDownloadCacheDir(Context context) {
        return Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/download/cache/";
    }


    public interface ImageFormat {
        String PNG = "image/png";
        String JPG = "image/jpg";
        String JPEG = "image/jpeg";
        String WEBP = "image/webp";
        String GIF = "image/gif";
        String SVG = "image/svg+xml";

        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(PNG);
                add(JPG);
                add(JPEG);
                add(WEBP);
                add(GIF);
                add(SVG);
            }
        });
    }

    public interface AudioFormat {
        String RFC = "audio/basic";
        String PCM = "audio/L24";
        String MP4 = "audio/mp4";
        String AAC = "audio/aac";
        String MPEG = "audio/mpeg";
        String OGG = "audio/ogg";
        String VORBIS = "audio/vorbis";

        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(RFC);
                add(PCM);
                add(MP4);
                add(AAC);
                add(MPEG);
                add(OGG);
                add(VORBIS);
            }
        });
    }

    public interface FileFormat {
        String JSON = "application/json";
        String JAVASCRIPT = "application/javascript";
        String OCET = "application/octet-stream";
        String OGG = "application/ogg";
        String PDF = "application/pdf";
        String ZIP = "application/zip";
        String GZIP = "application/gzip";
        String XML = "application/xml";

        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(JSON);
                add(JAVASCRIPT);
                add(OCET);
                add(OGG);
                add(PDF);
                add(ZIP);
                add(GZIP);
                add(XML);
            }
        });
    }

    public interface TextFormat {
        String HTML = "text/html";
        String PLAIN = "text/plain";

        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(HTML);
                add(PLAIN);
            }
        });
    }

    public interface VideoFormat {
        String MPEG = "video/mpeg";
        String MP4 = "video/mp4";
        String OGG = "video/ogg";
        String WEBM = "video/webm";
        String TREE_GPP = "video/3gpp";
        String TREE_GPP2 = "video/3gpp2";

        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(MPEG);
                add(MP4);
                add(OGG);
                add(WEBM);
                add(TREE_GPP);
                add(TREE_GPP2);
            }
        });
    }

}
