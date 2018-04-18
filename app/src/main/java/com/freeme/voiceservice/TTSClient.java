/*
 * File name: OnlineTTSService.java
 * 
 * Description: Online TTS service, use BAIDU SDK.
 *
 * Author: Theobald_wu, contact with wuqizhi@tydtech.com
 * 
 * Date: 2014-7-16   
 * 
 * Copyright (C) 2014 TYD Technology Co.,Ltd.
 * 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.freeme.voiceservice;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.freeme.jsonparse.areas.MessageArea;
import com.freeme.util.ASRHelper;
import com.freeme.voiceassistant.ManMachinePanel;

import android.util.Log;
import android.view.View;

public class TTSClient {
    private ManMachinePanel mContext;
    private Messenger mServiceMessenger;
    private boolean mBound;

    public TTSClient(ManMachinePanel context) {
        mContext = context;

        if (!mBound) {
            Intent intent = new Intent();
            intent.setAction(ASRHelper.ACTION_TTS);
            intent.setPackage(ASRHelper.SERVICE_PACKAGE);
            mContext.bindService(intent, mASRServiceConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
        }
    }

    /**
     * Unbind service
     */
    public void release() {
        if (mBound) {
            mContext.unbindService(mASRServiceConnection);
            mBound = false;
        }
    }

    public void speak(String text) {
        if (mServiceMessenger == null) {
            return;
        }

        try {
            Bundle data = new Bundle();
            data.putString(ASRHelper.TTS_SPEAK_TEXT_KEY, text);
            Message msg = Message.obtain(null, ASRHelper.TTS_SPEAK);
            msg.setData(data);
            mServiceMessenger.send(msg);
            mContext.rorateiv.clearAnimation();
            mContext.rorateiv.setVisibility(View.GONE);
            mContext.mStartBtn.setClickable(true);
            mContext.mASRWorkingIc.setVisibility(View.GONE);
            mContext.mStartBtn.setSelected(false);
            mContext.mASRWorkingAnim.cancel();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        if (mServiceMessenger == null) {
            return;
        }

        try {
            Message msg = Message.obtain(null, ASRHelper.TTS_CANCEL);
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection mASRServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mServiceMessenger = new Messenger(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            mServiceMessenger = null;
        }
    };
}