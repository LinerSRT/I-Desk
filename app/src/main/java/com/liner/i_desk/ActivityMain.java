package com.liner.i_desk;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.irfaan008.irbottomnavigation.SpaceItem;
import com.irfaan008.irbottomnavigation.SpaceNavigationView;
import com.irfaan008.irbottomnavigation.SpaceOnClickListener;
import com.irfaan008.irbottomnavigation.SpaceOnLongClickListener;
import com.liner.i_desk.Firebase.FireActivity;

public class ActivityMain extends FireActivity {


    private SpaceNavigationView spaceNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
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
                Log.d("onCentreButtonClick ", "onCentreButtonClick");
                spaceNavigationView.shouldShowFullBadgeText(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Log.d("onItemReselected ", "" + itemIndex + " " + itemName);
            }
        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
//                Toast.makeText(MainActivity.this, "onCentreButtonLongClick", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
                Toast.makeText(ActivityMain.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
