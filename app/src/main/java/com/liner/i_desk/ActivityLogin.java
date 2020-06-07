package com.liner.i_desk;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.liner.bottomdialogs.IndeterminateDialog;
import com.liner.bottomdialogs.SimpleDialog;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.i_desk.Firebase.UserObject;

import java.util.ArrayList;
import java.util.Objects;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

@SuppressWarnings( "deprecation" )
public class ActivityLogin extends AppCompatActivity {
    private TextFieldBoxes loginTextFieldEmailBox;
    private TextFieldBoxes loginTextFieldPasswordBox;
    private ExtendedEditText loginExtendedPasswordEdit;
    private String userEmail = "", userPassword = "";
    private SimpleDialog.Builder errorDialog;
    private IndeterminateDialog.Builder progressBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        Button loginSignWithGoogle = findViewById(R.id.loginSignWithGoogle);
        Button loginSignIn = findViewById(R.id.loginSignIn);
        Button loginRegisterAccount = findViewById(R.id.loginRegisterAccount);
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

        progressBottomSheetDialog = new IndeterminateDialog.Builder(this)
                .setDialogText("Вход").setTitleText("Подождите...").build();
        errorDialog = new SimpleDialog.Builder(this)
                .setDismissTouchOutside(false)
                .setTitleText("Ошибка")
                .setDialogText("Невозможно войти в аккаунт")
                .setDone("Ок", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorDialog.close();
                    }
                }).build();


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
                        progressBottomSheetDialog.show();
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
                                            progressBottomSheetDialog.close();
                                            errorDialog.show();
                                        }
                                    });
                                } else {
                                    progressBottomSheetDialog.close();
                                    errorDialog.show();
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
                progressBottomSheetDialog = new IndeterminateDialog.Builder(ActivityLogin.this);
                progressBottomSheetDialog.setTitleText("Подождите").setDialogText("Выполняется вход в аккаунт").build();
                progressBottomSheetDialog.show();
                FirebaseAuth.getInstance().signOut();
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(ActivityLogin.this)
                        .enableAutoManage(ActivityLogin.this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                progressBottomSheetDialog.close();
                                errorDialog.show();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1058) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (googleSignInResult.isSuccess()) {
                final GoogleSignInAccount googleAccount = googleSignInResult.getSignInAccount();
                if (googleAccount != null) {
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
                    FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if(Constants.USERS_REQUIRE_EMAIL_VERIFICATION)
                                    Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).sendEmailVerification();
                                UserObject userObject = new UserObject(
                                        Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid(),
                                        UserObject.UserType.CLIENT,
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

                            } else {
                                progressBottomSheetDialog.close();
                                errorDialog.show();
                            }
                        }
                    });
                } else {
                    progressBottomSheetDialog.close();
                    errorDialog.show();
                }
            } else {
                progressBottomSheetDialog.close();
                errorDialog.show();
            }
        }

    }
}
