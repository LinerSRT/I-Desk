package com.liner.i_desk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liner.i_desk.Firebase.FireActivity;
import com.liner.i_desk.Fragments.CreateRequestFragment;
import com.liner.i_desk.Fragments.MainFragment;
import com.liner.i_desk.Fragments.UserProfileFragment;
import com.liner.views.irbottomnavigation.SpaceItem;
import com.liner.views.irbottomnavigation.SpaceNavigationView;
import com.liner.views.irbottomnavigation.SpaceOnClickListener;

public class ActivityMain extends FireActivity {
    private SpaceNavigationView spaceNavigationView;
    private FrameLayout fragmentContainer;

    private MainFragment mainFragment;
    private UserProfileFragment userProfileFragment;
    private CreateRequestFragment createRequestFragment;

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
        spaceNavigationView.addSpaceItem(new SpaceItem("Заявки", R.drawable.requests_icon));
        spaceNavigationView.addSpaceItem(new SpaceItem("Профиль", R.drawable.user_icon));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        createRequestFragment.requestFileStep.submitPicker(requestCode, resultCode, data);
    }

    private void replaceFragment(final Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }
}
