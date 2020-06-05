package com.liner.i_desk.UI;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;
import com.liner.i_desk.Utils.ImageUtils;
import com.liner.i_desk.Utils.Server.Time;
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

import java.io.File;
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
    private User.Type registerAccountType;
    private IndeterminateBottomSheetDialog.Builder signWithGoogleDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabase = firebaseDatabase.getReference("Users");
        firebaseStorage = FirebaseStorage.getInstance();
        final TextView splashForgotPassword;
        final GoogleSignInButton splashSighWithGoogle;
        final RelativeLayout splashRegisterAddPhotoView;
        final RadioButton splashRegisterAccountTypeClient;
        final RadioButton splashRegisterAccountTypeExecutor;
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


        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
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
                                                startActivity(new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
                                                finish();
                                            }
                                        }, 0);
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
                final IndeterminateBottomSheetDialog.Builder loginDialog = new IndeterminateBottomSheetDialog.Builder(SplashActivity.this);
                loginDialog.setTitleText("Подождите").setTitleText("Выполняется вход в аккаунт").build();
                loginDialog.setDialogText("");
                loginDialog.show();
                firebaseAuth.signOut();
                firebaseAuth.signInWithEmailAndPassword(splashLoginEmailField.getText().toString().trim(), splashLoginPasswordField.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (!Objects.requireNonNull(authResult.getUser()).isEmailVerified()) {
                            loginDialog.close();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
                            finish();
                        } else {
                            loginDialog.close();
                            final SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(SplashActivity.this);
                            simpleDialog.setTitleText("Внимание!")
                                    .setDialogText("Вы не подтвердили свой E-Mail. Инструкция для подтверждения была выслана вам на почту")
                                    .setDone("Ок", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            simpleDialog.close();
                                        }
                                    }).build();
                            simpleDialog.show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginDialog.close();
                        if (Objects.requireNonNull(e.getLocalizedMessage()).contains("invalid") || Objects.requireNonNull(e.getLocalizedMessage().contains("no user"))) {
                            final SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(SplashActivity.this);
                            simpleDialog.setTitleText("Внимание!")
                                    .setDialogText("Неверный пароль или E-Mail")
                                    .setDone("Ок", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            simpleDialog.close();
                                        }
                                    }).build();
                            simpleDialog.show();
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
                splashRegisterButton.setEnabled((registerPasswordCorrect && registerPasswordSame && registerNickNameCorrect && registerAccountType != null));
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
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordSame && registerNickNameCorrect && registerAccountType != null));
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
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordSame && registerNickNameCorrect && registerAccountType != null));
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
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordCorrect && registerPasswordSame && registerAccountType != null));
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
                registerAccountType = User.Type.SERVICE;
                registerPasswordSame = splashRegisterPasswordField.getText().toString().trim().equals(splashRegisterReplyPasswordField.getText().toString().trim());
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordCorrect && registerPasswordSame && registerAccountType != null));


            }
        });
        splashRegisterAccountTypeClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAccountType = User.Type.CLIENT;
                registerPasswordSame = splashRegisterPasswordField.getText().toString().trim().equals(splashRegisterReplyPasswordField.getText().toString().trim());
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordCorrect && registerPasswordSame && registerAccountType != null));

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
                    final IndeterminateBottomSheetDialog.Builder resetPasswordDialog = new IndeterminateBottomSheetDialog.Builder(SplashActivity.this);
                    resetPasswordDialog.setTitleText("Подождите").setDialogText("Идет обработка").build();
                    resetPasswordDialog.show();
                    firebaseAuth.sendPasswordResetEmail(splashLoginEmailField.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            resetPasswordDialog.close();
                            if (task.isSuccessful()) {
                                showDialogMessage("Восстановление пароля!", "На ваш E-Mail была выслана инструкция по восстановлению пароля");
                            } else {
                                showDialogMessage("Внимание!", "Произошла непредвиденная ошибка, попробуйте позже");
                            }
                        }
                    });
                } else {
                    showDialogMessage("Внимание!", "Введите корректный E-Mail!");
                }

            }
        });

        splashRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userEmail = splashRegisterEmailField.getText().toString().trim();
                final String userPassword = splashRegisterPasswordField.getText().toString().trim();
                final String userNickName = splashRegisterNickNameField.getText().toString().trim();
                final IndeterminateBottomSheetDialog.Builder registerDialog = new IndeterminateBottomSheetDialog.Builder(SplashActivity.this);
                registerDialog.setTitleText("Подождите").setDialogText("Выполняется регистрация").build();
                registerDialog.show();
                FirebaseHelper.checkUsersDatabaseExistValue(userEmail, "userEmail", new FirebaseHelper.IFirebaseDatabaseValueListener() {
                    @Override
                    public void onValueExists() {
                        registerDialog.close();
                        showDialogMessage("Внимание!", "Данный E-Mail уже используется");
                    }

                    @Override
                    public void onValueNotFound() {
                        FirebaseHelper.checkUsersDatabaseExistValue(userNickName, "userName", new FirebaseHelper.IFirebaseDatabaseValueListener() {
                            @Override
                            public void onValueExists() {
                                registerDialog.close();
                                showDialogMessage("Внимание!", "Данный никнейм уже используется");
                            }

                            @Override
                            public void onValueNotFound() {
                                firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull final Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            firebaseAuth.signOut();
                                            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull final Task<AuthResult> authResultTask) {
                                                    if (authResultTask.isSuccessful()) {
                                                        final User user = new User();
                                                        user.setUserUID(Objects.requireNonNull(authResultTask.getResult().getUser()).getUid());

                                                        FirebaseHelper.uploadByteArray(ImageUtils.getDrawableByteArray(splashRegisterProfilePhotoView.getDrawable()),
                                                                userNickName + "_" + TextUtils.generateRandomString(10) + ".jpg",
                                                                "user_images" + File.separator + firebaseAuth.getCurrentUser().getUid() + File.separator + "profile_photos",
                                                                new FirebaseHelper.UploadListener() {
                                                                    @Override
                                                                    public void onFileUploading(int percent, long transferred, long total, String filename) {

                                                                    }

                                                                    @Override
                                                                    public void onFileUploaded(Request.FileData fileData) {
                                                                        user.setUserPhotoURL(fileData.getDownloadURL());
                                                                        user.setUserPassword(userPassword);
                                                                        user.setUserName(userNickName);
                                                                        user.setLastOnlineTime(Time.getTime());
                                                                        user.setUserEmail(userEmail);
                                                                        user.setUserAccountType(registerAccountType);
                                                                        FirebaseHelper.setUserModel(user.getUserUID(), user, new FirebaseHelper.IFirebaseHelperListener() {
                                                                            @Override
                                                                            public void onSuccess(Object result) {
                                                                                registerDialog.close();
                                                                                authResultTask.getResult().getUser().sendEmailVerification();
                                                                                final SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(SplashActivity.this);
                                                                                simpleDialog.setTitleText("Регистрация прошла успешно!")
                                                                                        .setDialogText("На ваш E-Mail было выслано сообщение для подтверждения регистрации")
                                                                                        .setDone("Ок", new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View view) {
                                                                                                simpleDialog.close();
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
                                                                                                                registerAccountType = null;
                                                                                                                splashLoginEmailField.setText(userEmail);
                                                                                                                splashLoginPasswordField.setText(userPassword);
                                                                                                                splashCreateNewAccountButton.setText("Создать новый");
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }).build();
                                                                                simpleDialog.show();
                                                                            }

                                                                            @Override
                                                                            public void onFail(String reason) {

                                                                                registerDialog.close();
                                                                                showDialogMessage("Внимание!", "Произошла ошибка при регистрации, попробуйте позже");
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onFileUploadFail(String reason) {
                                                                        registerDialog.close();
                                                                        showDialogMessage("Внимание!", "Произошла ошибка при регистрации, попробуйте позже");
                                                                    }
                                                                });
                                                    } else {
                                                        registerDialog.close();
                                                        showDialogMessage("Внимание!", "Произошла ошибка при регистрации, попробуйте позже");
                                                    }
                                                }
                                            });
                                        } else {
                                            registerDialog.close();
                                            showDialogMessage("Внимание!", "Произошла ошибка при регистрации, попробуйте позже");

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
                signWithGoogleDialog = new IndeterminateBottomSheetDialog.Builder(SplashActivity.this);
                signWithGoogleDialog.setTitleText("Подождите").setDialogText("Выполняется вход в аккаунт").build();
                signWithGoogleDialog.show();
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
                showDialogMessage("Внимание!", "Невозможно загрузить фото, попробуйте выбрать другое");
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
                                            signWithGoogleDialog.close();
                                            startActivity(new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
                                            finish();
                                        }

                                        @Override
                                        public void onValueNotFound() {
                                            final SelectionBottomSheetDialog selectionBottomSheetDialog = new SelectionBottomSheetDialog(SplashActivity.this);
                                            selectionBottomSheetDialog.setDialogTitle("Внимание");
                                            selectionBottomSheetDialog.setDialogText("Выберите тип вашего аккаунта");
                                            selectionBottomSheetDialog.setListener(new SelectionBottomSheetDialog.ISeledtionDialogListener() {
                                                @Override
                                                public void onSelected(User.Type id, int viewID, String text) {
                                                    registerAccountType = id;
                                                    selectionBottomSheetDialog.dismiss(true);
                                                    task.getResult().getUser().sendEmailVerification();
                                                    final User user = new User();
                                                    user.setUserUID(userID);
                                                    user.setUserPhotoURL(photoURL);
                                                    user.setUserPassword(TextUtils.generateRandomString(64));
                                                    user.setUserName(userNickName);
                                                    user.setLastOnlineTime(Time.getTime());
                                                    user.setUserEmail(userEmail);
                                                    user.setUserAccountType(registerAccountType);
                                                    FirebaseHelper.setUserModel(user.getUserUID(), user, new FirebaseHelper.IFirebaseHelperListener() {
                                                        @Override
                                                        public void onSuccess(Object result) {
                                                            firebaseAuth.createUserWithEmailAndPassword(Objects.requireNonNull(userEmail), user.getUserPassword());
                                                            startActivity(new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
                                                            finish();

                                                            final SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(SplashActivity.this);
                                                            simpleDialog.setTitleText("Регистрация прошла успешно!")
                                                                    .setDialogText("На ваш E-Mail было выслано сообщение для подтверждения регистрации")
                                                                    .setDone("Ок", new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            simpleDialog.close();
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
                                                                                            registerAccountType = null;
                                                                                            splashLoginEmailField.setText(userEmail);
                                                                                            splashLoginPasswordField.setText(user.getUserPassword());
                                                                                            splashCreateNewAccountButton.setText("Создать новый");
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }).build();
                                                            simpleDialog.show();
                                                        }

                                                        @Override
                                                        public void onFail(String reason) {
                                                            showDialogMessage("Внимание!", "Произошла ошибка при регистрации, попробуйте позже");
                                                        }
                                                    });


                                                }
                                            });
                                            selectionBottomSheetDialog.create();


                                        }
                                    });
                                } else {
                                    showDialogMessage("Внимание!", "Невозможно загрузить фото, попробуйте выбрать другое");
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

    private void showDialogMessage(String title, String text) {
        final SimpleBottomSheetDialog.Builder simpleDialog = new SimpleBottomSheetDialog.Builder(SplashActivity.this);
        simpleDialog.setTitleText(title)
                .setDialogText(text)
                .setDone("Ок", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        simpleDialog.close();
                    }
                }).build();
        simpleDialog.show();
    }
}
