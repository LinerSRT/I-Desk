package com.liner.i_desk.Fragments.Request;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.liner.i_desk.Constants;
import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.MessageObject;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.Storage.FirebaseFileManager;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.i_desk.Fragments.Request.Messages.CustomIncomingViewHolder;
import com.liner.i_desk.Fragments.Request.Messages.CustomOutcomingViewHolder;
import com.liner.i_desk.R;
import com.liner.utils.FileUtils;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.FileListLayoutView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class RequestMessagesFragment extends Fragment implements MessageInput.AttachmentsListener, MessageInput.InputListener {
    public DatabaseListener databaseListener;
    public BaseDialog uploadFilesDialog;
    private RequestObject requestObject;
    private MessagesListAdapter<MessageObject> messagesListAdapter;
    private MessageInput messageInput;
    private MessagesList messagesList;
    private FileListLayoutView fragmentMessagesFileLayoutView;
    private FilePicker filePicker;
    private BaseDialog processingDialog;
    private BaseDialog maxSizeLimitDialog;
    private BaseDialog errorDialog;
    private List<MessageObject> messageObjectList;


    public RequestMessagesFragment(RequestObject requestObject) {
        this.requestObject = requestObject;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_messages, container, false);
        messagesList = view.findViewById(R.id.messagesList);
        messageInput = view.findViewById(R.id.detailRequestInputMessage);
        fragmentMessagesFileLayoutView = view.findViewById(R.id.fragmentMessagesFileLayoutView);
        this.filePicker = new FilePicker(getActivity());
        messageObjectList = new ArrayList<>();
        filePicker.allowMultiple();
        filePicker.setFilePickerCallback(new FilePickerCallback() {
            @Override
            public void onFilesChosen(List<ChosenFile> list) {
                long selectedFilesSize = 0;
                long existedFilesSize = 0;
                for (ChosenFile existFile : fragmentMessagesFileLayoutView.getFileItemList())
                    existedFilesSize += existFile.getSize();
                List<String> skippedFilenames = new ArrayList<>();
                for (ChosenFile file : list) {
                    selectedFilesSize += file.getSize();
                    if ((existedFilesSize + selectedFilesSize) > Constants.USER_STORAGE_SIZE) {
                        skippedFilenames.add(file.getDisplayName());
                    } else {
                        fragmentMessagesFileLayoutView.addFile(file);
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


        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(CustomIncomingViewHolder.class, R.layout.item_custom_incoming_message)
                .setOutcomingTextConfig(CustomOutcomingViewHolder.class, R.layout.item_custom_outcoming_message);

        messagesListAdapter = new MessagesListAdapter<>(Firebase.getUserUID(), holdersConfig, new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        });
        messagesList.setAdapter(messagesListAdapter);
        messageInput.setInputListener(this);
        messageInput.setAttachmentsListener(this);
        databaseListener = new DatabaseListener() {
            @Override
            public void onMessageAdded(final MessageObject messageObject, int position) {
                super.onMessageAdded(messageObject, position);
                FirebaseValue.getRequest(requestObject.getRequestID(), new FirebaseValue.ValueListener() {
                    @Override
                    public void onSuccess(Object object, DatabaseReference databaseReference) {
                        requestObject = (RequestObject) object;
                        if (requestObject.getRequestMessages() == null)
                            requestObject.setRequestMessages(new HashMap<String, String>());
                        HashMap<String, String> requestMessages = requestObject.getRequestMessages();
                        if (requestMessages.containsKey(messageObject.getId())) {
                            //todo sort by date
                            if(!contain(messageObject))
                                messageObjectList.add(messageObject);
                            sortMessages();
                            messagesListAdapter.clear();
                            for(MessageObject item:messageObjectList) {
                                messagesListAdapter.addToStart(item, true);
                            }

                        }
                    }

                    @Override
                    public void onFail(String errorMessage) {

                    }
                });

            }
        };
        databaseListener.startListening();
        return view;
    }

    @Override
    public void onDetach() {
        if (databaseListener.isListening())
            databaseListener.stopListening();
        super.onDetach();
    }

    @Override
    public void onAddAttachments() {
        processingDialog.showDialog();
        filePicker.pickFile();
    }

    @Override
    public boolean onSubmit(final CharSequence input) {
        if (fragmentMessagesFileLayoutView.getFilesItemList().size() > 0) {
            uploadFilesDialog.showDialog();
            new FirebaseFileManager().uploadFiles(fragmentMessagesFileLayoutView.getFilesItemList(), new TaskListener<List<FileObject>>() {
                @Override
                public void onStart(String fileUID) {

                }

                @Override
                public void onProgress(long transferredBytes, long totalBytes) {

                }

                @Override
                public void onFinish(final List<FileObject> result, String fileUID) {
                    fragmentMessagesFileLayoutView.removeAll();
                    FirebaseValue.getUser(Firebase.getUserUID(), new FirebaseValue.ValueListener() {
                        @Override
                        public void onSuccess(Object object, DatabaseReference databaseReference) {
                            final UserObject userObject = (UserObject) object;
                            FirebaseValue.getRequest(requestObject.getRequestID(), new FirebaseValue.ValueListener() {
                                @Override
                                public void onSuccess(Object object, DatabaseReference databaseReference) {
                                    MessageObject messageObject = new MessageObject();
                                    messageObject.setMessageFiles(new HashMap<String, String>());
                                    requestObject = (RequestObject) object;
                                    for (FileObject fileObject : result) {
                                        if(messageObject.getMessageFiles() == null)
                                            messageObject.setMessageFiles(new HashMap<String, String>());
                                        if(requestObject.getRequestFiles() == null)
                                            requestObject.setRequestFiles(new HashMap<String, String>());
                                        messageObject.getMessageFiles().put(fileObject.getFileID(), fileObject.getFileCreatorID());
                                        requestObject.getRequestFiles().put(fileObject.getFileID(), fileObject.getFileCreatorID());
                                    }
                                    messageObject.setCreatorID(Firebase.getUserUID());
                                    messageObject.setRead(false);
                                    messageObject.setCreatorName(userObject.getUserName());
                                    messageObject.setCreatorPhotoURL(userObject.getUserProfilePhotoURL());
                                    messageObject.setMessageCreatedAt(Time.getTime());
                                    messageObject.setMessageID(TextUtils.getUniqueString());
                                    messageObject.setMessageStatus(MessageObject.MessageStatus.SENDED);
                                    messageObject.setMessageText(input.toString());
                                    if (requestObject.getRequestMessages() == null)
                                        requestObject.setRequestMessages(new HashMap<String, String>());
                                    requestObject.getRequestMessages().put(messageObject.getId(), Firebase.getUserUID());
                                    FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
                                    FirebaseValue.setMessage(messageObject.getId(), messageObject);
                                    fragmentMessagesFileLayoutView.removeAll();
                                    uploadFilesDialog.closeDialog();
                                    processingDialog.closeDialog();
                                }

                                @Override
                                public void onFail(String errorMessage) {
                                    processingDialog.closeDialog();
                                    errorDialog.showDialog();
                                }
                            });
                        }

                        @Override
                        public void onFail(String errorMessage) {
                            processingDialog.closeDialog();
                            errorDialog.showDialog();
                        }
                    });

                }

                @Override
                public void onFailed(Exception reason) {
                    uploadFilesDialog.closeDialog();
                    errorDialog.showDialog();
                }
            });
        } else {
            FirebaseValue.getUser(Firebase.getUserUID(), new FirebaseValue.ValueListener() {
                @Override
                public void onSuccess(Object object, DatabaseReference databaseReference) {
                    final UserObject userObject = (UserObject) object;
                    FirebaseValue.getRequest(requestObject.getRequestID(), new FirebaseValue.ValueListener() {
                        @Override
                        public void onSuccess(Object object, DatabaseReference databaseReference) {
                            requestObject = (RequestObject) object;
                            MessageObject messageObject = new MessageObject();
                            messageObject.setCreatorID(Firebase.getUserUID());
                            messageObject.setRead(false);
                            messageObject.setCreatorName(userObject.getUserName());
                            messageObject.setCreatorPhotoURL(userObject.getUserProfilePhotoURL());
                            messageObject.setMessageCreatedAt(Time.getTime());
                            messageObject.setMessageID(TextUtils.getUniqueString());
                            messageObject.setMessageStatus(MessageObject.MessageStatus.SENDED);
                            messageObject.setMessageText(input.toString());
                            if (requestObject.getRequestMessages() == null)
                                requestObject.setRequestMessages(new HashMap<String, String>());
                            requestObject.getRequestMessages().put(messageObject.getId(), Firebase.getUserUID());
                            FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
                            FirebaseValue.setMessage(messageObject.getId(), messageObject);
                            fragmentMessagesFileLayoutView.removeAll();
                            uploadFilesDialog.closeDialog();
                            processingDialog.closeDialog();
                        }

                        @Override
                        public void onFail(String errorMessage) {
                            processingDialog.closeDialog();
                            errorDialog.showDialog();
                        }
                    });
                }

                @Override
                public void onFail(String errorMessage) {
                    processingDialog.closeDialog();
                    errorDialog.showDialog();
                }
            });
        }

        return true;
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



    public boolean contain(MessageObject item) {
        if (messageObjectList.size() <= 0)
            return false;
        for (MessageObject messageObject : messageObjectList) {
            if (messageObject.getId().equals(item.getId())) {
                return true;
            }
        }
        return false;
    }

    public int getIndex(MessageObject item) {
        int index = 0;
        if (messageObjectList.size() <= 0)
            return index;

        for (MessageObject messageObject : messageObjectList) {
            if (messageObject.getId().equals(item.getId())) {
                return index;
            }
            index++;
        }
        return index;
    }

    private void sortMessages(){
        Collections.sort(messageObjectList, new Comparator<MessageObject>() {
            @Override
            public int compare(MessageObject a, MessageObject b) {
                return Long.compare(a.getMessageCreatedAt(), b.getMessageCreatedAt());
            }
        });
    }

}
