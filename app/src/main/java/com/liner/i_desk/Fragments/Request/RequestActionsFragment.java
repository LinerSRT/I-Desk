package com.liner.i_desk.Fragments.Request;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.R;

public class RequestActionsFragment extends Fragment {
    public DatabaseListener databaseListener;
    private RequestObject requestObject;
    public RequestActionsFragment(RequestObject requestObject) {
        this.requestObject = requestObject;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_settings, container, false);
        databaseListener = new DatabaseListener() {

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
}
