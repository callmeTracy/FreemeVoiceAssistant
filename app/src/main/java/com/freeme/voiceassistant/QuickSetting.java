package com.freeme.voiceassistant;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
//import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;

import com.freeme.data.SpeechData;
import com.freeme.util.ASRHelper;
import com.freeme.util.Util;
import com.freeme.voiceassistant.ASRRequestor.onRecongnitionListener;

public class QuickSetting extends AsyncTask<Void, Void, Integer> {
    private static final int NONE = 0;
    private static final int WIFI_ON = 1;
    private static final int WIFI_OFF = 2;
    private static final int DATA_ON = 3;
    private static final int DATA_OFF = 4;
    private static final int BLUETOOTH_ON = 5;
    private static final int BLUETOOTH_OFF = 6;
    private static final int GPS_ON = 7;
    private static final int GPS_OFF = 8;
    private static final int RING_SILENT = 9;
    private static final int RING_NORMAL = 10;
    private static final int RING_SHARKL = 11;
    private static final int NETWORK_SET = 12;
    private static final int OK = 0;             // success
    private static final int DUPLICATE = -1;     // avoid setting repeat
    private static final int INVALID = -2;       // invalid, maybe no device 
    private static final int ILLEGAL = -4;       // error device type
    private static final int UNEXITSTS = 5;       // error device type
    private Context mContext;
    private String mDevice;
    private int mDeviceType;
    private WifiManager mWifiManager;
    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager mLocationManager;
    private ConnectivityManager mConnectManager;
    private TelephonyManager mTelephonyManager;
    private AudioManager mAudioManager;
    private boolean mEnabled;
    private onRecongnitionListener mListener;

    public QuickSetting(Context context, String device, onRecongnitionListener l) {
        mContext = context;
        mDevice = device;
        mListener = l;

        if (mContext.getString(R.string.settings_on_wifi).equals(device)) {
            mDeviceType = WIFI_ON;
            mEnabled = true;
        } else if (mContext.getString(R.string.settings_off_wifi).equals(device)) {
            mDeviceType = WIFI_OFF;
            mEnabled = false;
        } //else if (ASRHelper.SETTING_GPS_ON.equals(device)) {
        //  mDeviceType = GPS_ON;
        //   mEnabled=true;
        //} else if (ASRHelper.SETTING_GPS_OFF.equals(device)) {
        //   mDeviceType = GPS_OFF;
        //    mEnabled=false;
        //} 
        else if (mContext.getString(R.string.settings_on_bluetooth).equals(device)) {
            mDeviceType = BLUETOOTH_ON;
            mEnabled = true;
        } else if (mContext.getString(R.string.settings_off_bluetooth).equals(device)) {
            mDeviceType = BLUETOOTH_OFF;
            mEnabled = false;
        } else if (mContext.getString(R.string.settings_csilent_mode).equals(device) ||
                mContext.getString(R.string.settings_osilent_mode).equals(device)) {
            mDeviceType = RING_SILENT;
            mEnabled = true;
        } else if (mContext.getString(R.string.settings_cnormal_mode).equals(device) ||
                mContext.getString(R.string.settings_onormal_mode).equals(device)) {
            mDeviceType = RING_NORMAL;
            mEnabled = true;
        } else if (mContext.getString(R.string.settings_cshake_mode).equals(device) ||
                mContext.getString(R.string.settings_oshake_mode).equals(device)) {
            mDeviceType = RING_SHARKL;
            mEnabled = true;
        } else if (mContext.getString(R.string.settings_networkset).equals(device)) {
            mDeviceType = NETWORK_SET;
            mEnabled = true;
        } else {
            mDeviceType = NONE;
        }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Integer result = OK;

        switch (mDeviceType) {
            case WIFI_ON:
                if (mWifiManager == null) {
                    mWifiManager = (WifiManager) mContext
                            .getSystemService(Context.WIFI_SERVICE);
                }

                if (mWifiManager != null) {
                    if (!mWifiManager.isWifiEnabled()) {
                        mWifiManager.setWifiEnabled(true);
                    } else {
                        result = DUPLICATE;
                    }
                } else {
                    result = INVALID;
                }

                break;
            case WIFI_OFF:
                if (mWifiManager == null) {
                    mWifiManager = (WifiManager) mContext
                            .getSystemService(Context.WIFI_SERVICE);
                }

                if (mWifiManager != null) {
                    if (mWifiManager.isWifiEnabled()) {
                        mWifiManager.setWifiEnabled(false);
                    } else {
                        result = DUPLICATE;
                    }
                } else {
                    result = INVALID;
                }

                break;

            case BLUETOOTH_ON:
                if (mBluetoothAdapter == null) {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                }

                if (mBluetoothAdapter != null) {
                    if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF
                            || mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                        mBluetoothAdapter.enable();
                    } else {
                        result = DUPLICATE;
                    }
                } else {
                    result = INVALID;
                }

                break;

            case BLUETOOTH_OFF:
                if (mBluetoothAdapter == null) {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                }

                if (mBluetoothAdapter != null) {
                    if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON
                            || mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON) {
                        mBluetoothAdapter.disable();
                    } else {
                        result = DUPLICATE;
                    }
                } else {
                    result = INVALID;
                }

                break;

            case GPS_ON:
                if (mListener != null) {
                    mListener.onResponseSpeechResult(
                            new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mContext.getString(R.string.error_insufficient_permissions)), false);
                    mListener.onSpeak(mContext.getString(R.string.error_insufficient_permissions));
                }
            /*if (mLocationManager == null) {
                mLocationManager = (LocationManager) mContext
                        .getSystemService(Context.LOCATION_SERVICE);
            }
            
            if (mLocationManager != null) {
                if (!mLocationManager.isProviderEnabled(
                        LocationManager.GPS_PROVIDER)) {
                    Settings.Secure.setLocationProviderEnabled(
                            mContext.getContentResolver(),
                            LocationManager.GPS_PROVIDER, true);
               } else {
                   result = DUPLICATE;
                }
            } else {
                result = INVALID;
            }
            result = INVALID;*/
                break;
            case GPS_OFF:
                if (mListener != null) {
                    mListener.onResponseSpeechResult(
                            new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mContext.getString(R.string.error_insufficient_permissions)), false);
                    mListener.onSpeak(mContext.getString(R.string.error_insufficient_permissions));
                }
                result = INVALID;
                // try{
                //       if (mLocationManager == null) {
                //           mLocationManager = (LocationManager) mContext
                //                   .getSystemService(Context.LOCATION_SERVICE);
                //       }
                //
                //       if (mLocationManager != null) {
                //           if (mLocationManager.isProviderEnabled(
                //                   LocationManager.GPS_PROVIDER)) {
                //               Settings.Secure.setLocationProviderEnabled(
                //                       mContext.getContentResolver(),
                //                       LocationManager.GPS_PROVIDER, false);
                //          } else {
                //              result = DUPLICATE;
                //           }
                //       } else {
                //           result = INVALID;
                //       }
                //   }
                //   catch (Exception e) {
                //   }
                break;

            case RING_SILENT:
                if (mAudioManager == null) {
                    mAudioManager = (AudioManager) mContext
                            .getSystemService(Context.AUDIO_SERVICE);
                }
                if (mAudioManager != null) {
                    // int mode = mAudioManager.getRingerMode();
                    //if (AudioManager.RINGER_MODE_SILENT != mAudioManager.getRingerMode()) {
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    //}  else {
                    //   result = DUPLICATE;
                    // }
                } else {
                    result = INVALID;
                }
                break;
            case RING_SHARKL:
                if (mAudioManager == null) {
                    mAudioManager = (AudioManager) mContext
                            .getSystemService(Context.AUDIO_SERVICE);
                }
                if (mAudioManager != null) {
                    // int mode = mAudioManager.getRingerMode();
                    // if (AudioManager.RINGER_MODE_VIBRATE != mode) {
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    // }  else {
                    //    result = DUPLICATE;
                    // }
                } else {
                    result = INVALID;
                }
                break;
            case RING_NORMAL:
                if (mAudioManager == null) {
                    mAudioManager = (AudioManager) mContext
                            .getSystemService(Context.AUDIO_SERVICE);
                }
                if (mAudioManager != null) {
                    // int mode = mAudioManager.getRingerMode();
                    // if (AudioManager.RINGER_MODE_NORMAL != mAudioManager.getRingerMode()) {
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    // }  else {
                    //    result = DUPLICATE;
                    // }
                } else {
                    result = INVALID;
                }
                break;

            case DATA_ON:
                if (!Util.isSimCardExist(mContext)) {
                    result = INVALID;
                } else {
                    if (mConnectManager == null) {
                        mConnectManager = (ConnectivityManager) mContext
                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                    }
                    if (mTelephonyManager == null) {
                        mTelephonyManager = (TelephonyManager) mContext
                                .getSystemService(Context.TELEPHONY_SERVICE);
                    }
                    if (mTelephonyManager != null) {
                        if (mTelephonyManager.getDataState() == TelephonyManager.DATA_DISCONNECTED) {

                        } else {
                            result = DUPLICATE;
                        }
                    } else {
                        result = INVALID;
                    }
                }
                break;
            case NETWORK_SET:
                result = OK;
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                mContext.startActivity(wifiSettingsIntent);
                break;
            case DATA_OFF:
                if (!Util.isSimCardExist(mContext)) {
                    result = INVALID;
                } else {
                    if (mConnectManager == null) {

                        mConnectManager = (ConnectivityManager) mContext
                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                    }
                    if (mTelephonyManager == null) {
                        mTelephonyManager = (TelephonyManager) mContext
                                .getSystemService(Context.TELEPHONY_SERVICE);
                    }

                    if (mTelephonyManager != null) {
                        if (mTelephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) {

                        } else {
                            result = DUPLICATE;
                        }
                    } else {
                        result = INVALID;
                    }
                }
                break;

            case NONE:
                result = UNEXITSTS;
                break;

            default:
                result = ILLEGAL;
                break;
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        StringBuilder actionBuilder = new StringBuilder();
        switch (mDeviceType) {
            case 1:
            case 2:
                mDevice = mContext.getString(R.string.hint_device_wifi);
                break;
            case 3:
            case 4:
                mDevice = mContext.getString(R.string.hint_device_data);
                break;
            case 5:
            case 6:
                mDevice = mContext.getString(R.string.hint_device_bluetooth);
                break;
            case 7:
            case 8:
                mDevice = mContext.getString(R.string.hint_device_gps);
                break;
            case 9:
                mDevice = mContext.getString(R.string.hint_device_ring_silent);
                break;
            case 10:
                mDevice = mContext.getString(R.string.hint_device_ring_normal);
                break;
            case 11:
                mDevice = mContext.getString(R.string.hint_device_ring_shack);
                break;
            case 12:
                mDevice = mContext.getString(R.string.response_please_set);
                break;
            case 0:
                mDevice = mContext.getString(R.string.response_setting_unsupport);
                break;
            default:
                break;
        }
        switch (result) {
            case OK:

                if (mContext.getString(R.string.hint_device_wifi).equals(mDevice) ||
                        mContext.getString(R.string.hint_device_bluetooth).equals(mDevice)) {
                    actionBuilder.append(mContext.getString(R.string.response_setting_ok));
                    actionBuilder.append(mContext.getString(mEnabled ? R.string.response_setting_on
                            : R.string.response_setting_off));
                } else if (mContext.getString(R.string.hint_device_ring_silent).equals(mDevice) ||
                        mContext.getString(R.string.hint_device_ring_normal).equals(mDevice) ||
                        mContext.getString(R.string.hint_device_ring_shack).equals(mDevice)) {
                    actionBuilder.append(mContext.getString(R.string.response_setting_ok));
                    actionBuilder.append(mContext.getString(R.string.response_setting_change));
                } else if (mContext.getString(R.string.response_please_set).equals(mDevice)) {
                }
                actionBuilder.append(mDevice);
                break;

            case DUPLICATE:
                actionBuilder.append(mDevice);
                actionBuilder.append(mContext.getString(mEnabled ? R.string.response_setting_duplicate_on
                        : R.string.response_setting_duplicate_off));
                break;

            case INVALID:
                actionBuilder.append(mDevice);
                actionBuilder.append(mContext.getString(R.string.response_setting_invalid));
                break;

            case ILLEGAL:
                // do nothing
                actionBuilder.append(mContext.getString(R.string.response_setting_invalid));
                return;
            case UNEXITSTS:
                actionBuilder.append(mDevice);
                break;
            default:
                break;
        }

        String action = actionBuilder.toString();

        if (mListener != null) {
            mListener.onResponseSpeechResult(
                    new SpeechData(SpeechData.RESPONSE_TEXT_MODE, action), false);
            mListener.onSpeak(action);
        }
    }

}
