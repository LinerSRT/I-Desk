package com.liner.i_desk.UI;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.developer.gbuttons.GoogleSignInButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;
import com.liner.i_desk.Utils.ImageUtils;
import com.liner.i_desk.Utils.TextUtils;
import com.liner.i_desk.Utils.Views.EditRegexTextView;
import com.liner.i_desk.Utils.Views.IndeterminateBottomSheetDialog;
import com.liner.i_desk.Utils.Views.SelectionBottomSheetDialog;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashActivity extends AppCompatActivity {
    private Handler handler;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDatabase;
    private CardView loginCardView;
    private CardView registerCardView;
    private LinearLayout splashAdditionalAccountFunctions;
    private TextView splashDescriptionText;
    private EditRegexTextView splashLoginEmailField;
    private EditRegexTextView splashLoginPasswordField;
    private ImageButton splashLoginShowPassword;
    private Button splashLoginButton;
    private TextView splashCreateNewAccountButton;
    private EditRegexTextView splashRegisterEmailField;
    private EditRegexTextView splashRegisterPasswordField;
    private EditRegexTextView splashRegisterReplyPasswordField;
    private EditRegexTextView splashRegisterNickNameField;
    private CircleImageView splashRegisterProfilePhotoView;
    private Button splashRegisterButton;
    private boolean loginEmailCorrect = false;
    private boolean loginPasswordCorrect = false;
    private boolean registerEmailCorrect = false;
    private boolean registerPasswordCorrect = false;
    private boolean registerPasswordSame = false;
    private boolean registerNickNameCorrect = false;
    private int registerAccountType = -1;
    private IndeterminateBottomSheetDialog signWithGoogleDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabase = firebaseDatabase.getReference("Users");
        firebaseStorage = FirebaseStorage.getInstance();
        TextView splashForgotPassword;
        GoogleSignInButton splashSighWithGoogle;
        RelativeLayout splashRegisterAddPhotoView;
        RadioButton splashRegisterAccountTypeClient;
        RadioButton splashRegisterAccountTypeExecutor;
        {
            loginCardView = findViewById(R.id.splashLoginCardView);
            registerCardView = findViewById(R.id.splashRegisterCardView);
            splashAdditionalAccountFunctions = findViewById(R.id.splashAdditionalAccountFunctions);
            splashDescriptionText = findViewById(R.id.splashDescriptionText);
            splashLoginEmailField = findViewById(R.id.splashLoginEmailField);
            splashLoginPasswordField = findViewById(R.id.splashLoginPasswordField);
            splashLoginShowPassword = findViewById(R.id.splashLoginShowPassword);
            splashLoginButton = findViewById(R.id.splashLoginButton);
            splashForgotPassword = findViewById(R.id.splashForgotPassword);
            splashCreateNewAccountButton = findViewById(R.id.splashCreateNewAccountButton);
            splashSighWithGoogle = findViewById(R.id.splashSighWithGoogle);
            //----------------------------
            splashRegisterEmailField = findViewById(R.id.splashRegisterEmailField);
            splashRegisterPasswordField = findViewById(R.id.splashRegisterPasswordField);
            splashRegisterReplyPasswordField = findViewById(R.id.splashRegisterReplyPasswordField);
            splashRegisterNickNameField = findViewById(R.id.splashRegisterNickNameField);
            splashRegisterAccountTypeClient = findViewById(R.id.splashRegisterAccountTypeClient);
            splashRegisterAccountTypeExecutor = findViewById(R.id.splashRegisterAccountTypeExecutor);
            splashRegisterAddPhotoView = findViewById(R.id.splashRegisterAddPhotoView);
            splashRegisterProfilePhotoView = findViewById(R.id.splashRegisterProfilePhotoView);
            splashRegisterButton = findViewById(R.id.splashRegisterButton);
        }

        handler = new Handler();


        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                if (checkCurrentUser()) {
                    FirebaseHelper.getUserModel(new FirebaseHelper.IFirebaseHelperListener() {
                        @Override
                        public void onSuccess(Object result) {
                            User user = (User) result;
                            if (user != null && user.getUserEmail() != null) {
                                FirebaseHelper.checkUsersDatabaseExistValue(user.getUserEmail(), "userEmail", new FirebaseHelper.IFirebaseDatabaseValueListener() {
                                    @Override
                                    public void onValueExists() {
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginCardView.animate()
                                                        .alpha(1f)
                                                        .setDuration(300)
                                                        .setListener(new AnimatorListenerAdapter() {
                                                            @Override
                                                            public void onAnimationEnd(Animator animation) {
                                                                super.onAnimationEnd(animation);
                                                                splashDescriptionText.setVisibility(View.VISIBLE);
                                                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                                                finish();
                                                            }
                                                        });
                                            }
                                        }, 600);
                                    }

                                    @Override
                                    public void onValueNotFound() {
                                        firebaseAuth.signOut();
                                        showLoginCard();
                                    }
                                });
                            } else {
                                firebaseAuth.signOut();
                                showLoginCard();
                            }
                        }

                        @Override
                        public void onFail(String reason) {
                            showLoginCard();
                        }
                    });
                } else {
                    showLoginCard();
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                finish();
            }
        });
        splashLoginEmailField.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                loginEmailCorrect = true;
                splashLoginButton.setEnabled(loginPasswordCorrect);
            }

            @Override
            public void onNotValid() {
                loginEmailCorrect = false;
                splashLoginButton.setEnabled(false);
            }
        });
        splashLoginPasswordField.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                loginPasswordCorrect = true;
                splashLoginButton.setEnabled(loginEmailCorrect);
            }

            @Override
            public void onNotValid() {
                loginPasswordCorrect = false;
                splashLoginButton.setEnabled(false);
            }
        });
        splashLoginShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (splashLoginPasswordField.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    splashLoginPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    splashLoginShowPassword.setImageResource(R.drawable.hide_password_icon);
                    splashLoginShowPassword.setColorFilter(ColorUtils.getThemeColor(SplashActivity.this, R.attr.disabledColor));
                } else {
                    splashLoginShowPassword.setImageResource(R.drawable.show_password_icon);
                    splashLoginShowPassword.setColorFilter(ColorUtils.getThemeColor(SplashActivity.this, R.attr.colorPrimaryDark));
                    splashLoginPasswordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                splashLoginPasswordField.setSelection(splashLoginPasswordField.length());
            }
        });
        splashLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final IndeterminateBottomSheetDialog loginDialog = new IndeterminateBottomSheetDialog(SplashActivity.this);
                loginDialog.setDialogTitle("Подождите");
                loginDialog.setDialogText("Выполняется вход в аккаунт");
                loginDialog.create();
                firebaseAuth.signOut();
                firebaseAuth.signInWithEmailAndPassword(splashLoginEmailField.getText().toString().trim(), splashLoginPasswordField.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (Objects.requireNonNull(authResult.getUser()).isEmailVerified()) {
                            loginDialog.dismiss();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        } else {
                            loginDialog.dismiss();
                            final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                            simpleBottomSheetDialog.setDialogTitle("Внимание!");
                            simpleBottomSheetDialog.setDialogText("Вы не подтвердили свой E-Mail. Инструкция для подтверждения была выслана вам на почту");
                            simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                            simpleBottomSheetDialog.create();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginDialog.dismiss();
                        if (Objects.requireNonNull(e.getLocalizedMessage()).contains("invalid") || Objects.requireNonNull(e.getLocalizedMessage().contains("no user"))) {
                            final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                            simpleBottomSheetDialog.setDialogTitle("Ошибка!");
                            simpleBottomSheetDialog.setDialogText("Неверный пароль или E-Mail");
                            simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                            simpleBottomSheetDialog.create();
                        }
                    }
                });
            }
        });

        //--------------------------------------
        splashCreateNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginCardView.getVisibility() == View.VISIBLE) {
                    loginCardView.animate()
                            .translationX(-loginCardView.getWidth())
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    loginCardView.setVisibility(View.GONE);
                                }
                            });
                    registerCardView.animate()
                            .setDuration(300)
                            .translationX(0)
                            .alpha(1f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    registerCardView.setVisibility(View.VISIBLE);
                                    splashCreateNewAccountButton.setText("Войти в существующий аккаунт");
                                }
                            });

                } else {
                    loginCardView.animate()
                            .translationX(0)
                            .alpha(1f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    loginCardView.setVisibility(View.VISIBLE);
                                }
                            });
                    registerCardView.animate()
                            .translationX(registerCardView.getWidth())
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    registerCardView.setVisibility(View.GONE);
                                    splashCreateNewAccountButton.setText("Создать новый");
                                }
                            });
                }
            }
        });


        splashRegisterEmailField.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                registerEmailCorrect = true;
                registerPasswordSame = splashRegisterPasswordField.getText().toString().trim().equals(splashRegisterReplyPasswordField.getText().toString().trim());
                splashRegisterButton.setEnabled((registerPasswordCorrect && registerPasswordSame && registerNickNameCorrect && registerAccountType != -1));
            }

            @Override
            public void onNotValid() {
                registerEmailCorrect = false;
                splashRegisterButton.setEnabled(false);
            }
        });
        splashRegisterPasswordField.setControlIconAutomatically(false);
        splashRegisterPasswordField.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                registerPasswordCorrect = true;
                registerPasswordSame = splashRegisterPasswordField.getText().toString().trim().equals(splashRegisterReplyPasswordField.getText().toString().trim());
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordSame && registerNickNameCorrect && registerAccountType != -1));
            }

            @Override
            public void onNotValid() {
                registerPasswordCorrect = false;
                splashRegisterButton.setEnabled(false);
                splashRegisterReplyPasswordField.setFieldCorrect(false);
            }
        });
        splashRegisterReplyPasswordField.setControlIconAutomatically(false);
        splashRegisterReplyPasswordField.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                registerPasswordCorrect = true;
                registerPasswordSame = splashRegisterPasswordField.getText().toString().trim().equals(splashRegisterReplyPasswordField.getText().toString().trim());
                splashRegisterReplyPasswordField.setFieldCorrect(registerPasswordSame);
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordSame && registerNickNameCorrect && registerAccountType != -1));
            }

            @Override
            public void onNotValid() {
                registerPasswordCorrect = false;
                registerPasswordSame = false;
                splashRegisterReplyPasswordField.setFieldCorrect(false);
                splashRegisterButton.setEnabled(false);
            }
        });
        splashRegisterNickNameField.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                registerNickNameCorrect = true;
                registerPasswordSame = splashRegisterPasswordField.getText().toString().trim().equals(splashRegisterReplyPasswordField.getText().toString().trim());
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordCorrect && registerPasswordSame && registerAccountType != -1));
            }

            @Override
            public void onNotValid() {
                registerNickNameCorrect = false;
                splashRegisterButton.setEnabled(false);
            }
        });

        splashRegisterAccountTypeExecutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAccountType = 1;
                registerPasswordSame = splashRegisterPasswordField.getText().toString().trim().equals(splashRegisterReplyPasswordField.getText().toString().trim());
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordCorrect && registerPasswordSame && registerAccountType != -1));
            }
        });
        splashRegisterAccountTypeClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAccountType = 0;
                registerPasswordSame = splashRegisterPasswordField.getText().toString().trim().equals(splashRegisterReplyPasswordField.getText().toString().trim());
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordCorrect && registerPasswordSame && registerAccountType != -1));
            }
        });


        splashRegisterAddPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(SplashActivity.this);
            }
        });

        splashForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginEmailCorrect) {
                    final IndeterminateBottomSheetDialog resetPasswordDialog = new IndeterminateBottomSheetDialog(SplashActivity.this);
                    resetPasswordDialog.setDialogTitle("Подождите");
                    resetPasswordDialog.setDialogText("Идет обработка");
                    resetPasswordDialog.create();
                    firebaseAuth.sendPasswordResetEmail(splashLoginEmailField.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            resetPasswordDialog.dismiss();
                            if (task.isSuccessful()) {
                                final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                                simpleBottomSheetDialog.setDialogTitle("Восстановление пароля!");
                                simpleBottomSheetDialog.setDialogText("На ваш E-Mail была выслана инструкция по восстановлению пароля");
                                simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                simpleBottomSheetDialog.create();
                            } else {
                                final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                                simpleBottomSheetDialog.setDialogTitle("Внимание!");
                                simpleBottomSheetDialog.setDialogText("Произошла непредвиденная ошибка, попробуйте позже");
                                simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                simpleBottomSheetDialog.create();
                            }
                        }
                    });
                } else {
                    final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                    simpleBottomSheetDialog.setDialogTitle("Внимание!");
                    simpleBottomSheetDialog.setDialogText("Введите корректный E-Mail!");
                    simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                    simpleBottomSheetDialog.create();
                }
            }
        });

        splashRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userEmail = splashRegisterEmailField.getText().toString().trim();
                final String userPassword = splashRegisterPasswordField.getText().toString().trim();
                final String userNickName = splashRegisterNickNameField.getText().toString().trim();
                final IndeterminateBottomSheetDialog registerDialog = new IndeterminateBottomSheetDialog(SplashActivity.this);
                registerDialog.setDialogTitle("Подождите");
                registerDialog.setDialogText("Выполняется регистрация");
                registerDialog.create();
                FirebaseHelper.checkUsersDatabaseExistValue(userEmail, "userEmail", new FirebaseHelper.IFirebaseDatabaseValueListener() {
                    @Override
                    public void onValueExists() {
                        registerDialog.dismiss();
                        final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                        simpleBottomSheetDialog.setDialogTitle("Внимание!");
                        simpleBottomSheetDialog.setDialogText("Данный E-Mail уже используется");
                        simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                        simpleBottomSheetDialog.create();
                    }

                    @Override
                    public void onValueNotFound() {
                        FirebaseHelper.checkUsersDatabaseExistValue(userNickName, "userName", new FirebaseHelper.IFirebaseDatabaseValueListener() {
                            @Override
                            public void onValueExists() {
                                registerDialog.dismiss();
                                final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                                simpleBottomSheetDialog.setDialogTitle("Внимание!");
                                simpleBottomSheetDialog.setDialogText("Данный никнейм уже используется");
                                simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                simpleBottomSheetDialog.create();
                            }

                            @Override
                            public void onValueNotFound() {
                                firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            firebaseAuth.signOut();
                                            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull final Task<AuthResult> authResultTask) {
                                                    if (authResultTask.isSuccessful()) {
                                                        final DatabaseReference currentUserDatabase = firebaseDatabase.getReference().child("Users").child(Objects.requireNonNull(Objects.requireNonNull(authResultTask.getResult()).getUser()).getUid());
                                                        usersDatabase.keepSynced(true);
                                                        final StorageReference path = firebaseStorage.getReference().child("user_images").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("profile_photos").child(userNickName + "_" + TextUtils.generateRandomString(10) + ".jpg");
                                                        UploadTask uploadTask = path.putBytes(ImageUtils.getDrawableByteArray(splashRegisterProfilePhotoView.getDrawable()));
                                                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Uri> task) {
                                                                            if (task.isSuccessful()) {
                                                                                currentUserDatabase.child("userPhotoURL").setValue(Objects.requireNonNull(task.getResult()).toString());
                                                                                currentUserDatabase.child("userUID").setValue(Objects.requireNonNull(authResultTask.getResult().getUser()).getUid());
                                                                                currentUserDatabase.child("userEmail").setValue(userEmail);
                                                                                currentUserDatabase.child("userPassword").setValue(userPassword);
                                                                                currentUserDatabase.child("userName").setValue(userNickName);
                                                                                currentUserDatabase.child("isClientAccount").setValue(registerAccountType == 0);
                                                                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                                                                currentUser.sendEmailVerification()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    firebaseAuth.signOut();
                                                                                                    registerDialog.dismiss();

                                                                                                    final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                                                                                                    simpleBottomSheetDialog.setDialogTitle("Регистрация прошла успешно!");
                                                                                                    simpleBottomSheetDialog.setDialogText("На ваш E-Mail было выслано сообщение для подтверждения регистрации");
                                                                                                    simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                                                                                    simpleBottomSheetDialog.setDoneClickListener(new View.OnClickListener() {
                                                                                                        @Override
                                                                                                        public void onClick(View view) {
                                                                                                            simpleBottomSheetDialog.dismiss(true);
                                                                                                            loginCardView.animate()
                                                                                                                    .translationX(0)
                                                                                                                    .alpha(1f)
                                                                                                                    .setDuration(300)
                                                                                                                    .setListener(new AnimatorListenerAdapter() {
                                                                                                                        @Override
                                                                                                                        public void onAnimationEnd(Animator animation) {
                                                                                                                            super.onAnimationEnd(animation);
                                                                                                                            loginCardView.setVisibility(View.VISIBLE);
                                                                                                                        }
                                                                                                                    });
                                                                                                            registerCardView.animate()
                                                                                                                    .translationX(registerCardView.getWidth())
                                                                                                                    .alpha(0.0f)
                                                                                                                    .setDuration(300)
                                                                                                                    .setListener(new AnimatorListenerAdapter() {
                                                                                                                        @Override
                                                                                                                        public void onAnimationEnd(Animator animation) {
                                                                                                                            super.onAnimationEnd(animation);
                                                                                                                            registerCardView.setVisibility(View.GONE);
                                                                                                                            splashRegisterEmailField.setText("");
                                                                                                                            splashRegisterPasswordField.setText("");
                                                                                                                            splashRegisterReplyPasswordField.setText("");
                                                                                                                            splashRegisterNickNameField.setText("");
                                                                                                                            splashRegisterProfilePhotoView.setImageResource(R.drawable.temp_user_photo);
                                                                                                                            splashRegisterEmailField.setText("");
                                                                                                                            splashRegisterEmailField.setText("");
                                                                                                                            splashRegisterEmailField.setText("");
                                                                                                                            registerAccountType = -1;
                                                                                                                            splashLoginEmailField.setText(userEmail);
                                                                                                                            splashLoginPasswordField.setText(userPassword);
                                                                                                                            splashCreateNewAccountButton.setText("Создать новый");
                                                                                                                        }
                                                                                                                    });
                                                                                                        }
                                                                                                    });
                                                                                                    simpleBottomSheetDialog.create();
                                                                                                } else {
                                                                                                    registerDialog.dismiss();
                                                                                                    final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                                                                                                    simpleBottomSheetDialog.setDialogTitle("Внимание!");
                                                                                                    simpleBottomSheetDialog.setDialogText("Произошла ошибка при регистрации, попробуйте позже");
                                                                                                    simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                                                                                    simpleBottomSheetDialog.create();
                                                                                                }
                                                                                            }
                                                                                        });

                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    registerDialog.dismiss();
                                                                    final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                                                                    simpleBottomSheetDialog.setDialogTitle("Внимание!");
                                                                    simpleBottomSheetDialog.setDialogText("Произошла ошибка при регистрации, попробуйте позже");
                                                                    simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                                                    simpleBottomSheetDialog.create();
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        registerDialog.dismiss();
                                                        final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                                                        simpleBottomSheetDialog.setDialogTitle("Внимание!");
                                                        simpleBottomSheetDialog.setDialogText("Произошла ошибка при регистрации, попробуйте позже");
                                                        simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                                        simpleBottomSheetDialog.create();
                                                    }
                                                }
                                            });
                                        } else {
                                            registerDialog.dismiss();
                                            final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                                            simpleBottomSheetDialog.setDialogTitle("Внимание!");
                                            simpleBottomSheetDialog.setDialogText("Произошла ошибка при регистрации, попробуйте позже");
                                            simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                            simpleBottomSheetDialog.create();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });


            }
        });

        splashSighWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signWithGoogleDialog = new IndeterminateBottomSheetDialog(SplashActivity.this);
                signWithGoogleDialog.setDialogTitle("Подождите");
                signWithGoogleDialog.setDialogText("Выполняется вход в аккаунт");
                signWithGoogleDialog.create();
                firebaseAuth.signOut();
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(SplashActivity.this)
                        .enableAutoManage(SplashActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                        .build();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, 1058);
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Picasso.get().load(Objects.requireNonNull(CropImage.getActivityResult(data)).getUri()).into(splashRegisterProfilePhotoView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                simpleBottomSheetDialog.setDialogTitle("Внимание!");
                simpleBottomSheetDialog.setDialogText("Невозможно загрузить фото, попробуйте выбрать другое");
                simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                simpleBottomSheetDialog.create();
            }
        } else if (requestCode == 1058) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                final GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(Objects.requireNonNull(account).getIdToken(), null);
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull final Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final String userID = Objects.requireNonNull(task.getResult()).getUser().getUid();
                                    final String userEmail = account.getEmail();
                                    final String userNickName = account.getDisplayName();
                                    final String photoURL = String.valueOf(account.getPhotoUrl());
                                    FirebaseHelper.checkUsersDatabaseExistValue(userEmail, "userEmail", new FirebaseHelper.IFirebaseDatabaseValueListener() {
                                        @Override
                                        public void onValueExists() {
                                            signWithGoogleDialog.dismiss();
                                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                            finish();
                                        }

                                        @Override
                                        public void onValueNotFound() {

                                            final SelectionBottomSheetDialog selectionBottomSheetDialog = new SelectionBottomSheetDialog(SplashActivity.this);
                                            selectionBottomSheetDialog.setDialogTitle("Внимание");
                                            selectionBottomSheetDialog.setDialogText("Выберите тип вашего аккаунта");
                                            selectionBottomSheetDialog.setListener(new SelectionBottomSheetDialog.ISeledtionDialogListener() {
                                                @Override
                                                public void onSelected(int id, int viewID, String text) {
                                                    registerAccountType = id;
                                                    selectionBottomSheetDialog.dismiss(true);
                                                    final DatabaseReference currentUserDatabase = firebaseDatabase.getReference().child("Users").child(userID);
                                                    usersDatabase.keepSynced(true);
                                                    currentUserDatabase.child("userPhotoURL").setValue(photoURL);
                                                    currentUserDatabase.child("userUID").setValue(userID);
                                                    currentUserDatabase.child("userEmail").setValue(userEmail);
                                                    currentUserDatabase.child("userPassword").setValue(TextUtils.generateRandomString(20));
                                                    currentUserDatabase.child("userName").setValue(userNickName);
                                                    currentUserDatabase.child("isClientAccount").setValue(registerAccountType == 0);
                                                    if (userEmail != null) {
                                                        firebaseAuth.createUserWithEmailAndPassword(userEmail, TextUtils.generateRandomString(20));
                                                    }
                                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            });
                                            selectionBottomSheetDialog.create();
                                        }
                                    });
                                } else {
                                    final SimpleBottomSheetDialog simpleBottomSheetDialog = new SimpleBottomSheetDialog(SplashActivity.this);
                                    simpleBottomSheetDialog.setDialogTitle("Внимание!");
                                    simpleBottomSheetDialog.setDialogText("Невозможно загрузить фото, попробуйте выбрать другое");
                                    simpleBottomSheetDialog.setDialogDoneBtnText("ОК");
                                    simpleBottomSheetDialog.create();
                                }
                            }
                        });
            }
        }
    }



    private boolean checkCurrentUser() {
        return firebaseAuth.getCurrentUser() != null && !firebaseAuth.getCurrentUser().isAnonymous() && firebaseAuth.getCurrentUser().isEmailVerified();
    }

    private void showLoginCard() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginCardView.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                loginCardView.setVisibility(View.VISIBLE);
                                splashAdditionalAccountFunctions.setVisibility(View.VISIBLE);
                                splashDescriptionText.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }, 600);
    }
}
