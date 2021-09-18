package com.borntocode.covidshield;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.borntocode.covidshield.managers.SharedPreferencesManager;
import com.borntocode.covidshield.ui.AppSnackbar;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private SharedPreferencesManager sharedPreferencesManager;
    private boolean doubleBackToExitPressedOnce = false;

    private ConstraintLayout link_signup;
    private TextView forget_password;
    private TextInputLayout txt_emailAddress, txt_password;
    private Button btn_signIn;

    private String[] PERMISSIONS;
    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_emailAddress = (TextInputLayout) findViewById(R.id.txt_loginEmail);
        txt_password = (TextInputLayout) findViewById(R.id.txt_loginPassword);
        forget_password = (TextView) findViewById(R.id.signIn_forget_password);
        link_signup = (ConstraintLayout) findViewById(R.id.signup_link_signin);
        btn_signIn = (Button) findViewById(R.id.btn_signIn);

        PERMISSIONS = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE
        };

        mAuth = FirebaseAuth.getInstance();
        sharedPreferencesManager = new SharedPreferencesManager(this);

        link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect_signup();
            }
        });

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_user(view);
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect_resetPassword();
            }
        });

        init();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void init() {
        textChangedListener(txt_emailAddress, txt_emailAddress.getEditText());
        textChangedListener(txt_password, txt_password.getEditText());
    }

    private void redirect_resetPassword() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    private void redirect_signup() {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
    }

    @SuppressLint("ResourceType")
    private void login_user(View view) {
        String email = txt_emailAddress.getEditText().getText().toString();
        String pwd = txt_password.getEditText().getText().toString();

        if (!isNetworkConnected()) {
            AppSnackbar.SnackbarINFO(view, this, "NO INTERNET CONNECTION");
        } else if (email.length() == 0) {
            txt_emailAddress.requestFocus();
            txt_emailAddress.setError("Email cannot be empty");
        } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            txt_emailAddress.requestFocus();
            txt_emailAddress.setError("Please enter valid Email");
        } else if (pwd.isEmpty()) {
            txt_password.requestFocus();
            txt_password.setError("Please enter valid Password");
        } else {
            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor(getResources().getString(R.color.colorPrimary)));
            pDialog.setTitleText("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
            mAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");
                                pDialog.cancel();
                                FirebaseUser user = mAuth.getCurrentUser();
                                userID = user.getUid();
                                loadMainActivity(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                        if(task.getResult().getSignInMethods().isEmpty()){
                                            pDialog.cancel();
                                            AppSnackbar.SnackbarINFO(view,LoginActivity.this,"Invalid User Credentials. Please Try Again!");
                                        }else{
                                            pDialog.cancel();
                                            AppSnackbar.SnackbarINFO(view,LoginActivity.this,"Invalid User Credentials. Please check you password!");
                                        }
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void loadMainActivity(FirebaseUser user){
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(LoginActivity.this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setContentText("This app requires permissions for Access Location and Call Phone.\nPlease grant access for following requests.")
                    .setConfirmText("Sure!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(PERMISSIONS, 80);
                            }
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Toast.makeText(LoginActivity.this, "Permission Denied ", Toast.LENGTH_SHORT).show();
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        } else {
            sharedPreferencesManager.savePreferences(SharedPreferencesManager.FIREBASE_USER_ID,user.getUid());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 80) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sharedPreferencesManager.savePreferences(SharedPreferencesManager.FIREBASE_USER_ID,userID);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Permission Denied ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


