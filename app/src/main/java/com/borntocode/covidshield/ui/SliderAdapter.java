package com.borntocode.covidshield.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.borntocode.covidshield.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderViewHolder> {

     List<Integer> imageList;

    public SliderAdapter(List<Integer> list){
        this.imageList = list;
    }

    @Override
    public SliderViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_row_layout,parent,false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SliderViewHolder viewHolder, int position) {
        viewHolder.imageView.setImageResource(imageList.get(position));
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    class SliderViewHolder extends  SliderViewAdapter.ViewHolder{

        ImageView imageView;

        public SliderViewHolder(View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.slider_row_image);
        }
    }
}
