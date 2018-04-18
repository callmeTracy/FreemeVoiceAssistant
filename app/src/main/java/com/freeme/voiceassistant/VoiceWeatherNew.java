package com.freeme.voiceassistant;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.freeme.data.SpeechData;
import com.freeme.util.Util;
import com.freeme.voiceweather.DataHelper;
import com.freeme.voiceweather.WeatherInfo;
import com.freeme.voiceweathernew.DataUtilsNew;
import com.freeme.voiceweathernew.jsonweather.WeatherFuture;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by heqianqian on 2016/6/20.
 */
public class VoiceWeatherNew {
    private Context mContext;
    private Calendar mCalendar;
    private String mAllData;
    private String city;
    private ASRRequestor.onRecongnitionListener mListener;
    private ArrayList<String> cityList = new ArrayList<String>();
    private String mWeatherTime = null;

    public VoiceWeatherNew() {
        // TODO Auto-generated constructor stub
    }

    public VoiceWeatherNew(Context context, String city, String dateorig, ASRRequestor.onRecongnitionListener l) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.mListener = l;
        this.city = city;
        this.mWeatherTime = dateorig;
        filterCity(dateorig);
        updateState();
    }

    public ArrayList<String> filterCity(String cityName) {
        if (cityName.contains(mContext.getString(R.string.voice_weather_today))) {
            mWeatherTime = mContext.getString(R.string.voice_weather_today);
            cityList.add(cityName.replace(mContext.getString(R.string.voice_weather_today), ""));
        } else if (cityName.contains(mContext.getString(R.string.voice_weather_tomorrow))) {
            mWeatherTime = mContext.getString(R.string.voice_weather_tomorrow);
            cityList.add(cityName.replace(mContext.getString(R.string.voice_weather_tomorrow), ""));
        } else if (cityName.contains(mContext.getString(R.string.voice_weather_after_tomorrow))) {
            mWeatherTime = mContext.getString(R.string.voice_weather_after_tomorrow);
            cityList.add(cityName.replace(mContext.getString(R.string.voice_weather_after_tomorrow), ""));
        } else {
            cityList.add(cityName);
        }

        return cityList;

    }

    public void updateState() {
        queryWeather(city);
    }

    public void setCity(int index) {
        queryWeather(city);
    }

    public ArrayList<String> getCity() {
        return cityList;
    }

    public String getAllData() {
        return mAllData;
    }

    public void queryWeather(String mCity) {
        String mCode = null;
        Cursor mCursor = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                DataHelper.DATABASE_PATH + DataHelper.DATABASE_NAME, null,
                SQLiteDatabase.OPEN_READONLY);
        try {
            mCursor = db.rawQuery("select * from city where city = ?",
                    new String[]{mCity});
        } catch (Exception e) {

        }
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                mCode = mCursor.getString(1);
            }
        }
        if (mCode != null) {
            if (Util.isNetworkAvailable(mContext)) {
                weatherAsync weather = new weatherAsync();
                weather.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mCode);
            } else {
                mListener.onResponseSpeechResult(
                        new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mContext.getString(R.string.voice_weather_network)), false);
                mListener.onSpeak(mContext.getString(R.string.voice_weather_network));
            }
        } else {
            mListener.onResponseSpeechResult(
                    new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mContext.getString(R.string.voice_weather_city)), false);
            mListener.onSpeak(mContext.getString(R.string.voice_weather_city));
        }
        if (mCursor != null) {
            db.close();
            mCursor.close();
        }
    }

    public String qureyTodayWeather(WeatherInfo info) {
        String mWind = null;
        String mWeatherDate = null;
        String mWeatherDescription = null;
        int tempLow = 0;
        int tempHign = 0;
        int mIcon1 = 0;
        int mIcon2 = 0;
        String mDay = null;
        if (mWeatherTime == null) {
            info.todayParent.getTodayWeather().getCode();
            mDay = mContext.getResources().getString(
                    R.string.voice_weather_today);

            mWind = info.todayParent.getTodayWeather().getWind();
            mWeatherDate = info.todayParent.getTodayWeather()
                    .getWeatherDate();
            tempLow = info.todayParent.getTodayWeather().getTempLow();
            tempHign = info.todayParent.getTodayWeather().getTempHign();
            mIcon1 = info.todayParent.getTodayWeather().getIcon1();
            mIcon2 = info.todayParent.getTodayWeather().getIcon2();
            mWeatherDescription = info.todayParent.getTodayWeather()
                    .getWeatherDescription();
        } else {
            if (mWeatherTime.equals(mContext.getResources().getString(
                    R.string.voice_weather_tomorrow))) {
                mDay = mContext.getResources().getString(
                        R.string.voice_weather_tomorrow);
                tempHign = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(1).getTempHign();
                tempLow = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(1).getTempLow();
                mWind = info.fiveDaysParent.getFiveDaysWeather().getWeathers()
                        .get(1).getWind();
                mWeatherDescription = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(1).getWeatherDescription();
                mWeatherDate = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(1).getWeatherDate();
                mIcon1 = info.fiveDaysParent.getFiveDaysWeather().getWeathers()
                        .get(1).getIcon1();
                mIcon2 = info.fiveDaysParent.getFiveDaysWeather().getWeathers()
                        .get(1).getIcon2();
            } else if (mWeatherTime.equals(mContext.getResources()
                    .getString(R.string.voice_weather_after_tomorrow))) {
                mDay = mContext.getResources().getString(
                        R.string.voice_weather_after_tomorrow);
                tempHign = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(2).getTempHign();
                tempLow = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(2).getTempLow();
                mWind = info.fiveDaysParent.getFiveDaysWeather().getWeathers()
                        .get(2).getWind();
                mWeatherDescription = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(2).getWeatherDescription();
                mWeatherDate = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(2).getWeatherDate();
                mIcon1 = info.fiveDaysParent.getFiveDaysWeather().getWeathers()
                        .get(2).getIcon1();
                mIcon2 = info.fiveDaysParent.getFiveDaysWeather().getWeathers()
                        .get(2).getIcon2();
            } else if (mWeatherTime.equals(mContext.getResources()
                    .getString(R.string.voice_weather_today))) {
                info.todayParent.getTodayWeather().getCode();
                mDay = mContext.getResources().getString(
                        R.string.voice_weather_today);
                mWind = info.todayParent.getTodayWeather().getWind();
                mWeatherDate = info.todayParent.getTodayWeather()
                        .getWeatherDate();
                tempLow = info.todayParent.getTodayWeather().getTempLow();
                tempHign = info.todayParent.getTodayWeather().getTempHign();
                mIcon1 = info.todayParent.getTodayWeather().getIcon1();
                mIcon2 = info.todayParent.getTodayWeather().getIcon2();
                mWeatherDescription = info.todayParent.getTodayWeather()
                        .getWeatherDescription();
            }
        }

        String mCity = info.todayParent.getTodayWeather().getCity();
        mCalendar = Calendar.getInstance();
        if (mWeatherDate != null && !"".equals(mWeatherDate)) {
            String[] dates = mWeatherDate.split("/");
            mCalendar.set(Integer.parseInt(dates[0]),
                    Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]));
            String weeks = mContext.getResources().getStringArray(R.array.week_day)[mCalendar
                    .get(Calendar.DAY_OF_WEEK) - 1];
            String date = weeks + "\t\t" + mWeatherDate;
            String mTemp = tempLow + "°/" + tempHign + "°";
            String mHignText = mContext.getResources().getString(
                    R.string.voice_weather_hign_temp);
            String mLowText = mContext.getResources().getString(
                    R.string.voice_weather_low_temp);
            String mTempText = mContext.getResources().getString(
                    R.string.voice_weather_temp);
            mListener.onSpeak(mCity + mDay + mWeatherDescription + mWind
                    + mHignText + tempHign + mTempText + mLowText + tempLow
                    + mTempText);
            String todayData = mCity + "," + mDay + "," + mWeatherDescription + ","
                    + mWind + "," + mTemp + "," + date + "," + mIcon1 + ","
                    + mIcon2;
            return todayData;
        } else {
            return "";
        }


    }

    public String queryFiveWeather(WeatherInfo info) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> maps;
        String fiveData = "";
        int daysParent = info.fiveDaysParent.getFiveDaysWeather().getWeathers()
                .size();
        for (int i = 0; i < daysParent; i++) {
            if (info.fiveDaysParent.getFiveDaysWeather().getWeathers().get(i) != null) {
                int tempHign = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(i).getTempHign();
                int tempLow = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(i).getTempLow();
                String weatherDate = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(i).getWeatherDate();
                int mIcon1 = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(i).getIcon1();
                int mIcon2 = info.fiveDaysParent.getFiveDaysWeather()
                        .getWeathers().get(i).getIcon2();

                String mTemp = tempLow + "°/" + tempHign + "°";
                mCalendar = Calendar.getInstance();
                String[] dates = weatherDate.split("/");
                mCalendar.set(Integer.parseInt(dates[0]),
                        Integer.parseInt(dates[1]) - 1,
                        Integer.parseInt(dates[2]));
                String weeks = mContext.getResources().getStringArray(
                        R.array.week_day)[mCalendar.get(Calendar.DAY_OF_WEEK) - 1];
//                String date = weeks + "\t" + weatherDate;
                fiveData = fiveData + "," + weeks + "," + weatherDate + "," + mIcon1 + "," + mIcon2
                        + "," + mTemp;
            }
        }
        return fiveData;
    }


    class weatherAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... arg0) {
            HttpURLConnection con = null;
            String result = DataUtilsNew.connectToServerNew(
                    Integer.parseInt(arg0[0]), con);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            List<WeatherFuture> futures = DataUtilsNew.handleData(result);
            mListener.onResponseSpeechResult(
                    new SpeechData(SpeechData.RESPONSE_TEXT_MODE, result.toString()), false);
            mListener.onSpeak(mContext.getString(R.string.voice_weather_city));
//            String todayData = qureyTodayWeather(info);
//            if(info!=null&&!"".equals(todayData)){
//                String fiveData = queryFiveWeather(info);
//                mAllData = todayData + fiveData;
//                mListener.onResponseSpeechResult(
//                        new SpeechData(VoiceWeatherNew.this),false);
//            }else{
//                mListener.onResponseSpeechResult(
//                        new SpeechData(SpeechData.RESPONSE_TEXT_MODE,mContext.getString(R.string.voice_weather_city)),false);
//                mListener.onSpeak(mContext.getString(R.string.voice_weather_city));
//            }
        }
    }

}
