package com.freeme.voiceweathernew.jsonweather;

/**
 * Created by heqianqian on 2016/6/21.
 */
public class TodaySuggestion {

    private String sunset;

    private WeatherSuggestion suggestion;

    private String sunrise;

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public WeatherSuggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(WeatherSuggestion suggestion) {
        this.suggestion = suggestion;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }



}
