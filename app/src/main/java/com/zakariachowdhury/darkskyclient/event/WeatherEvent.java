package com.zakariachowdhury.darkskyclient.event;

import com.zakariachowdhury.darkskyclient.model.Weather;

/**
 * Created by Zakaria Chowdhury on 8/12/17.
 */

public class WeatherEvent {
    private final Weather weather;

    public WeatherEvent(Weather weather) {
        this.weather = weather;
    }

    public Weather getWeather() {
        return weather;
    }
}
