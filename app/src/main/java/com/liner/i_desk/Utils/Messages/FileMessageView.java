package com.liner.i_desk.Utils.Messages;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.R;
import com.liner.utils.FileUtils;
import com.liner.views.CircleProgressBar;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;

import java.io.File;
import java.io.IOException;

public class FileMessageView extends BaseMessageItem {
    private ImageView fileTypeIcon;
    private CircleProgressBar fileDownload;
    private TextView fileName;
    private FileObject fileObject;
    private TextView fileSize;
    private Listener listener;
    private File downloadedFile;

    public FileMessageView(Context context) {
        super(context);
    }

    public FileMessageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.message_filetype_layout, this);
    }

    @Override
    protected void onFindViewById() {
        fileTypeIcon = findViewById(R.id.messageFileTypeIcon);
        fileDownload = findViewById(R.id.fileDownloadBtn);
        fileName = findViewById(R.id.messageFileName);
        fileSize = findViewById(R.id.messageFileSize);
        setVisibility(GONE);
    }

    public void setup(final FileObject fileObject ) {
        this.fileObject = fileObject;
        showView();
        downloadedFile = new File(FileUtils.getDownloadDir()+fileObject.getFileName());
        if(downloadedFile.exists()) {
            fileDownload.setProgressState(CircleProgressBar.ProgressState.FINISHED, false);
        }
        listener = new Listener();
        fileDownload.setOnStateChageListener(new CircleProgressBar.onStateChageListener() {
            @Override
            public void onIdle() {

            }

            @Override
            public void onStart() {
                if(!downloadedFile.exists())
                    FileUtils.createDownloadTask(fileObject.getFileURL(), FileUtils.getDownloadDir() + fileObject.getFileName(), listener).start();
            }

            @Override
            public void onProgressing() {
                FileUtils.cancelDownloadFile(listener);
            }

            @Override
            public void onCancel() {
                FileUtils.cancelDownloadFile(listener);
            }

            @Override
            public void onFinish() {
                FileUtils.openFile(context, downloadedFile, FileUtils.getMimeType(downloadedFile.getAbsolutePath()));

            }

            @Override
            public void onError() {

            }
        });
        fileName.setText(fileObject.getFileName());
        fileSize.setText(FileUtils.humanReadableByteCount(fileObject.getFileSizeInBytes()));
        try {
            fileTypeIcon.setImageDrawable(FileUtils.getFileIcon(context, fileObject.getFileType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class Listener extends FileDownloadListener {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            fileDownload.setProgressState(CircleProgressBar.ProgressState.PROGRESS, false);
            //fileDownload.setProgressWithAnimation(TextUtils.getPercent(soFarBytes, totalBytes));
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            fileDownload.setProgressState(CircleProgressBar.ProgressState.FINISHED, false);
            downloadedFile = new File(task.getTargetFilePath());

        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            fileDownload.setProgressState(CircleProgressBar.ProgressState.CANCEL, false);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            fileDownload.setProgressState(CircleProgressBar.ProgressState.ERROR, false);
        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    }
}
