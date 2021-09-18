package com.borntocode.covidshield;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgotPasswordActivity extends BaseActivity {
    private FirebaseAuth mAuth;

    TextInputLayout txt_forgotPassword;
    Button btn_resetPassword;
    Button btn_forgetPasswordBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        txt_forgotPassword = (TextInputLayout) findViewById(R.id.forgotPassword_Email);
        btn_resetPassword = (Button) findViewById(R.id.btn_resetPassword);
        btn_forgetPasswordBack = (Button) findViewById(R.id.btn_forgetPasswordBack);

        btn_forgetPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forget_Password();
            }
        });

        init();
    }

    private void init(){
        textChangedListener(txt_forgotPassword, txt_forgotPassword.getEditText());
    }

    @SuppressLint("ResourceType")
    private void forget_Password(){
        String email = txt_forgotPassword.getEditText().getText().toString();
        if (email.length() == 0) {
            txt_forgotPassword.requestFocus();
            txt_forgotPassword.setError("Email cannot be empty");
        } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            txt_forgotPassword.requestFocus();
            txt_forgotPassword.setError("Please enter valid Email");
        } else{
            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor(getResources().getString(R.color.colorPrimary)));
            pDialog.setTitleText("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pDialog.cancel();
                                Toast.makeText(ForgotPasswordActivity.this,"Covid Shield has sent a password reset link to " + email,Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                            }
                            else {
                                pDialog.cancel();
                                txt_forgotPassword.requestFocus();
                                txt_forgotPassword.setError("Please check your Email Address");
                            }
                        }
                    });
        }
    }
}