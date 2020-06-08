package com.liner.i_desk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liner.i_desk.R;
import com.liner.views.YSTextView;

import java.util.List;

public class ProfileInformationAdapter extends RecyclerView.Adapter<ProfileInformationAdapter.ViewHolder> {
    private List<InformationHolder> holderList;

    public ProfileInformationAdapter(List<InformationHolder> holderList) {
        this.holderList = holderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_information_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InformationHolder item = holderList.get(position);
        holder.profileInformationTitle.setText(item.getName());
        holder.profileInformationValue.setText(String.valueOf(item.getValue()));
    }

    @Override
    public int getItemCount() {
        return holderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private YSTextView profileInformationTitle;
        private YSTextView profileInformationValue;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileInformationTitle = itemView.findViewById(R.id.profileInformationTitle);
            profileInformationValue = itemView.findViewById(R.id.profileInformationValue);
        }
    }

    public static class InformationHolder{
        private String name;
        private Object value;

        public InformationHolder(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
