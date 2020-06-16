package com.liner.i_desk.Fragments.Request;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.liner.i_desk.Adapters.RequestFilesAdapter;
import com.liner.i_desk.Constants;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.Storage.FirebaseFileManager;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.i_desk.R;
import com.liner.utils.FileUtils;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.DisableLayout;
import com.liner.views.FileListLayoutView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class RequestFilesFragment extends Fragment {
    public BaseDialog uploadFilesDialog;
    public BaseDialog finishDialog;
    private RequestObject requestObject;
    private RecyclerView filesRecycler;
    private RequestFilesAdapter requestFilesAdapter;
    private MaterialButton fragmentRequestAddNewView;
    private MaterialButton fragmentRequestUpload;
    private FileListLayoutView fragmentRequestFileLayoutView;
    private FilePicker filePicker;
    private BaseDialog processingDialog;
    private BaseDialog maxSizeLimitDialog;
    private BaseDialog errorDialog;
    private FrameLayout fragmentRequestFileLayoutViewLayout;
    private DisableLayout disableLayout;

    public RequestFilesFragment(RequestObject requestObject) {
        this.requestObject = requestObject;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_files, container, false);
        fragmentRequestAddNewView = view.findViewById(R.id.fragmentRequestAddNewView);
        fragmentRequestUpload = view.findViewById(R.id.fragmentRequestUpload);
        fragmentRequestFileLayoutView = view.findViewById(R.id.fragmentRequestFileLayoutView);
        fragmentRequestFileLayoutViewLayout = view.findViewById(R.id.fragmentRequestFileLayoutViewLayout);
        disableLayout = view.findViewById(R.id.disableFilesLayout);
        if(requestObject.getRequestStatus() == RequestObject.RequestStatus.CLOSED){
            disableLayout.setVisibility(View.VISIBLE);
            disableLayout.setDisableTouch(true);
        } else {
            disableLayout.setVisibility(View.GONE);
        }
        this.filePicker = new FilePicker(getActivity());
        filePicker.allowMultiple();
        filePicker.setFilePickerCallback(new FilePickerCallback() {
            @Override
            public void onFilesChosen(List<ChosenFile> list) {
                long selectedFilesSize = 0;
                long existedFilesSize = 0;
                for (ChosenFile existFile : fragmentRequestFileLayoutView.getFileItemList())
                    existedFilesSize += existFile.getSize();
                List<String> skippedFilenames = new ArrayList<>();
                for (ChosenFile file : list) {
                    selectedFilesSize += file.getSize();
                    if ((existedFilesSize + selectedFilesSize) > Constants.USER_STORAGE_SIZE) {
                        skippedFilenames.add(file.getDisplayName());
                    } else {
                        fragmentRequestFileLayoutView.addFile(file);
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
                fragmentRequestUpload.setVisibility((fragmentRequestFileLayoutView.getFileItemList().size() <= 0) ? View.GONE : View.VISIBLE);
                fragmentRequestFileLayoutViewLayout.setVisibility((fragmentRequestFileLayoutView.getFileItemList().size() <= 0) ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onError(String s) {
                processingDialog.closeDialog();
                errorDialog.showDialog();
            }
        });
        finishDialog = BaseDialogBuilder.buildFast(
                getActivity(),
                "Загружено",
                "Файлы были успешно загружены",
                null,
                "Ок",
                BaseDialogBuilder.Type.INFO,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finishDialog.closeDialog();
                        fragmentRequestUpload.setVisibility(View.GONE);
                        fragmentRequestFileLayoutViewLayout.setVisibility(View.GONE);
                    }
                }
        );
        uploadFilesDialog = BaseDialogBuilder.buildFast(
                getActivity(),
                "Загрузка файлов",
                "Пожалуйста подождите, ваши файлы загружаются на сервер",
                null,
                null,
                BaseDialogBuilder.Type.INDETERMINATE,
                null,
                null
        );
        processingDialog = BaseDialogBuilder.buildFast(getActivity(),
                "Обработка...",
                "Идет обработка, пожалуйста подождите",
                null,
                null,
                BaseDialogBuilder.Type.INDETERMINATE,
                null,
                null);
        maxSizeLimitDialog = BaseDialogBuilder.buildFast(getActivity(),
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
        errorDialog = BaseDialogBuilder.buildFast(getActivity(),
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

        fragmentRequestFileLayoutView.setActivity(getActivity());
        fragmentRequestAddNewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processingDialog.showDialog();
                filePicker.pickFile();
            }
        });
        fragmentRequestUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFilesDialog.showDialog();
                new FirebaseFileManager().uploadFiles(fragmentRequestFileLayoutView.getFilesItemList(), new TaskListener<List<FileObject>>() {
                    @Override
                    public void onStart(String fileUID) {

                    }

                    @Override
                    public void onProgress(long transferredBytes, long totalBytes) {

                    }

                    @Override
                    public void onFinish(final List<FileObject> result, String fileUID) {
                        FirebaseValue.getRequest(requestObject.getRequestID(), new FirebaseValue.ValueListener() {
                            @Override
                            public void onSuccess(Object object, DatabaseReference databaseReference) {
                                RequestObject newObj = (RequestObject) object;
                                for (FileObject fileObject : result) {
                                    newObj.getRequestFiles().put(fileObject.getFileID(), fileObject.getFileCreatorID());
                                    requestFilesAdapter.addFile(fileObject);
                                }
                                FirebaseValue.setRequest(newObj.getRequestID(), newObj);
                                requestObject = newObj;
                                fragmentRequestFileLayoutView.removeAll();
                                uploadFilesDialog.closeDialog();
                                processingDialog.closeDialog();
                                finishDialog.showDialog();
                            }

                            @Override
                            public void onFail(String errorMessage) {

                            }
                        });


                    }

                    @Override
                    public void onFailed(Exception reason) {
                        uploadFilesDialog.closeDialog();
                        errorDialog.showDialog();
                    }
                });
            }
        });

        filesRecycler = view.findViewById(R.id.fragmentRequestFilesRecycler);
        requestFilesAdapter = new RequestFilesAdapter(getActivity(), requestObject);
        filesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        filesRecycler.setAdapter(requestFilesAdapter);
        requestFilesAdapter.onStart();

        return view;
    }

    @Override
    public void onDetach() {
        requestFilesAdapter.onDestroy();
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Picker.PICK_FILE && resultCode == RESULT_OK) {
            filePicker.submit(data);
        } else if (requestCode == Picker.PICK_FILE && resultCode == RESULT_CANCELED) {
            processingDialog.closeDialog();
        }
    }
}
