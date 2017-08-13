package com.zakariachowdhury.darkskyclient.service;

import android.content.Context;

import com.zakariachowdhury.darkskyclient.R;
import com.zakariachowdhury.darkskyclient.event.ErrorEvent;
import com.zakariachowdhury.darkskyclient.event.WeatherEvent;
import com.zakariachowdhury.darkskyclient.model.Weather;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Zakaria Chowdhury on 8/12/17.
 */

public class WeatherApi {
    private static final String BASE_URL = "https://api.darksky.net/forecast/e70540659086436bbc38ee21bc37cbd8/";
    private Retrofit retrofit;

    private Retrofit getRetrofit() {
        if (this.retrofit == null) {
            this.retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return this.retrofit;
    }

    public void getWeather(double lat, double lng) {
        WeatherService weatherService = getRetrofit().create(WeatherService.class);

        Call<Weather> weatherData = weatherService.getWeather(lat, lng);
        weatherData.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                Weather weather = response.body();

                if(weather != null) {
                    EventBus.getDefault().postSticky(new WeatherEvent(weather));
                } else {
                    EventBus.getDefault().postSticky(new ErrorEvent("No weather data found"));
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                EventBus.getDefault().postSticky(new ErrorEvent("Unable to get weather data"));
            }
        });
    }
}