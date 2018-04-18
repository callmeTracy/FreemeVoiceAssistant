package com.freeme.voiceweathernew.jsonweather;

import com.freeme.voiceweathernew.jsonweather.NowWeather;
import com.freeme.voiceweathernew.jsonweather.TodaySuggestion;
import com.freeme.voiceweathernew.jsonweather.WeatherFuture;

import java.util.List;

/**
 * Created by heqianqian on 2016/6/21.
 */
public class WeatherJsonResults {

    private String city_id;

    private String city_name;

    private String last_update;

    private NowWeather now;

    private List<WeatherFuture> future ;

    private TodaySuggestion today;

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public NowWeather getNow() {
        return now;
    }

    public void setNow(NowWeather now) {
        this.now = now;
    }

    public List<WeatherFuture> getFuture() {
        return future;
    }

    public void setFuture(List<WeatherFuture> future) {
        this.future = future;
    }

    public TodaySuggestion getToday() {
        return today;
    }

    public void setToday(TodaySuggestion today) {
        this.today = today;
    }





}
