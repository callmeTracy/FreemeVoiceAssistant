package com.freeme.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.freeme.jsonparse.areas.TelephoneArea;
import com.google.gson.Gson;

public class ParseJsonUtil {

    public static String parseUnderstandResultanswer(String json) {
        String answer = "";
        try {
            JSONObject jo = new JSONObject(json);
            if (jo.getJSONArray("results") != null && jo.getJSONArray("results").getJSONObject(0) != null) {
                answer = jo.getJSONArray("results").getJSONObject(1).getJSONObject("object").getString("ANSWER");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return answer;
    }

    public static List<String> parseUnderstandResultbirthdays(String json) {
        List<String> birthdaydatas = new ArrayList<String>();
        try {
            JSONObject jo = new JSONObject(json);
            if (jo.getJSONArray("results") != null && jo.getJSONArray("results").getJSONObject(0) != null) {
                String intent = jo.getJSONArray("results").getJSONObject(1).getString("intent");
                birthdaydatas.add(intent);
                String focus = jo.getJSONArray("results").getJSONObject(1).getJSONObject("object").getString("focus");
                ;
                birthdaydatas.add(focus);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return birthdaydatas;
    }

    public static String parseUnderstanddescription(String json) {
        String answer = "";
        try {
            JSONObject jo = new JSONObject(json);
            if (jo.getJSONArray("data") != null && jo.getJSONArray("data").getJSONObject(0) != null) {
                answer = jo.getJSONArray("data").getJSONObject(0).getString("description");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return answer;
    }


    //*/start add tyd heqianqian for parsejson to get type area
    public static int parseUnderstandResult(String json) {
        String type;
        try {
            JSONObject jo = new JSONObject(json);
            if (jo.getJSONArray("results") != null && jo.getJSONArray("results").getJSONObject(0) != null) {
                type = jo.getJSONArray("results").getJSONObject(0).getString("domain");
                if (type != null && !" ".equals(type)) {
                    if (ASRHelper.TELEPHONE_AREA.equals(type)) {
                        return ASRHelper.TELEPHONE_NO;
                    } else if (ASRHelper.MESSAGE_AREA.equals(type)) {
                        return ASRHelper.MESSAGE_NO;
                    } else if (ASRHelper.CONTACTS_AREA.equals(type)) {
                        return ASRHelper.CONTACTS_NO;
                    } else if (ASRHelper.SETTING_AREA.equals(type)) {
                        return ASRHelper.SETTING_NO;
                    } else if (ASRHelper.MUSIC_AREA.equals(type)) {
                        return ASRHelper.MUSIC_NO;
                    } else if (ASRHelper.ALARM_AREA.equals(type)) {
                        return ASRHelper.ALARM_NO;
                    } else if (ASRHelper.APP_AREA.equals(type)) {
                        return ASRHelper.APP_NO;
                    } else if (ASRHelper.CALENDAR_AREA.equals(type)) {
                        return ASRHelper.CALENDAR_NO;
                    } else if (ASRHelper.WEATHER_AREA.equals(type)) {
                        return ASRHelper.WEATHER_NO;
                    } else if (ASRHelper.TRANSLATION_AREA.equals(type)) {
                        return ASRHelper.TRANSLATION_NO;
                    } else if (ASRHelper.SEARCH_AREA.equals(type)) {
                        return ASRHelper.SEARCH_NO;
                    } else if (ASRHelper.JOKE_AREA.equals(type)) {
                        return ASRHelper.JOKE_NO;
                    } else if (ASRHelper.PERSON_AREA.equals(type)) {
                        return ASRHelper.PERSON_NO;
                    } else if (ASRHelper.MAP_AREA.equals(type)) {
                        return ASRHelper.MAP_NO;
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ASRHelper.ASR_ERROR_NORESULTS;
    }


    public static boolean searchList(ArrayList list, String subStr) {
        if (list == null || list.size() < 1 || subStr == null) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            if (subStr.equals(list.get(i))) {
                return true;
            }
        }
        return false;
    }


}
