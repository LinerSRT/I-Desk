package com.liner.i_desk.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liner.i_desk.ActivityRequestDetail;
import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.i_desk.R;
import com.liner.utils.Time;
import com.liner.utils.ViewUtils;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.YSTextView;
import com.liner.views.verticalstepperform.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
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
                if (!contain(requestObject)) {
                    switch (userType) {
                        case ADMIN:
                        case SERVICE:
                            requestObjectList.add(requestObject);
                            if (requestObjectList.size() != 0)
                                notifyItemInserted(requestObjectList.size() - 1);
                            else
                                notifyItemInserted(0);
                            break;
                        case CLIENT:
                            if (requestObject.getRequestCreatorID().equals(Firebase.getUserUID())) {
                                requestObjectList.add(requestObject);
                                if (requestObjectList.size() != 0)
                                    notifyItemInserted(requestObjectList.size() - 1);
                                else
                                    notifyItemInserted(0);
                            }
                            break;
                    }
                    if (adapterCallback != null)
                        adapterCallback.onDataChanged(requestObjectList.size());
                }
                sortRequests();
            }

            @Override
            public void onRequestChanged(RequestObject requestObject, int position) {
                super.onRequestChanged(requestObject, position);
                if (contain(requestObject)) {
                    int index = getIndex(requestObject);
                    requestObjectList.set(index, requestObject);
                    notifyItemChanged(index);
                    if (adapterCallback != null)
                        adapterCallback.onDataChanged(requestObjectList.size());
                }
                sortRequests();
            }

            @Override
            public void onRequestDeleted(RequestObject requestObject, int position) {
                super.onRequestDeleted(requestObject, position);
                if (requestObjectList.contains(requestObject)) {
                    int index = getIndex(requestObject);
                    requestObjectList.remove(index);
                    notifyItemRemoved(index);
                    if (adapterCallback != null)
                        adapterCallback.onDataChanged(requestObjectList.size());
                }
                sortRequests();
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

    private void sortRequests() {
        Collections.sort(requestObjectList, new Comparator<RequestObject>() {
            @Override
            public int compare(RequestObject a, RequestObject b) {
                return Long.compare(b.getRequestCreatedAt(), a.getRequestCreatedAt());
            }
        });
    }


    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.request_service_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestViewHolder holder, int position) {
        adapterPosition = position;
        RequestObject item = requestObjectList.get(position);
        holder.requestRate.setVisibility((item.isRequestRated())?View.VISIBLE:View.GONE);
        holder.requestClosedHolder.setVisibility((item.getRequestStatus() == RequestObject.RequestStatus.CLOSED)?View.VISIBLE:View.GONE);
        if(item.getRequestRate() != 0)
            holder.requestRate.setText(String.valueOf(item.getRequestRate()));
        Picasso.get().load(item.getRequestCreatorPhotoURL()).resize(ViewUtils.dpToPx(48), ViewUtils.dpToPx(48)).into(holder.serviceHolderUserPhoto);
        holder.serviceHolderUserName.setText(item.getRequestCreatorName());
        holder.serviceHolderTitle.setText(item.getRequestTitle());
        holder.serviceHolderText.setText(item.getRequestText());
        switch (item.getRequestType()) {
            case SERVICE:
                holder.serviceHolderRequestType.setText("Сервис");
                holder.serviceHolderRequestType.getBackground().setColorFilter(activity.getResources().getColor(R.color.service_request), PorterDuff.Mode.SRC_IN);
                break;
            case INCIDENT:
                holder.serviceHolderRequestType.setText("Инцидент");
                holder.serviceHolderRequestType.getBackground().setColorFilter(activity.getResources().getColor(R.color.incident_request), PorterDuff.Mode.SRC_IN);
                break;
            case CONSULTATION:
                holder.serviceHolderRequestType.setText("Консультация");
                holder.serviceHolderRequestType.getBackground().setColorFilter(activity.getResources().getColor(R.color.consultation_request), PorterDuff.Mode.SRC_IN);
                break;
        }
        switch (item.getRequestPriority()) {
            case VERY_LOW:
                holder.serviceHolderRequestPriority.setText("Оч. низкий приоритет");
                holder.serviceHolderRequestPriority.getBackground().setColorFilter(activity.getResources().getColor(R.color.very_low_priority), PorterDuff.Mode.SRC_IN);
                break;
            case LOW:
                holder.serviceHolderRequestPriority.setText("Низкий приоритет");
                holder.serviceHolderRequestPriority.getBackground().setColorFilter(activity.getResources().getColor(R.color.low_priority), PorterDuff.Mode.SRC_IN);
                break;
            case MEDIUM:
                holder.serviceHolderRequestPriority.setText("Нормальный приоритет");
                holder.serviceHolderRequestPriority.getBackground().setColorFilter(activity.getResources().getColor(R.color.medium_priority), PorterDuff.Mode.SRC_IN);
                break;
            case HIGH:
                holder.serviceHolderRequestPriority.setText("Оч. высокий приоритет");
                holder.serviceHolderRequestPriority.getBackground().setColorFilter(activity.getResources().getColor(R.color.high_priority), PorterDuff.Mode.SRC_IN);
                break;
            case VERY_HIGH:
                holder.serviceHolderRequestPriority.setText("Высокий приоритет");
                holder.serviceHolderRequestPriority.getBackground().setColorFilter(activity.getResources().getColor(R.color.very_high_priority), PorterDuff.Mode.SRC_IN);
                break;
        }
        holder.serviceHolderCreatedAt.setText(Time.getHumanReadableTime(item.getRequestCreatedAt(), "dd MMM HH:mm"));
        holder.serviceHolderDeadlineAt.setText(Time.getHumanReadableTime(item.getRequestDeadlineAt(), "dd MMM HH:mm"));

    }

    @Override
    public int getItemCount() {
        return requestObjectList.size();
    }

    public void setAdapterCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

    public void onStart() {
        databaseListener.startListening();
    }

    public void onDestroy() {
        requestObjectList.clear();
        notifyDataSetChanged();
        databaseListener.stopListening();
    }

    private boolean contain(RequestObject item) {
        if (requestObjectList.size() <= 0)
            return false;
        for (RequestObject requestObject : requestObjectList) {
            if (requestObject.getRequestID().equals(item.getRequestID())) {
                return true;
            }
        }
        return false;
    }

    private int getIndex(RequestObject item) {
        int index = 0;
        if (requestObjectList.size() <= 0)
            return index;

        for (RequestObject requestObject : requestObjectList) {
            if (requestObject.getRequestID().equals(item.getRequestID())) {
                return index;
            }
            index++;
        }
        return index;
    }

    public interface AdapterCallback {
        void onDataChanged(int itemsSize);

        void onLoadError();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView serviceHolderUserPhoto;
        private YSTextView serviceHolderUserName;
        private YSTextView serviceHolderTitle;
        private YSTextView serviceHolderText;
        private YSTextView serviceHolderRequestType;
        private YSTextView serviceHolderRequestPriority;
        private YSTextView serviceHolderCreatedAt;
        private YSTextView serviceHolderDeadlineAt;
        private YSTextView requestRate;
        private YSTextView requestClosedHolder;
        private ImageButton serviceHolderOpenRequest;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceHolderUserPhoto = itemView.findViewById(R.id.serviceHolderUserPhoto);
            requestRate = itemView.findViewById(R.id.requestRate);
            requestClosedHolder = itemView.findViewById(R.id.requestClosedHolder);
            serviceHolderUserName = itemView.findViewById(R.id.serviceHolderUserName);
            serviceHolderTitle = itemView.findViewById(R.id.serviceHolderTitle);
            serviceHolderText = itemView.findViewById(R.id.serviceHolderText);
            serviceHolderRequestType = itemView.findViewById(R.id.serviceHolderRequestType);
            serviceHolderRequestPriority = itemView.findViewById(R.id.serviceHolderRequestPriority);
            serviceHolderCreatedAt = itemView.findViewById(R.id.serviceHolderCreatedAt);
            serviceHolderDeadlineAt = itemView.findViewById(R.id.serviceHolderDeadlineAt);
            serviceHolderOpenRequest = itemView.findViewById(R.id.serviceHolderOpenRequest);
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
}
