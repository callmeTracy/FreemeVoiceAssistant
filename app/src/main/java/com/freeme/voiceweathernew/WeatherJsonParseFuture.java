package com.freeme.voiceweathernew;

import com.freeme.voiceweathernew.jsonweather.WeatherFuture;
import com.freeme.voiceweathernew.jsonweather.WeatherJsonResults;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by heqianqian on 2016/6/21.
 */
public class WeatherJsonParseFuture {

    public static List<WeatherFuture> returnfutures(String Results) {
        Gson gson = new Gson();
        List<WeatherFuture> futures = null;
        WeatherJsonResults WJR = gson.fromJson(Results, WeatherJsonResults.class);
        if (WJR.getFuture() != null) {
            futures = WJR.getFuture();
        }
        return futures;
    }
}
