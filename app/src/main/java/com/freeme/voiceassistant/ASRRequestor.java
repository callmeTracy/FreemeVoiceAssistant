package com.freeme.voiceassistant;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.http.util.ByteArrayBuffer;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.freeme.data.SpeechData;
import com.freeme.jsonparse.areas.AppArea;
import com.freeme.jsonparse.areas.CalendarArea;
import com.freeme.jsonparse.areas.MapArea;
import com.freeme.jsonparse.areas.MessageArea;
import com.freeme.jsonparse.areas.MusicArea;
import com.freeme.jsonparse.areas.PersonArea;
import com.freeme.jsonparse.areas.SearchArea;
import com.freeme.jsonparse.areas.SettingArea;
import com.freeme.jsonparse.areas.TelephoneArea;
import com.freeme.jsonparse.areas.TranslateArea;
import com.freeme.jsonparse.areas.WeatherArea;
import com.freeme.jsonparse.pojo.JokeDataArea;
import com.freeme.jsonparse.pojo.PersonDataArea;
import com.freeme.jsonparse.pojo.Origin;
import com.freeme.statistic.VoiceStatisticUtil;
import com.freeme.util.ASRHelper;
import com.freeme.util.ParseJsonUtil;
import com.freeme.util.Util;
import com.freeme.voiceservice.ASRClient;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class ASRRequestor extends ASRClient {

    ManMachinePanel mContext;
    onRecongnitionListener mListener;
    private Handler mHandler;
    int mytype;
    private static final int START_ACTIVITY_DELAY = 300;
    MusicMediaPlayer player;
    private MyLocationListenner mLocationListener;
    private LocationClientOption mOption;
    private LocationClient mLocationClient = null;

    public ASRRequestor(ManMachinePanel context, onRecongnitionListener l) {
        super(context);
        mContext = context;
        mListener = l;
        getcity();
    }

    private void getcity() {
        mLocationClient = new LocationClient(mContext);
        mLocationListener = new MyLocationListenner();
        getDefaultLocationClientOption();
        mLocationClient.registerLocationListener(mLocationListener);
        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    private boolean istranslate = false;
    private Object getcity;
    String focus;
    String intent;
    String person;
    String relative;
    Gson gson = new Gson();
    private String data0;
    private String arraval;
    private String intent2;
    private String center;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void executeInstruction(ArrayList<String> slotDatas,
                                      String results_nlu) {
        mHandler = new Handler(mContext.getMainLooper());
        data0 = null;
        if (slotDatas != null && !slotDatas.isEmpty()) {
            data0 = slotDatas.get(0);
            if (!" ".equals(slotDatas.get(0))
                    && slotDatas.get(0).toString().trim().length() > 1) {
                if (mContext.getString(R.string.asr_playmusic).equals(
                        slotDatas.get(0)) || mContext.getString(R.string.asr_playmusics).equals(
                        slotDatas.get(0))) {
                    player = MusicMediaPlayer.getInstance(mContext, mListener);
                    mytype = MusicMediaPlayer.TITLE_LIST;
                    mListener.onResponseSpeechResult(new SpeechData(
                                    SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
                            false);
                    player.play(mytype, null);
                    mContext.rorateiv.clearAnimation();
                    mContext.rorateiv.setVisibility(View.GONE);
                    mContext.mStartBtn.setClickable(true);
                    return;
                }
                String answerString = Util.returnAnswer(mContext,
                        slotDatas.get(0));
                if (!"".equals(answerString)) {
                    mListener.onResponseSpeechResult(new SpeechData(
                                    SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
                            false);
                    mListener
                            .onResponseSpeechResult(
                                    new SpeechData(
                                            SpeechData.RESPONSE_TEXT_MODE,
                                            answerString), false);
                    mListener.onSpeak(answerString);
                    return;
                }
                if (slotDatas.get(0).startsWith(mContext.getString(R.string.search_start))) {
                    try {
                        mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
                                false);
                        String key = slotDatas.get(0).substring(slotDatas.get(0).indexOf(mContext.getString(R.string.search_start)) + 2);
//						mListener.onResponseSpeechResult(new SpeechData(
//								SpeechData.RESPONSE_TEXT_MODE, mContext.getString(R.string.searching_start)+key), false);
                        mListener.onSpeak(mContext.getString(R.string.searching_start) + key);
                        mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.SEARCH_WIDGET_MODE, slotDatas.get(0)),
                                false);
                        key = URLEncoder.encode(key, "gb2312");
                        URL url = new URL("http://www.baidu.com.cn/s?wd="
                                + key + "&cl=3");
                        Intent intent = Intent.parseUri(url.toString(), 0);
                        intent.setClass(mContext, WebViewActivity.class);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return;
                }
                Log.i("heqianqian", "results_nlu=========" + results_nlu);
                int type = ParseJsonUtil.parseUnderstandResult(results_nlu);
                int cmd = 0;
                switch (type) {
                    case ASRHelper.TELEPHONE_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENCALLMSG);
                        TelephoneArea tel = gson.fromJson(results_nlu,
                                TelephoneArea.class);
                        String teloperation = tel.getResults().get(0).getIntent();
                        if (ASRHelper.TELEPHONE_INTENT_CALL.equals(teloperation)) {
                            mListener.onResponseSpeechResult(
                                    new SpeechData(SpeechData.REQUEST_TEXT_MODE,
                                            slotDatas.get(0)), false);
                            cmd = ASRHelper.ASR_GRAM_CALL_UP;
                            String names[] = new String[2];
                            if (tel.getResults().get(0).getObject().getName() != null
                                    && !"".equals(tel.getResults().get(0)
                                    .getObject().getName())) {
                                names[0] = tel.getResults().get(0).getObject()
                                        .getName();
                                Pattern p = Pattern
                                        .compile("[\u4e00-\u9fa5]+[0-9]");
                                if (p.matcher(names[0]).matches()) {
                                    names[0] = names[0].substring(0,
                                            names[0].length() - 1);
                                }
                                callOrSendMsg(cmd, names);

                            } else {
                                mListener
                                        .onResponseSpeechResult(
                                                new SpeechData(
                                                        SpeechData.RESPONSE_TEXT_MODE,
                                                        mContext.getString(R.string.response_call_contacts)),
                                                false);
                                mListener
                                        .onSpeak(mContext
                                                .getString(R.string.response_call_contacts));
                            }
                        }
                        break;

                    case ASRHelper.MESSAGE_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENCALLMSG);
                        MessageArea sms = gson
                                .fromJson(results_nlu, MessageArea.class);
                        String smsoperation = sms.getResults().get(0).getIntent();
                        if (ASRHelper.MESSAGE_INTENT_SEND.equals(smsoperation)) {
                            mListener.onResponseSpeechResult(
                                    new SpeechData(SpeechData.REQUEST_TEXT_MODE,
                                            slotDatas.get(0)), false);
                            cmd = ASRHelper.ASR_GRAM_SEND_MSG;
                            String names[] = new String[2];

                            if (sms.getResults().get(0).getObject().getName() != null
                                    && !"".equals(sms.getResults().get(0)
                                    .getObject().getName())) {
                                String name = sms.getResults().get(0).getObject()
                                        .getName().toString();
                                if (name.contains("[")) {
                                    name = name.substring(name.indexOf("[") + 1,
                                            name.indexOf("]"));
                                }
                                Pattern p = Pattern
                                        .compile("[\u4e00-\u9fa5]+[0-9]");
                                if (p.matcher(name).matches()) {
                                    name = name.substring(0, name.length() - 1);
                                }

                                names[0] = name;
                                callOrSendMsg(cmd, names);

                            } else {
                                mListener
                                        .onResponseSpeechResult(
                                                new SpeechData(
                                                        SpeechData.RESPONSE_TEXT_MODE,
                                                        mContext.getString(R.string.response_send_contacts)),
                                                false);
                                mListener
                                        .onSpeak(mContext
                                                .getString(R.string.response_send_contacts));
                            }
                        }
                        break;

                    case ASRHelper.CONTACTS_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENCALLMSG);
                        TelephoneArea contact = gson.fromJson(results_nlu,
                                TelephoneArea.class);
                        String contactoperation = contact.getResults().get(0)
                                .getIntent();
                        if (ASRHelper.CONTACT_INTENT_VIEW.equals(contactoperation)) {
                            mListener.onResponseSpeechResult(
                                    new SpeechData(SpeechData.REQUEST_TEXT_MODE,
                                            slotDatas.get(0)), false);
                            cmd = ASRHelper.ASR_GRAM_SEARCH_CONTACT;
                            String names[] = new String[2];
                            if (contact.getResults().get(0).getObject().getName() != null
                                    && !" ".equals(contact.getResults().get(0)
                                    .getObject().getName())) {
                                names[0] = contact.getResults().get(0).getObject()
                                        .getName();
                                Pattern p = Pattern
                                        .compile("[\u4e00-\u9fa5]+[0-9]");
                                if (p.matcher(names[0]).matches()) {
                                    names[0] = names[0].substring(0,
                                            names[0].length() - 1);
                                }
                                ContactCore core = new ContactCore(mContext,
                                        ASRHelper.ASR_GRAM_SEARCH_CONTACT, names,
                                        mListener);

                            }
                        }
                        mContext.rorateiv.clearAnimation();
                        mContext.rorateiv.setVisibility(View.GONE);
                        mContext.mStartBtn.setClickable(true);
                        break;
                    case ASRHelper.SETTING_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENSYSTEMSETTING);
                        SettingArea setting = gson.fromJson(results_nlu,
                                SettingArea.class);
                        if ("set".equals(setting.getResults().get(0).getIntent())) {
                            mListener.onResponseSpeechResult(
                                    new SpeechData(SpeechData.REQUEST_TEXT_MODE,
                                            slotDatas.get(0)), false);

                            QuickSetting quickSetting = new QuickSetting(
                                    mContext, slotDatas.get(0), mListener);
                            quickSetting
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            mListener
                                    .onResponseSpeechResult(
                                            new SpeechData(
                                                    SpeechData.RESPONSE_TEXT_MODE,
                                                    mContext.getString(R.string.answ_please_search)),
                                            false);
                            mListener.onSpeak(mContext
                                    .getString(R.string.answ_please_search));
                            mListener.onResponseSpeechResult(new SpeechData(
                                            SpeechData.SEARCH_WIDGET_MODE, slotDatas.get(0)),
                                    false);
                        }
                        break;

                    case ASRHelper.CALENDAR_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENTIMEDATE);
                        CalendarArea canl = gson.fromJson(results_nlu,
                                CalendarArea.class);
                        if (canl.getResults().get(0).getObject().getANSWER() != null
                                && !" ".equals(canl.getResults().get(0).getObject()
                                .getANSWER())) {
                            mListener.onResponseSpeechResult(
                                    new SpeechData(SpeechData.REQUEST_TEXT_MODE,
                                            slotDatas.get(0)), false);
                            String answer = canl.getResults().get(0).getObject()
                                    .getANSWER();
                            mListener.onResponseSpeechResult(new SpeechData(
                                    SpeechData.RESPONSE_TEXT_MODE, answer), false);
                            mListener.onSpeak(answer);
                        } else {
                            mListener.onResponseSpeechResult(
                                    new SpeechData(SpeechData.REQUEST_TEXT_MODE,
                                            slotDatas.get(0)), false);
                            mListener
                                    .onResponseSpeechResult(
                                            new SpeechData(
                                                    SpeechData.RESPONSE_TEXT_MODE,
                                                    mContext.getString(R.string.answ_please_search)),
                                            false);
                            mListener.onSpeak(mContext
                                    .getString(R.string.answ_please_search));
                            mListener.onResponseSpeechResult(new SpeechData(
                                            SpeechData.SEARCH_WIDGET_MODE, slotDatas.get(0)),
                                    false);
                        }
                        break;
                    case ASRHelper.TRANSLATION_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENTRANSLATE);
                        TranslateArea trans = gson.fromJson(results_nlu,
                                TranslateArea.class);
                        if (trans.getResults().get(0).getObject().getTransbody() != null
                                && !" ".equals(trans.getResults().get(0)
                                .getObject().getTransbody())) {

                            mListener
                                    .onResponseSpeechResult(
                                            new SpeechData(
                                                    SpeechData.REQUEST_TEXT_MODE,
                                                    slotDatas
                                                            .get(0)
                                                            .replace(
                                                                    mContext.getString(R.string.asr_contactsa),
                                                                    mContext.getString(R.string.asr_contactsb))),
                                            false);
                            String content = trans.getResults().get(0).getObject()
                                    .getTransbody();
                            String targetstr = "";
                            int target = 0;
                            istranslate = false;
                            if (trans.getResults().get(0).getObject().getTarget() != null
                                    && !" ".equals(trans.getResults().get(0)
                                    .getObject().getTarget())) {
                                targetstr = trans.getResults().get(0).getObject()
                                        .getTarget();
                                target = Util.returntarget(mContext, targetstr);
                                switch (target) {
                                    case 0:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "zh"));
                                        break;
                                    case 1:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "en"));
                                        break;
                                    case 141:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "yue"));
                                        break;
                                    case 110:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "wyw"));
                                        break;
                                    case 3:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "jp"));
                                        break;
                                    case 4:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "kor"));
                                        break;
                                    case 10:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "fra"));
                                        break;
                                    case 6:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "th"));
                                        break;
                                    case 13:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "ara"));
                                        break;
                                    case 11:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "ru"));
                                        break;
                                    case 34:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "pt"));
                                        break;
                                    case 9:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "de"));
                                        break;
                                    case 47:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "it"));
                                        break;
                                    case 44:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "el"));
                                        break;
                                    case 24:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "nl"));
                                    case 19:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "pl"));
                                    case 16:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "bul"));
                                        break;
                                    case 14:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "est"));
                                        break;
                                    case 21:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "dan"));
                                    case 23:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "fin"));
                                    case 26:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "cs"));
                                        break;
                                    case 30:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "rom"));
                                        break;
                                    case 39:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "slo"));
                                    case 35:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "swe"));
                                    case 45:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "hu"));
                                        break;
                                    case 65:
                                        new TransAsyncTask().execute(Util.getUrl(
                                                content, "auto", "spa"));
                                        break;
                                    default:
                                        mListener
                                                .onResponseSpeechResult(
                                                        new SpeechData(
                                                                SpeechData.RESPONSE_TEXT_MODE,
                                                                mContext.getString(R.string.response_no_language)),
                                                        false);
                                        mListener
                                                .onSpeak(mContext
                                                        .getString(R.string.response_no_language));
                                        break;
                                }
                            } else {
                                new TransAsyncTask().execute(Util.getUrl(content,
                                        "auto", "en"));
                            }
                        }
                        break;

                    case ASRHelper.APP_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENAPPLICATION);
                        mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
                                false);
                        AppArea appa = gson.fromJson(results_nlu, AppArea.class);
                        if (mContext.getString(R.string.asr_nowtime).equals(
                                slotDatas.get(0))) {
                            mListener
                                    .onResponseSpeechResult(
                                            new SpeechData(
                                                    SpeechData.RESPONSE_TEXT_MODE,
                                                    ParseJsonUtil
                                                            .parseUnderstandResultanswer(results_nlu)),
                                            false);
                            return;

                        } else if (appa.getResults().get(0).getObject()
                                .getAppname() != null
                                && !"".equals(appa.getResults().get(0).getObject()
                                .getAppname())) {
                            String appname = appa.getResults().get(0).getObject()
                                    .getAppname();
                            if (ASRHelper.APP_INTENT_OPEN.equals(appa.getResults()
                                    .get(0).getIntent())) {
                                final ComponentName component = Util
                                        .getAppComponentName(mContext, appname);
                                String action = " ";
                                if (mListener != null) {
                                    action = mContext
                                            .getString(R.string.response_start_activity)
                                            + appname;
                                }

                                if (component != null) {
                                    if (mContext.getString(R.string.app_name)
                                            .equals(appname)) {
                                        mListener
                                                .onResponseSpeechResult(
                                                        new SpeechData(
                                                                SpeechData.RESPONSE_TEXT_MODE,
                                                                mContext.getString(R.string.response_norallow_start)),
                                                        false);
                                        mListener
                                                .onSpeak(mContext
                                                        .getString(R.string.response_norallow_start));
                                    } else {
                                        mListener
                                                .onResponseSpeechResult(
                                                        new SpeechData(
                                                                SpeechData.RESPONSE_TEXT_MODE,
                                                                action), false);
                                        mListener.onSpeak(action);
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent();
                                                intent.setComponent(component);
                                                Util.launcherIntent(mContext,
                                                        intent);
                                            }
                                        }, START_ACTIVITY_DELAY);
                                    }
                                } else {
                                    if (appname.startsWith(mContext.getString(R.string.app_install))) {
                                        mListener
                                                .onResponseSpeechResult(
                                                        new SpeechData(
                                                                SpeechData.RESPONSE_TEXT_MODE,
                                                                mContext.getString(R.string.app_intall)),
                                                        false);
                                        mListener.onSpeak(mContext.getString(R.string.app_intall));
                                        return;
                                    }
                                    mListener
                                            .onResponseSpeechResult(
                                                    new SpeechData(
                                                            SpeechData.RESPONSE_TEXT_MODE,
                                                            mContext.getString(R.string.response_no_app)),
                                                    false);
                                    mListener.onSpeak(mContext
                                            .getString(R.string.response_no_app));
                                }
                            } else if (ASRHelper.APP_INTENT_SEARCH.equals(appa
                                    .getResults().get(0).getIntent())) {
                                cmd = ASRHelper.ASR_GRAM_SEARCH_APP;
                                String[] names = new String[1];
                                names[0] = appname;
                                searchHotApp(cmd, names);
                            } else if (ASRHelper.APP_INTENT_DOWNLOAD.equals(appa
                                    .getResults().get(0).getIntent())) {
                                cmd = ASRHelper.ASR_GRAM_DOWNLOAD_APP;
                                String[] names = new String[1];
                                names[0] = appname;
                                searchHotApp(cmd, names);
                            } else {
                                mListener
                                        .onResponseSpeechResult(
                                                new SpeechData(
                                                        SpeechData.RESPONSE_TEXT_MODE,
                                                        mContext.getString(R.string.response_more_message)),
                                                false);
                                mListener.onSpeak(mContext
                                        .getString(R.string.response_more_message));
                            }

                        } else {
                            mListener
                                    .onResponseSpeechResult(
                                            new SpeechData(
                                                    SpeechData.RESPONSE_TEXT_MODE,
                                                    mContext.getString(R.string.response_speak_appname)),
                                            false);
                            mListener.onSpeak(mContext
                                    .getString(R.string.response_speak_appname));
                        }
                        break;

                    case ASRHelper.MUSIC_NO:
                       // VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENMUSIC);
                        MusicArea musica = gson.fromJson(results_nlu, MusicArea.class);
                        String artist = null;
                        String musicname = null;
                        mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
                                false);
                        if (slotDatas.get(0).endsWith(mContext.getString(R.string.focus_birthdate))) {
                            intent = ParseJsonUtil.parseUnderstandResultbirthdays(
                                    results_nlu).get(0);
                            focus = ParseJsonUtil.parseUnderstandResultbirthdays(
                                    results_nlu).get(1);
                            String httpArg = "{\"query\":\"" + slotDatas.get(0)
                                    + "\",\"resource\":\"spo_person\"}";
                            new PersonAsyncTask().execute(httpArg);
                            return;
                        }
                        if (ASRHelper.MUSIC_INTENT_PLAY.equals(musica.getResults()
                                .get(0).getIntent())) {
                            mContext.rorateiv.clearAnimation();
                            mContext.rorateiv.setVisibility(View.GONE);
                            mContext.mStartBtn.setClickable(true);
                            Log.i("heqianqian","music-title-="+equals(musica.getResults().get(0)
                                    .getObject().getSong()));
                            if (musica.getResults().get(0).getObject().getSong() != null
                                    && !"".equals(musica.getResults().get(0)
                                    .getObject().getSong())
                                    && musica.getResults().get(0).getObject()
                                    .getByartist() == null) {
                                musicname = musica.getResults().get(0).getObject()
                                        .getSong();
                                mytype = MusicMediaPlayer.TITLE_LIST;
                                if (player == null) {
                                    player = MusicMediaPlayer.getInstance(mContext,
                                            mListener);
                                }
                                player.play(mytype, musicname);

                            } else if (musica.getResults().get(0).getObject()
                                    .getByartist() != null
                                    && !"".equals(musica.getResults().get(0)
                                    .getObject().getByartist())
                                    && musica.getResults().get(0).getObject()
                                    .getSong() == null) {
                                mytype = MusicMediaPlayer.ARTIST_LIST;
                                artist = musica.getResults().get(0).getObject()
                                        .getByartist().get(0);
                                if (player == null) {
                                    player = MusicMediaPlayer.getInstance(mContext,
                                            mListener);
                                }
                                player.play(mytype, artist);
                            } else if (musica.getResults().get(0).getObject()
                                    .getSinger() != null
                                    && musica.getResults().get(0).getObject()
                                    .getSong() != null) {
                                mytype = MusicMediaPlayer.TITLE_LIST;
                                artist = musica.getResults().get(0).getObject()
                                        .getSinger();
                                musicname = musica.getResults().get(0).getObject()
                                        .getSong();
                                if (player == null) {
                                    player = MusicMediaPlayer.getInstance(mContext,
                                            mListener);
                                }
                                player.play(mytype, musicname, artist);
                            } else if (musica.getResults().get(0).getObject()
                                    .getSong() == null) {
                                mytype = MusicMediaPlayer.TITLE_LIST;
                                if (player == null) {
                                    player = MusicMediaPlayer.getInstance(mContext,
                                            mListener);
                                }
                                player.play(mytype, null);
                            }
                        } else {
                            mListener
                                    .onResponseSpeechResult(
                                            new SpeechData(
                                                    SpeechData.RESPONSE_TEXT_MODE,
                                                    mContext.getString(R.string.error_onlyplay)),
                                            false);
                            mListener.onSpeak(mContext
                                    .getString(R.string.error_onlyplay));
                        }

                        break;

                    case ASRHelper.WEATHER_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENWEATHER);
                        WeatherArea weathera = gson.fromJson(results_nlu,
                                WeatherArea.class);
                        String weathercity = null;
                        String dateorig;
                        mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
                                false);
                        if (weathera.getResults().get(0).getObject().getRegion() != null
                                && !" ".equals(weathera.getResults().get(0)
                                .getObject().get_region())) {
                            weathercity = weathera.getResults().get(0).getObject()
                                    .get_region();
                        } else {
                            if (mLocationListener.cityname != null
                                    && !"".equals(mLocationListener.cityname)) {
                                weathercity = mLocationListener.cityname;

                            }

                        }
                        if (weathera.getResults().get(0).getObject().get_date() != null
                                && ("今天".equals(weathera.getResults().get(0)
                                .getObject().get_date())
                                || "明天".equals(weathera.getResults().get(0)
                                .getObject().get_date()) || "后天"
                                .equals(weathera.getResults().get(0)
                                        .getObject().get_date()))) {
                            dateorig = weathera.getResults().get(0).getObject()
                                    .get_date();
                            if (isEN()) {
                                if ("今天".equals(dateorig)) {
                                    dateorig = mContext
                                            .getString(R.string.voice_weather_today);
                                } else if ("明天".equals(dateorig)) {
                                    dateorig = mContext
                                            .getString(R.string.voice_weather_tomorrow);
                                } else if ("后天".equals(dateorig)) {
                                    dateorig = mContext
                                            .getString(R.string.voice_weather_after_tomorrow);
                                }
                            } else if (isTW()) {
                                if ("今天".equals(dateorig)) {
                                    dateorig = mContext
                                            .getString(R.string.voice_weather_today);
                                } else if ("明天".equals(dateorig)) {
                                    dateorig = mContext
                                            .getString(R.string.voice_weather_tomorrow);
                                } else if ("后天".equals(dateorig)) {
                                    dateorig = mContext
                                            .getString(R.string.voice_weather_after_tomorrow);
                                }
                            } else {
                                dateorig = weathera.getResults().get(0).getObject()
                                        .get_date();
                            }

                        } else {
                            dateorig = mContext
                                    .getString(R.string.voice_weather_today);
                        }
                        VoiceWeather core = new VoiceWeather(mContext, weathercity,
                                dateorig, mListener);
                        break;

                    case ASRHelper.SEARCH_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENSEARCH);
                        SearchArea searcha = gson.fromJson(results_nlu,
                                SearchArea.class);
                        mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
                                false);
                        if (searcha.getResults().get(0).getObject().getContent() != null
                                && !"".equals(searcha.getResults().get(0)
                                .getObject().getContent())) {
                            String key = searcha.getResults().get(0).getObject()
                                    .getContent();
                            try {
                                mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.RESPONSE_TEXT_MODE, key), false);
                                mListener.onSpeak(mContext.getString(R.string.searching_start) + key);
                                key = URLEncoder.encode(key, "gb2312");
                                mListener.onResponseSpeechResult(new SpeechData(
                                                SpeechData.SEARCH_WIDGET_MODE, slotDatas.get(0)),
                                        false);
                                URL url = new URL("http://www.baidu.com.cn/s?wd="
                                        + key + "&cl=3");
                                Intent intent = Intent.parseUri(url.toString(), 0);
                                intent.setClass(mContext, WebViewActivity.class);
                                mContext.startActivity(intent);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        break;
//                    case ASRHelper.JOKE_NO:
//                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENJOKE);
//                        mListener.onResponseSpeechResult(new SpeechData(
//                                        SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
//                                false);
//                        new JokeAsyncTask().execute("");
//                        break;
                    case ASRHelper.MAP_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENMAPGUIDE);
                        mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
                                false);
                        MapArea mapa = gson.fromJson(results_nlu, MapArea.class);
                        if (mapa.getResults().get(0).getObject().getArrival() != null) {
                            arraval = mapa.getResults().get(0).getObject().getArrival();
                        }
                        if (mapa.getResults().get(0).getIntent() != null) {
                            intent2 = mapa.getResults().get(0).getIntent();
                        }
                        if (mapa.getResults().get(0).getObject().get_centre() != null) {
                            center = mapa.getResults().get(0).getObject().get_centre();
                        }
                        Intent intentmap;
                        double pointx = mLocationListener.pointx;
                        double pointy = mLocationListener.pointy;
                        String address = mLocationListener.address;
                        String area = mLocationListener.cityname;
                        String strs[] = null;
                        try {
                            Log.i("heqianqian", "isinstallbyread====" + Util.isInstallByread("com.baidu.BaiduMap"));
                            if (Util.isInstallByread("com.baidu.BaiduMap")) {
                                Log.i("heqianqian", "arraval==" + arraval + "  intent==" + intent2 + "  center==" + center + "  area==" + area);
                                if (ASRHelper.MAP_INTENT_POI.equals(intent2) && center != null) {

                                    mListener.onResponseSpeechResult(new SpeechData(
                                                    SpeechData.MAP_WIDGET_MODE, intent2 + "," + center + "," + area),
                                            false);
                                    intentmap = Intent.getIntent("intent://map/place/search?query=" + center + "region=" + area + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                    mContext.startActivity(intentmap);


                                } else if (ASRHelper.MAP_INTENT_ROUTE.equals(intent2)) {
                                    if (center != null && arraval != null) {
                                        mListener.onResponseSpeechResult(new SpeechData(
                                                        SpeechData.MAP_WIDGET_MODE, intent2 + "," + pointx + "," + pointy + "," + address + "," + arraval + "," + center + "," + area),
                                                false);
                                        intentmap = Intent.getIntent("intent://map/direction?origin=latlng:" + pointx + "," + pointy + "|name:" + address + "&destination=" + arraval + "&mode=driving&" +
                                                "region=" + area + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                        mContext.startActivity(intentmap);
                                    } else if (center == null && arraval != null) {
                                        mListener.onResponseSpeechResult(new SpeechData(
                                                        SpeechData.MAP_WIDGET_MODE, intent2 + "," + pointx + "," + pointy + "," + address + "," + arraval + "," + center + "," + area),
                                                false);
                                        intentmap = Intent.getIntent("intent://map/direction?origin=latlng:" + pointx + "," + pointy + "|name:" + address + "&destination=" + arraval + "&mode=driving&" +
                                                "region=" + area + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                        mContext.startActivity(intentmap);
                                    } else if (center != null && arraval == null) {
                                        mListener.onResponseSpeechResult(new SpeechData(
                                                        SpeechData.MAP_WIDGET_MODE, intent2 + "," + pointx + "," + pointy + "," + address + "," + arraval + "," + center + "," + area),
                                                false);
                                        intentmap = Intent.getIntent("intent://map/direction?origin=latlng:" + pointx + "," + pointy + "|name:" + address + "&destination=" + center + "&mode=driving&" +
                                                "region=" + area + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                        mContext.startActivity(intentmap);
                                    }
                                }

                            } else {
                                if (ASRHelper.MAP_INTENT_POI.equals(intent2) && center != null) {
                                    mListener.onResponseSpeechResult(new SpeechData(
                                                    SpeechData.MAP_WIDGET_MODE, intent2 + "," + center + "," + area),
                                            false);
                                    String url = "http://api.map.baidu.com/place/search?query=" + center + "&region=" + area + "&output=html&src=yourCompanyName|yourAppName";
                                    intentmap = new Intent(Intent.ACTION_VIEW);
                                    intentmap.setData(Uri.parse(url));
                                    mContext.startActivity(intentmap);
                                } else if (ASRHelper.MAP_INTENT_ROUTE.equals(intent2)) {
                                    if (center != null && arraval != null) {
                                        mListener.onResponseSpeechResult(new SpeechData(
                                                        SpeechData.MAP_WIDGET_MODE, intent2 + "," + pointx + "," + pointy + "," + address + "," + arraval + "," + center + "," + area),
                                                false);
                                        String url = "http://api.map.baidu.com/direction?origin=latlng:" + pointx + "," + pointy + "|name:" + address + "&destination=" + arraval + "&" +
                                                "mode=driving&region=" + area + "&output=html&src=yourCompanyName|yourAppName";
                                        intentmap = new Intent(Intent.ACTION_VIEW);
                                        intentmap.setData(Uri.parse(url));
                                        mContext.startActivity(intentmap);
                                    } else if (center == null && arraval != null) {
                                        mListener.onResponseSpeechResult(new SpeechData(
                                                        SpeechData.MAP_WIDGET_MODE, intent2 + "," + pointx + "," + pointy + "," + address + "," + arraval + "," + center + "," + area),
                                                false);
                                        String url = "http://api.map.baidu.com/direction?origin=latlng:" + pointx + "," + pointy + "|name:" + address + "&destination=" + arraval + "&" +
                                                "mode=driving&region=" + area + "&output=html&src=yourCompanyName|yourAppName";
                                        intentmap = new Intent(Intent.ACTION_VIEW);
                                        intentmap.setData(Uri.parse(url));
                                        mContext.startActivity(intentmap);
                                    } else if (center != null && arraval == null) {
                                        mListener.onResponseSpeechResult(new SpeechData(
                                                        SpeechData.MAP_WIDGET_MODE, intent2 + "," + pointx + "," + pointy + "," + address + "," + arraval + "," + center + "," + area),
                                                false);
                                        String url = "http://api.map.baidu.com/direction?origin=latlng:" + pointx + "," + pointy + "|name:" + address + "&destination=" + center + "&" +
                                                "mode=driving&region=" + area + "&output=html&src=yourCompanyName|yourAppName";
                                        intentmap = new Intent(Intent.ACTION_VIEW);
                                        intentmap.setData(Uri.parse(url));
                                        mContext.startActivity(intentmap);
                                    }


                                }
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        mContext.rorateiv.clearAnimation();
                        mContext.rorateiv.setVisibility(View.GONE);
                        mContext.mStartBtn.setClickable(true);
                        break;
                    case ASRHelper.PERSON_NO:
                        VoiceStatisticUtil.generateStatisticInfo(mContext, VoiceStatisticUtil.OPTION_OPENKNOWLEDGE);
                        mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
                                false);
                        String httpArg = "{\"query\":\"" + slotDatas.get(0)
                                + "\",\"resource\":\"spo_person\"}";
                        PersonArea persona = gson.fromJson(results_nlu,
                                PersonArea.class);
                        intent2 = persona.getResults().get(0).getIntent();
                        focus = persona.getResults().get(0).getObject().getFocus();
                        person = persona.getResults().get(0).getObject()
                                .getPerson();
                        relative = persona.getResults().get(0).getObject()
                                .getRelative();
                        new PersonAsyncTask().execute(httpArg);
                        break;
                    case ASRHelper.ASR_ERROR_NORESULTS:
                        mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.REQUEST_TEXT_MODE, slotDatas.get(0)),
                                false);


                        mListener
                                .onResponseSpeechResult(
                                        new SpeechData(
                                                SpeechData.RESPONSE_TEXT_MODE,
                                                mContext.getString(R.string.answ_please_search)),
                                        false);
                        mListener.onSpeak(mContext
                                .getString(R.string.answ_please_search));
                        mListener.onResponseSpeechResult(new SpeechData(
                                        SpeechData.SEARCH_WIDGET_MODE, slotDatas.get(0)),
                                false);
                        break;
                }
            }
        }

    }

    // telephone or message
    private void callOrSendMsg(int cmd, String[] name) {
        String action = null;
        // check Sim-card state at first
        if (!Util.isSimCardExist(mContext)) {
            action = mContext.getString(R.string.response_no_available_sim);
        }
        if (action != null) {
            if (mListener != null) {
                mListener.onResponseSpeechResult(new SpeechData(
                        SpeechData.RESPONSE_TEXT_MODE, action), false);
                mListener.onSpeak(action);
            }
        } else {
            // calling or sending message
            ContactCore core = new ContactCore(mContext, cmd, name, mListener);
        }
    }

    @Override
    protected void onSmsSpeechInputText(String speechText) {
        if (mListener != null) {
            mListener.onFlushSmsSpeechText(speechText);
        }
    }

    ;

    @Override
    protected void onRecognitionFinish(String error) {
        if (mListener != null) {
            mListener.onRecognitionFinish(error);
        }
    }

    interface onRecongnitionListener {
        // one recognition finished
        void onRecognitionFinish(String error);

        // the recognition speech result
        void onResponseSpeechResult(SpeechData speech, boolean ishotapp);

        // speech synthesizer
        void onSpeak(String result);

        void onRemoveContactSpeech(ContactCore contact);

        // flush speech text of SMS
        void onFlushSmsSpeechText(String text);
    }

    private boolean isTW() {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getCountry();
        if (language.endsWith("TW"))
            return true;
        else
            return false;
    }

    private boolean isEN() {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("en"))
            return true;
        else
            return false;
    }

    private void searchHotApp(int cmd, String[] apps) {
        String action = null;
        // check network is available at first
        if (Util.isNetworkAvailable(mContext)) {
            HotappCore appCore = HotappCore.getInstance(mContext);
            appCore.search(cmd, apps);
        } else {
            action = mContext.getString(R.string.response_no_available_network);
            if (mListener != null) {
                mListener.onResponseSpeechResult(new SpeechData(
                        SpeechData.RESPONSE_TEXT_MODE, action), false);
                mListener.onSpeak(action);
            }
        }
    }

    class TransAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String myString = "";
            try {
                URL uri = new URL(params[0]);
                URLConnection ucon = uri.openConnection();
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayBuffer baf = new ByteArrayBuffer(100);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                myString = new String(baf.toByteArray(), "GBK");
            } catch (Exception e) {
                mListener.onResponseSpeechResult(
                        new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mContext
                                .getString(R.string.error_network)), false);
                mListener.onSpeak(mContext.getString(R.string.error_network));
            }

            return myString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Gson gson = new Gson();
            Origin ori = gson.fromJson(result, Origin.class);
            if (ori.getTrans_result().get(0).getDst() != null
                    && !" ".equals(ori.getTrans_result().get(0).getDst())) {
                if (istranslate == false) {
                    mListener
                            .onResponseSpeechResult(
                                    new SpeechData(
                                            SpeechData.RESPONSE_TEXT_MODE,
                                            mContext.getString(R.string.response_translate_result)),
                                    false);
                    mListener.onSpeak(mContext
                            .getString(R.string.response_translate_result));
                    mListener.onResponseSpeechResult(new SpeechData(
                            SpeechData.RESPONSE_TEXT_MODE, ori
                            .getTrans_result().get(0).getDst()), false);
                    istranslate = true;
                }

            } else {
                mListener.onResponseSpeechResult(
                        new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mContext
                                .getString(R.string.error_network)), false);
                mListener.onSpeak(mContext.getString(R.string.error_network));
            }
        }

    }

    public LocationClientOption getDefaultLocationClientOption() {
        if (mOption == null) {
            mOption = new LocationClientOption();
            mOption.setOpenGps(isGpsEnable());
            mOption.setCoorType("bd09ll");
            mOption.setServiceName("com.baidu.location.service_v2.9");
            mOption.setAddrType("all");
            mOption.setScanSpan(Integer.parseInt("0"));
            mOption.disableCache(true);
            mLocationClient.setLocOption(mOption);
        }
        return mOption;
    }

    private boolean isGpsEnable() {
        LocationManager locationManager = ((LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public class MyLocationListenner implements BDLocationListener {
        String cityname;
        double pointx;
        double pointy;
        String address;

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location == null)
                return;
            cityname = location.getCity();
            pointx = location.getLatitude();
            pointy = location.getLongitude();
            address = location.getAddrStr();
            Log.i("heqianqian",
                    "location.getLatitude()=" + location.getLatitude()
                            + "location.getLongitude()"
                            + location.getLongitude());
            if (cityname == null) {
                mLocationClient.requestLocation();
            } else {
                cityname = cityname.substring(0, cityname.length() - 1);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }

    }

    @Override
    public void release() {
        // TODO Auto-generated method stub
        super.release();
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(mLocationListener);
            mLocationClient = null;
        }
    }

    class JokeAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            BufferedReader reader = null;
            String result = null;
            StringBuffer sbf = new StringBuffer();
            try {
                int page = new Random().nextInt(500) + 1;
                Log.i("heqianqian", "page======" + page);
                URL url = new URL(
                        "http://apis.baidu.com/showapi_open_bus/showapi_joke/joke_text?page="
                                + page);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestProperty("apikey",
                        "9a4ef4cfa178c25132978775318ba5ba");
                connection.connect();
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                reader.close();
                result = sbf.toString();
            } catch (Exception ex) {
                mListener.onResponseSpeechResult(
                        new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mContext
                                .getString(R.string.error_network_timeout)),
                        false);
                mListener.onSpeak(mContext
                        .getString(R.string.error_network_timeout));
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            int index = new Random().nextInt(18) + 1;
            Log.i("heqianqian", "index======" + index);
            // Log.i("heqianqian","results======"+result);
            JokeDataArea jokea = gson.fromJson(result, JokeDataArea.class);
            String jokeString = jokea.getShowapi_res_body().getContentlist()
                    .get(index).getText().replaceAll("<p>", "").replaceAll("</p>","").replaceAll("<p></p>","");
            if (!"".equals(jokeString)) {
                mListener.onResponseSpeechResult(new SpeechData(
                        SpeechData.RESPONSE_TEXT_MODE, jokeString), false);
                String[] strs = jokeString.split("。");
                for (String string : strs) {
                    mListener.onSpeak(string);
                }
            }

        }
    }

    class PersonAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            BufferedReader reader = null;
            String jsonresult = null;
            StringBuffer sbf = new StringBuffer();

            try {
                URL url = new URL(
                        "http://apis.baidu.com/baidu_openkg/person_kg/person_kg");
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("apikey",
                        "9a4ef4cfa178c25132978775318ba5ba");
                connection.setDoOutput(true);
                connection.getOutputStream().write(params[0].getBytes("UTF-8"));
                connection.connect();
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                reader.close();
                jsonresult = sbf.toString();
            } catch (Exception e) {
                mListener.onResponseSpeechResult(
                        new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mContext
                                .getString(R.string.error_network_timeout)),
                        false);
                mListener.onSpeak(mContext
                        .getString(R.string.error_network_timeout));
            }
            return jsonresult;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.i("heqianqian", "results======" + result);
            // Log.i("heqianqian","focus===="+focus+"======intent==========="+intent);
            String answers = "";
            String returnanswer = "";
            if (ASRHelper.FOCUS_HEIGHT.equals(focus) && "get".equals(intent)) {
                PersonDataArea pda = gson
                        .fromJson(result, PersonDataArea.class);
                if (pda.getData() != null && pda.getData().get(0) != null && pda.getData().get(0).getName() != null && pda.getData().get(0).getHeight() != null) {
                    answers = (pda.getData().get(0).getName().toString() + mContext.getString(R.string.focus_height) + pda
                            .getData().get(0).getHeight().toString()).toString()
                            .replace("value", "").replace("unitCode", "").trim();
                    returnanswer = answers
                            .replaceAll(
                                    "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】‘；：”“\"\\s’。，、？|-]",
                                    "");
                }
            } else if (ASRHelper.FOCUS_WEIGHT.equals(focus)
                    && "get".equals(intent)) {
                PersonDataArea pda = gson
                        .fromJson(result, PersonDataArea.class);
                if (pda.getData() != null && pda.getData().get(0) != null && pda.getData().get(0).getName() != null && pda.getData().get(0).getWeight() != null) {
                    answers = pda.getData().get(0).getName().toString() + mContext.getString(R.string.focus_weight)
                            + pda.getData().get(0).getWeight().toString();
                    returnanswer = answers
                            .replaceAll(
                                    "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】‘；：”“\"’。，、？|-]",
                                    "");
                }
            } else if (ASRHelper.FOCUS_BIRTHDATE.equals(focus)
                    && "get".equals(intent)) {
                PersonDataArea pda = gson
                        .fromJson(result, PersonDataArea.class);
                if (pda.getData() != null && pda.getData().get(0) != null && pda.getData().get(0).getName() != null && pda.getData().get(0).getBirthDate() != null) {
                    returnanswer = (pda.getData().get(0).getName() + mContext.getString(R.string.focus_birthdate) + pda
                            .getData().get(0).getBirthDate()).toString()
                            .replaceAll("\"", "").replaceAll("\\]", "");
                }
            } else if (relative != null && !"".equals(relative)
                    && ASRHelper.APP_INTENT_GET.equals(intent)) {
                if (ParseJsonUtil.parseUnderstanddescription(result) != null
                        && !"".equals(ParseJsonUtil
                        .parseUnderstanddescription(result))) {
                    returnanswer = ParseJsonUtil
                            .parseUnderstanddescription(result)
                            .replaceAll("\\[", "").replaceAll("\"\\]", "");
                }
            } else if (ASRHelper.APP_INTENT_SEARCH.equals(intent)
                    && !person.equals("")) {
                returnanswer = ParseJsonUtil.parseUnderstanddescription(result)
                        .replaceAll("\\[", "").replaceAll("\\]", "");
            } else {
                if (!"".equals(returnanswer)) {
                    mListener.onResponseSpeechResult(new SpeechData(
                            SpeechData.RESPONSE_TEXT_MODE, returnanswer), false);
                }

            }
            if (!"".equals(returnanswer)) {
                mListener.onResponseSpeechResult(new SpeechData(
                        SpeechData.RESPONSE_TEXT_MODE, returnanswer), false);
            } else {

                returnanswer = mContext.getString(R.string.answ_please_search);
                mListener.onResponseSpeechResult(new SpeechData(
                        SpeechData.RESPONSE_TEXT_MODE, returnanswer), false);
                mListener.onResponseSpeechResult(new SpeechData(
                        SpeechData.SEARCH_WIDGET_MODE, data0), false);
                Log.i("heqianqian", "quesiton====" + data0);
            }


            if (returnanswer.split("。") != null) {
                String[] strs = returnanswer.split("。");
                for (String string : strs) {
                    mListener.onSpeak(string);
                }
            } else {
                mListener.onSpeak(returnanswer);
            }
        }
    }


}
