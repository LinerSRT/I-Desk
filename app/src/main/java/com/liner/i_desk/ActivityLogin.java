package com.liner.i_desk;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.BaseDialogSelectionItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

@SuppressWarnings("deprecation")
public class ActivityLogin extends AppCompatActivity {
    private TextFieldBoxes loginTextFieldEmailBox;
    private TextFieldBoxes loginTextFieldPasswordBox;
    private ExtendedEditText loginExtendedPasswordEdit;
    private String userEmail = "", userPassword = "";
    private BaseDialog errorDialog;
    private BaseDialog progressDialog;
    private BaseDialog resetPasswordDialog;
    private BaseDialog accountTypeSelectionDialog;
    private UserObject.UserType accountType = UserObject.UserType.CLIENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        MaterialButton loginSignWithGoogle = findViewById(R.id.loginSignWithGoogle);
        MaterialButton loginSignIn = findViewById(R.id.loginSignIn);
        MaterialButton loginRegisterAccount = findViewById(R.id.loginRegisterAccount);
        MaterialButton loginResetPassword = findViewById(R.id.loginResetPassword);
        loginTextFieldEmailBox = findViewById(R.id.loginTextFieldEmailBox);
        loginTextFieldPasswordBox = findViewById(R.id.loginTextFieldPasswordBox);
        loginExtendedPasswordEdit = findViewById(R.id.loginExtendedPasswordEdit);
        ExtendedEditText loginExtendedEmailEdit = findViewById(R.id.loginExtendedEmailEdit);
        if (getIntent().getStringExtra("userEmail") != null) {
            userEmail = getIntent().getStringExtra("userEmail");
            loginExtendedEmailEdit.setText(userEmail);
            loginExtendedEmailEdit.setSelection(userEmail.length());
        }
        if (getIntent().getStringExtra("userPassword") != null) {
            userPassword = getIntent().getStringExtra("userPassword");
            loginExtendedPasswordEdit.setText(userPassword);
            loginExtendedPasswordEdit.setSelection(userPassword.length());
        }


        progressDialog = BaseDialogBuilder.buildFast(this,
                "Вход",
                "Подождите...",
                null,
                "Ок",
                BaseDialogBuilder.Type.INDETERMINATE,
                null,
                null);
        resetPasswordDialog = BaseDialogBuilder.buildFast(this,
                "Сброс пароля",
                "На ваш E-mail выслана инструкция для сброса пароля",
                null,
                "Ок",
                BaseDialogBuilder.Type.INFO,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetPasswordDialog.closeDialog();
                    }
                });

        errorDialog = BaseDialogBuilder.buildFast(this,
                "Ошибка",
                "Неверный логин или пароль!",
                null,
                "Ок",
                BaseDialogBuilder.Type.ERROR,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorDialog.closeDialog();
                    }
                });

        loginTextFieldEmailBox.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                userEmail = theNewText;
            }
        });
        loginTextFieldPasswordBox.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                userPassword = theNewText;
            }
        });
        loginTextFieldPasswordBox.getEndIconImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginExtendedPasswordEdit.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    loginExtendedPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    loginTextFieldPasswordBox.setEndIcon(R.drawable.hide_password_icon);
                } else {
                    loginTextFieldPasswordBox.setEndIcon(R.drawable.show_password_icon);
                    loginExtendedPasswordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                loginExtendedPasswordEdit.setSelection(userPassword.length());
            }
        });
        loginSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextUtils.hideKeyboard(ActivityLogin.this);
                if (TextUtils.isEmailValid(userEmail)) {
                    loginTextFieldEmailBox.removeError();
                    if (TextUtils.isPasswordValid(userPassword)) {
                        loginTextFieldPasswordBox.removeError();
                        progressDialog.showDialog();
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseValue.getUser(Firebase.getUserUID(), new FirebaseValue.ValueListener() {
                                        @Override
                                        public void onSuccess(Object object, DatabaseReference databaseReference) {
                                            UserObject userObject = (UserObject) object;
                                            if (userObject.isUserRegisterFinished()) {
                                                startActivity(new Intent(ActivityLogin.this, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
                                                finish();
                                            } else {
                                                Intent intent = new Intent(ActivityLogin.this, ActivityCreateProfile.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("userObject", userObject);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onFail(String errorMessage) {
                                            progressDialog.closeDialog();
                                            errorDialog.showDialog();
                                        }
                                    });
                                } else {
                                    progressDialog.closeDialog();
                                    errorDialog.showDialog();
                                }
                            }
                        });
                    } else {
                        loginTextFieldPasswordBox.setError("Пароль должен быть длинее 6 символов и иметь хотя бы 1 букву и цифру", true);
                    }
                } else {
                    loginTextFieldEmailBox.setError("Введите правильный Email", true);
                }
            }
        });
        loginSignWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.showDialog();
                FirebaseAuth.getInstance().signOut();
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(ActivityLogin.this)
                        .enableAutoManage(ActivityLogin.this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                progressDialog.closeDialog();
                                errorDialog.showDialog();
                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                        .build();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, 1058);

            }
        });
        loginRegisterAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (!TextUtils.isTextEmpty(userEmail))
                    intent.putExtra("userEmail", userEmail);
                if (!TextUtils.isTextEmpty(userPassword))
                    intent.putExtra("userPassword", userPassword);
                startActivity(intent);
            }
        });
        loginResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextUtils.hideKeyboard(ActivityLogin.this);
                if (TextUtils.isEmailValid(userEmail)) {
                    loginTextFieldEmailBox.removeError();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                resetPasswordDialog.showDialog();
                            } else {
                                errorDialog.showDialog();
                            }
                        }
                    });
                } else {
                    loginTextFieldEmailBox.setError("Введите правильный Email", true);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1058) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (googleSignInResult.isSuccess()) {
                final GoogleSignInAccount googleAccount = googleSignInResult.getSignInAccount();
                if (googleAccount != null) {
                    List<BaseDialogSelectionItem> selectionItems = new ArrayList<>();
                    selectionItems.add(new BaseDialogSelectionItem(R.drawable.user_icon, "Заявитель", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            accountType = UserObject.UserType.CLIENT;
                            createWithGoogle(googleAccount);
                            accountTypeSelectionDialog.closeDialog();
                            progressDialog.showDialog();
                        }
                    }));
                    selectionItems.add(new BaseDialogSelectionItem(R.drawable.user_icon, "Исполнитель", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            accountType = UserObject.UserType.SERVICE;
                            createWithGoogle(googleAccount);
                            accountTypeSelectionDialog.closeDialog();
                            progressDialog.showDialog();
                        }
                    }));
                    accountTypeSelectionDialog = new BaseDialogBuilder(this)
                            .setDialogType(BaseDialogBuilder.Type.SINGLE_CHOOSE)
                            .setDialogTitle("Тип аккаунта")
                            .setDialogText("Выберите тип вашего аккаунта")
                            .setSelectionList(selectionItems)
                            .build();

                    accountTypeSelectionDialog.showDialog();

                } else {
                    progressDialog.closeDialog();
                    errorDialog.showDialog();
                }
            } else {
                progressDialog.closeDialog();
                errorDialog.showDialog();
            }
        }
    }
    private void createWithGoogle(final GoogleSignInAccount googleAccount){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseValue.getUser(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid(), new FirebaseValue.ValueListener() {
                        @Override
                        public void onSuccess(Object object, DatabaseReference databaseReference) {
                            startActivity(new Intent(ActivityLogin.this, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                        @Override
                        public void onFail(String errorMessage) {
                            if (Constants.USERS_REQUIRE_EMAIL_VERIFICATION)
                                Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).sendEmailVerification();
                            UserObject userObject = new UserObject(
                                    Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid(),
                                    accountType,
                                    FirebaseInstanceId.getInstance().getToken(),
                                    googleAccount.getDisplayName(),
                                    googleAccount.getEmail(),
                                    String.valueOf(googleAccount.getPhotoUrl()),
                                    "Информация о себе не указана",
                                    "",
                                    UserObject.UserStatus.ONLINE,
                                    Time.getTime(),
                                    Time.getTime(),
                                    true,
                                    new ArrayList<String>(),
                                    new ArrayList<String>(),
                                    new ArrayList<String>()
                            );
                            Objects.requireNonNull(Firebase.getUsersDatabase()).child(userObject.getUserID()).setValue(userObject)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startActivity(new Intent(ActivityLogin.this, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
                                            finish();
                                        }
                                    });
                        }
                    });
                } else {
                    progressDialog.closeDialog();
                    errorDialog.showDialog();
                }
            }
        });
    }
}
