
package com.freeme.voiceassistant;

import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

//import com.freeme.about.AboutAcitivity;
import com.freeme.data.DataManager;
import com.freeme.data.SpeechData;
import com.freeme.data.TalkAdapter;
import com.freeme.jsonparse.areas.HotappArea;
import com.freeme.statistic.VoiceStatisticUtil;
//import com.freeme.updateself.custom.Custom;
//import com.freeme.updateself.update.UpdateManager;
//import com.freeme.updateself.update.UpdateMonitor;
import com.freeme.util.ASRHelper;
import com.freeme.view.ASRWorkingView;
import com.freeme.voiceservice.TTSClient;
import com.freeme.voiceweather.DataHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author heqianqian on 20160106
 */
public class ManMachinePanel extends Activity implements OnClickListener, ASRRequestor.onRecongnitionListener {
    private static final String TAG = "[Freeme]ManMachinePanel";
    private static final int ASR_WORKING_AINM_DURATION = 2000;
    private ListView mTalkList;
    private TalkAdapter mTalkAdapter;
    public ImageView mStartBtn;
    public ASRWorkingView mASRWorkingIc;
    public ValueAnimator mASRWorkingAnim;
    private int mASRAnimRadius;
    private int mRecognitionScene = DataManager.MAIN_SCENARIO;
    private DataManager mDataManager;
    private static final int SCOPE_HOURS = 24 * 60 * 60 * 1000;
    private FrameLayout mFrameLayout;
    public TTSClient mTTSClient;
    private ASRRequestor mASRRequestor;
    private ContactCore mSmsSpeechContact;
    private AlertDialog mSmsSpeechDialog;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String KEY_UPDATE_TIME_DELAY = "FreemeVoiceAssistant_update";
    private AudioManager maudiomanager;
    private MyOnAudioFocusChangeListener mListener;
    public Animation operatingAnim;
    public ImageView rorateiv;
    public boolean isautoopen = false;
    public boolean mshowreturn = false;
    public ActionBar mactionBar;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mTTSClient = new TTSClient(ManMachinePanel.this);
                    mASRRequestor = new ASRRequestor(ManMachinePanel.this, ManMachinePanel.this);
                    VoiceStatisticUtil.generateStatisticInfo(ManMachinePanel.this, VoiceStatisticUtil.OPTION_ENTER);
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                    registerReceiver(mNetWorkStateReceiver, filter);
                    if (mASRRequestor != null) {
                        if (getIntent() != null) {
                            isautoopen = getIntent().getBooleanExtra("mytarget", false);
                            if (isautoopen) {
                                startFirstASR();
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long time1 = System.currentTimeMillis();
        setContentView(R.layout.panel);
        mactionBar = getActionBar();
        mactionBar.setDisplayShowHomeEnabled(false);
        initviews();
        mASRWorkingIc = (ASRWorkingView) findViewById(R.id.ic_sr_working);
        mASRAnimRadius = getResources().getDimensionPixelOffset(R.dimen.sr_working_anim_radius);
        mASRWorkingAnim = new ValueAnimator();
        mASRWorkingAnim.setRepeatCount(ValueAnimator.INFINITE);
        mASRWorkingAnim.setDuration(ASR_WORKING_AINM_DURATION);
        mASRWorkingAnim.setFloatValues(0, 1);
        mASRWorkingAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (Float) animation.getAnimatedValue();
                mASRWorkingIc.updateTile(t);
            }
        });
        isShowReturn(mshowreturn);
        HotappCore.getInstance(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mSharedPreferences.edit();
        boolean ishowdialogguide = mSharedPreferences.getBoolean("voiceassistantguide", true);
        if(ishowdialogguide) {
            showGuideDialog();
        }
//        UpdateMonitor.Builder
//                //*/ init UpdateMonitor
//                .getInstance(getApplicationContext())
//                //*/ register you Application to obsever
//                .registerApplication(getApplication())
//                //*/ register you Application is Service or hasEnrtyActivity
//                .setApplicationIsServices(true)
//                //*/ default notify small icon, ifnot set use updateself_ic_notify_small
//                .setDefaultNotifyIcon(R.drawable.updateself_ic_notify_small)
//                .complete();

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }, 200);
        DataHelper.init(ManMachinePanel.this);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.voice_asr_rotate_animation);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        VoiceStatisticUtil.saveStatisticInfoToFileFromDB(ManMachinePanel.this);
        VoiceStatisticUtil.generateStatisticInfo(ManMachinePanel.this, VoiceStatisticUtil.OPTION_OPENVOICEASSISTANT);
    }

    private void initviews() {
        mDataManager = new DataManager(this);
        mTalkAdapter = new TalkAdapter(this);
        rorateiv = (ImageView) findViewById(R.id.start_rorate);
        rorateiv.setOnClickListener(this);
        mTalkList = (ListView) findViewById(R.id.talk_list);
        mTalkList.setAdapter(mTalkAdapter);
        mTalkList.setDivider(getResources().getDrawable(R.drawable.divider));
        mStartBtn = (ImageView) findViewById(R.id.btn_start);
        mStartBtn.setOnClickListener(this);
        mFrameLayout = (FrameLayout) findViewById(R.id.panel_framelayout);
        maudiomanager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
        mListener = new MyOnAudioFocusChangeListener();
        mshowreturn = false;
        isShowReturn(mshowreturn);
        mactionBar.setTitle(getString(R.string.app_hello));
    }

    public void isShowReturn(boolean showreturn) {
        if (!showreturn) {
            mactionBar.setDisplayHomeAsUpEnabled(false);
            mactionBar.setHomeButtonEnabled(false);
        } else {
            mactionBar.setDisplayHomeAsUpEnabled(true);
            mactionBar.setHomeButtonEnabled(true); 
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
//         menu.add(0, 0, 0, this.getString(R.string.software_update));
//         menu.add(0, 1, 0, this.getString(R.string.freemeabout));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (item.getItemId()) {
//            case 0:
//                intent = new Intent();
//                intent.setClass(this, UpdateSettingActivity.class);
//                startActivity(intent);
//                break;

//            case 1:
//                intent = new Intent(this, AboutAcitivity.class);
//                startActivity(intent);
//                break;
            case android.R.id.home:
                if (mRecognitionScene == DataManager.MAIN_SCENARIO) {
                    this.finish();
                } else {
                    updateScenario(DataManager.MAIN_SCENARIO);
                    mshowreturn = false;
                    isShowReturn(mshowreturn);
                    mactionBar.setTitle(getString(R.string.app_hello));
                }
        }
        return super.onMenuItemSelected(featureId, item);
    }


    private class MyOnAudioFocusChangeListener implements OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.i(TAG, "focusChange=" + focusChange);
        }
    }

    @Override
    protected void onStart() {
        super.onRestart();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopASR();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTSClient != null && mASRRequestor != null) {
            mTTSClient.cancel();
            mTTSClient.release();
            HotappCore.release();
            mASRRequestor.release();
            MusicMediaPlayer.release();
        }
        VoiceStatisticUtil.generateStatisticInfo(ManMachinePanel.this, VoiceStatisticUtil.OPTION_EXIT);
        this.unregisterReceiver(mNetWorkStateReceiver);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_start:
                Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{
                        0, 50, 50, 100, 50
                }, -1);
                startASR();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        int mLastScene = mRecognitionScene;
        if (DataManager.MAIN_SCENARIO != mRecognitionScene) {
            updateScenario(DataManager.MAIN_SCENARIO);
            mshowreturn = false;
            isShowReturn(mshowreturn);
            mactionBar.setTitle(getString(R.string.app_hello));
            switch (mLastScene) {
                case DataManager.WEATHER_SCENARIO:
                case DataManager.SPECIAL_SR_SETTINGS:
                case DataManager.DATE_SCENARIO:
                case DataManager.TRANSLATE_SCENARIO:
                    mTalkList.setSelection(mTalkList.getBottom());
                    break;
            }

        } else {
            super.onBackPressed();
        }
    }

    public int getCurrentScene() {
        return mRecognitionScene;
    }

    public void startSmsVoiceInput(ContactCore contactCore) {
        mSmsSpeechContact = contactCore;
        mASRRequestor.startASR(ASRHelper.SMS_SCENE);

        // pop up window
        if (mSmsSpeechDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.message_speech_dialog_title);
           /* builder.setPositiveButton(
                    getString(R.string.message_speech_dialog_say_over),
                    new DialogInterface.OnClickListener() 
                    {
                        public void onClick(DialogInterface dialog, int which) 
                        {
                            mASRRequestor.stopASR();
                        }
                    });*/
            mSmsSpeechDialog = builder.create();
        }

        mSmsSpeechDialog.show();
    }

    public List<SpeechData> getCurrentScenarioData() {
        return mDataManager.getScenarioData(mRecognitionScene);
    }

    public void enterRecognizerScenario(int specialItem) {
        int scene = DataManager.MAIN_SCENARIO;
        int title = R.string.app_hello;
        switch (specialItem) {
            case SpeechData.SPECIAL_SR_CONTACT:
                scene = DataManager.CONTACT_SCENARIO;
                title = R.string.sr_contact_title;
                break;
            case SpeechData.SPECIAL_SR_MUSIC:
                scene = DataManager.MUSIC_SCENARIO;
                title = R.string.sr_music_title;
                break;
           /* case SpeechData.SPECIAL_SR_ALARM:
                scene = DataManager.ALARM_SCENARIO;
                title = R.string.sr_alarm_title;
                break;*/
            case SpeechData.SPECIAL_SR_WEATHER:
                scene = DataManager.WEATHER_SCENARIO;
                title = R.string.sr_weather_title;
                break;
            case SpeechData.SPECIAL_SR_APPSTORE:
                scene = DataManager.APPSTORE_SCENARIO;
                title = R.string.sr_app_title;
                break;

            case SpeechData.SPECIAL_SR_SETTINGS:
                scene = DataManager.SPECIAL_SR_SETTINGS;
                title = R.string.sr_settings_title;
                break;
            case SpeechData.SPECIAL_SR_DATE:
                scene = DataManager.DATE_SCENARIO;
                title = R.string.sr_date_title;
                break;
            case SpeechData.SPECIAL_SR_TRANSLATE:
                scene = DataManager.TRANSLATE_SCENARIO;
                title = R.string.sr_translate_title;
                break;
            case SpeechData.SPECIAL_SR_SEARCH:
                scene = DataManager.SERACH_SCENARIO;
                title = R.string.sr_search_title;
                break;
            case SpeechData.SPECIAL_SR_JOKE:
                scene = DataManager.JOKE_SCENARIO;
                title = R.string.sr_joke_title;
                break;
//            case SpeechData.SPECIAL_SR_CHAT:
//                scene = DataManager.CHAT_SCENARIO;
//                title = R.string.sr_chat_title;
//                break;
            case SpeechData.SPECIAL_SR_MAP:
                scene = DataManager.MAP_SCENARIO;
                title = R.string.sr_map_title;
                break;

            default:
                break;
        }
        mactionBar.setTitle(getString(title));
        if (title == R.string.app_hello) {
            mshowreturn = false;
            isShowReturn(mshowreturn);
        } else {
            mshowreturn = true;
            isShowReturn(mshowreturn);
        }

        updateScenario(scene);
    }

    public void updateTalkAdapter() {
        mTalkAdapter.notifyDataSetChanged();
    }

    public void updateHotappSlotDatas(ArrayList<String> datas) {

    }

    BroadcastReceiver mNetWorkStateReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            State wifiState = null;
            State mobileState = null;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (wifiState != null && mobileState != null
                    && State.CONNECTED != wifiState
                    && State.CONNECTED == mobileState) {
            } else if (wifiState != null && mobileState != null
                    && State.CONNECTED != wifiState
                    && State.CONNECTED != mobileState) {
                mTalkAdapter.addSpeech(new SpeechData(SpeechData.RESPONSE_TEXT_MODE, getString(R.string.network_no)));
                mTalkAdapter.addSpeech(new SpeechData(SpeechData.UNLINE_WIDGET_MODE, "setting"));
                mTalkList.setSelection(mTalkAdapter.getCount() - 1);
            } else if (wifiState != null && State.CONNECTED == wifiState) {

            }
        }
    };


    private void startASR() {
        if(mTTSClient!=null){
            mTTSClient.cancel();
        }else{
            return;
        }

        if (!mASRWorkingAnim.isRunning()) {
            int result = maudiomanager.requestAudioFocus(mListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            mASRWorkingIc.setVisibility(View.VISIBLE);
            mStartBtn.setSelected(true);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mASRWorkingAnim.start();
                mASRRequestor.startASR(ASRHelper.ID_SCENE);

            }
        } else {
            maudiomanager.abandonAudioFocus(mListener);
            mTTSClient.cancel();
            stopASR();
        }
    }


    private void startFirstASR() {
        mASRWorkingIc.setVisibility(View.VISIBLE);
        mStartBtn.setSelected(true);
        mASRWorkingAnim.start();
        mASRRequestor.startASR(ASRHelper.ID_SCENE);
        VoiceStatisticUtil.generateStatisticInfo(ManMachinePanel.this, VoiceStatisticUtil.OPTION_LONGPRESSMENU);
    }

    public void stopASR() {
        maudiomanager.abandonAudioFocus(mListener);
        mASRWorkingIc.setVisibility(View.GONE);
        mStartBtn.setSelected(false);
        mASRWorkingAnim.cancel();
        // mTTSClient.cancel();
    }

    private void updateScenario(int scene) {
        if (scene != mRecognitionScene) {
            stopASR();
            mRecognitionScene = scene;
            mTalkAdapter.setTalkData();
            if (scene != DataManager.MAIN_SCENARIO) {
                mTalkList.setDivider(null);
            } else {
                mTalkList.setDivider(getResources().getDrawable(R.drawable.divider));
            }
        }
    }

    @Override
    public void onRecognitionFinish(String error) {
        // TODO Auto-generated method stub
        maudiomanager.abandonAudioFocus(mListener);
        if (error != null) {
            updateScenario(DataManager.MAIN_SCENARIO);
            mshowreturn = false;
            isShowReturn(mshowreturn);
            mactionBar.setTitle(getString(R.string.app_hello));
            mTalkAdapter.addSpeech(new SpeechData(SpeechData.RESPONSE_TEXT_MODE, error));
            mTalkList.setSelection(mTalkAdapter.getCount() - 1);
            onSpeak(error);
            if (mSmsSpeechDialog != null && mSmsSpeechDialog.isShowing()) {
                mSmsSpeechDialog.cancel();
            }
        }
    }

    @Override
    public void onSpeak(String result) {
        // TODO Auto-generated method stub
        mTTSClient.speak(result);
    }

    @Override
    public void onFlushSmsSpeechText(String text) {
        // TODO Auto-generated method stub
        mSmsSpeechContact.insertMessageContent(text);
        mTalkAdapter.notifyDataSetChanged();
        if (mSmsSpeechDialog != null && mSmsSpeechDialog.isShowing()) {
            mSmsSpeechDialog.dismiss();
        }
    }

    @Override
    public void onResponseSpeechResult(SpeechData speech, boolean ishotapp) {
        // TODO Auto-generated method stub
        if (speech != null) {
            if (DataManager.MAIN_SCENARIO != mRecognitionScene) {
                //  if(DataManager.APPSTORE_SCENARIO==mRecognitionScene)
                //  {
                //      if(ishotapp)
                //      {
                //        updateScenario(DataManager.APPSTORE_SCENARIO);
                //      }
                //     else
                //      {
                updateScenario(DataManager.MAIN_SCENARIO);
                mshowreturn = false;
                isShowReturn(mshowreturn);
                mactionBar.setTitle(getString(R.string.app_hello));
                //   }
            } else {
                updateScenario(DataManager.MAIN_SCENARIO);
                mshowreturn = false;
                isShowReturn(mshowreturn);
                mactionBar.setTitle(getString(R.string.app_hello));
            }
            //  }
            mTalkAdapter.addSpeech(speech);
            mTalkList.setSelection(mTalkAdapter.getCount() - 1);
        }
    }

    @Override
    public void onRemoveContactSpeech(ContactCore contact) {
        // mTTSClient.cancel();
        mTalkAdapter.removeContactWidget(contact);
    }


    private void showGuideDialog() {
        LayoutInflater inflater = (LayoutInflater)
                this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.menuguide, null);
        final AlertDialog.Builder guide = new AlertDialog.Builder(this);
        TextView known = (TextView) view.findViewById(R.id.known_tv);
        editor.putBoolean("voiceassistantguide", false);
        editor.commit();
        guide.setView(view);
        final AlertDialog dialog = guide.show();
        dialog.getWindow().setLayout(640, 917);
        known.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
