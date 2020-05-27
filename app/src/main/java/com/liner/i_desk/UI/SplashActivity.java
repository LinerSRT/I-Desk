package com.liner.i_desk.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.gbuttons.GoogleSignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;
import com.liner.i_desk.Utils.Views.EditRegexTextView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashActivity extends AppCompatActivity {
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
    private TextView splashForgotPassword;
    private TextView splashCreateNewAccountButton;
    private GoogleSignInButton splashSighWithGoogle;
    private EditRegexTextView splashRegisterEmailField;
    private EditRegexTextView splashRegisterPasswordField;
    private EditRegexTextView splashRegisterReplyPasswordField;
    private EditRegexTextView splashRegisterNickNameField;
    private RelativeLayout splashRegisterAddPhotoView;
    private CircleImageView splashRegisterProfilePhotoView;
    private Button splashRegisterButton;


    private boolean loginEmailCorrect = false;
    private boolean loginPasswordCorrect = false;

    private boolean registerEmailCorrect = false;
    private boolean registerPasswordCorrect = false;
    private boolean registerPasswordSame = false;
    private boolean registerNickNameCorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabase = firebaseDatabase.getReference("Users");
        firebaseStorage = FirebaseStorage.getInstance();
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
            splashRegisterAddPhotoView = findViewById(R.id.splashRegisterAddPhotoView);
            splashRegisterProfilePhotoView = findViewById(R.id.splashRegisterProfilePhotoView);
            splashRegisterButton = findViewById(R.id.splashRegisterButton);
        }

        final Handler handler = new Handler();
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
                splashRegisterButton.setEnabled((registerPasswordCorrect && registerPasswordSame && registerNickNameCorrect));
            }

            @Override
            public void onNotValid() {
                registerEmailCorrect = false;
                splashRegisterButton.setEnabled(false);
            }
        });
        splashRegisterPasswordField.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                registerPasswordCorrect = true;
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordSame && registerNickNameCorrect));
            }

            @Override
            public void onNotValid() {
                registerPasswordCorrect = false;
                splashRegisterButton.setEnabled(false);
            }
        });
        splashRegisterReplyPasswordField.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                registerPasswordCorrect = true;
                registerPasswordSame = splashRegisterPasswordField.getText().toString().trim().equals(splashRegisterReplyPasswordField.getText().toString().trim());
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordSame && registerNickNameCorrect));
            }

            @Override
            public void onNotValid() {
                registerPasswordCorrect = false;
                registerPasswordSame = false;
                splashRegisterButton.setEnabled(false);
            }
        });
        splashRegisterNickNameField.setTextListener(new EditRegexTextView.IEditTextListener() {
            @Override
            public void onValid(String result) {
                registerNickNameCorrect = true;
                splashRegisterButton.setEnabled((registerEmailCorrect && registerPasswordCorrect && registerPasswordSame));
            }

            @Override
            public void onNotValid() {
                registerNickNameCorrect = false;
                splashRegisterButton.setEnabled(false);
            }
        });

        splashRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
}
