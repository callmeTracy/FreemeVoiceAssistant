package com.freeme.voiceweathernew.jsonweather;

import com.freeme.voiceweathernew.jsonweather.Car_washing;
import com.freeme.voiceweathernew.jsonweather.Dressing;
import com.freeme.voiceweathernew.jsonweather.Flu;
import com.freeme.voiceweathernew.jsonweather.Sport;
import com.freeme.voiceweathernew.jsonweather.Travel;
import com.freeme.voiceweathernew.jsonweather.Uv;

/**
 * Created by heqianqian on 2016/6/21.
 */
public class WeatherSuggestion {

    private Dressing dressing;

    private Car_washing car_washing;

    private Uv uv;

    private Flu flu;

    private Sport sport;

    private Travel travel;

    public Dressing getDressing() {
        return dressing;
    }

    public void setDressing(Dressing dressing) {
        this.dressing = dressing;
    }

    public Car_washing getCar_washing() {
        return car_washing;
    }

    public void setCar_washing(Car_washing car_washing) {
        this.car_washing = car_washing;
    }

    public Uv getUv() {
        return uv;
    }

    public void setUv(Uv uv) {
        this.uv = uv;
    }

    public Flu getFlu() {
        return flu;
    }

    public void setFlu(Flu flu) {
        this.flu = flu;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }



}
