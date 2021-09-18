package com.borntocode.covidshield;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends BaseActivity {

    private static  int SPLASH_TIMEOUT = 1500;
    private Animation anim;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private ConstraintLayout anim_Layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        anim_Layout = (ConstraintLayout) findViewById(R.id.animation_Layout);
        anim = AnimationUtils.loadAnimation(this,R.anim.ic_app_icon);

        anim_Layout.setAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentUser != null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIMEOUT);
    }
}