package com.liner.i_desk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liner.i_desk.Firebase.FireActivity;
import com.liner.i_desk.Fragments.CreateRequestFragment;
import com.liner.i_desk.Fragments.MainFragment;
import com.liner.i_desk.Fragments.UserProfileFragment;
import com.liner.utils.ViewUtils;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.irbottomnavigation.SpaceItem;
import com.liner.views.irbottomnavigation.SpaceNavigationView;
import com.liner.views.irbottomnavigation.SpaceOnClickListener;

public class ActivityMain extends FireActivity {
    private SpaceNavigationView spaceNavigationView;
    private FrameLayout fragmentContainer;

    private MainFragment mainFragment;
    private UserProfileFragment userProfileFragment;
    private CreateRequestFragment createRequestFragment;

    private BaseDialog exitDialog;

    @Override
    protected void onStart() {
        super.onStart();
        ViewUtils.setStatusBarColor(this, getResources().getColor(R.color.window_background));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        mainFragment = new MainFragment();
        userProfileFragment = new UserProfileFragment();
        createRequestFragment = new CreateRequestFragment();
        createRequestFragment.setCloseCallback(new CreateRequestFragment.CloseCallback() {
            @Override
            public void onClose() {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, mainFragment).commit();
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, mainFragment).commit();
        spaceNavigationView = findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Заявки", R.drawable.requests_icon, SpaceItem.Align.LEFT));
        spaceNavigationView.addSpaceItem(new SpaceItem(R.drawable.user_icon, SpaceItem.Align.RIGHT));
        spaceNavigationView.addSpaceItem(new SpaceItem(R.drawable.message_icon, SpaceItem.Align.RIGHT));
        spaceNavigationView.setCentreButtonIcon(R.drawable.add_icon_white);
        spaceNavigationView.setCentreButtonColor(getResources().getColor(R.color.primary));
        spaceNavigationView.shouldShowFullBadgeText(true);
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false);
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        replaceFragment(createRequestFragment);
                    }
                }, 200);
            }
            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if(itemIndex == 0)
                    replaceFragment(mainFragment);
                else if (itemIndex == 1)
                    replaceFragment(userProfileFragment);
            }
            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                if(itemIndex == 0)
                    replaceFragment(mainFragment);
                else if (itemIndex == 1)
                    replaceFragment(userProfileFragment);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        exitDialog = BaseDialogBuilder.buildFast(this,
                "Выход",
                "Вы действительно хотите выйти из приложения?",
                "Выйти",
                "Остаться",
                BaseDialogBuilder.Type.WARNING,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exitDialog.closeDialog();
                        ActivityMain.super.onBackPressed();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exitDialog.closeDialog();
                    }
                }
        );
        exitDialog.showDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        createRequestFragment.requestFileStep.submitPicker(requestCode, resultCode, data);
    }

    private void replaceFragment(final Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }
}
