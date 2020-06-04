package com.liner.i_desk.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.Adapters.RequestAdapter;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.Views.FirebaseActivity;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.liner.i_desk.IDesk.getContext;

public class MainActivity extends FirebaseActivity {
    private CircleImageView userPhoto;
    private TextView userName;
    private TextView userType;
    private CardView accountActions;
    private TextView accountActionsAddNewRequest;
    private RecyclerView requestRecyclerView;
    private RequestAdapter requestAdapter;
    private PullRefreshLayout requestRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userPhoto = findViewById(R.id.userPhoto);
        userName = findViewById(R.id.userName);
        userType = findViewById(R.id.userType);
        accountActionsAddNewRequest = findViewById(R.id.accountActionsAddNewRequest);
        requestRecyclerView = findViewById(R.id.requestRecyclerView);
        requestRefreshLayout = findViewById(R.id.requestRefreshLayout);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        accountActions = findViewById(R.id.accountActions);
        accountActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        accountActionsAddNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SimpleBottomSheetDialog.Builder createNewRequestDialog = new SimpleBottomSheetDialog.Builder(MainActivity.this);
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

        requestRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUserOnlineTimeStamp();
            }
        });
        requestRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onSomethingChanged() {
        Log.d("TAGTAG", getCurrentUser().toString());
        updateValues();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {

    }


    private void updateValues(){
        requestRefreshLayout.setRefreshing(true);
        requestAdapter = new RequestAdapter(MainActivity.this, getRequestList(), getCurrentUser());
        requestRecyclerView.setAdapter(requestAdapter);
        Picasso.get().load(getCurrentUser().getUserPhotoURL()).placeholder(R.drawable.temp_user_photo).into(userPhoto);
        userName.setText(getCurrentUser().getUserName());
        switch (getCurrentUser().getUserAccountType()){
            case SERVICE:
                userType.setText("Исполнитель");
                break;
            case CLIENT:
                userType.setText("Заявитель");
                break;
        }
        requestRefreshLayout.setRefreshing(false);
    }
}
