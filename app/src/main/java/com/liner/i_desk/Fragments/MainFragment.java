package com.liner.i_desk.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter_LifecycleAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.liner.i_desk.Adapters.RequestAdapter;
import com.liner.i_desk.Constants;
import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.i_desk.R;
import com.liner.views.YSTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainFragment extends Fragment implements PullRefreshLayout.OnRefreshListener, RequestAdapter.AdapterCallback {
    private RequestAdapter requestAdapter;
    private RecyclerView requestRecycler;
    private PullRefreshLayout requestRefreshLayout;
    private YSTextView noRequests;
    private DatabaseListener databaseListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmnet_main, container, false);
        requestRecycler = view.findViewById(R.id.requestRecycler);
        requestRefreshLayout = view.findViewById(R.id.requestRefreshLayout);
        noRequests = view.findViewById(R.id.noRequests);
        requestRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        databaseListener = new DatabaseListener() {
            @Override
            public void onUserAdded(UserObject userObject, int position) {
                super.onUserAdded(userObject, position);
                if(userObject.getUserID().equals(Firebase.getUserUID())){
                    requestAdapter = new RequestAdapter(userObject, getActivity());
                    requestAdapter.setAdapterCallback(MainFragment.this);
                    requestRecycler.setAdapter(requestAdapter);
                    requestAdapter.onStart();
                }
            }

            @Override
            public void onUserChanged(final UserObject userObject, int position) {
                super.onUserChanged(userObject, position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(userObject.getUserID().equals(Firebase.getUserUID())){
                            if(requestAdapter != null)
                                requestAdapter.onDestroy();
                            requestAdapter = new RequestAdapter(userObject, getActivity());
                            requestAdapter.setAdapterCallback(MainFragment.this);
                            requestRecycler.setAdapter(requestAdapter);
                            requestAdapter.onStart();
                        }
                    }
                }, TimeUnit.SECONDS.toMillis(1));
            }
        };
        databaseListener.startListening();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        databaseListener.stopListening();
        requestAdapter.onDestroy();
    }

    @Override
    public void onDataChanged(int itemsSize) {
        noRequests.setVisibility((itemsSize <= 0) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onLoadError() {
        noRequests.setVisibility(View.VISIBLE);
    }
}
