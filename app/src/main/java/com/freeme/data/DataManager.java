package com.freeme.data;

import java.util.ArrayList;
import java.util.List;

import com.freeme.voiceassistant.R;

import android.content.Context;
import android.util.SparseArray;

/**
 * @author heqianqian on 20160106
 */
public class DataManager {

    public static final String SYSTEM_DEFAULT = "system.default.icon";
    public static final String SEPERATOR = ";";
    public static final int MAIN_SCENARIO = 0; //main
    public static final int CONTACT_SCENARIO = 1; //contact
    public static final int MUSIC_SCENARIO = 2; //music
    // public static final int ALARM_SCENARIO = 3; ////alarm
    public static final int WEATHER_SCENARIO = 4;//weather
    public static final int APPSTORE_SCENARIO = 5;//app
    public static final int SPECIAL_SR_SETTINGS = 6;//setting
    public static final int DATE_SCENARIO = 7;//calendar
    public static final int TRANSLATE_SCENARIO = 8;//translate
    public static final int SERACH_SCENARIO = 9;//search
    public static final int JOKE_SCENARIO = 10;//joke
    public static final int CHAT_SCENARIO = 11;//chat
    public static final int MAP_SCENARIO = 12;//map
    public static final int MAX_SCENARIOS = 13;//total count
    private SparseArray<List<SpeechData>> mScenarioDatas;
    private Context mContext;

    public DataManager(Context context) {
        mContext = context;
        mScenarioDatas = new SparseArray<List<SpeechData>>(MAX_SCENARIOS);
        intScenarioData();
    }

    public List<SpeechData> getScenarioData(int scene) {
        if (scene < MAIN_SCENARIO || scene >= MAX_SCENARIOS) {
            throw new IllegalArgumentException("illegal scene index!");
        }
        return mScenarioDatas.get(scene);
    }

    private void intScenarioData() {
        // initialize contact scenario data list
        List<SpeechData> contactdatas = new ArrayList<SpeechData>();
        contactdatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
                buildSpeechData("" + R.string.prompt_contact_grammar1, ""
                        + R.string.prompt_contact_grammar2, ""
                        + R.string.prompt_contact_grammar3, ""
                        + R.string.prompt_contact_grammar4, ""
                        + R.string.prompt_contact_grammar5)));
        mScenarioDatas.put(CONTACT_SCENARIO, contactdatas);
        // initialize music scenario data list
        List<SpeechData> musicdatas = new ArrayList<SpeechData>();
        musicdatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
                buildSpeechData("" + R.string.prompt_music_grammar1, ""
                        + R.string.prompt_music_grammar2, ""
                        + R.string.prompt_music_grammar3, ""
                        + R.string.prompt_music_grammar4)));
        mScenarioDatas.put(MUSIC_SCENARIO, musicdatas);
        // initialize alarm scenario data list
        /*
         * List<SpeechData> alarmdatas = new ArrayList<SpeechData>();
		 * alarmdatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
		 * buildSpeechData("" + R.string.prompt_alarm_grammar1, "" +
		 * R.string.prompt_alarm_grammar2, "" +
		 * R.string.prompt_alarm_grammar3))); mScenarioDatas.put(ALARM_SCENARIO,
		 * alarmdatas);
		 */
        // initialize weather scenario data list
        List<SpeechData> weatherdatas = new ArrayList<SpeechData>();
        weatherdatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
                buildSpeechData("" + R.string.prompt_weather_grammar1, ""
                        + R.string.prompt_weather_grammar2, ""
                        + R.string.prompt_weather_grammar3, ""
                        + R.string.prompt_weather_grammar4)));
        mScenarioDatas.put(WEATHER_SCENARIO, weatherdatas);
        // initialize application start up scenario data list
        List<SpeechData> appdatas = new ArrayList<SpeechData>();
        appdatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
                buildSpeechData("" + R.string.prompt_appstart_grammar1, ""
                                + R.string.prompt_appstart_grammar2, ""
                                + R.string.prompt_appstart_grammar3
                /* "" + R.string.prompt_appstart_grammar4 */)));
        mScenarioDatas.put(APPSTORE_SCENARIO, appdatas);
        // initialize setting start up scenario data list
        List<SpeechData> settingdatas = new ArrayList<SpeechData>();
        settingdatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
                buildSpeechData("" + R.string.prompt_settings_grammar1, ""
                        + R.string.prompt_settings_grammar2, ""
                        + R.string.prompt_settings_grammar3, ""
                        + R.string.prompt_settings_grammar4, ""
                        + R.string.prompt_settings_grammar5)));
        mScenarioDatas.put(SPECIAL_SR_SETTINGS, settingdatas);
        // initialize calendar scenario data list
        List<SpeechData> datedatas = new ArrayList<SpeechData>();
        datedatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
                buildSpeechData("" + R.string.prompt_date_grammar1, ""
                        + R.string.prompt_date_grammar2, ""
                        + R.string.prompt_date_grammar3, ""
                        + R.string.prompt_date_grammar5, ""
                        + R.string.prompt_date_grammar6, ""
                        + R.string.prompt_date_grammar7, ""
                        + R.string.prompt_date_grammar8)));
        mScenarioDatas.put(DATE_SCENARIO, datedatas);
        // initialize translate scenario data list
        List<SpeechData> translatedates = new ArrayList<SpeechData>();
        translatedates.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
                buildSpeechData("" + R.string.prompt_translate_grammar1, ""
                        + R.string.prompt_translate_grammar2, ""
                        + R.string.prompt_translate_grammar3)));
        mScenarioDatas.put(TRANSLATE_SCENARIO, translatedates);
        // initialize search scenario data list
        List<SpeechData> searchdatas = new ArrayList<SpeechData>();
        searchdatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
                buildSpeechData("" + R.string.prompt_search_grammar1, ""
                        + R.string.prompt_search_grammar2, ""
                        + R.string.prompt_chat_grammar3, ""
                        + R.string.prompt_search_grammar4, ""
                        + R.string.prompt_search_grammar5)));
        mScenarioDatas.put(SERACH_SCENARIO, searchdatas);
        // initialize joke scenario data list
//        List<SpeechData> jokedatas = new ArrayList<SpeechData>();
//        jokedatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
//                buildSpeechData("" + R.string.prompt_joke_grammar1,
//                        "" + R.string.prompt_joke_grammar2)));
//        mScenarioDatas.put(JOKE_SCENARIO, jokedatas);
        // initialize chat scenario data list
//		List<SpeechData> chatdatas = new ArrayList<SpeechData>();
//		chatdatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
//				buildSpeechData("" + R.string.prompt_chat_grammar1, ""
//						+ R.string.prompt_chat_grammar2, ""
//						+ R.string.prompt_chat_grammar3, ""
//						+ R.string.prompt_chat_grammar4, ""
//						+ R.string.prompt_chat_grammar5)));
        //mScenarioDatas.put(CHAT_SCENARIO, chatdatas);
        // initialize map scenario data list
        List<SpeechData> mapdatas = new ArrayList<SpeechData>();
        mapdatas.add(new SpeechData(SpeechData.PROMPT_WIDGET_MODE,
                buildSpeechData("" + R.string.prompt_map_grammar1, ""
                        + R.string.prompt_map_grammar2, ""
                        + R.string.prompt_map_grammar3)));
        mScenarioDatas.put(MAP_SCENARIO, mapdatas);

        // initialize main recognizer scenario data list
        List<SpeechData> mainDatas = new ArrayList<SpeechData>();

        // contact
        mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
                | SpeechData.SPECIAL_SR_CONTACT, buildSpeechData(
                "phone", ""
                        + R.string.sr_contact_title, ""
                        + R.string.sr_contact_hint)));
        // music
        mainDatas
                .add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
                        | SpeechData.SPECIAL_SR_MUSIC, buildSpeechData(
                        "music", ""
                                + R.string.sr_music_title, ""
                                + R.string.sr_music_hint)));
        /*
         * // alarm mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
		 * | SpeechData.SPECIAL_SR_ALARM,
		 * buildSpeechData("com.freeme.deskclock/.DeskClock","alarm", "" +
		 * R.string.sr_alarm_title, "" + R.string.sr_alarm_hint)));
		 */
        // weather
        mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
                | SpeechData.SPECIAL_SR_WEATHER, buildSpeechData(
                "weather", ""
                        + R.string.sr_weather_title, ""
                        + R.string.sr_weather_hint)));
        // start up application
        mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
                | SpeechData.SPECIAL_SR_APPSTORE, buildSpeechData(
                "app", "" + R.string.sr_app_title,
                "" + R.string.sr_app_hint)));
        // settings
        mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
                | SpeechData.SPECIAL_SR_SETTINGS, buildSpeechData(
                "setting", ""
                        + R.string.sr_settings_title, ""
                        + R.string.sr_settings_hint)));
        // date and time
        mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
                | SpeechData.SPECIAL_SR_DATE, buildSpeechData(
                "date", ""
                        + R.string.sr_date_title, "" + R.string.sr_date_hint)));
        mScenarioDatas.put(MAIN_SCENARIO, mainDatas);
        // translate
        mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
                | SpeechData.SPECIAL_SR_TRANSLATE, buildSpeechData(
                "translate", "" + R.string.sr_translate_title,
                "" + R.string.sr_translate_hint)));
        mScenarioDatas.put(MAIN_SCENARIO, mainDatas);
        // search
        mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
                | SpeechData.SPECIAL_SR_SEARCH, buildSpeechData(
                "search", "" + R.string.sr_search_title,
                "" + R.string.sr_search_hint)));
        mScenarioDatas.put(MAIN_SCENARIO, mainDatas);
       // joke
//        mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
//                | SpeechData.SPECIAL_SR_JOKE, buildSpeechData(
//                "joke", "" + R.string.sr_joke_title,
//                "" + R.string.sr_joke_hint)));
//        mScenarioDatas.put(MAIN_SCENARIO, mainDatas);
        // chat
//		mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
//				| SpeechData.SPECIAL_SR_CHAT, buildSpeechData(
//				"chat", "" + R.string.sr_chat_title,
//				"" + R.string.sr_chat_hint)));
        //mScenarioDatas.put(MAIN_SCENARIO, mainDatas);
        // map
        mainDatas.add(new SpeechData(SpeechData.SPECIAL_SR_ITEM_MODE
                | SpeechData.SPECIAL_SR_MAP, buildSpeechData(
                "map", "" + R.string.sr_map_title,
                "" + R.string.sr_map_hint)));
        mScenarioDatas.put(MAIN_SCENARIO, mainDatas);
    }

    private String buildSpeechData(String... strs) {
        String data = "";
        for (String str : strs) {
            data += str;
            data += SEPERATOR;
        }

        return data;
    }
}