package com.zakariachowdhury.darkskyclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zakariachowdhury.darkskyclient.event.ErrorEvent;
import com.zakariachowdhury.darkskyclient.event.WeatherEvent;
import com.zakariachowdhury.darkskyclient.model.Currently;
import com.zakariachowdhury.darkskyclient.service.WeatherApi;
import com.zakariachowdhury.darkskyclient.util.WeatherIconUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.temperatureContainer)
    LinearLayout temperatureContainer;

    @BindView(R.id.temperatureTextView)
    TextView temperatureTextView;

    @BindView(R.id.summaryTextView)
    TextView summaryTextView;

    @BindView(R.id.iconImageView)
    ImageView iconImageView;

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
        temperatureTextView.setText(String.valueOf(Math.round(currently.getTemperature())));
        summaryTextView.setText(currently.getSummary());
        temperatureContainer.setVisibility(View.VISIBLE);

        String icon = currently.getIcon();
        if (WeatherIconUtil.ICONS.get(icon) != null) {
            iconImageView.setImageResource(WeatherIconUtil.ICONS.get(icon));
        }
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
