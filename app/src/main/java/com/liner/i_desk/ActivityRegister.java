package com.liner.i_desk;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.views.BaseDialogSelectionItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

@SuppressWarnings( "deprecation" )
public class ActivityRegister extends AppCompatActivity {
    private TextFieldBoxes registerTextFieldEmailBox;
    private TextFieldBoxes registerTextFieldPasswordBox;
    private String userEmail = "", userPassword = "";
    private BaseDialog errorDialog;
    private BaseDialog progressDialog;
    private UserObject.UserType accountType = UserObject.UserType.CLIENT;
    private BaseDialog accountTypeSelectionDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);
        MaterialButton registerSignWithGoogle = findViewById(R.id.registerSignWithGoogle);
        MaterialButton registerNextStep = findViewById(R.id.registerNextStep);
        MaterialButton registerLoginAccount = findViewById(R.id.registerLoginAccount);
        registerTextFieldEmailBox = findViewById(R.id.registerTextFieldEmailBox);
        registerTextFieldPasswordBox = findViewById(R.id.registerTextFieldPasswordBox);
        ExtendedEditText registerExtendedPasswordEdit = findViewById(R.id.registerExtendedPasswordEdit);
        ExtendedEditText registerExtendedEmailEdit = findViewById(R.id.registerExtendedEmailEdit);

        if(getIntent().getStringExtra("userEmail") != null) {
            userEmail = getIntent().getStringExtra("userEmail");
            registerExtendedEmailEdit.setText(userEmail);
            registerExtendedEmailEdit.setSelection(userEmail.length());
        }
        if(getIntent().getStringExtra("userPassword") != null) {
            userPassword = getIntent().getStringExtra("userPassword");
            registerExtendedPasswordEdit.setText(userPassword);
            registerExtendedPasswordEdit.setSelection(userPassword.length());
        }

        registerTextFieldEmailBox.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                userEmail = theNewText;
            }
        });
        registerTextFieldPasswordBox.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                userPassword = theNewText;
            }
        });

        progressDialog = BaseDialogBuilder.buildFast(this,
                "Регистрация",
                "Подождите...",
                null,
                "Ок",
                BaseDialogBuilder.Type.INDETERMINATE,
                null,
                null);
        errorDialog = BaseDialogBuilder.buildFast(this,
                "Ошибка",
                "Невозможно создать новый аккаунт. Попробуйте позже!",
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


        registerNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextUtils.hideKeyboard(ActivityRegister.this);
                if (TextUtils.isEmailValid(userEmail)) {
                    registerTextFieldEmailBox.removeError();
                    if (TextUtils.isPasswordValid(userPassword)) {
                        registerTextFieldPasswordBox.removeError();
                        progressDialog.showDialog();
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if(Constants.USERS_REQUIRE_EMAIL_VERIFICATION)
                                        Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).sendEmailVerification();
                                    final UserObject userObject = new UserObject(
                                            Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid(),
                                            UserObject.UserType.CLIENT,
                                            FirebaseInstanceId.getInstance().getToken(),
                                            "",
                                            userEmail,
                                            "",
                                            "Информация о себе не указана",
                                            "",
                                            UserObject.UserStatus.ONLINE,
                                            Time.getTime(),
                                            Time.getTime(),
                                            false,
                                            new ArrayList<String>(),
                                            new ArrayList<String>(),
                                            new ArrayList<String>()
                                    );
                                    FirebaseValue.setUser(Firebase.getUserUID(), userObject);
                                    Intent intent = new Intent(ActivityRegister.this, ActivityCreateProfile.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("userObject", userObject);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if ("The email address is already in use by another account.".equals(Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()))) {
                                        errorDialog.setDialogTextText("Невозможно создать новый аккаунт. Данный Email уже используется!");
                                    } else {
                                        errorDialog.setDialogTextText("Невозможно создать новый аккаунт. Попробуйте позже!");
                                    }
                                    progressDialog.closeDialog();
                                    errorDialog.showDialog();
                                }
                            }
                        });
                    } else {
                        registerTextFieldPasswordBox.setError("Пароль должен быть длинее 6 символов и иметь хотя бы 1 букву и цифру", true);
                    }
                } else {
                    registerTextFieldEmailBox.setError("Введите правильный Email", true);
                }
            }
        });
        registerSignWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setDialogTextText("Выполняется вход в аккаунт");
                progressDialog.showDialog();
                FirebaseAuth.getInstance().signOut();
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(ActivityRegister.this)
                        .enableAutoManage(ActivityRegister.this, new GoogleApiClient.OnConnectionFailedListener() {
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
        registerLoginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityRegister.this, ActivityLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                if(!TextUtils.isTextEmpty(userEmail))
                    intent.putExtra("userEmail", userEmail);
                if(!TextUtils.isTextEmpty(userPassword))
                    intent.putExtra("userPassword", userPassword);
                startActivity(intent);
                finish();
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
                            startActivity(new Intent(ActivityRegister.this, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
                                            startActivity(new Intent(ActivityRegister.this, ActivityMain.class).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
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
