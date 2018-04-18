package com.freeme.voiceservice;import android.app.Service;import android.content.Intent;import android.os.Bundle;import android.os.Environment;import android.os.Handler;import android.os.IBinder;import android.os.Message;import android.os.Messenger;import android.util.Log;import com.baidu.tts.answer.auth.AuthInfo;import com.baidu.tts.client.SpeechError;import com.baidu.tts.client.SpeechSynthesizer;import com.baidu.tts.client.SpeechSynthesizerListener;import com.baidu.tts.client.TtsMode;import com.freeme.util.ASRHelper;import java.io.File;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;public class VoiceTTSService extends Service implements SpeechSynthesizerListener {    private static final String TAG = "[TYD_DEBUG]VoiceTTSService";    private static final String APPID = "7613341";    private static final String APIKEY = "y4ef9UY6xvrSY6WTjSx3LwYH";    private static final String SECRETKEY = "688f9e77e51aff7e1dfd855c9e0c0f88";    private static final boolean DEBUG = true;    private SpeechSynthesizer mSpeechSynthesizer;    private String mSampleDirPath;    private static final String SAMPLE_DIR_NAME = "baiduTTS";    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";    private static final String LICENSE_FILE_NAME = "temp_license";    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";    private Handler mHandler = new Handler() {        public void dispatchMessage(Message msg) {            if (DEBUG) {                Log.i(TAG, "dispatchMessage(): msg = " + msg.what);            }            switch (msg.what) {                case ASRHelper.TTS_SPEAK:                    speak(msg);                    break;                case ASRHelper.TTS_CANCEL:                    if (mSpeechSynthesizer != null) {                        mSpeechSynthesizer.stop();                    }                    break;                default:                    break;            }        }    };    private Messenger mMessenger = new Messenger(mHandler);    @Override    public void onCreate() {        super.onCreate();        new Thread(new Runnable() {            @Override            public void run() {                initialEnv();                initParams();            }        }).start();    }    private void initialEnv() {        if (mSampleDirPath == null) {            String sdcardPath = Environment.getExternalStorageDirectory().toString();            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;        }        makeDir(mSampleDirPath);        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);        copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"                + ENGLISH_SPEECH_MALE_MODEL_NAME);        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"                + ENGLISH_TEXT_MODEL_NAME);    }    private void makeDir(String dirPath) {        File file = new File(dirPath);        if (!file.exists()) {            file.mkdirs();        }    }    /**     * @param isCover if cover the target file     * @param source     * @param dest     */    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {        File file = new File(dest);        if (isCover || (!isCover && !file.exists())) {            InputStream is = null;            FileOutputStream fos = null;            try {                is = getResources().getAssets().open(source);                String path = dest;                fos = new FileOutputStream(path);                byte[] buffer = new byte[1024];                int size = 0;                while ((size = is.read(buffer, 0, 1024)) >= 0) {                    fos.write(buffer, 0, size);                }            } catch (FileNotFoundException e) {                e.printStackTrace();            } catch (IOException e) {                e.printStackTrace();            } finally {                if (fos != null) {                    try {                        fos.close();                    } catch (IOException e) {                        e.printStackTrace();                    }                }                try {                    if (is != null) {                        is.close();                    }                } catch (IOException e) {                    e.printStackTrace();                }            }        }    }    public void initParams() {        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();        this.mSpeechSynthesizer.setContext(this);        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"                + TEXT_MODEL_NAME);        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"                + SPEECH_FEMALE_MODEL_NAME);        this.mSpeechSynthesizer.setAppId(APPID);        this.mSpeechSynthesizer.setApiKey(APIKEY, SECRETKEY);        // AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE);        // if (authInfo.isSuccess()) {        mSpeechSynthesizer.initTts(TtsMode.MIX);        int result =                mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath                        + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);        //} else {        //   String errorMsg = authInfo.getTtsError().getDetailMessage();        // }    }    public void speak(Message msg) {        Bundle data = msg.getData();        String text = data.getString(ASRHelper.TTS_SPEAK_TEXT_KEY);        if (text != null && mSpeechSynthesizer != null) {            mSpeechSynthesizer.speak(text);        }    }    @Override    public IBinder onBind(Intent arg0) {        // TODO Auto-generated method stub        return mMessenger.getBinder();    }    @Override    public void onError(String arg0, SpeechError arg1) {        // TODO Auto-generated method stub        Log.i("heqianqian", "SpeechError==========" + arg1.description);    }    @Override    public void onSpeechFinish(String arg0) {        // TODO Auto-generated method stub    }    @Override    public void onSpeechProgressChanged(String arg0, int arg1) {        // TODO Auto-generated method stub    }    @Override    public void onSpeechStart(String arg0) {        // TODO Auto-generated method stub    }    @Override    public void onSynthesizeDataArrived(String arg0, byte[] arg1, int arg2) {        // TODO Auto-generated method stub    }    @Override    public void onSynthesizeFinish(String arg0) {        // TODO Auto-generated method stub    }    @Override    public void onSynthesizeStart(String arg0) {        // TODO Auto-generated method stub    }}