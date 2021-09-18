package com.borntocode.covidshield;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.borntocode.covidshield.dto.NewsModel;
import com.borntocode.covidshield.managers.Methods;
import com.borntocode.covidshield.managers.RetrofitClient;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlideShowFragment extends Fragment {

    private ListView lstViewNews;
    private ArrayList<NewsModel.articles> newsList;
    private Methods methods;
    private Call<NewsModel> call;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_slide_show, container, false);

        lstViewNews = v.findViewById(R.id.lst_newsListView);

        methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
        call = methods.getAllData();

        init();

        lstViewNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsList.get(i).getUrl()));
                startActivity(intent);
            }
        });

        return v;
    }

    @SuppressLint("ResourceType")
    private void init() {
        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getResources().getString(R.color.colorPrimary)));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(@NonNull Call<NewsModel> call, @NonNull Response<NewsModel> response) {
                try {
                    Log.e("TAG", "onResponse: code : " + response.code());
                    newsList = new ArrayList<>();
                    for (int i = 0; i < response.body().getArticles().size(); i++) {
                        newsList.add(response.body().getArticles().get(i));
                    }
                    NewsAdapter newsAdapter = new NewsAdapter(getActivity(), newsList);
                    lstViewNews.setAdapter(newsAdapter);
                    pDialog.cancel();
                } catch (Exception ex) {
                    pDialog.cancel();
                    Log.e("TAG", "onResponse: " + ex.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                pDialog.cancel();
                Log.e("TAG", "onFailure:  " + t.getMessage());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}

class NewsAdapter extends ArrayAdapter<NewsModel.articles> {
    ArrayList<NewsModel.articles> newsList;
    Activity mActivity;

    public NewsAdapter(Activity activity, ArrayList<NewsModel.articles> newsList) {
        super(activity, R.layout.news_row_layout, newsList);
        this.mActivity = activity;
        this.newsList = newsList;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View row = inflater.inflate(R.layout.news_row_layout, null, true);

        TextView newsTitle = row.findViewById(R.id.txt_newsTitle);
        TextView newsUpdate = row.findViewById(R.id.txt_newsUpdateTime);
        ImageView newsImage = row.findViewById(R.id.img_newsImage);

        try {
            Glide.with(getContext()).load(newsList.get(position).getUrlToImage()).into(newsImage);
            newsTitle.setText(newsList.get(position).getTitle());
            if (newsList.get(position).getAuthor() != null) {
                newsUpdate.setText("- " + newsList.get(position).getAuthor());
            } else {
                newsUpdate.setText("- Unknown Author");
            }
        } catch (Exception ex) {
            Log.e("TAG", "getView: " + ex.getLocalizedMessage());
        }

        return row;
    }
}