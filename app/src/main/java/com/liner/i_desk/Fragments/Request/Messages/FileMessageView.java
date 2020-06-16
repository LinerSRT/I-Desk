package com.liner.i_desk.Fragments.Request.Messages;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.github.abdularis.buttonprogress.DownloadButtonProgress;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.Storage.FirebaseFileManager;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.i_desk.R;
import com.liner.utils.FileUtils;
import com.liner.views.BaseItem;
import com.liner.views.YSMarqueTextView;
import com.liner.views.YSTextView;

import java.io.File;
import java.io.IOException;

public class FileMessageView extends BaseItem {
    private ImageView fileHolderIcon;
    private YSMarqueTextView fileHolderName;
    private YSTextView fileHolderSize;
    private DownloadButtonProgress fileHolderDownload;
    private FirebaseFileManager firebaseFileManager;
    private File file;

    public FileMessageView(Context context) {
        super(context);
    }

    public FileMessageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.files_view_holder, this);
    }

    @Override
    protected void onFindViewById() {
        fileHolderIcon = findViewById(R.id.fileHolderIcon);
        fileHolderName = findViewById(R.id.fileHolderName);
        fileHolderSize = findViewById(R.id.fileHolderSize);
        fileHolderDownload = findViewById(R.id.fileHolderDownload);
        ImageButton fileHolderDelete = findViewById(R.id.fileHolderDelete);
        fileHolderDelete.setVisibility(GONE);
        setVisibility(GONE);
    }

    public void setup(final FileObject fileObject) {
        showView();
        try {
            fileHolderIcon.setImageDrawable(FileUtils.getFileIcon(getContext(), fileObject.getFileType()));
        } catch (IOException e) {
            e.printStackTrace();
            fileHolderIcon.setImageResource(R.drawable.file_icon);
        }
        fileHolderName.setText(fileObject.getFileName());
        fileHolderSize.setText(FileUtils.humanReadableByteCount(fileObject.getFileSizeInBytes()));
        firebaseFileManager = new FirebaseFileManager();
        fileHolderDownload.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                firebaseFileManager.download(fileObject, new TaskListener<File>() {
                    @Override
                    public void onStart(String fileUID) {
                        fileHolderDownload.setDeterminate();
                        file = null;
                    }

                    @Override
                    public void onProgress(long transferredBytes, long totalBytes) {
                        fileHolderDownload.setCurrentProgress(Math.round(((float)transferredBytes/(float)totalBytes)*100));
                    }

                    @Override
                    public void onFinish(File result, String fileUID) {
                        fileHolderDownload.setFinish();
                        file = result;
                    }

                    @Override
                    public void onFailed(Exception reason) {
                        fileHolderDownload.setIdle();
                        file = null;
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {
                firebaseFileManager.cancel();
                fileHolderDownload.setIdle();
                file = null;

            }

            @Override
            public void onFinishButtonClick(View view) {
                if(file != null)
                    FileUtils.openFile(getContext(), file, FileUtils.getMimeType(file.getAbsolutePath()));
            }
        });
    }

}
