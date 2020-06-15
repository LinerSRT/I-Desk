package com.liner.i_desk.Adapters;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.liner.i_desk.ActivityRequestDetail;
import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.i_desk.R;
import com.liner.utils.Time;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.YSTextView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private AdapterCallback adapterCallback;
    private UserObject.UserType userType;
    private UserObject currentUser;
    private Activity activity;
    private List<RequestObject> requestObjectList;
    private DatabaseListener databaseListener;
    private int adapterPosition;
    private BaseDialog acceptRequestDialog;
    private BaseDialog closeRequestDialog;
    private BaseDialog closeRequestDialogFinish;


    public RequestAdapter(final UserObject userObject, Activity activity) {
        this.currentUser = userObject;
        this.userType = userObject.getUserType();
        this.activity = activity;
        this.requestObjectList = new ArrayList<>();
        this.databaseListener = new DatabaseListener() {
            @Override
            public void onRequestAdded(RequestObject requestObject, int position) {
                super.onRequestAdded(requestObject, position);
                if(!contain(requestObject)){
                    switch (userType){
                        case ADMIN:
                        case SERVICE:
                            requestObjectList.add(requestObject);
                            if(requestObjectList.size() != 0)
                                notifyItemInserted(requestObjectList.size()-1);
                            else
                                notifyItemInserted(0);
                            break;
                        case CLIENT:
                            if(requestObject.getRequestCreatorID().equals(Firebase.getUserUID())) {
                                requestObjectList.add(requestObject);
                                if (requestObjectList.size() != 0)
                                    notifyItemInserted(requestObjectList.size() - 1);
                                else
                                    notifyItemInserted(0);
                            }
                            break;
                    }
                    if(adapterCallback != null)
                        adapterCallback.onDataChanged(requestObjectList.size());
                }
            }

            @Override
            public void onRequestChanged(RequestObject requestObject, int position) {
                super.onRequestChanged(requestObject, position);
                if(contain(requestObject)) {
                    int index = getIndex(requestObject);
                    requestObjectList.set(index, requestObject);
                    notifyItemChanged(index);
                    if(adapterCallback != null)
                        adapterCallback.onDataChanged(requestObjectList.size());
                }
            }

            @Override
            public void onRequestDeleted(RequestObject requestObject, int position) {
                super.onRequestDeleted(requestObject, position);
                if(requestObjectList.contains(requestObject)) {
                    int index = getIndex(requestObject);
                    requestObjectList.remove(index);
                    notifyItemRemoved(index);
                    if(adapterCallback != null)
                        adapterCallback.onDataChanged(requestObjectList.size());
                }
            }
        };

        acceptRequestDialog = BaseDialogBuilder.buildFast(activity,
                "Принять заявку?",
                "Вы хотите принять текущую заявку? Это действие нельзя будет отменить!",
                "Нет",
                "Да",
                BaseDialogBuilder.Type.WARNING,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        acceptRequestDialog.closeDialog();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RequestObject requestObject = requestObjectList.get(adapterPosition);
                        requestObject.setRequestAcceptorID(Firebase.getUserUID());
                        requestObject.setRequestAcceptorLastOnlineTime(currentUser.getUserLastOnlineAt());
                        requestObject.setRequestAcceptorName(currentUser.getUserName());
                        requestObject.setRequestAcceptorPhotoURL(currentUser.getUserProfilePhotoURL());
                        requestObject.setRequestStatus(RequestObject.RequestStatus.PROCESSING);
                        FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
                        notifyItemChanged(adapterPosition);
                        acceptRequestDialog.closeDialog();
                    }
                }
        );

        closeRequestDialogFinish = BaseDialogBuilder.buildFast(activity,
                "Запрос отправлен",
                "Ваш запрос на закрытие заявки отправлен заявителю. Когда он будет одобрен, заявка автоматически закроется. Спасибо.",
                null,
                "Понятно",
                BaseDialogBuilder.Type.INFO,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeRequestDialogFinish.closeDialog();
                    }
                }
        );
        closeRequestDialog = BaseDialogBuilder.buildFast(activity,
                "Закрыть заявку?",
                "Отправить запрос заявителю для закрытия заявки?",
                "Нет",
                "Отправить",
                BaseDialogBuilder.Type.QUESTION,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeRequestDialog.closeDialog();

                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RequestObject requestObject = requestObjectList.get(adapterPosition);
                        requestObject.setRequestClose(RequestObject.RequestClose.SEND);
                        FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
                        notifyItemChanged(adapterPosition);
                        closeRequestDialog.closeDialog();
                        closeRequestDialogFinish.showDialog();
                    }
                }
        );
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (userType) {
            case ADMIN:
            case SERVICE:
                return new ServiceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.request_service_holder, parent, false));
            case CLIENT:
            default:
                return new ClientViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.request_client_holder, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        adapterPosition = position;
        RequestObject item = requestObjectList.get(position);
        if (holder instanceof ClientViewHolder) {
            ((ClientViewHolder) holder).clientHolderTitle.setText(item.getRequestTitle());
            ((ClientViewHolder) holder).clientHolderText.setText(item.getRequestText());
            ((ClientViewHolder) holder).clientHolderDeadline.setText(Time.getHumanReadableTime(item.getRequestDeadlineAt(), "dd MMM HH:mm"));
            switch (item.getRequestStatus()) {
                case CLOSED:
                    ((ClientViewHolder) holder).clientHolderStatus.setText("Закрыта");
                    break;
                case PENDING:
                    ((ClientViewHolder) holder).clientHolderStatus.setText("В обработке");
                    break;
                case PROCESSING:
                    ((ClientViewHolder) holder).clientHolderStatus.setText("В процессе");
                    break;
            }
            if (item.getRequestAcceptorName() == null || item.getRequestAcceptorName().length() <= 0) {
                ((ClientViewHolder) holder).clientHolderAcceptorName.setVisibility(View.GONE);
            } else {
                ((ClientViewHolder) holder).clientHolderAcceptorName.setVisibility(View.VISIBLE);
                ((ClientViewHolder) holder).clientHolderAcceptorName.setText(item.getRequestAcceptorName());
            }
        } else if (holder instanceof ServiceViewHolder) {
            Picasso.get().load(item.getRequestCreatorPhotoURL()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    ((ServiceViewHolder) holder).serviceHolderUserPhoto.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            ((ServiceViewHolder) holder).serviceHolderUserName.setText(item.getRequestCreatorName());
            ((ServiceViewHolder) holder).serviceHolderTitle.setText(item.getRequestTitle());
            ((ServiceViewHolder) holder).serviceHolderText.setText(item.getRequestText());
            switch (item.getRequestType()) {
                case SERVICE:
                    ((ServiceViewHolder) holder).serviceHolderRequestType.setText("Сервис");
                    ((ServiceViewHolder) holder).serviceHolderRequestType.getBackground().setColorFilter(activity.getResources().getColor(R.color.service_request), PorterDuff.Mode.SRC_IN);
                    break;
                case INCIDENT:
                    ((ServiceViewHolder) holder).serviceHolderRequestType.setText("Инцидент");
                    ((ServiceViewHolder) holder).serviceHolderRequestType.getBackground().setColorFilter(activity.getResources().getColor(R.color.incident_request), PorterDuff.Mode.SRC_IN);
                    break;
                case CONSULTATION:
                    ((ServiceViewHolder) holder).serviceHolderRequestType.setText("Консультация");
                    ((ServiceViewHolder) holder).serviceHolderRequestType.getBackground().setColorFilter(activity.getResources().getColor(R.color.consultation_request), PorterDuff.Mode.SRC_IN);
                    break;
            }
            switch (item.getRequestPriority()) {
                case VERY_LOW:
                    ((ServiceViewHolder) holder).serviceHolderRequestPriority.setText("Очень низкий приоритет");
                    ((ServiceViewHolder) holder).serviceHolderRequestPriority.getBackground().setColorFilter(activity.getResources().getColor(R.color.very_low_priority), PorterDuff.Mode.SRC_IN);
                    break;
                case LOW:
                    ((ServiceViewHolder) holder).serviceHolderRequestPriority.setText("Низкий приоритет");
                    ((ServiceViewHolder) holder).serviceHolderRequestPriority.getBackground().setColorFilter(activity.getResources().getColor(R.color.low_priority), PorterDuff.Mode.SRC_IN);
                    break;
                case MEDIUM:
                    ((ServiceViewHolder) holder).serviceHolderRequestPriority.setText("Нормальный приоритет");
                    ((ServiceViewHolder) holder).serviceHolderRequestPriority.getBackground().setColorFilter(activity.getResources().getColor(R.color.medium_priority), PorterDuff.Mode.SRC_IN);
                    break;
                case HIGH:
                    ((ServiceViewHolder) holder).serviceHolderRequestPriority.setText("Очень высокий приоритет");
                    ((ServiceViewHolder) holder).serviceHolderRequestPriority.getBackground().setColorFilter(activity.getResources().getColor(R.color.high_priority), PorterDuff.Mode.SRC_IN);
                    break;
                case VERY_HIGH:
                    ((ServiceViewHolder) holder).serviceHolderRequestPriority.setText("Высокий приоритет");
                    ((ServiceViewHolder) holder).serviceHolderRequestPriority.getBackground().setColorFilter(activity.getResources().getColor(R.color.very_high_priority), PorterDuff.Mode.SRC_IN);
                    break;
            }
            ((ServiceViewHolder) holder).serviceHolderCreatedAt.setText(Time.getHumanReadableTime(item.getRequestCreatedAt(), "dd MMM HH:mm"));
            ((ServiceViewHolder) holder).serviceHolderDeadlineAt.setText(Time.getHumanReadableTime(item.getRequestDeadlineAt(), "dd MMM HH:mm"));
            if (item.getRequestAcceptorID() != null) {
                if (item.getRequestAcceptorID().equals(Firebase.getUserUID())) {
                    ((ServiceViewHolder) holder).serviceHolderAcceptorName.setText("Вы приняли эту заявку");
                    ((ServiceViewHolder) holder).serviceHolderAcceptRequest.setVisibility(View.GONE);

                    ((ServiceViewHolder) holder).serviceHolderCloseRequest.setVisibility(View.VISIBLE);
                    if(item.getRequestClose() != null){
                        switch (item.getRequestClose()){
                            case SEND:
                                ((ServiceViewHolder) holder).serviceHolderCloseRequest.setEnabled(false);
                                ((ServiceViewHolder) holder).serviceHolderCloseRequest.setText("Ожидание");
                                break;
                            case DENIED:
                                ((ServiceViewHolder) holder).serviceHolderCloseRequest.setEnabled(true);
                                ((ServiceViewHolder) holder).serviceHolderCloseRequest.setText("Закрыть");
                                break;
                            case ACCEPTED:
                                ((ServiceViewHolder) holder).serviceHolderCloseRequest.setEnabled(false);
                                ((ServiceViewHolder) holder).serviceHolderCloseRequest.setText("Закрыто");

                                //todo показывать что заявка закрыта



                                break;
                        }
                    }



                } else {
                    ((ServiceViewHolder) holder).serviceHolderAcceptorName.setText("Эту заявку принял: "+item.getRequestAcceptorName());
                    ((ServiceViewHolder) holder).serviceHolderAcceptRequest.setVisibility(View.GONE);
                    ((ServiceViewHolder) holder).serviceHolderCloseRequest.setVisibility(View.GONE);
                }
            } else {
                ((ServiceViewHolder) holder).serviceHolderCloseRequest.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return requestObjectList.size();
    }

    class ClientViewHolder extends RecyclerView.ViewHolder {
        private YSTextView clientHolderTitle;
        private YSTextView clientHolderText;
        private YSTextView clientHolderDeadline;
        private YSTextView clientHolderStatus;
        private YSTextView clientHolderAcceptorName;
        private ImageButton clientHolderOpenRequest;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            clientHolderTitle = itemView.findViewById(R.id.clientHolderTitle);
            clientHolderText = itemView.findViewById(R.id.clientHolderText);
            clientHolderDeadline = itemView.findViewById(R.id.clientHolderDeadline);
            clientHolderStatus = itemView.findViewById(R.id.clientHolderStatus);
            clientHolderAcceptorName = itemView.findViewById(R.id.clientHolderAcceptorName);
            clientHolderOpenRequest = itemView.findViewById(R.id.clientHolderOpenRequest);
            clientHolderOpenRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ActivityRequestDetail.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("requestObject", requestObjectList.get(getAdapterPosition()));
                    activity.startActivity(intent);
                }
            });
        }
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView serviceHolderUserPhoto;
        private YSTextView serviceHolderUserName;
        private YSTextView serviceHolderTitle;
        private YSTextView serviceHolderText;
        private YSTextView serviceHolderRequestType;
        private YSTextView serviceHolderRequestPriority;
        private YSTextView serviceHolderCreatedAt;
        private YSTextView serviceHolderDeadlineAt;
        private YSTextView serviceHolderAcceptorName;
        private ImageButton serviceHolderOpenRequest;
        private MaterialButton serviceHolderAcceptRequest;
        private MaterialButton serviceHolderCloseRequest;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceHolderUserPhoto = itemView.findViewById(R.id.serviceHolderUserPhoto);
            serviceHolderUserName = itemView.findViewById(R.id.serviceHolderUserName);
            serviceHolderTitle = itemView.findViewById(R.id.serviceHolderTitle);
            serviceHolderText = itemView.findViewById(R.id.serviceHolderText);
            serviceHolderRequestType = itemView.findViewById(R.id.serviceHolderRequestType);
            serviceHolderRequestPriority = itemView.findViewById(R.id.serviceHolderRequestPriority);
            serviceHolderCreatedAt = itemView.findViewById(R.id.serviceHolderCreatedAt);
            serviceHolderDeadlineAt = itemView.findViewById(R.id.serviceHolderDeadlineAt);
            serviceHolderAcceptorName = itemView.findViewById(R.id.serviceHolderAcceptorName);
            serviceHolderOpenRequest = itemView.findViewById(R.id.serviceHolderOpenRequest);
            serviceHolderAcceptRequest = itemView.findViewById(R.id.serviceHolderAcceptRequest);
            serviceHolderCloseRequest = itemView.findViewById(R.id.serviceHolderCloseRequest);
            serviceHolderAcceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterPosition = getLayoutPosition();
                    acceptRequestDialog.showDialog();
                }
            });
            serviceHolderCloseRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterPosition = getLayoutPosition();
                    closeRequestDialog.showDialog();
                }
            });
            serviceHolderOpenRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ActivityRequestDetail.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("requestObject", requestObjectList.get(getAdapterPosition()));
                    activity.startActivity(intent);
                }
            });
        }
    }

    public void setAdapterCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

    public void onStart(){
        databaseListener.startListening();
    }

    public void onDestroy(){
        databaseListener.stopListening();
    }

    public interface AdapterCallback{
        void onDataChanged(int itemsSize);
        void onLoadError();
    }

    private boolean contain(RequestObject item){
        if(requestObjectList.size() <= 0)
            return false;
        for(RequestObject requestObject:requestObjectList){
            if(requestObject.getRequestID().equals(item.getRequestID())){
                return true;
            }
        }
        return false;
    }
    private int getIndex(RequestObject item){
        int index = 0;
        if(requestObjectList.size() <= 0)
            return index;

        for(RequestObject requestObject:requestObjectList){
            if(requestObject.getRequestID().equals(item.getRequestID())){
                return index;
            }
            index++;
        }
        return index;
    }
}
