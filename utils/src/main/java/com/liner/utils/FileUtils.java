package com.liner.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.webkit.MimeTypeMap;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {

    public static long getListFilesSize(List<File> fileList){
        long size = 0;
        for(File file:fileList)
            size += file.length();
        return size;
    }

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


    public static String getDownloadDir() {
        return FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "temp_files" + File.separator;
    }

    public static void openFile(Context context, File file, String fileType) {
        try {
            Uri uri = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, fileType);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String humanReadableByteCount(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static BaseDownloadTask createDownloadTask(String url, String path, FileDownloadListener listener) {
        return FileDownloader.getImpl().create(url)
                .setPath(path)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(listener);
    }

    public static void pauseDownloadFile(FileDownloadListener listener) {
        FileDownloader.getImpl().pause(listener);
    }

    public static void cancelDownloadFile(FileDownloadListener listener) {
        FileDownloader.getImpl().pause(listener);
        FileDownloader.getImpl().clearAllTaskData();
    }

    public static Drawable getFileIcon(Context context, File file) throws IOException {
        String fileType = getMimeType(file.getAbsolutePath());
        return getFileIcon(context, fileType);
    }

    public static Drawable getFileIcon(Context context, String fileType) throws IOException {
        if (AudioFormat.SUPPORTED_LIST.contains(fileType)) {
            return Drawable.createFromStream(context.getAssets().open("filetypes/mp3.png"), null);
        } else if (ImageFormat.SUPPORTED_LIST.contains(fileType)) {
            if (fileType.contains(ImageFormat.PNG))
                return Drawable.createFromStream(context.getAssets().open("filetypes/png.png"), null);
            return Drawable.createFromStream(context.getAssets().open("filetypes/jpg.png"), null);
        } else if (FileFormat.SUPPORTED_LIST.contains(fileType)) {
            if (fileType.contains(FileFormat.ZIP))
                return Drawable.createFromStream(context.getAssets().open("filetypes/zip.png"), null);
            if (fileType.contains(FileFormat.GZIP))
                return Drawable.createFromStream(context.getAssets().open("filetypes/zip.png"), null);
            return Drawable.createFromStream(context.getAssets().open("filetypes/file.png"), null);
        } else if (TextFormat.SUPPORTED_LIST.contains(fileType)) {
            return Drawable.createFromStream(context.getAssets().open("filetypes/txt.png"), null);
        } else if (VideoFormat.SUPPORTED_LIST.contains(fileType)) {
            return Drawable.createFromStream(context.getAssets().open("filetypes/mp4.png"), null);
        } else {
            return Drawable.createFromStream(context.getAssets().open("filetypes/file.png"), null);
        }
    }


    public static File inputStreamToFile(InputStream inputStream) {
        OutputStream outputStream ;
        try {
            File file = new File(getDownloadDir()+"temp_"+TextUtils.getUniqueString());
            outputStream = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public interface AllowedMediaSearchFolders{
        String DCIM = "DCIM";
        String PICTURES = "Pictures";
        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(DCIM);
                add(PICTURES);
            }
        });
    }

    public interface FileFormat {
        String OCET = "application/octet-stream";
        String OGG = "application/ogg";
        String ZIP = "application/zip";
        String GZIP = "application/gzip";

        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(OCET);
                add(OGG);
                add(ZIP);
                add(GZIP);
            }
        });
    }

    public interface TextFormat {
        String HTML = "text/html";
        String PLAIN = "text/plain";
        String XML = "application/xml";
        String PDF = "application/pdf";
        String JSON = "application/json";
        String JAVASCRIPT = "application/javascript";

        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(PDF);
                add(JSON);
                add(JAVASCRIPT);
                add(XML);
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
