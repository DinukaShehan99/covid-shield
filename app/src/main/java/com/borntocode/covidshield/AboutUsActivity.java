package com.borntocode.covidshield;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity {

    TextView txt_aboutUsTitle,txt_aboutUsDescription;
    ScrollView scroll_aboutUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        txt_aboutUsTitle = (TextView) findViewById(R.id.txt_aboutusTitile);
        txt_aboutUsDescription = (TextView) findViewById(R.id.txt_aboutUs);
        scroll_aboutUs = (ScrollView) findViewById(R.id.scroll_aboutus);

        getScroll();
    }

    private void getScroll() {
        scroll_aboutUs.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onGlobalLayout() {
                scroll_aboutUs.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ObjectAnimator objectAnimator = ObjectAnimator.ofInt(scroll_aboutUs, "scrollY", scroll_aboutUs.getChildAt(0).getHeight() - scroll_aboutUs.getHeight());
                objectAnimator.setDuration(25000);
                objectAnimator.setInterpolator(new LinearInterpolator());
                objectAnimator.start();

                objectAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        finish();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });

                scroll_aboutUs.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                        {
                            objectAnimator.resume();
                            return true;
                        }
                        else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            objectAnimator.pause();
                            return false;
                        }
                        else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                        {
                            scroll_aboutUs.clearAnimation();
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}