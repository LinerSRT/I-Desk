package com.liner.i_desk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.google.firebase.database.DatabaseReference;
import com.liner.bottomdialogs.SimpleDialog;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.UserObject;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;


public class ActivitySplash extends AppCompatActivity {
    private TextView splashAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_layout);
        splashAppName = findViewById(R.id.splashAppName);

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Firebase.isUserLoginned()) {
                            FirebaseValue.getUser(Firebase.getUserUID(), new FirebaseValue.ValueListener() {
                                @Override
                                public void onSuccess(Object object, DatabaseReference databaseReference) {
                                    UserObject userObject = (UserObject) object;
                                    if (userObject.isUserRegisterFinished()) {
                                        startActivity(new Intent(ActivitySplash.this, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
                                        finish();
                                    } else {
                                        Intent intent = new Intent(ActivitySplash.this, ActivityCreateProfile.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("userObject", userObject);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFail(String errorMessage) {
                                    startLoginActivity();
                                }
                            });
                        } else {
                            startLoginActivity();
                        }
                    }
                }, 1200);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                SimpleDialog.Builder builder = new SimpleDialog.Builder(ActivitySplash.this)
                        .setTitleText("Вы не предоставили разрешения")
                        .setDialogText("Приложению необходимы некоторые разрешения для своей работы.\nЕсли вы хотите воспользоваться данным приложением, предоставьте разрешения в настройках вашего устройтсва")
                        .setCancel("Выйти", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        })
                        .setDone("Настройки", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName())));
                                finish();
                            }
                        }).build();
                builder.show();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, ActivityLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, splashAppName, "header_bar");
        startActivity(intent, options.toBundle());
    }
}
