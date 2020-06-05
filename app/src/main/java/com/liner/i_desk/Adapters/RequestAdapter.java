package com.liner.i_desk.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.UI.RequestDetailActivity;
import com.liner.i_desk.UI.SplashActivity;
import com.liner.i_desk.Utils.ColorUtils;
import com.liner.i_desk.Utils.Server.Time;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;
import com.liner.i_desk.Utils.Views.VerticalTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private Activity activity;
    private List<Request> requestList;
    private User currentUser;

    public RequestAdapter(Activity activity, List<Request> requestList, User currentUser) {
        this.activity = activity;
        this.requestList = requestList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Request item = requestList.get(position);
        if(item.getRequestStatus() == Request.Status.CLOSED){
            holder.requestLockedView.setVisibility(View.VISIBLE);
        } else {
            holder.requestLockedView.setVisibility(View.GONE);
        }
        if(item.getRequestCreatorID().equals(currentUser.getUserUID())){
            Picasso.get().load(currentUser.getUserPhotoURL()).into(holder.requestCreatorPhoto);
            holder.requestCreatorName.setText(currentUser.getUserName());
            holder.requestCreatorLayout.setVisibility(View.VISIBLE);
        } else {
            FirebaseHelper.getUserModel(item.getRequestCreatorID(), new FirebaseHelper.IFirebaseHelperListener() {
                @Override
                public void onSuccess(Object result) {
                    User user = (User) result;
                    Picasso.get().load(user.getUserPhotoURL()).into(holder.requestCreatorPhoto);
                    holder.requestCreatorName.setText(user.getUserName());
                }

                @Override
                public void onFail(String reason) {

                }
            });
        }


        switch (item.getRequestType()) {
            case SERVICE:
                holder.requestTypeString.setText("Сервис");
                holder.requestAccentLayout.getBackground().setColorFilter(activity.getResources().getColor(R.color.request_service), PorterDuff.Mode.SRC_IN);
                break;
            case INCIDENT:
                holder.requestTypeString.setText("Инцидент");
                holder.requestAccentLayout.getBackground().setColorFilter(activity.getResources().getColor(R.color.request_incident), PorterDuff.Mode.SRC_IN);
                break;
            case CONSULTATION:
                holder.requestTypeString.setText("Консультация");
                holder.requestAccentLayout.getBackground().setColorFilter(activity.getResources().getColor(R.color.request_consultation), PorterDuff.Mode.SRC_IN);
                break;
        }

        holder.requestName.setText(item.getRequestTitle());
        holder.requestText.setText(item.getRequestShortDescription());
        holder.requestTimeCreation.setText(Time.getHumanReadableTime(item.getCreateTime(), "yyyy.MM.dd HH:mm"));
        holder.requestTimeDeadline.setText(Time.getHumanReadableTime(item.getDeadlineTime(), "yyyy.MM.dd HH:mm"));
        holder.requestTimeDeadlineProgress.setProgress((int) Time.getPercent(item.getCreateTime(), item.getDeadlineTime()));
        holder.requestTimeDeadlineProgress.getProgressDrawable().setColorFilter(ColorUtils.interpolateColor(ColorUtils.getThemeColor(activity, R.attr.colorPrimaryDark), Color.RED, holder.requestTimeDeadlineProgress.getProgress()), PorterDuff.Mode.SRC_IN);
        holder.requestDeadlineWarning.setVisibility((holder.requestTimeDeadlineProgress.getProgress() > 80) ? View.VISIBLE : View.GONE);


    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView requestTimeCreation;
        private TextView requestTimeDeadline;
        private TextView requestName;
        private TextView requestText;
        private TextView requestLockedView;
        private VerticalTextView requestTypeString;
        private ProgressBar requestTimeDeadlineProgress;
        private LinearLayout requestAccentLayout;
        private LinearLayout requestCreatorLayout;
        private CircleImageView requestCreatorPhoto;
        private TextView requestCreatorName;
        private ImageView requestDeadlineWarning;

        ViewHolder(final View view) {
            super(view);
            requestTimeCreation = view.findViewById(R.id.requestTimeCreation);
            requestTimeDeadline = view.findViewById(R.id.requestTimeDeadline);
            requestName = view.findViewById(R.id.requestName);
            requestText = view.findViewById(R.id.requestText);
            requestTypeString = view.findViewById(R.id.requestTypeString);
            requestAccentLayout = view.findViewById(R.id.requestAccentLayout);
            requestTimeDeadlineProgress = view.findViewById(R.id.requestTimeDeadlineProgress);
            requestDeadlineWarning = view.findViewById(R.id.requestDeadlineWarning);
            requestCreatorLayout = view.findViewById(R.id.requestCreatorLayout);
            requestCreatorPhoto = view.findViewById(R.id.requestCreatorPhoto);
            requestCreatorName = view.findViewById(R.id.requestCreatorName);
            requestLockedView = view.findViewById(R.id.requestLockedView);
            requestDeadlineWarning = view.findViewById(R.id.requestDeadlineWarning);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openRequest(getAdapterPosition());
                }
            });
        }

    }


    private void openRequest(final int position){
        if(currentUser.getUserAccountType() == User.Type.SERVICE){
            if(requestList.get(position).getRequestStatus() == Request.Status.CLOSED){
                final SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(activity);
                simpleDialog.setTitleText("Заявка закрыта!")
                        .setDialogText("Открыть в любом случае?")
                        .setCancel("Нет", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                simpleDialog.close();
                            }
                        })
                        .setDone("Да", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                simpleDialog.close();
                                launchRequestActivity(position);
                            }
                        }).build();

                simpleDialog.show();
            } else {
                launchRequestActivity(position);
            }
        } else {
            if(requestList.get(position).getRequestStatus() == Request.Status.CLOSED){
                final SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(activity);
                simpleDialog.setTitleText("Заявка закрыта!")
                        .setDialogText("Бы больше не можете просматривать данную заявку, так как она закрыта")
                        .setDone("Ок", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                simpleDialog.close();
                            }
                        }).build();
                simpleDialog.show();
            } else {
                launchRequestActivity(position);
            }
        }
    }


    private void launchRequestActivity(int position){
        Intent intent = new Intent(activity, RequestDetailActivity.class);
        intent.putExtra("requestObject", requestList.get(position));
        activity.startActivity(intent);
    }

}
