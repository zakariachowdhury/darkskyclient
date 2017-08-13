package com.zakariachowdhury.darkskyclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.zakariachowdhury.darkskyclient.event.ErrorEvent;
import com.zakariachowdhury.darkskyclient.event.WeatherEvent;
import com.zakariachowdhury.darkskyclient.model.Currently;
import com.zakariachowdhury.darkskyclient.service.WeatherApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tempTextView)
    TextView tempTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        getLocation();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WeatherEvent weatherEvent) {
        Currently currently = weatherEvent.getWeather().getCurrently();
        tempTextView.setText(String.valueOf(Math.round(currently.getTemperature())));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ErrorEvent errorEvent) {
        Toast.makeText(this, errorEvent.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void getLocation() {
        WeatherApi weatherApi = new WeatherApi();
        weatherApi.getWeather(42.6340327, -83.2184753);
    }
}
