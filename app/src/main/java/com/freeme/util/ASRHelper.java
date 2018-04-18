package com.freeme.util;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.freeme.jsonparse.areas.TelephoneArea;
import com.freeme.voiceassistant.R.string;
import com.google.gson.Gson;

/**
 * @author heqianqian on 20160106
 */
public class ASRHelper {
    public static final String ACTION_TTS = "android.intent.action.freeme.TTS.voice";
    public static final String TTS_SPEAK_TEXT_KEY = "freeme.TTS.speak.text";
    public static final int TTS_SPEAK = 1;
    public static final int TTS_CANCEL = 2;
    public static final String SERVICE_PACKAGE = "com.freeme.voiceassistant";
    public static final String EXTRA_SOUND_START = "sound_start";
    public static final String EXTRA_SOUND_END = "sound_end";
    public static final String EXTRA_SOUND_SUCCESS = "sound_success";
    public static final String EXTRA_SOUND_ERROR = "sound_error";
    public static final String EXTRA_SOUND_CANCEL = "sound_cancel";
    public static final String EXTRA_LANGUAGE = "language";
    public static final String EXTRA_SAMPLE = "sample";
    public static final String EXTRA_NLU = "nlu";
    public static final String EXTRA_VAD = "vad";
    public static final String EXTRA_PROP = "prop";
    public static final String EXTRA_PROP_VALUE = "10003,10008,100014,100016";

    public static final int ID_SCENE = 1;
    public static final int SMS_SCENE = 2;


    public static final String PARAMS_NLU = "enable";
    public static final String PARAMS_LANGUAGE = "cmn-Hans-CN";
    public static final String EXTRA_VAD_SEARCH = "search";
    public static final String EXTRA_VAD_INPUT = "input";
    public static final int PARAMS_SAMPLE = 16000;
    public static final String SLOT_DATA = "slot-data";
    public static final String EXTRA_OUT_FILE = "outfile";
    public static final String EXTRA_AUDIO_SOURCE = "audio.source";
    public static final String EXTRA_GRAMMER = "grammar";
    public static final String EXTRA_GRAMMER_VALUE = "assets:///baidu_speech_grammar.bsg";
    public static final String EXTRA_LICENSE_FILE_PATH = "license";
    public static final String EXTRA_LICENSE_OUT_FILE = "sdcard/outfile.pcm";
    public static final String EXTRA_LICENSE_FILE_PATH_VALUE = "license-android-easr_freeme.so";
    public static final int ASR_ERROR_START_OK = 0;
    public static final int ASR_ERROR_AUDIO_PROBLEM = 1;
    public static final int ASR_ERROR_NO_VOICEINPUT = 2;
    public static final int ASR_ERROR_ELSE = 3;
    public static final int ASR_ERROR_NETWORK_PROBLEM = 4;
    public static final int ASR_ERROR_NO_RESULTS = 5;
    public static final int ASR_ERROR_ENGINE_BUSY = 6;
    public static final int ASR_ERROR_SERVICE_ERROR = 7;
    public static final int ASR_ERROR_CONNECTION_TIMEOUT = 8;
    public static final int ASR_ERROR_NORESULTS = 9;

    public static final int ASR_GRAM_DOWNLOAD_APP = 130;
    public static final int ASR_GRAM_SEARCH_APP = 131;

    public static final String TELEPHONE_AREA = "telephone";
    public static final int TELEPHONE_NO = 1;
    public static final String MESSAGE_AREA = "message";
    public static final int MESSAGE_NO = 2;
    public static final String CONTACTS_AREA = "contacts";
    public static final int CONTACTS_NO = 3;
    public static final String ALARM_AREA = "alarm";
    public static final int ALARM_NO = 4;
    public static final String SETTING_AREA = "setting";
    public static final int SETTING_NO = 5;
    public static final String MUSIC_AREA = "music";
    public static final int MUSIC_NO = 6;
    public static final String CALENDAR_AREA = "calendar";
    public static final int CALENDAR_NO = 7;
    public static final String WEATHER_AREA = "weather";
    public static final int WEATHER_NO = 8;
    public static final String TRANSLATION_AREA = "translation";
    public static final int TRANSLATION_NO = 10;
    public static final String APP_AREA = "app";
    public static final int APP_NO = 11;
    public static final String SEARCH_AREA = "search";
    public static final int SEARCH_NO = 12;
    public static final String JOKE_AREA = "joke";
    public static final int JOKE_NO = 13;
    public static final String MAP_AREA = "map";
    public static final int MAP_NO = 15;
    public static final String PERSON_AREA = "person";
    public static final int PERSON_NO = 14;
    public static final String TELEPHONE_INTENT_CALL = "call";
    public static final String TELEPHONE_INTENT_VIEW = "view";

    public static final String MESSAGE_INTENT_SEND = "send";
    public static final String MESSAGE_INTENT_VIEW = "view";

    public static final String CONTACT_INTENT_VIEW = "view";
    public static final String CONTACT_INTENT_CREATE = "create";
    public static final String CONTACT_INTENT_REMOVE = "remove";

    public static final String APP_INTENT_OPEN = "open";
    public static final String APP_INTENT_SEARCH = "search";
    public static final String APP_INTENT_DOWNLOAD = "download";
    public static final String APP_INTENT_GET = "get";

    public static final String ALARM_INTENT_INSERT = "insert";

    public static final String MUSIC_INTENT_PLAY = "play";

    public static final String MAP_INTENT_POI = "poi";
    public static final String MAP_INTENT_ROUTE = "route";
    public static final String MAP_INTENT_NEARBY = "nearby";
    //setting type
    public static final String SETTING_WIFI_ON = "wifi_on";
    public static final String SETTING_WIFI_OFF = "wifi_off";
    public static final String SETTING_RING_SILENT = "ring_silent";
    public static final String SETTING_RING_NORMAL = "ring_normal";
    public static final String SETTING_RING_SHARKL = "ring_shake";
    public static final String SETTING_FLIGHT_ON = "flight_on";
    public static final String SETTING_BLUETOOTH_ON = "bluetooth_on";
    public static final String SETTING_BLUETOOTH_OFF = "bluetooth_off";
    public static final String SETTING_GPS_ON = "gps_on";
    public static final String SETTING_GPS_OFF = "gps_off";
    public static final String SETTING_DATA_ON = "data_on";
    public static final String SETTING_DATA_OFF = "data_off";
    public static final String SETTING_SETTING = "settings_setting";

    //person focus
    public static final String FOCUS_HEIGHT = "height";
    public static final String FOCUS_WEIGHT = "weight";
    public static final String FOCUS_BIRTHDATE = "birthdate";
    //Non-al signage
    public static final int ASR_GRAM_CALL_UP = 114;
    public static final int ASR_GRAM_SEND_MSG = 115;
    public static final int ASR_GRAM_CONTACT_NAME = 116;
    public static final int ASR_GRAM_SEARCH_CONTACT = 117;


}