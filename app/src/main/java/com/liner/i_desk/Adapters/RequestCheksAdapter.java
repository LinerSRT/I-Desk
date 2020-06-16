package com.liner.i_desk.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.abdularis.buttonprogress.DownloadButtonProgress;
import com.google.firebase.database.DatabaseReference;
import com.liner.i_desk.Firebase.CheckObject;
import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.FileObject;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.Storage.FirebaseFileManager;
import com.liner.i_desk.Firebase.Storage.TaskListener;
import com.liner.i_desk.R;
import com.liner.utils.FileUtils;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.YSMarqueTextView;
import com.liner.views.YSTextView;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RequestCheksAdapter extends RecyclerView.Adapter<RequestCheksAdapter.FilesViewHolder> {
    private Activity activity;
    private RequestObject requestObject;
    private List<CheckObject> checkList;
    private BaseDialog deleteWarnDialog;

    public RequestCheksAdapter(Activity activity, final RequestObject requestObject) {
        this.activity = activity;
        this.requestObject = requestObject;
        if(requestObject.getRequestChecks() == null)
            checkList = new ArrayList<>();
        checkList = requestObject.getRequestChecks();
    }

    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilesViewHolder(LayoutInflater.from(activity).inflate(R.layout.cheks_view_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FilesViewHolder holder, int position) {
        holder.requestCheck.setEnabled(!requestObject.getRequestCreatorID().equals(Firebase.getUserUID()));
        holder.requestCheck.setChecked(checkList.get(position).getCheckStatus());
        holder.requestCheck.setText(checkList.get(position).getCheckName());
    }

    @Override
    public int getItemCount() {
        return checkList == null? 0:checkList.size();
    }

    class FilesViewHolder extends RecyclerView.ViewHolder {
        private CheckBox requestCheck;

        public FilesViewHolder(@NonNull View view) {
            super(view);
            requestCheck = view.findViewById(R.id.requestCheck);
            requestCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    checkList.get(getAdapterPosition()).setCheckStatus(b);
                    requestObject.setRequestChecks(checkList);
                    FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
                }
            });
        }
    }




}
