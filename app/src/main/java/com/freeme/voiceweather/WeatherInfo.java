package com.freeme.voiceweather;

import java.util.Calendar;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.text.TextUtils;

public class WeatherInfo {
    public FiveDaysParent fiveDaysParent;
    public TodayParent todayParent;
    private boolean haveTodayDetail = false;

    public FiveDaysParent getFiveDaysParent() {
        return fiveDaysParent;
    }

    public void setFiveDaysParent(FiveDaysParent fiveDaysParent) {
        this.fiveDaysParent = fiveDaysParent;
    }

    public boolean isHaveTodayDetail() {
        return haveTodayDetail;
    }

    public void setHaveTodayDetail(boolean haveTodayDetail) {
        this.haveTodayDetail = haveTodayDetail;
    }

    public TodayParent getTodayParent() {
        return todayParent;
    }

    public void setTodayParent(TodayParent todayParent) {
        this.todayParent = todayParent;
    }

    public static WeatherInfo stringToWeatherInfo(String result, int code) {
        return parseJsonWeatherInfo(result, code);
    }

    public static WeatherInfo parseJsonWeatherInfo(String result, int code) {

        WeatherInfo weatherInfo = new WeatherInfo();
        try {
            JSONObject jsonObject = new JSONObject(result);
            String desc = "";
            int successCode = -1;
            if (jsonObject.has("desc") && !jsonObject.isNull("desc")) {
                desc = jsonObject.getString("desc");
            }
            if (jsonObject.has("result") && !jsonObject.isNull("result")) {
                successCode = jsonObject.getInt("result");
            }

            if ("成功".equals(desc) && successCode == 0) {
                String cityname = null;
                if (jsonObject.has("cityname") && !jsonObject.isNull("cityname")) {
                    cityname = jsonObject.getString("cityname");
                }
                TodayParent today = new TodayParent();
                long todayServerTime = System.currentTimeMillis();
                //todayweather
                if (jsonObject.has("todayWeather") && !jsonObject.isNull("todayWeather") && jsonObject.length() != 0) {
                    JSONObject todayJsonObject = jsonObject.getJSONObject("todayWeather");
                    CommonDaysWeather todayWeather = new CommonDaysWeather();
                    if (todayJsonObject.length() != 0) {
                        //code
                        todayWeather.setCode(code);
                        if (cityname != null) {
                            todayWeather.setCity(cityname);
                        } else {
                            return null;
                        }
                        parseTodayWeatherInfo(todayJsonObject, todayWeather);

                        today.setDesc(desc);
                        today.setResult(0);

                        //warn info
                        if (jsonObject.has("alarm") && !jsonObject.isNull("alarm") && jsonObject.length() != 0) {
                            JSONObject warnJsonObject = jsonObject.getJSONObject("alarm");
                            if (warnJsonObject.length() != 0) {
                                String temp = "";
                                //warn icon
                                if (warnJsonObject.has("alarm_img") && !warnJsonObject.isNull("alarm_img")) {
                                    temp = warnJsonObject.getString("alarm_img");
                                    if (TextUtils.isEmpty(temp)) {
                                        todayWeather.setWarnIcon("0000");
                                    } else {
                                        todayWeather.setWarnIcon(temp);
                                    }
                                    temp = "";
                                } else {
                                    todayWeather.setWarnIcon("0000");
                                }
                                //warn details
                                if (warnJsonObject.has("alarm_detail") && !warnJsonObject.isNull("alarm_detail")) {
                                    temp = warnJsonObject.getString("alarm_detail");
                                    if (TextUtils.isEmpty(temp)) {
                                        todayWeather.setmWarnInfo("");
                                    } else {
                                        todayWeather.setmWarnInfo(temp);
                                    }
                                    temp = "";
                                } else {
                                    todayWeather.setmWarnInfo("");
                                }

                            } else {
                                todayWeather.setWarnIcon("0000");
                                todayWeather.setmWarnInfo("");
                            }
                        } else {
                            todayWeather.setWarnIcon("0000");
                            todayWeather.setmWarnInfo("");
                        }
                        //PM2.5
                        if (jsonObject.has("pm25") && !jsonObject.isNull("pm25") && jsonObject.length() != 0) {
                            JSONObject pmJsonObject = jsonObject.getJSONObject("pm25");
                            if (pmJsonObject.length() != 0) {
                                parsePM25Info(todayWeather, pmJsonObject);
                            }
                        }
                        today.setTodayWeather(todayWeather);
                        weatherInfo.setTodayParent(today);
                        //fivedays weather
                        FiveDaysParent parent = new FiveDaysParent();
                        FiveDaysWeather fiveDaysWeather = new FiveDaysWeather();

                        if (todayWeather != null) {
                            //add the todayweather as the first item of fivedaysweather
                            fiveDaysWeather.weathers.add(todayWeather);

                            if (todayWeather.getWeatherDate() != null && !TextUtils.isEmpty(todayWeather.getWeatherDate())) {
//                				todayServerTime = WeatherUtils.getDateMilllion(todayWeather.getWeatherDate());

                                if (jsonObject.has("forecastWeather") && !jsonObject.isNull("forecastWeather") && jsonObject.length() != 0) {
                                    JSONObject fiveJsonObject = jsonObject.getJSONObject("forecastWeather");
                                    if (fiveJsonObject.length() != 0) {
                                        parseFiveWeatherInfo(fiveDaysWeather, fiveJsonObject, cityname, code, todayServerTime);
                                        parent.setDesc(desc);
                                        parent.setResult(successCode);
                                        parent.setFiveDaysWeather(fiveDaysWeather);

                                        weatherInfo.setFiveDaysParent(parent);
                                        return weatherInfo;
                                    }

                                }
                            } else {
                                return null;
                            }
                        }

                    } else {
                        return null;
                    }


                } else {
                    return null;
                }

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return weatherInfo;
    }

    public static void parsePM25Info(CommonDaysWeather todayWeather, JSONObject pmJsonObject) {
        String temp = "";
        try {
            //pm_day
            if (pmJsonObject.has("pm_day") && !pmJsonObject.isNull("pm_day")) {
                temp = pmJsonObject.getString("pm_day");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmPm25Day("");
                } else {
                    todayWeather.setmPm25Day(temp);
                }
                temp = "";
            } else {
                todayWeather.setmPm25Day("");
            }
            //pm_day
            if (pmJsonObject.has("pm_now") && !pmJsonObject.isNull("pm_now")) {
                temp = pmJsonObject.getString("pm_now");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmPm25Hour("");
                } else {
                    todayWeather.setmPm25Hour(temp);
                }
                temp = "";
            } else {
                todayWeather.setmPm25Hour("");
            }
            //pm_now
            if (pmJsonObject.has("pm_now") && !pmJsonObject.isNull("pm_now")) {
                temp = pmJsonObject.getString("pm_now");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmPm25Hour("");
                } else {
                    todayWeather.setmPm25Hour(temp);
                }
                temp = "";
            } else {
                todayWeather.setmPm25Hour("");
            }
            //pm_quality
            if (pmJsonObject.has("pm_quality") && !pmJsonObject.isNull("pm_quality")) {
                temp = pmJsonObject.getString("pm_quality");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmQuality("");
                } else {
                    todayWeather.setmQuality(temp);
                }
                temp = "";
            } else {
                todayWeather.setmQuality("");
            }
            //level
            if (pmJsonObject.has("level") && !pmJsonObject.isNull("level")) {
                temp = pmJsonObject.getString("level");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmQualityLevel("");
                } else {
                    todayWeather.setmQualityLevel(temp);
                }
                temp = "";
            } else {
                todayWeather.setmQualityLevel("");
            }
            //aqi
            if (pmJsonObject.has("aqi") && !pmJsonObject.isNull("aqi")) {
                temp = pmJsonObject.getString("aqi");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmAqi("");
                } else {
                    todayWeather.setmAqi(temp);
                }
                temp = "";
            } else {
                todayWeather.setmAqi("");
            }
            //pm10
            if (pmJsonObject.has("pm10") && !pmJsonObject.isNull("pm10")) {
                temp = pmJsonObject.getString("pm10");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmPm10("");
                } else {
                    todayWeather.setmPm10(temp);
                }
                temp = "";
            } else {
                todayWeather.setmPm10("");
            }
            //pm10_24h
            if (pmJsonObject.has("pm10_24h") && !pmJsonObject.isNull("pm10_24h")) {
                temp = pmJsonObject.getString("pm10_24h");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmPm10Day("");
                } else {
                    todayWeather.setmPm10Day(temp);
                }
                temp = "";
            } else {
                todayWeather.setmPm10Day("");
            }
            //pm10_24h
            if (pmJsonObject.has("pm10_24h") && !pmJsonObject.isNull("pm10_24h")) {
                temp = pmJsonObject.getString("pm10_24h");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmPm10Day("");
                } else {
                    todayWeather.setmPm10Day(temp);
                }
                temp = "";
            } else {
                todayWeather.setmPm10Day("");
            }
            //co
            if (pmJsonObject.has("co") && !pmJsonObject.isNull("co")) {
                temp = pmJsonObject.getString("co");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmCo("");
                } else {
                    todayWeather.setmCo(temp);
                }
                temp = "";
            } else {
                todayWeather.setmCo("");
            }
            //co_24h
            if (pmJsonObject.has("co_24h") && !pmJsonObject.isNull("co_24h")) {
                temp = pmJsonObject.getString("co_24h");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmCoDay("");
                } else {
                    todayWeather.setmCoDay(temp);
                }
                temp = "";
            } else {
                todayWeather.setmCoDay("");
            }
            //so2
            if (pmJsonObject.has("so2") && !pmJsonObject.isNull("so2")) {
                temp = pmJsonObject.getString("so2");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmSo2("");
                } else {
                    todayWeather.setmSo2(temp);
                }
                temp = "";
            } else {
                todayWeather.setmSo2("");
            }
            //so2_24h
            if (pmJsonObject.has("so2_24h") && !pmJsonObject.isNull("so2_24h")) {
                temp = pmJsonObject.getString("so2_24h");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmSo2Day("");
                } else {
                    todayWeather.setmSo2Day(temp);
                }
                temp = "";
            } else {
                todayWeather.setmSo2Day("");
            }
            //no2
            if (pmJsonObject.has("no2") && !pmJsonObject.isNull("no2")) {
                temp = pmJsonObject.getString("no2");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmNo2("");
                } else {
                    todayWeather.setmNo2(temp);
                }
                temp = "";
            } else {
                todayWeather.setmNo2("");
            }
            //no2_24h
            if (pmJsonObject.has("no2_24h") && !pmJsonObject.isNull("no2_24h")) {
                temp = pmJsonObject.getString("no2_24h");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmNo2Day("");
                } else {
                    todayWeather.setmNo2Day(temp);
                }
                temp = "";
            } else {
                todayWeather.setmNo2Day("");
            }
            //o3
            if (pmJsonObject.has("o3") && !pmJsonObject.isNull("o3")) {
                temp = pmJsonObject.getString("o3");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmO3("");
                } else {
                    todayWeather.setmO3(temp);
                }
                temp = "";
            } else {
                todayWeather.setmO3("");
            }
            //o3_24h
            if (pmJsonObject.has("o3_24h") && !pmJsonObject.isNull("o3_24h")) {
                temp = pmJsonObject.getString("o3_24h");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmO3Day("");
                } else {
                    todayWeather.setmO3Day(temp);
                }
                temp = "";
            } else {
                todayWeather.setmO3Day("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseFiveWeatherInfo(FiveDaysWeather fiveDaysWeather, JSONObject fiveJsonObject, String cityname, int code, long todayServerTime) {
        String temp = "";
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(todayServerTime);
        try {
            /*
			 * first day
			 */
            CommonDaysWeather commonDaysWeather1 = new CommonDaysWeather();
            commonDaysWeather1.setCode(code);
            if (cityname != null) {
                commonDaysWeather1.setCity(cityname);
            }
            //firTem
            if (fiveJsonObject.has("firTem") && !fiveJsonObject.isNull("firTem")) {
                temp = fiveJsonObject.getString("firTem");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather1.setTempLow(-1000);
                    commonDaysWeather1.setTempHign(-1000);
                } else {
                    try {
                        if (temp.indexOf("/") == -1) {
                            commonDaysWeather1.setTempLow(-1000);
                            commonDaysWeather1.setTempHign(-1000);
                        } else {
                            String[] temps = temp.split("/");
                            commonDaysWeather1.setTempLow(Integer.parseInt(temps[0].replace("℃", "")));
                            commonDaysWeather1.setTempHign(Integer.parseInt(temps[1].replace("℃", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather1.setTempLow(-1000);
                        commonDaysWeather1.setTempHign(-1000);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather1.setTempLow(-1000);
                commonDaysWeather1.setTempHign(-1000);
            }
            //firWea
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            if (fiveJsonObject.has("firWea") && !fiveJsonObject.isNull("firWea")) {
                temp = fiveJsonObject.getString("firWea");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather1.setWeatherDate("");
                    commonDaysWeather1.setWeatherDiff(0);
                    commonDaysWeather1.setWeatherDescription("na");
                } else {
                    try {
                        if (temp.indexOf(" ") == -1) {
                            commonDaysWeather1.setWeatherDate("");
                            commonDaysWeather1.setWeatherDiff(0);
                            commonDaysWeather1.setWeatherDescription("na");
                        } else {
                            String date = temp.split("\\ ")[0];
                            String month = date.split("月")[0];
                            String day = date.split("月")[1].replace("日", "");
                            int year;
//							int month;
//							int day;
                            if (todayServerTime == 0) {
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                year = c.get(Calendar.YEAR);
//								month = c.get(Calendar.MONTH) + 1;
//								day = c.get(Calendar.DAY_OF_MONTH);
                                //String date = temp.split("\\ ")[0];
                                month = date.split("月")[0];
                                day = date.split("月")[1].replace("日", "");
                            } else {
                                //c.setTimeInMillis(todayServerTime + 24*60*60*1000);
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                year = c.get(Calendar.YEAR);
//								month = mCalendar.get(Calendar.MONTH) + 1;
//								day = mCalendar.get(Calendar.DAY_OF_MONTH);
                            }

                            commonDaysWeather1.setWeatherDate("" + year + CommonDaysWeather.DATE_SEPERATOR + month + CommonDaysWeather.DATE_SEPERATOR + day);
                            commonDaysWeather1.setWeatherDiff(1);
                            commonDaysWeather1.setWeatherDescription(temp.split("\\ ")[1]);
                        }

                    } catch (Exception e) {
                        commonDaysWeather1.setWeatherDate("");
                        commonDaysWeather1.setWeatherDiff(0);
                        commonDaysWeather1.setWeatherDescription("na");
                    }
                }
                temp = "";
            } else {
                commonDaysWeather1.setWeatherDate("");
                commonDaysWeather1.setWeatherDescription("na");
            }
            //firIcon1
            if (fiveJsonObject.has("firIcon1") && !fiveJsonObject.isNull("firIcon1")) {
                temp = fiveJsonObject.getString("firIcon1");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather1.setIcon1(-1);
                } else {
                    try {
                        if (temp.indexOf(".gif") == -1) {
                            commonDaysWeather1.setIcon1(-1);
                        } else {
                            commonDaysWeather1.setIcon1(Integer.parseInt(temp.replace(".gif", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather1.setIcon1(-1);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather1.setIcon1(-1);
            }
            //firIcon2
            if (fiveJsonObject.has("firIcon2") && !fiveJsonObject.isNull("firIcon2")) {
                temp = fiveJsonObject.getString("firIcon2");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather1.setIcon2(-1);
                } else {
                    try {
                        if (temp.indexOf(".gif") == -1) {
                            commonDaysWeather1.setIcon2(-1);
                        } else {
                            commonDaysWeather1.setIcon2(Integer.parseInt(temp.replace(".gif", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather1.setIcon2(-1);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather1.setIcon2(-1);
            }
            //firWind
            if (fiveJsonObject.has("firWind") && !fiveJsonObject.isNull("firWind")) {
                temp = fiveJsonObject.getString("firWind");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather1.setWind("na");
                } else {
                    commonDaysWeather1.setWind(temp);
                }
                temp = "";
            } else {
                commonDaysWeather1.setWind("na");
            }

			/*
			 * second day
			 */
            CommonDaysWeather commonDaysWeather2 = new CommonDaysWeather();
            commonDaysWeather2.setCode(code);
            if (cityname != null) {
                commonDaysWeather2.setCity(cityname);
            }
            //senTem
            if (fiveJsonObject.has("senTem") && !fiveJsonObject.isNull("senTem")) {
                temp = fiveJsonObject.getString("senTem");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather2.setTempLow(-1000);
                    commonDaysWeather2.setTempHign(-1000);
                } else {
                    try {
                        if (temp.indexOf("/") == -1) {
                            commonDaysWeather2.setTempLow(-1000);
                            commonDaysWeather2.setTempHign(-1000);
                        } else {
                            String[] temps = temp.split("/");
                            commonDaysWeather2.setTempLow(Integer.parseInt(temps[0].replace("℃", "")));
                            commonDaysWeather2.setTempHign(Integer.parseInt(temps[1].replace("℃", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather2.setTempLow(-1000);
                        commonDaysWeather2.setTempHign(-1000);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather2.setTempLow(-1000);
                commonDaysWeather2.setTempHign(-1000);
            }
            //senWea
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            if (fiveJsonObject.has("senWea") && !fiveJsonObject.isNull("senWea")) {
                temp = fiveJsonObject.getString("senWea");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather2.setWeatherDate("");
                    commonDaysWeather2.setWeatherDiff(0);
                    commonDaysWeather2.setWeatherDescription("na");
                } else {
                    try {
                        if (temp.indexOf(" ") == -1) {
                            commonDaysWeather2.setWeatherDate("");
                            commonDaysWeather2.setWeatherDiff(0);
                            commonDaysWeather2.setWeatherDescription("na");
                        } else {
                            String date = temp.split("\\ ")[0];
                            String month = date.split("月")[0];
                            String day = date.split("月")[1].replace("日", "");
                            int year;
//							int month;
//							int day;
                            if (todayServerTime == 0) {
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                year = c.get(Calendar.YEAR);
//								month = c.get(Calendar.MONTH) + 1;
//								day = c.get(Calendar.DAY_OF_MONTH);
                                date = temp.split("\\ ")[0];
                                month = date.split("月")[0];
                                day = date.split("月")[1].replace("日", "");
                            } else {
                                //c.clear();
                                //c.setTimeInMillis(todayServerTime + 2*24*60*60*1000);
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                year = c.get(Calendar.YEAR);
//								month = c.get(Calendar.MONTH) + 1;
//								day = c.get(Calendar.DAY_OF_MONTH);
                            }

                            commonDaysWeather2.setWeatherDate("" + year + CommonDaysWeather.DATE_SEPERATOR + month + CommonDaysWeather.DATE_SEPERATOR + day);
                            commonDaysWeather2.setWeatherDiff(2);
                            commonDaysWeather2.setWeatherDescription(temp.split("\\ ")[1]);
                        }

                    } catch (Exception e) {
                        commonDaysWeather2.setWeatherDate("");
                        commonDaysWeather2.setWeatherDiff(0);
                        commonDaysWeather2.setWeatherDescription("na");
                    }
                }
                temp = "";
            } else {
                commonDaysWeather2.setWeatherDate("");
                commonDaysWeather2.setWeatherDescription("na");
            }
            //senIcon1
            if (fiveJsonObject.has("senIcon1") && !fiveJsonObject.isNull("senIcon1")) {
                temp = fiveJsonObject.getString("senIcon1");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather2.setIcon1(-1);
                } else {
                    try {
                        if (temp.indexOf(".gif") == -1) {
                            commonDaysWeather2.setIcon1(-1);
                        } else {
                            commonDaysWeather2.setIcon1(Integer.parseInt(temp.replace(".gif", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather2.setIcon1(-1);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather2.setIcon1(-1);
            }
            //senIcon2
            if (fiveJsonObject.has("senIcon2") && !fiveJsonObject.isNull("senIcon2")) {
                temp = fiveJsonObject.getString("senIcon2");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather2.setIcon2(-1);
                } else {
                    try {
                        if (temp.indexOf(".gif") == -1) {
                            commonDaysWeather2.setIcon2(-1);
                        } else {
                            commonDaysWeather2.setIcon2(Integer.parseInt(temp.replace(".gif", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather2.setIcon2(-1);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather2.setIcon2(-1);
            }
            //senWind
            if (fiveJsonObject.has("senWind") && !fiveJsonObject.isNull("senWind")) {
                temp = fiveJsonObject.getString("senWind");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather2.setWind("na");
                } else {
                    commonDaysWeather2.setWind(temp);
                }
                temp = "";
            } else {
                commonDaysWeather2.setWind("na");
            }

			/*
			 * third day
			 */
            CommonDaysWeather commonDaysWeather3 = new CommonDaysWeather();
            commonDaysWeather3.setCode(code);
            if (cityname != null) {
                commonDaysWeather3.setCity(cityname);
            }
            //thTem
            if (fiveJsonObject.has("thTem") && !fiveJsonObject.isNull("thTem")) {
                temp = fiveJsonObject.getString("thTem");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather3.setTempLow(-1000);
                    commonDaysWeather3.setTempHign(-1000);
                } else {
                    try {
                        if (temp.indexOf("/") == -1) {
                            commonDaysWeather3.setTempLow(-1000);
                            commonDaysWeather3.setTempHign(-1000);
                        } else {
                            String[] temps = temp.split("/");
                            commonDaysWeather3.setTempLow(Integer.parseInt(temps[0].replace("℃", "")));
                            commonDaysWeather3.setTempHign(Integer.parseInt(temps[1].replace("℃", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather3.setTempLow(-1000);
                        commonDaysWeather3.setTempHign(-1000);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather3.setTempLow(-1000);
                commonDaysWeather3.setTempHign(-1000);
            }
            //thWea
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            if (fiveJsonObject.has("thWea") && !fiveJsonObject.isNull("thWea")) {
                temp = fiveJsonObject.getString("thWea");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather3.setWeatherDate("");
                    commonDaysWeather3.setWeatherDiff(0);
                    commonDaysWeather3.setWeatherDescription("na");
                } else {
                    try {
                        if (temp.indexOf(" ") == -1) {
                            commonDaysWeather3.setWeatherDate("");
                            commonDaysWeather3.setWeatherDiff(0);
                            commonDaysWeather3.setWeatherDescription("na");
                        } else {
                            String date = temp.split("\\ ")[0];
                            String month = date.split("月")[0];
                            String day = date.split("月")[1].replace("日", "");
                            int year;
//							int month;
//							int day;
                            if (todayServerTime == 0) {
                                //c.clear();
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                year = c.get(Calendar.YEAR);
//								month = c.get(Calendar.MONTH) + 1;
//								day = c.get(Calendar.DAY_OF_MONTH);
                                date = temp.split("\\ ")[0];
                                month = date.split("月")[0];
                                day = date.split("月")[1].replace("日", "");
                            } else {
                                //c.clear();
                                //c.setTimeInMillis(todayServerTime + 3*24*60*60*1000);
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                year = c.get(Calendar.YEAR);
//								month = mCalendar.get(Calendar.MONTH) + 1;
//								day = mCalendar.get(Calendar.DAY_OF_MONTH);
                            }

                            commonDaysWeather3.setWeatherDate("" + year + CommonDaysWeather.DATE_SEPERATOR + month + CommonDaysWeather.DATE_SEPERATOR + day);
                            commonDaysWeather3.setWeatherDiff(3);
                            commonDaysWeather3.setWeatherDescription(temp.split("\\ ")[1]);
                        }

                    } catch (Exception e) {
                        commonDaysWeather3.setWeatherDate("");
                        commonDaysWeather3.setWeatherDiff(0);
                        commonDaysWeather3.setWeatherDescription("na");
                    }
                }
                temp = "";
            } else {
                commonDaysWeather3.setWeatherDate("");
                commonDaysWeather3.setWeatherDescription("na");
            }
            //thIcon1
            if (fiveJsonObject.has("thIcon1") && !fiveJsonObject.isNull("thIcon1")) {
                temp = fiveJsonObject.getString("thIcon1");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather3.setIcon1(-1);
                } else {
                    try {
                        if (temp.indexOf(".gif") == -1) {
                            commonDaysWeather3.setIcon1(-1);
                        } else {
                            commonDaysWeather3.setIcon1(Integer.parseInt(temp.replace(".gif", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather3.setIcon1(-1);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather3.setIcon1(-1);
            }
            //thIcon2
            if (fiveJsonObject.has("thIcon2") && !fiveJsonObject.isNull("thIcon2")) {
                temp = fiveJsonObject.getString("thIcon2");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather3.setIcon2(-1);
                } else {
                    try {
                        if (temp.indexOf(".gif") == -1) {
                            commonDaysWeather3.setIcon2(-1);
                        } else {
                            commonDaysWeather3.setIcon2(Integer.parseInt(temp.replace(".gif", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather3.setIcon2(-1);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather3.setIcon2(-1);
            }
            //thWind
            if (fiveJsonObject.has("thWind") && !fiveJsonObject.isNull("thWind")) {
                temp = fiveJsonObject.getString("thWind");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather3.setWind("na");
                } else {
                    commonDaysWeather3.setWind(temp);
                }
                temp = "";
            } else {
                commonDaysWeather3.setWind("na");
            }

			/*
			 * fourth day
			 */
            CommonDaysWeather commonDaysWeather4 = new CommonDaysWeather();
            commonDaysWeather4.setCode(code);
            if (cityname != null) {
                commonDaysWeather4.setCity(cityname);
            }
            //fouTem
            if (fiveJsonObject.has("fouTem") && !fiveJsonObject.isNull("fouTem")) {
                temp = fiveJsonObject.getString("fouTem");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather4.setTempLow(-1000);
                    commonDaysWeather4.setTempHign(-1000);
                } else {
                    try {
                        if (temp.indexOf("/") == -1) {
                            commonDaysWeather4.setTempLow(-1000);
                            commonDaysWeather4.setTempHign(-1000);
                        } else {
                            String[] temps = temp.split("/");
                            commonDaysWeather4.setTempLow(Integer.parseInt(temps[0].replace("℃", "")));
                            commonDaysWeather4.setTempHign(Integer.parseInt(temps[1].replace("℃", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather4.setTempLow(-1000);
                        commonDaysWeather4.setTempHign(-1000);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather4.setTempLow(-1000);
                commonDaysWeather4.setTempHign(-1000);
            }
            //fouWea
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            if (fiveJsonObject.has("fouWea") && !fiveJsonObject.isNull("fouWea")) {
                temp = fiveJsonObject.getString("fouWea");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather4.setWeatherDate("");
                    commonDaysWeather4.setWeatherDiff(0);
                    commonDaysWeather4.setWeatherDescription("na");
                } else {
                    try {
                        if (temp.indexOf(" ") == -1) {
                            commonDaysWeather4.setWeatherDate("");
                            commonDaysWeather4.setWeatherDiff(0);
                            commonDaysWeather4.setWeatherDescription("na");
                        } else {
                            String date = temp.split("\\ ")[0];
                            String month = date.split("月")[0];
                            String day = date.split("月")[1].replace("日", "");

                            int year;
//							int month;
//							int day;
                            if (todayServerTime == 0) {
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                year = c.get(Calendar.YEAR);
//								month = c.get(Calendar.MONTH) + 1;
//								day = c.get(Calendar.DAY_OF_MONTH);
                                date = temp.split("\\ ")[0];
                                month = date.split("月")[0];
                                day = date.split("月")[1].replace("日", "");
                            } else {
                                //c.clear();
                                //c.setTimeInMillis(todayServerTime + 4*24*60*60*1000);
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                year = c.get(Calendar.YEAR);
//								month = mCalendar.get(Calendar.MONTH) + 1;
//								day = mCalendar.get(Calendar.DAY_OF_MONTH);
                            }

                            commonDaysWeather4.setWeatherDate("" + year + CommonDaysWeather.DATE_SEPERATOR + month + CommonDaysWeather.DATE_SEPERATOR + day);
                            commonDaysWeather4.setWeatherDiff(4);
                            commonDaysWeather4.setWeatherDescription(temp.split("\\ ")[1]);
                        }

                    } catch (Exception e) {
                        commonDaysWeather4.setWeatherDate("");
                        commonDaysWeather4.setWeatherDiff(0);
                        commonDaysWeather4.setWeatherDescription("na");
                    }
                }
                temp = "";
            } else {
                commonDaysWeather4.setWeatherDate("");
                commonDaysWeather4.setWeatherDiff(0);
                commonDaysWeather4.setWeatherDescription("na");
            }
            //fouIcon1
            if (fiveJsonObject.has("fouIcon1") && !fiveJsonObject.isNull("fouIcon1")) {
                temp = fiveJsonObject.getString("fouIcon1");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather4.setIcon1(-1);
                } else {
                    try {
                        if (temp.indexOf(".gif") == -1) {
                            commonDaysWeather4.setIcon1(-1);
                        } else {
                            commonDaysWeather4.setIcon1(Integer.parseInt(temp.replace(".gif", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather4.setIcon1(-1);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather4.setIcon1(-1);
            }
            //fouIcon2
            if (fiveJsonObject.has("fouIcon2") && !fiveJsonObject.isNull("fouIcon2")) {
                temp = fiveJsonObject.getString("fouIcon2");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather4.setIcon2(-1);
                } else {
                    try {
                        if (temp.indexOf(".gif") == -1) {
                            commonDaysWeather4.setIcon2(-1);
                        } else {
                            commonDaysWeather4.setIcon2(Integer.parseInt(temp.replace(".gif", "")));
                        }

                    } catch (Exception e) {
                        commonDaysWeather4.setIcon2(-1);
                    }
                }
                temp = "";
            } else {
                commonDaysWeather4.setIcon2(-1);
            }
            //fouWind

            if (fiveJsonObject.has("fouWind") && !fiveJsonObject.isNull("fouWind")) {
                temp = fiveJsonObject.getString("fouWind");
                if (TextUtils.isEmpty(temp)) {
                    commonDaysWeather4.setWind("na");
                } else {
                    commonDaysWeather4.setWind(temp);
                }
                temp = "";
            } else {
                commonDaysWeather4.setWind("na");
            }
            fiveDaysWeather.weathers.add(commonDaysWeather1);
            fiveDaysWeather.weathers.add(commonDaysWeather2);
            fiveDaysWeather.weathers.add(commonDaysWeather3);
            fiveDaysWeather.weathers.add(commonDaysWeather4);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void parseTodayWeatherInfo(JSONObject todayJsonObject, CommonDaysWeather todayWeather) {
        String temp = "";
        try {
            //weather
            if (todayJsonObject.has("weather") && !todayJsonObject.isNull("weather")) {
                temp = todayJsonObject.getString("weather");
                if (TextUtils.isEmpty(temp)) {

                    todayWeather.setWeatherDescription("na");
                } else {
                    try {
                        if (temp.indexOf(" ") == -1) {

                            todayWeather.setWeatherDate("");
                            todayWeather.setWeatherDiff(0);
                            todayWeather.setWeatherDescription("na");
                        } else {

                            String date = temp.split("\\ ")[0];
                            String month = date.split("月")[0];
                            String day = date.split("月")[1].replace("日", "");
	    					/*the weather will update earlier of 24:00 at night
	    					 * so we should compare local time to time of server
	    					 * if they are not equal,we should add one day of local time as the first day, when
	    					 * local time is the last day of the year
	    					 */
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            int localyear = c.get(Calendar.YEAR);
                            int localmonth = c.get(Calendar.MONTH) + 1;
                            int localday = c.get(Calendar.DAY_OF_MONTH);
                            if (localmonth == 12 && localday == 31 && localmonth != Integer.parseInt(month) && localday != Integer.parseInt(day)) {
                                c.setTimeInMillis(c.getTimeInMillis() + 24 * 60 * 60 * 1000);
                                localyear = c.get(Calendar.YEAR);
                                localmonth = c.get(Calendar.MONTH) + 1;
                                localday = c.get(Calendar.DAY_OF_MONTH);
                                todayWeather.setWeatherDate("" + localyear + CommonDaysWeather.DATE_SEPERATOR + localmonth + CommonDaysWeather.DATE_SEPERATOR + localday);
                            } else {
                                todayWeather.setWeatherDate("" + localyear + CommonDaysWeather.DATE_SEPERATOR + month + CommonDaysWeather.DATE_SEPERATOR + day);
                            }
                            todayWeather.setWeatherDiff(0);
                            todayWeather.setWeatherDescription(temp.split("\\ ")[1]);

                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                        todayWeather.setWeatherDate("");
                        todayWeather.setWeatherDiff(0);
                        todayWeather.setWeatherDescription("na");
                    }
                }
                temp = "";
            } else {
                todayWeather.setWeatherDate("");
                todayWeather.setWeatherDiff(0);
                todayWeather.setWeatherDescription("na");
            }
            //wind
            if (todayJsonObject.has("wind") && !todayJsonObject.isNull("wind")) {
                temp = todayJsonObject.getString("wind");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setCurWind("na");
                    todayWeather.setWind("na");
                } else {
                    todayWeather.setCurWind(temp);
                    todayWeather.setWind(temp);
                }
                temp = "";
            } else {
                todayWeather.setCurWind("na");
                todayWeather.setWind("na");
            }
            //low/high temperature
            if (todayJsonObject.has("temperature") && !todayJsonObject.isNull("temperature")) {
                temp = todayJsonObject.getString("temperature");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setTempLow(-1000);
                    todayWeather.setTempHign(-1000);
                } else {
                    try {
                        if (temp.indexOf("/") == -1) {
                            todayWeather.setTempLow(-1000);
                            todayWeather.setTempHign(-1000);
                        } else {

                            String[] temps = temp.split("/");
                            todayWeather.setTempLow(Integer.parseInt(temps[0].replace("℃", "")));
                            todayWeather.setTempHign(Integer.parseInt(temps[1].replace("℃", "")));
                        }
                    } catch (Exception e) {
                        todayWeather.setTempLow(-1000);
                        todayWeather.setTempHign(-1000);
                    }
                }
                temp = "";
            } else {
                todayWeather.setTempLow(-1000);
                todayWeather.setTempHign(-1000);
            }
            //icon1
            if (todayJsonObject.has("icon1") && !todayJsonObject.isNull("icon1")) {
                temp = todayJsonObject.getString("icon1");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setIcon1(-1);
                } else {
                    try {
                        if (temp.indexOf(".gif") == -1) {
                            todayWeather.setIcon1(-1);
                        } else {

                            todayWeather.setIcon1(Integer.parseInt(temp.replace(".gif", "")));
                        }
                    } catch (Exception e) {
                        todayWeather.setIcon1(-1);
                    }
                }
                temp = "";
            } else {
                todayWeather.setIcon1(-1);
            }
            //icon2
            if (todayJsonObject.has("icon2") && !todayJsonObject.isNull("icon2")) {
                temp = todayJsonObject.getString("icon2");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setIcon2(-1);
                } else {
                    try {
                        if (temp.indexOf(".gif") == -1) {
                            todayWeather.setIcon2(-1);
                        } else {

                            todayWeather.setIcon2(Integer.parseInt(temp.replace(".gif", "")));

                        }
                    } catch (Exception e) {
                        todayWeather.setIcon2(-1);
                    }
                }
                temp = "";
            } else {
                todayWeather.setIcon2(-1);
            }
            //air
            if (todayJsonObject.has("air") && !todayJsonObject.isNull("air")) {
                temp = todayJsonObject.getString("air");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setCurAir("暂无");
                    todayWeather.setCurUPF("暂无");
                } else {
                    try {
                        if (temp.indexOf("；") == -1) {
                            todayWeather.setCurAir("暂无");
                            todayWeather.setCurUPF("暂无");
                        } else {

                            String[] airs = temp.split("；");
                            todayWeather.setCurAir(airs[0]);
                            todayWeather.setCurUPF(airs[1]);
                        }
                    } catch (Exception e) {
                        todayWeather.setCurAir("暂无");
                        todayWeather.setCurUPF("暂无");
                    }
                }
                temp = "";
            } else {
                todayWeather.setCurAir("暂无");
                todayWeather.setCurUPF("暂无");
            }
            //weather details
            if (todayJsonObject.has("detail") && !todayJsonObject.isNull("detail")) {
                temp = todayJsonObject.getString("detail");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setCurTemp(-1000);
                    todayWeather.setCurWind("na");
                    todayWeather.setCurHumidity("na");
                } else {
                    try {
                        if (temp.indexOf("；") == -1) {
                            todayWeather.setCurTemp(-1000);
                            todayWeather.setCurWind("na");
                            todayWeather.setCurHumidity("na");
                        } else {

                            String[] details = temp.split("；");
                            todayWeather.setCurTemp(Integer.parseInt(details[0].split("：")[2].replace("℃", "")));
                            todayWeather.setCurWind(details[1].split("：")[1]);
                            todayWeather.setCurHumidity(details[2].split("：")[1]);
                        }
                    } catch (Exception e) {
                        todayWeather.setCurTemp(-1000);
                        todayWeather.setCurWind("na");
                        todayWeather.setCurHumidity("na");
                    }
                }
                temp = "";
            } else {
                todayWeather.setCurTemp(-1000);
                todayWeather.setCurWind("na");
                todayWeather.setCurHumidity("na");
            }
            //weather comments
            if (todayJsonObject.has("comment") && !todayJsonObject.isNull("comment")) {
                temp = todayJsonObject.getString("comment");
                if (TextUtils.isEmpty(temp)) {
                    todayWeather.setmComment("暂无");
                } else {
                    todayWeather.setmComment(temp);

                }
                temp = "";
            } else {
                todayWeather.setmComment("暂无");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WeatherInfo readFromDatabase(ContentResolver resolver, int code) {
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.todayParent = TodayParent.readTodayParentFromDatabase(resolver, code);
        if (weatherInfo.todayParent != null) {
            int diff = diffNowDate(weatherInfo.todayParent.getTodayWeather().getWeatherDate());
            if (diff > -1 && diff < 5) {
                if (diff == 0) {
                    weatherInfo.setHaveTodayDetail(true);
                }
                weatherInfo.fiveDaysParent = FiveDaysParent.readFiveDaysParentFromDatabase(resolver, code);
                return weatherInfo;
            }
        }
        return null;
    }


    public static TodayParent readTodayFromDatabase(ContentResolver resolver, int code) {
        //WeatherInfo weatherInfo = new WeatherInfo();
        TodayParent parent = new TodayParent();
        parent = TodayParent.readTodayParentFromDatabase(resolver, code);
        if (parent != null) {
            return parent;
        }
        return null;
    }

    public static int diffNowDate(String preDate) {
        if (TextUtils.isEmpty(preDate)) {
            return -1;
        }
        String[] preDates = preDate.split(CommonDaysWeather.DATE_SEPERATOR);
        try {
            int preMonth = Integer.parseInt(preDates[0]);
            int preDay = Integer.parseInt(preDates[1]);
            return deltaDay(preMonth, preDay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int deltaDay(int weatherMonth, int weatherDay) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());

        Calendar old = Calendar.getInstance();
        old.setTimeInMillis(System.currentTimeMillis());
        old.set(Calendar.MONTH, weatherMonth - 1);
        old.set(Calendar.DAY_OF_MONTH, weatherDay);
        old.set(Calendar.HOUR_OF_DAY, 0);
        old.set(Calendar.MINUTE, 0);
        old.set(Calendar.SECOND, 0);

        return (int) ((now.getTimeInMillis() - old.getTimeInMillis()) / 1000 / 3600 / 24);
    }
}
