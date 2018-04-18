package com.freeme.voiceweathernew;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.freeme.voiceweather.TodayParent;
import com.freeme.voiceweather.WeatherInfo;
import com.freeme.voiceweathernew.jsonweather.WeatherFuture;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DataUtilsNew {
    public static String TAG = "com.freeme.weather.DataUtils";
    public static final String[] WEEK = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    public static final String[] WEEK_MIUI = new String[]{"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    public static final String[] MONTH_MIUI = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};

    //*/freeme.heqianqian. 20160620 for weather new interface
    public static String connectToServerNew(int code, HttpURLConnection con) {

        String result = "";

        URL url = null;
        try {
            String url_string = "http://weather.yy845.com/weather/services/WeatherWebService/getWeather?cityId=" + code;
            url = new URL(url_string);
            Log.i(TAG, "url :" + url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            result = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
            bis.close();
            baf.clear();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        Log.i(TAG, "the update result is *" + result + "*;");
        return result;
    }//*/

    public static WeatherInfo handleData(String result, int code) {
        return WeatherInfo.stringToWeatherInfo(result, code);
    }

    //*/freeme.heqianqian get all WEATHERFUTURE
    public static List<WeatherFuture> handleData(String result) {
        return WeatherJsonParseFuture.returnfutures(result);
    }


    public static TodayParent readDataOnlyToday(ContentResolver resolver, int code) {
        return WeatherInfo.readTodayFromDatabase(resolver, code);
    }

    public static void closeCursor(Cursor paramCursor) {
        if (paramCursor != null)
            paramCursor.close();
    }

    public static boolean haveNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getActiveNetworkInfo();
        if (mobNetInfo != null) {
            return true;
        }
        return false;
    }

    public static String getTimeString(Context paramContext, long paramLong) {
        String str = "";
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(paramLong);
        if (android.text.format.DateFormat.is24HourFormat(paramContext)) {
            str = "" + mCalendar.get(Calendar.HOUR_OF_DAY);
        } else {
            int i = mCalendar.get(Calendar.HOUR);
            if (mCalendar.get(Calendar.AM_PM) == 0) {
                if (i < 10) {
                    str = str + "0";
                }
            } else {
                i = i + 12;
            }
            str = str + i;
        }
        str = str + mCalendar.get(Calendar.MINUTE);
        return str;
    }

    private static String getDateStringWithFormat(long paramLong, String str) {
        GregorianCalendar localGregorianCalendar = new GregorianCalendar();
        localGregorianCalendar.setTimeInMillis(paramLong);
        return new SimpleDateFormat(str).format(localGregorianCalendar.getTime());
    }


    public static int getIntById(Context context, int resId) {
        return context.getResources().getInteger(resId);
    }

    public static float getDimenById(Context context, int resId) {
        return context.getResources().getDimension(resId);
    }

    public static Boolean getBooleanById(Context context, int resId) {
        return context.getResources().getBoolean(resId);
    }

    public static String getStringById(Context context, int resId) {
        return context.getResources().getString(resId);
    }

}
