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
import com.liner.i_desk.UI.CreateRequestActivity;
import com.liner.i_desk.UI.MainActivity;
import com.liner.i_desk.UI.SplashActivity;
import com.liner.i_desk.Utils.Views.FirebaseFragment;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainFragment extends FirebaseFragment{
    private CircleImageView userPhoto;
    private TextView userName;
    private TextView userType;
    private Handler handler;
    private CardView accountActions;
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

        accountActions = view.findViewById(R.id.accountActions);
        accountActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.extendedViewPager.setCurrentItem(1);
            }
        });
        handler = new Handler();

        loadUserData();


        accountActionsAddNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SimpleBottomSheetDialog.Builder createNewRequestDialog = new SimpleBottomSheetDialog.Builder(getActivity());
                createNewRequestDialog.setTitleText("Создать новую заявку?")
                        .setDialogText("Переход к окну создания новой заявки")
                        .setCancel("Отмена", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createNewRequestDialog.close();
                            }
                        })
                        .setDone("Создать", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createNewRequestDialog.close();
                                getContext().startActivity(new Intent(getContext(), CreateRequestActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        }).build();
                createNewRequestDialog.show();

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
                firebaseActivity.updateUserList();
            }
        });



        return view;
    }

    @Override
    public void onFirebaseChanged() {
        loadUserData();
    }

    @Override
    public void onUserObtained() {
        loadUserData();
    }


    private void loadUserData() {
        try {
            if(firebaseActivity.user.getRequestList() != null) {
                requestAdapter = new RequestAdapter(getActivity(), firebaseActivity.user.getRequestList(), firebaseActivity.user);
                requestRecyclerView.setAdapter(requestAdapter);
            }
            Picasso.get().load(firebaseActivity.user.getUserPhotoURL()).into(userPhoto);
            userName.setText(firebaseActivity.user.getUserName());

            switch (firebaseActivity.user.getUserAccountType()){
                case SERVICE:
                    userType.setText("Исполнитель");
                    break;
                case CLIENT:
                    userType.setText("Заявитель");
                    break;
            }
            FirebaseHelper.getRequests(firebaseActivity.user, new FirebaseHelper.IFirebaseHelperListener() {
                @Override
                public void onSuccess(Object result) {
                    requestAdapter = new RequestAdapter(getActivity(), (List<Request>) result, firebaseActivity.user);
                    requestRecyclerView.setAdapter(requestAdapter);
                    requestRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFail(String reason) {
                    requestAdapter = new RequestAdapter(getActivity(), new ArrayList<Request>(), firebaseActivity.user);
                    requestRecyclerView.setAdapter(requestAdapter);
                    requestRefreshLayout.setRefreshing(false);
                }
            });

        } catch (NullPointerException e){
            e.printStackTrace();
        }

    }

}
