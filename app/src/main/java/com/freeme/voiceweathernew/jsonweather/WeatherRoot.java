package com.freeme.voiceweathernew.jsonweather;

import com.freeme.voiceweathernew.jsonweather.WeatherJsonResults;

import java.util.List;

/**
 * Created by heqianqian on 2016/6/21.
 */
public class WeatherRoot {
    private String status;
    private List<WeatherJsonResults> weather ;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public List<WeatherJsonResults> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherJsonResults> weather) {
        this.weather = weather;
    }



}
