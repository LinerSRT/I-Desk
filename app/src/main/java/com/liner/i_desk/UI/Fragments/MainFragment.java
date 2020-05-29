package com.liner.i_desk.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.Adapters.RequestAdapter;
import com.liner.i_desk.R;
import com.liner.i_desk.UI.SplashActivity;
import com.liner.i_desk.Utils.TextUtils;
import com.liner.i_desk.Utils.TimeUtils;
import com.liner.i_desk.Utils.Views.FirebaseFragment;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainFragment extends FirebaseFragment{
    private CircleImageView userPhoto;
    private TextView userName;
    private TextView userType;
    private Handler handler;


    private TextView accountActionsAddNewRequest;

    private RecyclerView requestRecyclerView;
    private RequestAdapter requestAdapter;
    private PullRefreshLayout requestRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        userPhoto = view.findViewById(R.id.userPhoto);
        userName = view.findViewById(R.id.userName);
        userType = view.findViewById(R.id.userType);
        accountActionsAddNewRequest = view.findViewById(R.id.accountActionsAddNewRequest);
        requestRecyclerView = view.findViewById(R.id.requestRecyclerView);
        requestRefreshLayout = view.findViewById(R.id.requestRefreshLayout);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(firebaseActivity.user.getRequestList() != null) {
            requestAdapter = new RequestAdapter(getActivity(), firebaseActivity.user.getRequestList());
            requestRecyclerView.setAdapter(requestAdapter);
        }

        handler = new Handler();

        loadUserData();


        accountActionsAddNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request request = new Request();
                request.setRequestText(TextUtils.generateRandomString(100));
                request.setRequestName(TextUtils.generateRandomString(35));
                request.setRequestID(TextUtils.generateRandomString(64));
                request.setRequestUserID(firebaseActivity.firebaseUser.getUid());
                request.setRequestTime(TimeUtils.getCurrentTime(TimeUtils.Type.SERVER));
                request.setRequestDeadline(TimeUtils.getTime(TimeUtils.Type.SERVER, 1, TimeUnit.HOURS));
                switch (TextUtils.randInt(0, 2)){
                    case 0:
                        request.setRequestType(Request.Type.Consultation);
                        break;
                    case 1:
                        request.setRequestType(Request.Type.Service);
                        break;
                    case 2:
                        request.setRequestType(Request.Type.Incident);
                        break;
                }
                if(firebaseActivity.user.getRequestList() != null) {
                    requestAdapter = new RequestAdapter(getActivity(), firebaseActivity.user.getRequestList());
                } else {
                    requestAdapter = new RequestAdapter(getActivity(), new ArrayList<Request>());
                    firebaseActivity.user.setRequestList(new ArrayList<Request>());
                }
                requestRecyclerView.setAdapter(requestAdapter);
                firebaseActivity.user.getRequestList().add(request);
                FirebaseHelper.setUserValue(firebaseActivity.firebaseUser.getUid(), "requestList", firebaseActivity.user.getRequestList(), new FirebaseHelper.IFirebaseHelperListener() {
                    @Override
                    public void onSuccess(Object result) {

                    }

                    @Override
                    public void onFail(String reason) {

                    }
                });
            }
        });


        //accountBadge = new QBadgeView(getContext()).bindTarget(userPhoto)
        //        .setBadgePadding(0f, true)
        //        .setBadgeGravity(Gravity.END | Gravity.TOP)
        //        .setShowShadow(true)
        //        .setBadgeTextSize(12, true)
        //        .setBadgeNumber(3);


        requestRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FirebaseHelper.getUserModel(new FirebaseHelper.IFirebaseHelperListener() {
                    @Override
                    public void onSuccess(Object result) {
                        firebaseActivity.user = (User) result;
                        firebaseActivity.onFirebaseChanged(firebaseActivity.user);
                        broadcastManager.sendLocal(FIREBASE_ACTION);
                    }

                    @Override
                    public void onFail(String reason) {

                    }
                });
            }
        });
        return view;
    }

    private void runOnUIThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public void onFirebaseChanged() {
        loadUserData();
    }

    private void loadUserData() {
        Picasso.get().load(firebaseActivity.user.getUserPhotoURL()).into(userPhoto);
        userName.setText(firebaseActivity.user.getUserName());
        userType.setText((firebaseActivity.user.isClientAccount()) ? "Заявитель" : "Исполнитель");
        if(firebaseActivity.user.getRequestList() != null) {
            requestAdapter = new RequestAdapter(getActivity(), firebaseActivity.user.getRequestList());
        } else {
            requestAdapter = new RequestAdapter(getActivity(), new ArrayList<Request>());
        }
        requestRecyclerView.setAdapter(requestAdapter);
        requestRefreshLayout.setRefreshing(false);
    }

}
