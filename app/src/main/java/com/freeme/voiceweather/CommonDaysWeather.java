package com.freeme.voiceweather;


import android.database.Cursor;

import android.text.TextUtils;

public class CommonDaysWeather {

    public static final String DATE_SEPERATOR = "/";

    private long mId = -1;

    private int mCode;

    private String mCity;

    private String mWeatherDate;// format year/month/day,2014/2/3

    //private long mDateMillion = -1;

    private int mWeatherDiff;// between 0~4

    private String mWeatherDescription;

    private int mTempHign;

    private int mTempLow;

    private String mWind;

    private int mIcon1;

    private int mIcon2;


    private String mTempRange;

    private int mWeatherTypeIcon;

    private int mWeatherTypeBg;

    private int mLivingIcon;

    private int mVideoBackgroud;


    private int mCurTemp;

    private String mCurWind;

    private String mCurHumidity;

    private String mCurAir;

    private String mCurUPF;

    private String mComment;


    private String mWarnIcon;


    private String mWarnInfo;


    private String mAqi;//air quality index(AQI)

    private String mPm25Hour;//the pm2.5 of one hour

    private String mPm25Day;//the pm2.5 of one day

    private String mQuality;//the category of aqi,such as optimal\good\...

    private String mQualityLevel;//correspond to mQuality,such as one\two\three\fourth\five\six level

    private String mPm10;//the pm10 of one hour

    private String mPm10Day;//the pm10 of one day

    private String mCo;//Carbon monoxide 1 hour on average

    private String mCoDay;//Carbon monoxide 24 hour on average

    private String mSo2;//Sulfur dioxide 1 hour on average

    private String mSo2Day;//Sulfur dioxide 24 hour on average

    private String mNo2;//Nitrogen dioxide 1 hour on average

    private String mNo2Day;//Nitrogen dioxide 24 hour on average

    private String mO3;//Nitrogen dioxide 1 hour on average

    private String mO3Max;//1 hour on average of the max of Nitrogen dioxide

    private String mO3_H8;//Nitrogen dioxide 8 hour on average

    private String mO3Max_H8;//8 hour on average of the max of Nitrogen dioxide

    private String mO3Day;//Nitrogen dioxide 24 hour on average


    //not determine,no data

    private String mPrimaryPollutant;//the most important Pollutants

    private String mPositionName;//Monitoring stations

    private String mStationCode;//monitoring stations code

    private String mTimePoint;//release time


    public CommonDaysWeather() {

        mCurTemp = 1000;

        mLivingIcon = -1;

    }


    public long getId() {

        return mId;

    }


    public void setId(long id) {

        this.mId = id;

    }


    public int getCode() {

        return mCode;

    }


    public void setCode(int code) {

        this.mCode = code;

    }


    public String getCity() {

        return mCity;

    }


    public void setCity(String city) {

        this.mCity = city;

    }


    public String getWeatherDate() {

        return mWeatherDate;

    }


    public void setWeatherDate(String weatherDate) {

        this.mWeatherDate = weatherDate;

    }


    public int getWeatherDiff() {

        return mWeatherDiff;

    }


    public void setWeatherDiff(int weatherDiff) {

        this.mWeatherDiff = weatherDiff;

    }


    public String getWeatherDescription() {

        return mWeatherDescription;

    }


    public void setWeatherDescription(String weatherDescription) {

        this.mWeatherDescription = weatherDescription;

    }


    public int getTempHign() {

        return mTempHign;

    }


    public void setTempHign(int tempHign) {

        this.mTempHign = tempHign;

    }


    public int getTempLow() {

        return mTempLow;

    }


    public void setTempLow(int tempLow) {

        this.mTempLow = tempLow;

    }


    public String getWind() {

        return mWind;

    }


    public void setWind(String wind) {

        this.mWind = wind;

    }


    public int getIcon1() {

        return mIcon1;

    }


    public void setIcon1(int icon1) {

        this.mIcon1 = icon1;

    }


    public int getIcon2() {

        return mIcon2;

    }


    public void setIcon2(int icon2) {

        this.mIcon2 = icon2;

    }


    public void setTempRange(String tempRange) {

        this.mTempRange = tempRange;

    }


    public void setWeatherTypeIcon(int weatherType) {

        this.mWeatherTypeIcon = weatherType;

    }


    public void setWeatherTypeBg(int weatherTypeBg) {

        this.mWeatherTypeBg = weatherTypeBg;

    }


    public int getLivingIcon() {

        return mLivingIcon;

    }


    public void setLivingIcon(int livingIcon) {

        this.mLivingIcon = livingIcon;

    }


    public String getWarnIcon() {

        return mWarnIcon;

    }


    public void setWarnIcon(String warnIcon) {

        this.mWarnIcon = warnIcon;

    }


    public int getVideoBackgroud() {

        return mVideoBackgroud;

    }


    public void setVideoBackgroud(int videoBackgroud) {

        this.mVideoBackgroud = videoBackgroud;

    }


    public int getCurTemp() {

        return mCurTemp;

    }


    public void setCurTemp(int curTemp) {

        this.mCurTemp = curTemp;

    }


    public String getCurWind() {

        return mCurWind;

    }


    public void setCurWind(String curWind) {

        this.mCurWind = curWind;

    }


    public String getCurHumidity() {

        return mCurHumidity;

    }


    public void setCurHumidity(String curHumidity) {

        this.mCurHumidity = curHumidity;

    }


    public String getCurAir() {

        return mCurAir;

    }


    public void setCurAir(String curAir) {

        this.mCurAir = curAir;

    }


    public String getCurUPF() {

        return mCurUPF;

    }


    public void setCurUPF(String curUPF) {

        this.mCurUPF = curUPF;

    }


    public String getComment() {

        return mComment;

    }


    public void setmComment(String comment) {

        this.mComment = comment;

    }


    public String getmWarnInfo() {

        return mWarnInfo;

    }


    public void setmWarnInfo(String mWarnInfo) {

        this.mWarnInfo = mWarnInfo;

    }


    public String getmAqi() {

        return mAqi;

    }


    public void setmAqi(String mAqi) {

        this.mAqi = mAqi;

    }


    public String getmPm25Hour() {

        return mPm25Hour;

    }


    public void setmPm25Hour(String mPm25Hour) {

        this.mPm25Hour = mPm25Hour;

    }


    public String getmPm25Day() {

        return mPm25Day;

    }


    public void setmPm25Day(String mPm25Day) {

        this.mPm25Day = mPm25Day;

    }


    public String getmPm10() {

        return mPm10;

    }


    public void setmPm10(String mPm10) {

        this.mPm10 = mPm10;

    }


    public String getmPm10Day() {

        return mPm10Day;

    }


    public void setmPm10Day(String mPm10Day) {

        this.mPm10Day = mPm10Day;

    }


    public String getmQuality() {

        return mQuality;

    }


    public void setmQuality(String mQuality) {

        this.mQuality = mQuality;

    }


    public String getmCo() {

        return mCo;

    }


    public void setmCo(String mCo) {

        this.mCo = mCo;

    }


    public String getmCoDay() {

        return mCoDay;

    }


    public void setmCoDay(String mCoDay) {

        this.mCoDay = mCoDay;

    }


    public String getmSo2() {

        return mSo2;

    }


    public void setmSo2(String mSo2) {

        this.mSo2 = mSo2;

    }


    public String getmSo2Day() {

        return mSo2Day;

    }


    public void setmSo2Day(String mSo2Day) {

        this.mSo2Day = mSo2Day;

    }


    public String getmNo2() {

        return mNo2;

    }


    public void setmNo2(String mNo2) {

        this.mNo2 = mNo2;

    }


    public String getmNo2Day() {

        return mNo2Day;

    }


    public void setmNo2Day(String mNo2Day) {

        this.mNo2Day = mNo2Day;

    }


    public String getmO3() {

        return mO3;

    }


    public void setmO3(String mO3) {

        this.mO3 = mO3;

    }


    public String getmO3Max() {

        return mO3Max;

    }


    public void setmO3Max(String mO3Max) {

        this.mO3Max = mO3Max;

    }


    public String getmO3_H8() {

        return mO3_H8;

    }


    public void setmO3_H8(String mO3_H8) {

        this.mO3_H8 = mO3_H8;

    }


    public String getmO3Max_H8() {

        return mO3Max_H8;

    }


    public void setmO3Max_H8(String mO3Max_H8) {

        this.mO3Max_H8 = mO3Max_H8;

    }


    public String getmO3Day() {

        return mO3Day;

    }


    public void setmO3Day(String mO3Day) {

        this.mO3Day = mO3Day;

    }


    public String getmPrimaryPollutant() {

        return mPrimaryPollutant;

    }


    public void setmPrimaryPollutant(String mPrimaryPollutant) {

        this.mPrimaryPollutant = mPrimaryPollutant;

    }


    public String getmPositionName() {

        return mPositionName;

    }


    public void setmPositionName(String mPositionName) {

        this.mPositionName = mPositionName;

    }


    public String getmStationCode() {

        return mStationCode;

    }


    public void setmStationCode(String mStationCode) {

        this.mStationCode = mStationCode;

    }


    public String getmQualityLevel() {

        return mQualityLevel;

    }


    public void setmQualityLevel(String mQualityLevel) {

        this.mQualityLevel = mQualityLevel;

    }


    public String getmTimePoint() {

        return mTimePoint;

    }


    public void setmTimePoint(String mTimePoint) {

        this.mTimePoint = mTimePoint;

    }


    public static String splitString(String str) {

        if (TextUtils.isEmpty(str)) {

            return null;

        }

        try {

            return str.split(":")[1];

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }


    public static CommonDaysWeather stringToCommonDaysWeather(String info, int code) {

        if (TextUtils.isEmpty(info)) {

            return null;

        }

        String[] infos = info.split("\\|");

        CommonDaysWeather commonDaysWeather = new CommonDaysWeather();

        commonDaysWeather.setCode(code);

        try {

            String city = splitString(infos[0]);

            if (city != null) {

                commonDaysWeather.setCity(city);

            }

            String weather = splitString(infos[1]);

            if (weather != null) {

                try {

                    String date = weather.split("\\ ")[0];

                    String month = date.split("月")[0];

                    String day = date.split("月")[1].replace("日", "");

                    commonDaysWeather.setWeatherDate(month + CommonDaysWeather.DATE_SEPERATOR + day);

                    //commonDaysWeather.setWeatherDiff(0);

                    commonDaysWeather.setWeatherDescription(weather.split("\\ ")[1]);

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

            String temperature = splitString(infos[2]);

            if (temperature != null) {

                try {

                    String[] temps = temperature.split("/");

                    commonDaysWeather.setTempLow(Integer.parseInt(temps[0].replace("℃", "")));

                    commonDaysWeather.setTempHign(Integer.parseInt(temps[1].replace("℃", "")));

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

            String wind = splitString(infos[3]);

            if (wind != null) {

                commonDaysWeather.setWind(wind);

            }

            String icon1 = splitString(infos[4]);

            String icon2 = splitString(infos[5]);

            try {

                commonDaysWeather.setIcon1(Integer.parseInt(icon1.replace(".gif", "")));

                commonDaysWeather.setIcon2(Integer.parseInt(icon2.replace(".gif", "")));

            } catch (Exception e) {

                commonDaysWeather.setIcon1(-1);

                commonDaysWeather.setIcon2(-1);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return commonDaysWeather;

    }


//	public static void saveCommonDaysWeatherToDatabase(ContentResolver resolver, CommonDaysWeather commonDaysWeather) {

//		if (commonDaysWeather != null) {

//		    /*/Remarked by tyd Greg 2014-04-18,for avoid to use the today id to delete the fivedays' data

//			if (commonDaysWeather.getId() != -1) {

//				resolver.delete(ContentUris.withAppendedId(WeatherColumns.FIVEDAY_URI, commonDaysWeather.getId()),

//						null, null);

//			}

//			/*/

//			ContentValues values = new ContentValues();

//			values.put(WeatherColumns.CODE, commonDaysWeather.getCode());

//			values.put(WeatherColumns.CITY, commonDaysWeather.getCity());

//			values.put(WeatherColumns.WEATHER_DATE, commonDaysWeather.getWeatherDate());

//			values.put(WeatherColumns.WEATHER_DATE_DIFF, commonDaysWeather.getWeatherDiff());

//			values.put(WeatherColumns.WEATHER_DESCRIPTION, commonDaysWeather.getWeatherDescription());

//			values.put(WeatherColumns.TEMPRETURE_HIGH, commonDaysWeather.getTempHign());

//			values.put(WeatherColumns.TEMPRETURE_LOW, commonDaysWeather.getTempLow());

//			values.put(WeatherColumns.WIND, commonDaysWeather.getWind());

//			values.put(WeatherColumns.ICON1, commonDaysWeather.getIcon1());

//			values.put(WeatherColumns.ICON2, commonDaysWeather.getIcon2());

//			Uri newUri = resolver.insert(WeatherColumns.FIVEDAY_URI, values);

//			try {

//				commonDaysWeather.setId(Long.parseLong(newUri.getPathSegments().get(1)));

//			} catch (Exception e) {

//				e.printStackTrace();

//			}

//		}

//	}


//	public static CommonDaysWeather readCommonDaysWeatherFromDatabase(ContentResolver resolver, int code, int diff) {

//		Cursor cursor = resolver.query(WeatherColumns.FIVEDAY_URI, WeatherColumns.COMMON_QUERY,

//				"code = ? and weather_date_diff = ?", new String[] { code + "", diff + "" },

//				WeatherColumns.WEATHER_ORDER);

//		if ((cursor != null) && (cursor.getCount() > 0)) {

//			cursor.moveToFirst();

//			CommonDaysWeather commonDaysWeather = new CommonDaysWeather();

//			commonDaysWeather.setId(cursor.getLong(cursor.getColumnIndex(WeatherColumns.ID)));

//			commonDaysWeather.setCode(cursor.getInt(cursor.getColumnIndex(WeatherColumns.CODE)));

//			commonDaysWeather.setCity(cursor.getString(cursor.getColumnIndex(WeatherColumns.CITY)));

//			commonDaysWeather.setWeatherDate(cursor.getString(cursor.getColumnIndex(WeatherColumns.WEATHER_DATE)));

//			commonDaysWeather.setWeatherDiff(cursor.getInt(cursor.getColumnIndex(WeatherColumns.WEATHER_DATE_DIFF)));

//			commonDaysWeather.setWeatherDescription(cursor.getString(cursor

//					.getColumnIndex(WeatherColumns.WEATHER_DESCRIPTION)));

//			commonDaysWeather.setTempHign(cursor.getInt(cursor.getColumnIndex(WeatherColumns.TEMPRETURE_HIGH)));

//			commonDaysWeather.setTempLow(cursor.getInt(cursor.getColumnIndex(WeatherColumns.TEMPRETURE_LOW)));

//			commonDaysWeather.setWind(cursor.getString(cursor.getColumnIndex(WeatherColumns.WIND)));

//			commonDaysWeather.setIcon1(cursor.getInt(cursor.getColumnIndex(WeatherColumns.ICON1)));

//			commonDaysWeather.setIcon2(cursor.getInt(cursor.getColumnIndex(WeatherColumns.ICON2)));

//			closeCursor(cursor);

//			return commonDaysWeather;

//		}

//		closeCursor(cursor);

//		return null;

//	}


    public static void closeCursor(Cursor cursor) {

        if (cursor != null) {

            cursor.close();

        }

    }


}

