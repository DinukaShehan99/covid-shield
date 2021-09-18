package com.borntocode.covidshield.managers;

import com.borntocode.covidshield.dto.NewsModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Methods {
    @GET("v2/everything?q=covid-19&q=covid&q=coronavirus&apiKey=70c7242519b340a385842f7989b59e9d")
    Call<NewsModel> getAllData();
}
