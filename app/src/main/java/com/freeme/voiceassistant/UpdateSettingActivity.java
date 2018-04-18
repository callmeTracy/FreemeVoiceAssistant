//
//package com.freeme.voiceassistant;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.Preference;
//import android.preference.PreferenceActivity;
//import android.preference.PreferenceManager;
//import android.preference.PreferenceScreen;
//import android.util.Log;
//import android.view.MenuItem;
//
//import com.freeme.updateself.update.UpdateMonitor;
//
//public class UpdateSettingActivity extends PreferenceActivity {
//
//    private final static String TAG = "UpdateSettingActivity";
//
//    public final static String AUTOCHECK_UPDATE_KEY = "autocheck_update_key";
//
//    private final static String CHECK_UPDATE_KEY = "check_update_key";
//
//    private Preference checkUpdate = null;
//    private Preference autoUpdateSwitch = null;
//    private  SharedPreferences mPreference = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        ActionBar actionBar = getActionBar();
////        actionBar.setDisplayHomeAsUpEnabled(true);
//
//        addPreferencesFromResource(R.xml.update_setting_preference);
//
//        autoUpdateSwitch = findPreference(AUTOCHECK_UPDATE_KEY);
//
//        checkUpdate = findPreference(CHECK_UPDATE_KEY);
//        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        boolean isHandle = false;
//        switch (id) {
//            case android.R.id.home:
//                this.finish();
//                isHandle = true;
//                break;
//
//            default:
//                break;
//        }
//        if (isHandle) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//        String key = preference.getKey();
//        Log.i(TAG, "onPreferenceTreeClick key :" + key);
//        if (key.equals(CHECK_UPDATE_KEY)) {
//            checkUpdate();
//            return true;
//        } else if (key.equals(AUTOCHECK_UPDATE_KEY)) {
//            boolean isAutoCheck = mPreference.getBoolean(UpdateSettingActivity.AUTOCHECK_UPDATE_KEY, true);
//            UpdateMonitor.setAutoUpdate(this, isAutoCheck);
//            return true;
//        }
//        return super.onPreferenceTreeClick(preferenceScreen, preference);
//    }
//
//    private void checkUpdate() {
//        UpdateMonitor.doManualUpdate(this);
//    }
//
//}
