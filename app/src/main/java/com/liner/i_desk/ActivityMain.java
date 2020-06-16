package com.liner.i_desk;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.liner.i_desk.Firebase.FireActivity;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.MessagingService;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.i_desk.Fragments.CreateRequestFragment;
import com.liner.i_desk.Fragments.MainFragment;
import com.liner.i_desk.Fragments.UserProfileFragment;
import com.liner.i_desk.Utils.Test;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.irbottomnavigation.SpaceItem;
import com.liner.views.irbottomnavigation.SpaceNavigationView;
import com.liner.views.irbottomnavigation.SpaceOnClickListener;
import com.liner.views.irbottomnavigation.SpaceOnLongClickListener;

public class ActivityMain extends FireActivity {
    private Intent messagingService;


    private SpaceNavigationView spaceNavigationView;
    private FrameLayout fragmentContainer;

    private MainFragment mainFragment;
    private UserProfileFragment userProfileFragment;
    private CreateRequestFragment createRequestFragment;

    private BaseDialog exitDialog;
    private BaseDialog serviceAccountWarn;

    @Override
    protected void onStart() {
        super.onStart();
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName())) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivity(intent);
            }
        }
        if (MessagingService.serviceIntent == null) {
            messagingService = new Intent(this, MessagingService.class);
            startService(messagingService);
        } else {
            messagingService = MessagingService.serviceIntent;
        }
        serviceAccountWarn = BaseDialogBuilder.buildFast(this,
                "Ошибка!",
                "Сервисные аккаунты не могут создавать заявки!",
                null,
                "Понятно",
                BaseDialogBuilder.Type.ERROR,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        serviceAccountWarn.closeDialog();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        mainFragment = new MainFragment();
        userProfileFragment = new UserProfileFragment(Firebase.getUserUID());
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
        //spaceNavigationView.addSpaceItem(new SpaceItem(R.drawable.message_icon, SpaceItem.Align.RIGHT));
        spaceNavigationView.setCentreButtonIcon(R.drawable.add_icon_white);
        spaceNavigationView.setCentreButtonColor(getResources().getColor(R.color.primary));
        spaceNavigationView.shouldShowFullBadgeText(true);
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false);
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                FirebaseValue.getUser(Firebase.getUserUID(), new FirebaseValue.ValueListener() {
                    @Override
                    public void onSuccess(Object object, DatabaseReference databaseReference) {
                        UserObject userObject = (UserObject) object;
                        if (userObject.getUserType() == UserObject.UserType.SERVICE) {
                            serviceAccountWarn.showDialog();
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    replaceFragment(createRequestFragment);
                                }
                            }, 50);
                        }
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                replaceFragment(createRequestFragment);
                            }
                        }, 50);
                    }
                });
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemIndex == 0)
                    replaceFragment(mainFragment);
                else if (itemIndex == 1)
                    replaceFragment(userProfileFragment);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                if (itemIndex == 0)
                    replaceFragment(mainFragment);
                else if (itemIndex == 1)
                    replaceFragment(userProfileFragment);
            }
        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
                Test.createTestRequest(5);
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {

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
        try {
            userProfileFragment.onActivityResult(requestCode, resultCode, data);
            createRequestFragment.onActivityResult(requestCode, resultCode, data);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void replaceFragment(final Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }
}
