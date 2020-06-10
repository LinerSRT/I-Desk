package com.liner.i_desk.CreateRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.liner.bottomdialogs.BaseDialog;
import com.liner.bottomdialogs.BaseDialogBuilder;
import com.liner.i_desk.Constants;
import com.liner.i_desk.R;
import com.liner.utils.FileUtils;
import com.liner.views.FileListLayoutView;

import java.util.ArrayList;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.Step;

import static android.app.Activity.RESULT_OK;

@SuppressLint("InflateParams")
public class RequestFileStep extends Step<String> {
    private FileListLayoutView requestFileStepView;
    private Button requestFileStepAdd;
    private Activity activity;
    private FilePicker filePicker;
    private BaseDialog processingDialog;
    private BaseDialog maxSizeLimitDialog;
    private BaseDialog errorDialog;

    public RequestFileStep(Activity activity, String stepTitle) {
        super(stepTitle);
        this.activity = activity;
        this.filePicker = new FilePicker(activity);
        filePicker.allowMultiple();
        filePicker.setFilePickerCallback(new FilePickerCallback() {
            @Override
            public void onFilesChosen(List<ChosenFile> list) {
                long selectedFilesSize = 0;
                long existedFilesSize = 0;
                for (ChosenFile existFile : requestFileStepView.getFileItemList())
                    existedFilesSize += existFile.getSize();
                List<String> skippedFilenames = new ArrayList<>();
                for (ChosenFile file : list) {
                    selectedFilesSize += file.getSize();
                    if ((existedFilesSize + selectedFilesSize) > Constants.USER_STORAGE_SIZE) {
                        skippedFilenames.add(file.getDisplayName());
                    } else {
                        requestFileStepView.addFile(file);
                    }
                }
                if (!skippedFilenames.isEmpty()) {
                    StringBuilder message = new StringBuilder();
                    for (String filename : skippedFilenames)
                        message.append(" - ").append(filename).append("\n");
                    maxSizeLimitDialog.setDialogTextText("Выбранные файлы превышают лимит объема! (" + FileUtils.humanReadableByteCount(Constants.USER_STORAGE_SIZE) + ")\n" + "\nСледующие файлы были пропущены:\n" + message.toString());
                    maxSizeLimitDialog.showDialog();
                }
                processingDialog.closeDialog();
            }

            @Override
            public void onError(String s) {
                processingDialog.closeDialog();
                errorDialog.showDialog();
            }
        });
        processingDialog = BaseDialogBuilder.buildFast(activity,
                "Обработка...",
                "Идет обработка, пожалуйста подождите",
                null,
                null,
                BaseDialogBuilder.Type.INDETERMINATE,
                null,
                null);
        maxSizeLimitDialog = BaseDialogBuilder.buildFast(activity,
                "Внимание",
                "Выбранные файлы превышают лимит! (" + FileUtils.humanReadableByteCount(Constants.USER_STORAGE_SIZE) + ")",
                null,
                "Понятно",
                BaseDialogBuilder.Type.WARNING,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        maxSizeLimitDialog.closeDialog();
                    }
                });
        errorDialog = BaseDialogBuilder.buildFast(activity,
                "Ошибка",
                "Произошла ошибка при добавлении файлов. Попробуйте выбрать другие файлы",
                null,
                "Понятно",
                BaseDialogBuilder.Type.ERROR,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        maxSizeLimitDialog.closeDialog();
                    }
                });
    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.request_files_step, null, false);
        requestFileStepView = view.findViewById(R.id.requestFileStepView);
        requestFileStepAdd = view.findViewById(R.id.requestFileStepAdd);
        requestFileStepView.setActivity(activity);
        requestFileStepAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processingDialog.showDialog();
                filePicker.pickFile();
            }
        });
        return view;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(true, "");
    }

    @Override
    public String getStepData() {
        return "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        long totalSize = 0;
        for (ChosenFile file : requestFileStepView.getFileItemList())
            totalSize += file.getSize();
        return "Выбрано " + requestFileStepView.getFileItemList().size() + " объекта. Общим объемом " + FileUtils.humanReadableByteCount(totalSize);
    }

    @Override
    protected void onStepOpened(boolean animated) {

    }

    @Override
    protected void onStepClosed(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }

    @Override
    public void restoreStepData(String stepData) {

    }

    public void submitPicker(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Picker.PICK_FILE && resultCode == RESULT_OK) {
            filePicker.submit(data);
        }
    }
}