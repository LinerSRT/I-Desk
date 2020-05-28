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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.UI.MainActivity;
import com.liner.i_desk.UI.SplashActivity;
import com.liner.i_desk.Utils.TextUtils;
import com.liner.i_desk.Utils.Views.FirebaseFragment;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainFragment extends FirebaseFragment {
    private CircleImageView userPhoto;
    private TextView userName;
    private TextView userType;
    private CardView showAccountActions;
    private Badge accountBadge;
    private ExpandableLayout accountActionsLayout;
    private Handler handler;


    private TextView accountActionsAddNewRequest;
    private TextView accountActionsSignOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        userPhoto = view.findViewById(R.id.userPhoto);
        userName = view.findViewById(R.id.userName);
        userType = view.findViewById(R.id.userType);
        showAccountActions = view.findViewById(R.id.showAccountActions);
        accountActionsLayout = view.findViewById(R.id.accountActionsLayout);
        accountActionsAddNewRequest = view.findViewById(R.id.accountActionsAddNewRequest);
        accountActionsSignOut = view.findViewById(R.id.accountActionsSignOut);
        handler = new Handler();

        loadUserData();
        accountActionsSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountActionsLayout.collapse(true);
                final SimpleBottomSheetDialog confirmDialog = new SimpleBottomSheetDialog(getActivity());
                confirmDialog.setDialogTitle("Выход");
                confirmDialog.setDialogText("Вы действительно хотите выйти из своего аккаунта?");
                confirmDialog.setDialogDoneBtnText("Выйти");
                confirmDialog.setDialogCancelBtnText("Отмена");
                confirmDialog.setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accountActionsLayout.expand(true);
                        confirmDialog.dismiss(true);
                    }
                });
                confirmDialog.setDoneClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firebaseActivity.firebaseAuth.signOut();
                        getContext().startActivity(new Intent(getContext(), SplashActivity.class));
                        getActivity().finish();
                    }
                });
                confirmDialog.create();
            }
        });

        accountActionsAddNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        accountBadge = new QBadgeView(getContext()).bindTarget(userPhoto)
                .setBadgePadding(0f, true)
                .setBadgeGravity(Gravity.END | Gravity.TOP)
                .setShowShadow(true)
                .setBadgeTextSize(12, true)
                .setBadgeNumber(5);

        accountActionsLayout.setInterpolator(new AccelerateDecelerateInterpolator());
        showAccountActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if(accountActionsLayout.isExpanded()){
                            accountActionsLayout.collapse(true);
                        } else {
                            accountActionsLayout.expand(true);
                        }
                    }
                });
            }
        });







        return view;
    }

    private void runOnUIThread(Runnable runnable){
        handler.post(runnable);
    }

    @Override
    public void onFirebaseChanged() {
        loadUserData();
    }

    private void loadUserData(){
        Picasso.get().load(firebaseActivity.user.getUserPhotoURL()).into(userPhoto);
        userName.setText(firebaseActivity.user.getUserName());
        userType.setText((firebaseActivity.user.isClientAccount())? "Заявитель":"Исполнитель");
    }
}
