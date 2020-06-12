package com.liner.i_desk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.liner.i_desk.Firebase.FireActivity;
import com.liner.views.GradientImageView;
import com.liner.views.irbottomnavigation.SpaceItem;
import com.liner.views.irbottomnavigation.SpaceNavigationView;
import com.liner.views.irbottomnavigation.SpaceOnClickListener;

import spencerstudios.com.bungeelib.Bungee;

public class ActivityUserProfile extends FireActivity {
    private GradientImageView userProfileImage;

    private SpaceNavigationView profileNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_layout);
        profileNavigation = findViewById(R.id.profileNavigation);
        profileNavigation.initWithSaveInstanceState(savedInstanceState);
        profileNavigation.addSpaceItem(new SpaceItem("Заявки", R.drawable.requests_icon));
        profileNavigation.addSpaceItem(new SpaceItem("Профиль", R.drawable.user_icon));
        profileNavigation.changeCurrentItem(1);
        profileNavigation.setCentreButtonIcon(R.drawable.add_icon_white);
        profileNavigation.setCentreButtonColor(getResources().getColor(R.color.primary));
        profileNavigation.shouldShowFullBadgeText(true);
        profileNavigation.setCentreButtonIconColorFilterEnabled(false);

        profileNavigation.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Log.d("onCentreButtonClick ", "onCentreButtonClick");
                profileNavigation.showBadgeAtIndex(1, 2, getResources().getColor(R.color.red));
                profileNavigation.shouldShowFullBadgeText(true);
                startActivity(new Intent(ActivityUserProfile.this, ActivityCreateRequest.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex){
                    case 0:
                        startActivity(new Intent(ActivityUserProfile.this, ActivityMain.class));
                        break;
                }


                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Log.d("onItemReselected ", "" + itemIndex + " " + itemName);
            }
        });


    }
}
