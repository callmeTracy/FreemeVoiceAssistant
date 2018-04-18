package com.freeme.voiceweather;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class describe some constant of the content provider.
 *
 * @author gejiqiang
 */
public class WeatherColumns implements BaseColumns {

    public static final Uri TODAY_URI = Uri.parse("content://com.freeme.provider.weather/todayinfo");
    public static final Uri FIVEDAY_URI = Uri.parse("content://com.freeme.provider.weather/fivedayinfo");
    public static final Uri CITY_URI = Uri.parse("content://com.freeme.provider.weather/cityinfo");
    public static final Uri WIDGET_URI = Uri.parse("content://com.freeme.provider.weather/widgetinfo");

    // for the table of common weather
    public static final String ID = "_id";
    public static final String CODE = "code";
    public static final String CITY = "city";
    public static final String PROVINCE = "province";
    public static final String ORDER = "num ASC";
    public static final String WEATHER_DATE = "weather_date";
    public static final String WEATHER_DATE_DIFF = "weather_date_diff";
    public static final String WEATHER_DESCRIPTION = "weather_description";
    public static final String TEMPRETURE_HIGH = "temp_hign";
    public static final String TEMPRETURE_LOW = "temp_low";
    public static final String WIND = "wind";
    public static final String ICON1 = "icon1";
    public static final String ICON2 = "icon2";
    // just for city.can sort the city
    public static final String NUM = "num";
    public static final String DISPLAY = "display";
    public static final String TIME = "time";
    public static final String LOCATION = "location";
    // just for the weather of today
    public static final String CURRENT_TEMPRETURE = "current_tempreture";
    public static final String CURRENT_WIND = "current_wind";
    public static final String CURRENT_HUMIDITY = "current_humidity";
    public static final String CURRENT_AIR = "current_air";
    public static final String CURRENT_UPF = "current_upf";
    public static final String COMMENT = "comment";

    public static final String WARN_ICON = "warn_icon";
    public static final String WARN_INFO = "warn_info";

    public static final String PM_API = "pm_api";
    public static final String PM25_Hour = "pm25_hour";
    public static final String PM25_DAY = "pm25_day";
    public static final String PM_QUALITY = "pm_quality";
    public static final String PM_QUALITY_LEVEL = "pm_quality_level";
    public static final String PM10_HOUR = "pm10_hour";
    public static final String PM10_DAY = "pm10_day";
    public static final String PM_CO = "pm_co";
    public static final String PM_CO_DAY = "pm_co_day";
    public static final String PM_SO2 = "pm_so2";
    public static final String PM_SO2_DAY = "pm_so2_day";
    public static final String PM_NO2 = "pm_no2";
    public static final String PM_NO2_DAY = "pm_no2_day";
    public static final String PM_O3 = "pm_o3";
    public static final String PM_O3_DAY = "pm_o3_day";
    public static final String PM_O3_MAX = "pm_o3_max";
    public static final String PM_O3_H8 = "pm_o3_h8";
    public static final String PM_O3_H8_MAX = "pm_o3_h8_max";
    public static final String PM_PRIMARY_POLLUTANT = "pm_primary_pollutant";
    public static final String PM_POSITION_NAME = "pm_position_name";
    public static final String PM_STATION_CODE = "pm_station_code";
    public static final String PM_TIME_POINT = "pm_time_point";


    public static final String WIDGET_ID = "widgetId";
    public static final String TYPE = "type";
    public static final String UPDATE_MILLS = "updateMills";
    // WEATHER_DATE_DIFF
    public static final String[] COMMON_QUERY = {_ID, CITY, CODE, WEATHER_DATE,
            WEATHER_DESCRIPTION, TEMPRETURE_HIGH, TEMPRETURE_LOW, WIND, ICON1, ICON2};

    public static final String[] TODAY_QUERY = {_ID, CITY, CODE, WEATHER_DATE, WEATHER_DATE_DIFF, WEATHER_DESCRIPTION,
            TEMPRETURE_HIGH, TEMPRETURE_LOW, WIND, ICON1, ICON2, CURRENT_TEMPRETURE, CURRENT_WIND, CURRENT_HUMIDITY,
            CURRENT_AIR, CURRENT_UPF, COMMENT, WARN_ICON, WARN_INFO, PM_API, PM25_Hour, PM25_DAY, PM_QUALITY, PM_QUALITY_LEVEL,
            PM10_HOUR, PM10_DAY, PM_CO, PM_CO_DAY, PM_SO2, PM_SO2_DAY, PM_NO2, PM_NO2_DAY, PM_O3, PM_O3_DAY,
            PM_O3_MAX, PM_O3_H8, PM_O3_H8_MAX, PM_PRIMARY_POLLUTANT, PM_POSITION_NAME, PM_STATION_CODE, PM_TIME_POINT};

    public static final String[] CITY_INDEX_QUERY = {_ID, CODE, CITY, PROVINCE, NUM, TIME, DISPLAY, LOCATION};

    public static final String[] CITY_QUERY = {_ID, CITY, PROVINCE, NUM, TIME, DISPLAY, CODE, WEATHER_DATE, WEATHER_DESCRIPTION,
            ICON1, ICON2, CURRENT_TEMPRETURE};

    public static final String WEATHER_ORDER = WEATHER_DATE_DIFF + " ASC";
    public static final String CITY_ORDER = NUM + " ASC";

    public static final String[] WIGET_QUERY = {_ID, WIDGET_ID, TYPE, CODE, UPDATE_MILLS};
    public static final String WIDGET_ORDER = WIDGET_ID + " ASC";

}
