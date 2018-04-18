package com.freeme.voiceservice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.speech.VoiceRecognitionService;
import com.freeme.voiceassistant.ManMachinePanel;
import com.freeme.voiceassistant.R;

import android.content.Intent;
import android.content.ComponentName;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;

import com.freeme.jsonparse.areas.BaseArea;
import com.freeme.util.ASRHelper;
import com.freeme.util.Util;
import com.google.gson.Gson;


public abstract class ASRClient implements RecognitionListener {
    private ManMachinePanel mContext;
    private SpeechRecognizer mSpeechRecognizer;
    private int scenario = 0;
    private boolean isadd = false;

    public ASRClient(ManMachinePanel context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext, new ComponentName(mContext, VoiceRecognitionService.class));
        mSpeechRecognizer.setRecognitionListener(this);
    }

    /**
     * init params of SpeechRecognizer
     *
     * @param intent
     */

    public void initparams(Intent intent) {
        //settting the hint sound when start, end, succuss, error, cancel
        intent.putExtra(ASRHelper.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
        intent.putExtra(ASRHelper.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
        intent.putExtra(ASRHelper.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        intent.putExtra(ASRHelper.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
        intent.putExtra(ASRHelper.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        //setting nlu is enable
        intent.putExtra(ASRHelper.EXTRA_NLU, ASRHelper.PARAMS_NLU);
        //setting language is Chinese
        intent.putExtra(ASRHelper.EXTRA_LANGUAGE, ASRHelper.PARAMS_LANGUAGE);
        //setting sample
        intent.putExtra(ASRHelper.EXTRA_SAMPLE, ASRHelper.PARAMS_SAMPLE);

        intent.putExtra(ASRHelper.EXTRA_OUT_FILE, "sdcard/outfile.pcm");
        intent.putExtra("audio.source", MediaRecorder.AudioSource.VOICE_RECOGNITION);
        //offline asr
//        String dest=mContext.getApplicationInfo().nativeLibraryDir+"/libbds_1_yb_20151119.so";
//		if(!new File(dest).exists())
//		{
//			dest = "/system/lib64/libbds_1_yb_20151119.so";
//		}
//        intent.putExtra(ASRHelper.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, dest);
        intent.putExtra(ASRHelper.EXTRA_LICENSE_FILE_PATH, "asset:///license-android-easr_freeme.txt");
        intent.putExtra("grammar", "assets:///baidu_speech_grammar.bsg");
        //intent.putExtra(ASRHelper.EXTRA_PROP, "10003,10008,100014,100016");
        JSONObject slotData = new JSONObject();
        //contacts
        JSONArray name = new JSONArray();
        if (Util.getContactsNameList(mContext) != null && Util.getContactsNameList(mContext).size() >= 1) {
            ArrayList<String> localnames = Util.getContactsNameList(mContext);
            if (localnames.size() >= 1) {
                for (String string : localnames) {
                    name.put(string);
                }
            }
        } else {
            name.put(mContext.getString(R.string.hint_contactsname));
        }
        //appnames
        JSONArray appname = new JSONArray();
        List<String> localapps = Util.getAppNames(mContext);
        for (String string : localapps) {
            appname.put(string);
            Log.i("heqianqian","appname==="+string);
        }

        //songs
        JSONArray song = new JSONArray();
        List<String> localsongs = Util.getSongName(mContext);
        if (localsongs != null) {
            for (String string : localsongs) {
                song.put(string);
            }
        }
        JSONArray singer = new JSONArray();
        List<String> localsinger = Util.getArtistName(mContext);
        if (localsinger != null) {
            for (String string : localsinger) {
                singer.put(string);
            }
        }

        try {
            appname.put(mContext.getString(R.string.app_xiaoyun));
            appname.put(mContext.getString(R.string.app_xiaokang));
            appname.put(mContext.getString(R.string.app_znyun));
            slotData.put("name", name);
            slotData.put("appname", appname);
            slotData.put("song", song);
            slotData.put("singer", singer);
        } catch (JSONException e) {
        }
        intent.putExtra(ASRHelper.SLOT_DATA, slotData.toString());

    }

    public void startASR(int scenario) {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.cancel();
        }
        this.scenario = scenario;
        final Intent intent = new Intent();
        if (ASRHelper.ID_SCENE == scenario) {
            intent.putExtra(ASRHelper.EXTRA_VAD, ASRHelper.EXTRA_VAD_SEARCH);
        } else if (ASRHelper.SMS_SCENE == scenario) {
            intent.putExtra(ASRHelper.EXTRA_VAD, ASRHelper.EXTRA_VAD_INPUT);
        }
        initparams(intent);
        mSpeechRecognizer.startListening(intent);
    }

    public void stopASR() {
        if (mSpeechRecognizer.isRecognitionAvailable(mContext)) {
            mSpeechRecognizer.cancel();
        }
    }

    protected void onRecognitionFinish(String error) {
        stopASR();
    }

    protected abstract void executeInstruction(ArrayList<String> slotDatas, String results_nlu);

    {

    }

    protected void onSmsSpeechInputText(String speechText) {

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i("heqianqian", "onBeginningOfSpeech");

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.i("heqianqian", "onEndOfSpeech");
        if (ASRHelper.ID_SCENE == scenario) {
            mContext.rorateiv.setVisibility(View.VISIBLE);
            mContext.rorateiv.startAnimation(mContext.operatingAnim);
            mContext.mStartBtn.setClickable(false);
            mContext.mASRWorkingIc.setVisibility(View.GONE);
        }


    }

    @Override
    public void onError(int error) {
        String errorstr = "";
        Log.i("heqianqian", "asrerror=" + error);
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorstr = mContext.getString(R.string.error_audio);
                onRecognitionFinish(errorstr);
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorstr = mContext.getString(R.string.error_speech_timeout);
                onRecognitionFinish(errorstr);
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                //errorstr=mContext.getString(R.string.error_client);;
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorstr = mContext.getString(R.string.error_insufficient_permissions);
                onRecognitionFinish(errorstr);
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorstr = mContext.getString(R.string.error_speech_timeout);
                onRecognitionFinish(errorstr);
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:

                if (Util.isNetworkAvailable(mContext)) {
                    errorstr = mContext.getString(R.string.error_no_match);
                    onRecognitionFinish(errorstr);
                } else {
                    errorstr = mContext.getString(R.string.error_no_function);
                    onRecognitionFinish(errorstr);
                }
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorstr = mContext.getString(R.string.error_recognizer_busy);
                onRecognitionFinish(errorstr);
                break;
            case SpeechRecognizer.ERROR_SERVER:
                //errorstr=mContext.getString(R.string.error_speech_timeout);
                //onRecognitionFinish(errorstr);
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorstr = mContext.getString(R.string.error_network_timeout);
                onRecognitionFinish(errorstr);
                break;

        }


    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i("heqianqian", "eventType=" + eventType);
        switch (eventType) {
            case 11:
                mContext.stopASR();
                break;
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i("heqianqian", "onPartialResults====");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i("heqianqian", "onReadyForSpeech====");
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> slotDatas = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        //	String origin_result = results.getString("origin_result");
        String results_nlu = results.get("results_nlu").toString();
        Log.i("heqianqian", "results_nlu==" + results_nlu);
        mContext.onSpeak("");
//		Gson gson=new Gson();
        if (slotDatas != null && !" ".equals(slotDatas)) {
//    		BaseArea ba=gson.fromJson(origin_result, BaseArea.class);
            if (ASRHelper.ID_SCENE == scenario) {
                executeInstruction(slotDatas, results_nlu);
                mContext.stopASR();
            } else if (ASRHelper.SMS_SCENE == scenario) {
                onSmsSpeechInputText(slotDatas.get(0));
                mContext.stopASR();
                return;
            }

        } else {
            mContext.rorateiv.clearAnimation();
            mContext.rorateiv.setVisibility(View.GONE);
            mContext.mStartBtn.setClickable(true);
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {


    }

    public void release() {
        stopASR();
        if (mSpeechRecognizer!=null) {
            mSpeechRecognizer.destroy();
        }
    }


}
