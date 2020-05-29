package com.liner.i_desk.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;
import com.liner.i_desk.Utils.TimeUtils;
import com.liner.i_desk.Utils.Views.VerticalTextView;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private Context context;
    private List<Request> requestList;

    public RequestAdapter(Context activity, List<Request> requestList) {
        this.context = activity;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request item = requestList.get(position);
        switch (item.getRequestType()) {
            case SERVICE:
                holder.requestTypeString.setText("Сервис");
                holder.requestAccentLayout.getBackground().setColorFilter(context.getResources().getColor(R.color.request_service), PorterDuff.Mode.SRC_IN);
                break;
            case INCIDENT:
                holder.requestTypeString.setText("Инцидент");
                holder.requestAccentLayout.getBackground().setColorFilter(context.getResources().getColor(R.color.request_incident), PorterDuff.Mode.SRC_IN);
                break;
            case CONSULTATION:
                holder.requestTypeString.setText("Консультация");
                holder.requestAccentLayout.getBackground().setColorFilter(context.getResources().getColor(R.color.request_consultation), PorterDuff.Mode.SRC_IN);
                break;
        }


        holder.requestName.setText(item.getRequestTitle());
        holder.requestText.setText(item.getRequestShortDescription());
        holder.requestTimeCreation.setText(TimeUtils.convertDate(item.getRequestCreationTime(), true));
        holder.requestTimeDeadline.setText(TimeUtils.convertDate(item.getRequestDeadlineTime(), true));
        holder.requestTimeDeadlineProgress.setProgress((int) TimeUtils.getDeadlinePercent(item.getRequestCreationTime(), item.getRequestDeadlineTime()));
        holder.requestTimeDeadlineProgress.getProgressDrawable().setColorFilter(ColorUtils.interpolateColor(ColorUtils.getThemeColor(context, R.attr.colorPrimaryDark), Color.RED, holder.requestTimeDeadlineProgress.getProgress()), PorterDuff.Mode.SRC_IN);
        holder.requestDeadlineWarning.setVisibility((holder.requestTimeDeadlineProgress.getProgress() > 80)?View.VISIBLE:View.GONE);
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
        private VerticalTextView requestTypeString;
        private ProgressBar requestTimeDeadlineProgress;
        private LinearLayout requestAccentLayout;
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
        }

    }



}
