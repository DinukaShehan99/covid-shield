package com.borntocode.covidshield;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.borntocode.covidshield.managers.SharedPreferencesManager;
import com.borntocode.covidshield.ui.OnboardSliderAdapter;
import com.borntocode.covidshield.ui.SliderAdapter;

public class OnboardingActivity extends AppCompatActivity {

    private SharedPreferencesManager sharedPreferencesManager;

    private ViewPager viewPager;
    private LinearLayout mDotLayout;
    private OnboardSliderAdapter onboardSliderAdapter;
    private Button btnNext;
    private Button btnBack;
    private TextView btnSkip;
    private CheckBox chb_dontShow;

    private TextView[] dots;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        mDotLayout = findViewById(R.id.dotsLayout);
        btnBack = findViewById(R.id.btn_back);
        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btnSkip);
        chb_dontShow = findViewById(R.id.chb_dontShow);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        onboardSliderAdapter = new OnboardSliderAdapter(this);
        viewPager.setAdapter(onboardSliderAdapter);
        addDotsIndicators(0);
        viewPager.addOnPageChangeListener(viewListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentPage == dots.length - 1) {
                    if(chb_dontShow.isChecked()){
                        sharedPreferencesManager.savePreferences(SharedPreferencesManager.IS_DONE_TUTORIAL,true);
                    }

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.presidentsoffice.gov.lk/index.php/vaccination-dashboard/"));
                    startActivity(i);
                    ActivityCompat.finishAfterTransition(OnboardingActivity.this);
                } else {
                    viewPager.setCurrentItem(mCurrentPage + 1);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(mCurrentPage - 1);
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.presidentsoffice.gov.lk/index.php/vaccination-dashboard/"));
                startActivity(i);
                ActivityCompat.finishAfterTransition(OnboardingActivity.this);
            }
        });
    }

    public void addDotsIndicators(int position) {
        dots = new TextView[7];
        mDotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimaryVariant));

            mDotLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicators(position);
            mCurrentPage = position;
            if (position == 0) {
                btnNext.setEnabled(true);
                btnBack.setEnabled(false);
                btnBack.setVisibility(View.INVISIBLE);

                btnNext.setText("NEXT");
                btnBack.setText("");
            } else if (position == dots.length - 1) {
                btnNext.setEnabled(true);
                btnBack.setEnabled(true);
                btnBack.setVisibility(View.VISIBLE);

                btnNext.setText("Finish");
                btnBack.setText("BACk");
            } else {
                btnNext.setEnabled(true);
                btnBack.setEnabled(true);
                btnBack.setVisibility(View.VISIBLE);

                btnNext.setText("NEXT");
                btnBack.setText("BACk");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}