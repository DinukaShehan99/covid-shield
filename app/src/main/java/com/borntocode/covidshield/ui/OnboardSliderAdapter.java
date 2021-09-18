package com.borntocode.covidshield.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.borntocode.covidshield.R;

public class OnboardSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public OnboardSliderAdapter(Context context){
        this.context = context;
    }

    public String[] slide_desc = {
            "You will be redirected to official website of President Office.\n\nClick the button framed in red to find daily updated vaccination centers in Sri-Lanka.\n",
            "Click the button framed in red to get the vaccination progress map of Sri-Lanka.\n",
            "Click the button framed in red to get the district wise vaccination summary of Sri-Lanka.\n",
            "You will be asked to select app for open the detailed reports.\n",
            "Here is a sample report of daily updated vaccination centers in Sri-Lanka.\n",
            "Here is a sample report of vaccination progress map of Sri-Lanka.\n",
            "Here is a sample report of district wise vaccination summary of Sri-Lanka.\n"
    };

    public int[] slide_images = {
            R.drawable.img_01,
            R.drawable.img_02,
            R.drawable.img_03,
            R.drawable.img_04,
            R.drawable.img_05,
            R.drawable.img_06,
            R.drawable.img_07
    };

    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.img_slideImg);
        TextView slideDesc = (TextView) view.findViewById(R.id.txt_slideDesc);

        slideImageView.setImageResource(slide_images[position]);
        slideDesc.setText(slide_desc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
