/*
 * File name: HotappCore.java
 * 
 * Description: The Manager core of hot-app, it can search or
 *              download the popular APK.
 *
 * Author: Theobald_wu, contact with wuqizhi@tydtech.com
 * 
 * Date: 2014-9-26   
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
package com.freeme.voiceassistant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Override;
import java.lang.Runnable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.LinkedList;

import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.freeme.data.SpeechData;
import com.freeme.util.ASRHelper;
import com.freeme.util.Util;
import com.google.gson.Gson;

public class HotappCore {
    private static final String TAG = "[Freeme]HotappCore";
    private static final String STORE_URL = "http://sync-freevoice.tt286.com/";
    private static final boolean DEBUG = true;//false;
    private static final int TOP_NUM = 3;
    private static final int DOWNLOAD_PROGRESS_UPDATE_CACHE_SIZE = 20 << 10; // 20kb
    private static final String PACKAGE_MIME_TYPE = "application/vnd.android.package-archive";
    private static final String FILEMANAGER_REFRESH = "com.mediatek.filemanager.broadcast.refresh";

    // status constant
    private static final int NO_DOWNLOAD = 0;
    private static final int DOWNLOADING = 1;
    private static final int DOWNLOAD_FINISHED = 2;
    private static final int INSTALLED_UPGRADE = 3;
    private static final int INSTALLED = 4;
    private Gson gson = new Gson();
    Bitmap iconone = null;
    public ApkStruct apk = null;
    int strRes = 0;

    // data loading state
    private enum DataLoadState {
        NO_LOAD, // have not load
        LOADING, // loading
        LOADED   // have loaded
    }

    private static HotappCore mInstance;
    private List<ApkStruct> mApkList = new ArrayList<ApkStruct>();
    public static ManMachinePanel mActivity;
    private DataLoadState mLoadState; // data load status, success or fail.
    private Object mLoadingLock = new Object();
    private boolean mLoadTaskRunning = false; // load task is running
    private int mCmd;
    private String[] mFindAppNames;
    private List<ApkStruct> mFindResults = new ArrayList<ApkStruct>();
    private int mSelectedIndex;
    private View mSelectedItem;
    private ProgressDialog mWaitDialog;
    private LayoutInflater mInflater;
    private ApkStruct[] mTopApps;
    LinkedList<Map<String, Object>> mlist = new LinkedList<Map<String, Object>>();
    // avoid show action repeatedly so that it is go to list end.
    private boolean mTopAppShowed = false;
    // cache icon bitmaps: map(apk_id, bitmap)
    private CachePool<Bitmap> mIconCachePool = new CachePool<Bitmap>();
    // cache download task: map(apk_id, DownloadTask)
    private SparseArray<DownloadTask> mDownloadTasks = new SparseArray<DownloadTask>();
    private Handler handler = new Handler();
    private NotificationManager ntfm;
    private Notification ntf;
    public int downloaded = 0;
    public Runnable runnable;

    public HotappCore(ManMachinePanel activity) {
        mLoadState = DataLoadState.NO_LOAD;
        mInflater = activity.getLayoutInflater();
        mActivity = activity;
        // register intent receivers
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        mActivity.registerReceiver(mBroadcastReceiver, filter);
        filter = new IntentFilter();
        filter.addAction(FILEMANAGER_REFRESH);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addDataScheme("file");
        mActivity.registerReceiver(mBroadcastReceiver, filter);
        loadData();

    }

    public static HotappCore getInstance(ManMachinePanel activity) {
        if (mInstance == null) {
            mInstance = new HotappCore(activity);
        }
        return mInstance;
    }

    public static void release() {
        if (mInstance != null) {
            mInstance.onRelease();
            mInstance = null;
        }
    }

    public static View getWidget(String tag) {
        View w = null;

        if (mInstance != null) {
            w = mInstance.createWidget(tag);
        } else {
            throw new IllegalAccessError("HotappCore's instance is null!");
        }

        return w;
    }


    @SuppressLint("NewApi")
    public void search(int cmd, String[] apps) {
        Log.i(TAG, "search(): cmd = " + cmd);
        mCmd = cmd;
        mFindAppNames = apps;
        if (!mActivity.isDestroyed()) {
            mWaitDialog = ProgressDialog.show(mActivity, null,
                    mActivity.getString(R.string.hotapp_search_wait));
            mActivity.onSpeak(mActivity.getString(R.string.hotapp_search_wait));
        }
        if (mLoadState == DataLoadState.NO_LOAD) {
            loadData();

        }
        // 1. should create new wait thread, otherwise dialog can't pop up.
        // 2. finding and loading icon need new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mLoadingLock) {
                    try {
                        while (mLoadTaskRunning) {
                            mLoadingLock.wait();
                        }
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }

                findApps();
            }
        }).start();
    }

    public void showTops() {

// if (mTopAppShowed) {
//            return;
//        }

        Log.i(TAG, "showTops()...");
        mWaitDialog = ProgressDialog.show(mActivity, null,
                mActivity.getString(R.string.hotapp_wait));
        //if (mLoadState == DataLoadState.NO_LOAD) {
        //loadAd();
        //}

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mLoadingLock) {
                    try {
                        while (mLoadTaskRunning) {
                            mLoadingLock.wait();
                        }
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitDialog.isShowing()) {
                            mWaitDialog.dismiss();
                        }
                        if (mlist.size() != 0) {
                            mTopAppShowed = true;
                            mActivity.onResponseSpeechResult(
                                    new SpeechData(SpeechData.HOTAPP_WIDGET_MODE,
                                            SpeechData.HOTAPP_SHOW_TOP_TAG), true);
                        }
                    }
                });
            }
        }).start();
    }

    private void onRelease() {
        mActivity.unregisterReceiver(mBroadcastReceiver);
        // interrupt download tasks
        for (int i = 0; i < mDownloadTasks.size(); i++) {
            DownloadTask task = mDownloadTasks.valueAt(i);
            if (task != null) {
                task.cancel(true);
            }
        }
    }

    private void findApps() {
        final List<ApkStruct> results = new ArrayList<ApkStruct>();

        for (String appName : mFindAppNames) {
            Log.i(TAG, "findApps(): find app name = " + appName);
            for (ApkStruct apk : mApkList) {
                if (apk.name.equalsIgnoreCase(appName)) {
                    loadIcon(apk, false);
                    initApkState(apk);
                    results.add(apk);
                    break; // need also consider
                }
            }
        }

        if (!results.isEmpty()) {
            // if have new find results, replace
            mFindResults.clear();
            for (ApkStruct result : results) {
                mFindResults.add(result);
            }
            // single result
            if (mFindResults.size() == 1) {
                mSelectedIndex = 0;
            }
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                report(results);
            }
        });
    }

    /**
     * Loading the icon of APK, it doesn't run in UI thread the best.
     *
     * @param apk
     * @param isMaintain if true, it is not flush
     */
    private void loadIcon(ApkStruct apk, boolean isMaintain) {
        if (mIconCachePool.get(apk.id) != null) {
            // if icon cache exist, do not load from server.
            return;
        }
        HttpURLConnection connect = null;
        final int size = 1 << 10;
        try {
            URL url = new URL(apk.iconURL);
            connect = (HttpURLConnection) url.openConnection();
            int nRC = connect.getResponseCode();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Log.i(TAG, "loadIcon(): nRC = " + nRC + ", apk.iconURL = " + apk.iconURL);

            if (HttpURLConnection.HTTP_OK == nRC) {
                InputStream is = connect.getInputStream();
                byte[] buffer = new byte[size];
                int readBytes = 0;

                while ((readBytes = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, readBytes);
                }

                is.close();
            }

            byte[] data = baos.toByteArray();
            Bitmap icon = toRoundCorner(BitmapFactory.decodeByteArray(data, 0, data.length), 10);
            mIconCachePool.put(apk.id, icon, isMaintain);

            baos.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "loadIcon(): Can't parse URL!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }

    /**
     * Feedback search result
     *
     * @param results
     */
    String action = " ";

    private void report(List<ApkStruct> results) {
        action = mActivity.getString(R.string.response_search_no_result);
        if (mWaitDialog.isShowing()) {
            mWaitDialog.dismiss();
        }
        if (!results.isEmpty()) {
            if (ASRHelper.ASR_GRAM_SEARCH_APP == mCmd) {
                {
                    if (results.size() > 1) {
                        // should select at first
                        action = mActivity.getString(R.string.response_search_hotapp);
                    } else {
                        // single result
                        String name = results.get(mSelectedIndex).name;
                        int strRes = 0;
                        switch (results.get(mSelectedIndex).state) {
                            case DOWNLOAD_FINISHED:
                                strRes = R.string.response_hotapp_have_downloaded;
                                break;
                            case INSTALLED:
                                strRes = R.string.response_hotapp_have_intalled;
                                break;
                            case INSTALLED_UPGRADE:
                                strRes = R.string.response_hotapp_install_upgrade;
                                break;
                            case DOWNLOADING:
                                strRes = R.string.response_hotapp_downloading;
                                break;
                            default:
                                strRes = R.string.response_search_hotapp;
                                break;
                        }
                        action = String.format(mActivity.getString(strRes), name);
                    }
                }

            } else if (ASRHelper.ASR_GRAM_DOWNLOAD_APP == mCmd) {
                if (results.size() > 1) {
                    // should select at first
                    action = mActivity.getString(R.string.response_download_hotapp_sel);
                } else {
                    // single result
                    String name = results.get(mSelectedIndex).name;

                    switch (results.get(mSelectedIndex).state) {
                        case DOWNLOAD_FINISHED:
                            strRes = R.string.response_hotapp_have_downloaded;
                            break;
                        case INSTALLED:
                            strRes = R.string.response_hotapp_have_intalled;
                            break;
                        case INSTALLED_UPGRADE:
                            strRes = R.string.response_hotapp_install_upgrade;
                            break;
                        case DOWNLOADING:
                            strRes = R.string.response_hotapp_downloading;
                            break;
                        default:
                            strRes = R.string.response_search_hotapp;
                            break;
                    }
                    action = String.format(mActivity.getString(strRes), name);
                }
            }
        }

        mActivity.onResponseSpeechResult(
                new SpeechData(SpeechData.RESPONSE_TEXT_MODE, action), false);
        mActivity.onSpeak(action);

        if (!results.isEmpty()) {
            mActivity.onResponseSpeechResult(new SpeechData(SpeechData.HOTAPP_WIDGET_MODE,
                    SpeechData.HOTAPP_SEARCH_RESULT_TAG), false);
        }
    }

    /**
     * Initialize the status, NO_DOWNLOAD, DOWNLOADING, DOWNLOAD_FINISHED,
     * INSTALLED_UPGRADE, INSTALLED.
     *
     * @param apk
     */
    private void initApkState(ApkStruct apk) {
        if (apk.state == DOWNLOADING) {
            return;
        }

        apk.state = NO_DOWNLOAD;
        // Check the APK whether installed by package name, and whether update by version code.
        PackageManager pm = mActivity.getPackageManager();
        if(pm.isSafeMode()) {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            List<ResolveInfo> appList = pm.queryIntentActivities(intent, 0);
            Log.i(TAG, "initApkState(): apk.packageName = " + apk.packageName);
            for (ResolveInfo resovleInfo : appList) {
                if (resovleInfo.activityInfo.packageName.equals(apk.packageName)) {
                    try {
                        PackageInfo info = pm.getPackageInfo(apk.packageName, 0);
                        if (info.versionCode >= apk.versionCode) {
                            apk.state = INSTALLED; // the newest
                        } else {
                            apk.state = INSTALLED_UPGRADE; // can upgrade
                        }
                    } catch (NameNotFoundException e) {
                    }
                    break;
                }
            }

            // Check whether have download
            setDownloadedState(apk);
        }
    }

    private void updateApkState(ApkStruct apk, String action) {
        if (Intent.ACTION_PACKAGE_ADDED.equals(action) ||
                Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            apk.state = INSTALLED;
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            apk.state = NO_DOWNLOAD;
            setDownloadedState(apk);
        }
        mActivity.updateTalkAdapter();
    }

    private void setDownloadedState(ApkStruct apk) {
        String fileName = null;
        if (apk.state == NO_DOWNLOAD) {
            if (apk.downloadURL != null) {
                fileName = apk.downloadURL.substring(
                        apk.downloadURL.lastIndexOf('/') + 1);
            }
            String filePath = Util.getHotappDownloadPath();
            File dir = new File(filePath);
            if (dir.exists()) {
                for (File file : dir.listFiles()) {
                    if (file.isFile() && file.getName().equals(fileName)) {
                        apk.state = DOWNLOAD_FINISHED;
                        break;
                    }
                }
            }
        }
    }

    private void checkDownloadedState(ApkStruct apk) {
        if (apk.state == DOWNLOAD_FINISHED || apk.state == NO_DOWNLOAD) {
            boolean isClear = true;
            String fileName = null;
            if (apk.downloadURL != null) {
                fileName = apk.downloadURL.substring(
                        apk.downloadURL.lastIndexOf('/') + 1);
            }
            String filePath = Util.getHotappDownloadPath();
            File dir = new File(filePath);
            if (dir.exists()) {
                for (File file : dir.listFiles()) {
                    if (file.isFile() && file.getName().equals(fileName)) {
                        // do not clear downloaded state if exist
                        apk.state = DOWNLOAD_FINISHED;
                        isClear = false;
                        break;
                    }
                }
            }

            if (isClear) {
                apk.state = NO_DOWNLOAD;
                mActivity.updateTalkAdapter();
            }
        }
    }


    private void loadData() {
        if (mLoadState != DataLoadState.NO_LOAD) {
            // avoid load repeatedly
            return;
        }
        Log.i(TAG, "loadData(): bigin...");
        mLoadState = DataLoadState.LOADING;
        mLoadTaskRunning = true;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                HttpURLConnection connect = null;

                try {
                    URL url = new URL(STORE_URL);
                    connect = (HttpURLConnection) url.openConnection();
                    int nRC = connect.getResponseCode();
                    Log.i(TAG, "loadData(): nRC = " + nRC);

                    if (HttpURLConnection.HTTP_OK == nRC) {
                        InputStream is = connect.getInputStream();
                        String resultData = "";
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader bufferReader = new BufferedReader(isr);
                        String inputLine = "";
                        while ((inputLine = bufferReader.readLine()) != null) {
                            resultData += inputLine + "\n";
                        }

                        JSONArray array = new JSONArray(resultData);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonobject = array.getJSONObject(i);
                            ApkStruct item = new ApkStruct();
                            item.id = Integer.parseInt(jsonobject.getString("apk_id"));
                            item.name = jsonobject.getString("apk_name");
                            item.description = jsonobject.getString("description");
                            item.downloadNum = Integer.parseInt(jsonobject.getString("download_num"));
                            item.versionCode = Integer.parseInt(jsonobject.getString("version_code"));
                            item.versionName = jsonobject.getString("version_name");
                            item.fileSize = Integer.parseInt(jsonobject.getString("file_size"));
                            item.packageName = jsonobject.getString("package_name");
                            item.downloadURL = jsonobject.getString("download_url");
                            item.iconURL = jsonobject.getString("icon_url");
                            mApkList.add(item);
                            Log.i("heqianqian", "apkname===" + item.name);
                        }
                        is.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "loadData(): Can't parse URL!");
                } finally {
                    if (connect != null) {
                        connect.disconnect();
                    }
                }

                // get the top3 of hot-app list
                //getTop3();
                //loadAd();
                // get the name of apk-list to customizer ASR hot-app slot resource
                updateASRSlotDatas();
                // set the final load state
                if (!mApkList.isEmpty()) {
                    mLoadState = DataLoadState.LOADED;
                } else {
                    mLoadState = DataLoadState.NO_LOAD;
                }
                synchronized (mLoadingLock) {
                    mLoadTaskRunning = false;
                    mLoadingLock.notifyAll();
                }
                Log.i(TAG, "loadData(): end, mLoadState = " + mLoadState);

                return null;
            }
        }.execute();
    }

    /**
     * covert unicode encode "\\uXXXX" characters to String
     *
     * @param unicodeStr unicode characters
     * @return String
     */
    private String unicode2String(CharSequence unicodeStr) {
        // \n \BBس\B5(0xa)
        // \t ˮƽ\D6Ʊ\ED\B7\FB(0x9)
        // \b \BFո\F1(0x8)
        // \r \BB\BB\D0\D0(0xd)
        // \f \BB\BBҳ(0xc)
        // \' \B5\A5\D2\FD\BA\C5(0x27)
        // \" ˫\D2\FD\BA\C5(0x22)
        // \\ \B7\B4б\B8\DC(0x5c)
        // \/ б\B8\DC(0x2f)
        // \ddd \C8\FDλ\B0˽\F8\D6\C6
        // \udddd \CB\C4λʮ\C1\F9\BD\F8\D6\C6

        StringBuffer strBuf = new StringBuffer();
        if (DEBUG) {
            Log.i(TAG, "unicodeStr = " + unicodeStr.toString());
        }
        for (int i = 0; i < unicodeStr.length(); i++) {
            char c = unicodeStr.charAt(i);
            if (c == '\\') {
                if (i == unicodeStr.length() - 1) {
                    strBuf.append(c);
                    break;
                }
                char c1 = unicodeStr.charAt(++i);

                switch (c1) {
                    case 'n':
                        strBuf.append((char) 0xa);
                        break;
                    case 't':
                        strBuf.append((char) 0x9);
                        break;
                    case 'b':
                        strBuf.append((char) 0x8);
                        break;
                    case 'r':
                        strBuf.append((char) 0xd);
                        break;
                    case 'f':
                        strBuf.append((char) 0xc);
                        break;
                    case '\'':
                        strBuf.append((char) 0x27);
                        break;
                    case '"':
                        strBuf.append((char) 0x22);
                        break;
                    case '\\':
                        strBuf.append((char) 0x5c);
                        break;
                    case '/':
                        strBuf.append((char) 0x2f);
                        break;
                    case 'u':
                        // hex with 4 bytes
                        StringBuffer hex = new StringBuffer();
                        hex.append(unicodeStr.charAt(++i));
                        hex.append(unicodeStr.charAt(++i));
                        hex.append(unicodeStr.charAt(++i));
                        hex.append(unicodeStr.charAt(++i));
                        int hexData = Integer.parseInt(hex.toString(), 16);
                        strBuf.append((char) hexData);
                        break;
                    default:
                        // oct with 3 bytes
                        StringBuffer oct = new StringBuffer();
                        oct.append(c1);
                        oct.append(unicodeStr.charAt(++i));
                        oct.append(unicodeStr.charAt(++i));
                        int otcData = Integer.parseInt(oct.toString(), 8);
                        strBuf.append((char) otcData);
                        break;
                }
            } else {
                strBuf.append(c);
            }
        }

        return strBuf.toString();
    }

    private View createWidget(String tag) {
        View widget = null;

        if (SpeechData.HOTAPP_SEARCH_RESULT_TAG == tag) {
            widget = createSearchWidget();
        } else if (SpeechData.HOTAPP_SHOW_TOP_TAG == tag) {
            widget = createTopWidget();
        } else {
            throw new IllegalArgumentException("createWidget(): error tag!");
        }

        return widget;
    }

    private View createSearchWidget() {

        View main = mInflater.inflate(R.layout.hotapp_search_panel, null);
        final ViewGroup list = (ViewGroup) main.findViewById(R.id.app_list);

        // add list
        for (int i = 0; i < mFindResults.size(); i++) {
            apk = mFindResults.get(i);
            View item = mInflater.inflate(R.layout.hotapp_search_list_item, list, false);

            Bitmap icon = mIconCachePool.get(apk.id);
            if (icon != null) {
                ImageView photo = (ImageView) item.findViewById(R.id.icon);
                photo.setImageBitmap(icon);
            }

            TextView name = (TextView) item.findViewById(R.id.name);
            name.setText(apk.name);

            // versionName + size
            TextView info = (TextView) item.findViewById(R.id.info);
            float size = apk.fileSize / 1024.f; // K bytes
            String sizeStr;
            if (size > 1024) {
                size = size / 1024; // M bytes
                sizeStr = String.format("%.3fM", size);
            } else {
                sizeStr = String.format("%dK", (int) size);
            }
            String infoStr = String.format(mActivity.getString(R.string.hotapp_item_info),
                    apk.versionName, sizeStr);
            info.setText(infoStr);

            if (i < mFindResults.size() - 1) {
                View line = item.findViewById(R.id.line);
                line.setVisibility(View.VISIBLE);
            }

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = list.indexOfChild(v);
                    if (index != mSelectedIndex) {
                        mSelectedIndex = index;
                        mActivity.updateTalkAdapter();
                    }
                }
            });

            if (i == 0) {
                // head
                item.setBackgroundResource(R.drawable.panel_list_item);
            } else if (i == mFindResults.size() - 1) {
                // tail
                item.setBackgroundResource(R.drawable.panel_list_item);
            } else {
                // middle
                item.setBackgroundResource(R.drawable.panel_list_item);
            }

            if (mFindResults.size() > 1) {
                // selected state
                if (i == mSelectedIndex) {
                    item.setSelected(true);
                    mSelectedItem = item;
                }
            } else {
                mSelectedItem = item;
            }

            list.addView(item);
        }

        View descriptionBtn = main.findViewById(R.id.description);
        // final ComponentName component = ParseSlotDataFactory
        // .getAppComponentName(mActivity, mActivity.getString(R.string.droimarket));
        descriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(mFindResults.get(mSelectedIndex).name);
                builder.setMessage(mFindResults.get(mSelectedIndex).description);
                builder.show();
                //mActivity.onSpeak(mFindResults.get(mSelectedIndex).description);
                // Intent intent = new Intent();
                //intent.setComponent(component);
                //Util.launcherIntent(mActivity, intent);
            }
        });

        View downloadBtn = main.findViewById(R.id.download);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDownloadClick((TextView) v, mSelectedItem,
                        mFindResults.get(mSelectedIndex));
            }
        });
        if (mSelectedIndex != -1 && !(action.equals(mActivity.getString(R.string.response_search_no_result)))) {
            updateDownloadLabel((TextView) downloadBtn, mSelectedItem,
                    mFindResults.get(mSelectedIndex));
        }

        if (mFindResults.size() > 1 && mSelectedIndex == -1) {
            Util.setEnabled(descriptionBtn, false);
            Util.setEnabled(downloadBtn, false);
        }

        return main;
    }


    private void updateASRSlotDatas() {
        Log.i(TAG, "updateASRSlotDatas()...");
        ArrayList<String> datas = new ArrayList<String>();
        for (ApkStruct apk : mApkList) {
            datas.add(apk.name);
        }

        mActivity.updateHotappSlotDatas(datas);
    }

    private void tempWriteVoiceKeyFile() {
        StringBuffer buffer = new StringBuffer();
        for (ApkStruct apk : mApkList) {
            buffer.append(apk.name + "\n");
        }

        try {
            FileOutputStream fos = mActivity.openFileOutput("voice_keys", Activity.MODE_PRIVATE);
            fos.write(buffer.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ProgressBar progressBars;

    private void onDownloadClick(TextView v, View item, ApkStruct apk) {
        progressBars = (ProgressBar) item.findViewById(R.id.bar);
        //View pathV = item.findViewById(R.id.path);

        if (apk.state == DOWNLOADING) {
            // cancel action
            DownloadTask task = mDownloadTasks.get(apk.id);
            if (task != null) {
                task.cancel(true);
                mDownloadTasks.remove(apk.id);
            }

            apk.state = NO_DOWNLOAD;
            v.setText(R.string.btn_download);
            progressBars.setVisibility(View.GONE);
            // pathV.setVisibility(View.GONE);
        } else if (apk.state == NO_DOWNLOAD || apk.state == INSTALLED_UPGRADE) {
            // download action
            DownloadTask task = downloadApk(v, item, apk);
            if (task != null) {
                apk.state = DOWNLOADING;
                mDownloadTasks.put(apk.id, task);
                ShowAPKNotification(apk);
                v.setText(R.string.cancel);
//                pathV.setVisibility(View.VISIBLE);
                progressBars.setVisibility(View.VISIBLE);
                progressBars.setProgress(0);
            }
        } else if (apk.state == DOWNLOAD_FINISHED) {
            // install action
            String fileName = apk.downloadURL.substring(
                    apk.downloadURL.lastIndexOf('/') + 1);
            String filePath = Util.getHotappDownloadPath() + fileName;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), PACKAGE_MIME_TYPE);
            Util.launcherIntent(mActivity, intent);

        } else if (apk.state == INSTALLED) {
            // uninstall action
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + apk.packageName));
            //Util.launcherIntent(mActivity, intent);
            mActivity.startActivity(intent);
        }
    }

    private void updateDownloadLabel(TextView v, View item, ApkStruct apk) {
        progressBars = (ProgressBar) item.findViewById(R.id.bar);
        View progressBar = item.findViewById(R.id.bar);
        // View pathV = item.findViewById(R.id.path);
        progressBar.setVisibility(View.GONE);
        //pathV.setVisibility(View.GONE);
        if (apk.state == DOWNLOADING) {
            v.setText(R.string.cancel);
            progressBar.setVisibility(View.VISIBLE);
            //pathV.setVisibility(View.VISIBLE);
            // update the relative view of download task
            DownloadTask task = mDownloadTasks.get(apk.id);
            if (task != null) {
                task.updateView(v, item);
            }
        } else if (apk.state == NO_DOWNLOAD || apk.state == INSTALLED_UPGRADE) {
            v.setText(R.string.btn_download);
        } else if (apk.state == DOWNLOAD_FINISHED) {
            v.setText(R.string.install);

        } else if (apk.state == INSTALLED) {
            v.setText(R.string.uninstall);
        }
    }

    private DownloadTask downloadApk(TextView btn, View item, ApkStruct apk) {
        if (mSelectedIndex == -1) {
            return null;
        }

        // 1. Check external storage's space status
        int state = Util.getStorageSpaceStatus(apk.fileSize);
        int strRes = 0;
        switch (state) {
            case Util.UNAVALIABLE:
                strRes = R.string.response_storage_unavailable;
                break;
            case Util.SPACE_NOT_ENOUGH:
                strRes = R.string.response_storage_space_not_enough;
                break;
            default:
                break;
        }

        if (Util.STORAGE_OK != state) {
            String action = mActivity.getString(strRes);
            mActivity.onResponseSpeechResult(
                    new SpeechData(SpeechData.RESPONSE_TEXT_MODE, action), false);
            mActivity.onSpeak(action);
            return null;
        }
        DownloadTask task = null;
        // 2. Prepare file path
        String filePath = Util.getHotappDownloadPath();
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (apk.downloadURL != null) {
            String fileName = apk.downloadURL.substring(
                    apk.downloadURL.lastIndexOf('/') + 1);
            filePath += fileName;
            File file = new File(filePath);
            // if exist, override
            if (file.exists()) {
                file.delete();
            }
            Log.i(TAG, "downloadApk(): filePath = " + filePath
                    + ", file can write = " + file.canWrite());

            // 3. begin download
            task = new DownloadTask(apk, filePath, btn, item);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return task;

    }

    class DownloadTask extends AsyncTask<Void, Integer, Boolean> {
        private ApkStruct mDownloadApk;
        private String mFilePath;
        private TextView mBtnLabel;
        private ProgressBar mProgressBar;
        // private TextView mHintPath;

        public DownloadTask(ApkStruct apk, String filePath, TextView btn, View item) {
            mDownloadApk = apk;
            mFilePath = filePath;
            updateView(btn, item);
        }

        public void updateView(TextView btn, View item) {
            mBtnLabel = btn;
            // mHintPath = (TextView) item.findViewById(R.id.path);
            // mHintPath.setText(String.format(
            //mActivity.getString(R.string.hotapp_download_path_hint), mFilePath));
            mProgressBar = (ProgressBar) item.findViewById(R.id.bar);
            mProgressBar.setMax(mDownloadApk.fileSize);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection connect = null;
            int downloadedFileSize = 0;
            int cacheSize = 0;
            Boolean downloadOk = false;

            try {
                URL url = new URL(mDownloadApk.downloadURL);
                connect = (HttpURLConnection) url.openConnection();
                int nRC = connect.getResponseCode();
                Log.i(TAG, "DownloadTask: nRC = " + nRC
                        + ", download URL = " + mDownloadApk.downloadURL);

                if (HttpURLConnection.HTTP_OK == nRC) {
                    InputStream is = connect.getInputStream();
                    FileOutputStream fos = new FileOutputStream(mFilePath);
                    byte[] buffer = new byte[1 << 10];
                    int readBytes = 0;

                    while ((readBytes = is.read(buffer)) != -1 && !isCancelled()) {
                        fos.write(buffer, 0, readBytes);
                        cacheSize += readBytes;

                        if (cacheSize >= DOWNLOAD_PROGRESS_UPDATE_CACHE_SIZE) {
                            downloadedFileSize += cacheSize;
                            cacheSize = 0;
                            publishProgress(downloadedFileSize);
                        }
                    }

                    is.close();
                    fos.close();

                    if (isCancelled()) {
                        File file = new File(mFilePath);
                        file.delete();
                        ntfm.cancel(mDownloadApk.id);
                    } else {
                        downloadOk = true;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
                // delete cache file.
                File file = new File(mFilePath);
                if (file.exists()) {
                    file.delete();
                }
            } finally {
                if (connect != null) {
                    connect.disconnect();
                }
            }

            return downloadOk;
        }

        ;

        @Override
        protected void onProgressUpdate(Integer... values) {
            downloaded = values[0];
            mProgressBar.setProgress(downloaded);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(TAG, "DownloadTask: finished!");
            mDownloadTasks.remove(mDownloadApk.id);
            mProgressBar.setVisibility(View.GONE);
            //mHintPath.setVisibility(View.GONE);

            String action = null;
            if (result) {
                // download finished
                mDownloadApk.state = DOWNLOAD_FINISHED;
                mBtnLabel.setText(R.string.install);
                action = String.format(
                        mActivity.getString(R.string.hotapp_download_success),
                        mDownloadApk.name);
                String fileName = mDownloadApk.downloadURL.substring(
                        mDownloadApk.downloadURL.lastIndexOf('/') + 1);
                String filePath = Util.getHotappDownloadPath() + fileName;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(filePath)), PACKAGE_MIME_TYPE);
                Util.launcherIntent(mActivity, intent);
                ntfm.cancel(mDownloadApk.id);
                mActivity.onResponseSpeechResult(
                        new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mDownloadApk.name + mActivity.getString(R.string.hotapp_savepath_hint) + filePath), false);

                mActivity.onSpeak(mDownloadApk.name + mActivity.getString(R.string.hotapp_savepath_hint));
            } else {
                // download fail
                mDownloadApk.state = NO_DOWNLOAD;
                mBtnLabel.setText(R.string.btn_download);
                action = String.format(
                        mActivity.getString(R.string.hotapp_download_fail),
                        mDownloadApk.name);
            }

            mActivity.onResponseSpeechResult(
                    new SpeechData(SpeechData.RESPONSE_TEXT_MODE, action), false);
            mActivity.onSpeak(action);
        }
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e("Exception", e.getMessage());
            return null;
        }
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive(): action = " + action);

            if (Intent.ACTION_PACKAGE_ADDED.equals(action) ||
                    Intent.ACTION_PACKAGE_REMOVED.equals(action) ||
                    Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                if (packageName == null || packageName.length() == 0) {
                    // they sent us a bad intent
                    return;
                }

                // update top hot-app state if equal
                if (mTopApps != null) {
                    for (ApkStruct apk : mTopApps) {
                        if (packageName.equals(apk.packageName)) {
                            updateApkState(apk, action);
                            break;
                        }
                    }
                }

                // update selected hot-app state if equal
                if (mSelectedIndex != -1 && !mFindResults.isEmpty()) {
                    ApkStruct apk = mFindResults.get(mSelectedIndex);
                    if (packageName.equals(apk.packageName)) {
                        updateApkState(apk, action);
                    }
                }
            } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action) ||
                    FILEMANAGER_REFRESH.equals(action)) {
                // set NO_DOWNLOAD state if APK file be removed
                if (mTopApps != null) {
                    for (ApkStruct apk : mTopApps) {
                        checkDownloadedState(apk);
                    }
                }
                if (mSelectedIndex != -1 && !mFindResults.isEmpty()) {
                    ApkStruct apk = mFindResults.get(mSelectedIndex);
                    checkDownloadedState(apk);
                }
            }
        }
    };

    class ApkStruct {
        int id;
        String name;
        String iconURL;
        int versionCode;
        String versionName;
        int fileSize;
        String downloadURL;
        int downloadNum;
        String packageName;
        String description;
        String voiceKey;

        int state;

        static final String ID_FIELD = "apk_id";
        static final String NAME_FIELD = "apk_name";
        static final String ICON_FIELD = "icon_url";
        static final String VERSIONCODE_FIELD = "version_code";
        static final String VERSIONNAME_FIELD = "version_name";
        static final String FILESIZE_FIELD = "file_size";
        static final String DOWNLOADURL_FIELD = "download_url";
        static final String DOWNLOADNUM_FIELD = "download_num";
        static final String PACKAGENAME_FIELD = "package_name";
        static final String DESCRIPTION_FIELD = "description";
        static final String VOICEKEY_FIELD = "keyword";

        @Override
        public String toString() {
            return ("Apk item id = " + id + ", name = " + name + ", iconURL = " + iconURL
                    + ", versionCode = " + versionCode + ", versionName = " + versionName
                    + ", fileSize = " + fileSize + ", downloadURL = " + downloadURL
                    + ", downloadNum = " + downloadNum + ", packageName = " + packageName
                    + ", description = " + description);
        }
    }

    private View createTopWidget() {
        Log.i(TAG, "createTopWidget()...");
        View main = mInflater.inflate(R.layout.hotapp_top_panel, null);
        final ViewGroup list = (ViewGroup) main.findViewById(R.id.app_list);

        // add list
        for (int i = 0; i < mTopApps.length; i++) {
            final ApkStruct apk = mTopApps[i];
            final View item = mInflater.inflate(R.layout.hotapp_top_list_item, list, false);

            Bitmap icon = mIconCachePool.get(apk.id);
            if (icon != null) {
                ImageView photo = (ImageView) item.findViewById(R.id.icon);
                photo.setImageBitmap(icon);
            }

            TextView name = (TextView) item.findViewById(R.id.name);
            name.setText(apk.name);

            // versionName + size
            TextView info = (TextView) item.findViewById(R.id.info);
            float size = apk.fileSize / 1024.f; // K bytes
            String sizeStr;
            if (size > 1024) {
                size = size / 1024; // M bytes
                sizeStr = String.format("%.3fM", size);
            } else {
                sizeStr = String.format("%dK", size);
            }
            String infoStr = String.format(mActivity.getString(R.string.hotapp_item_info),
                    apk.versionName, sizeStr);
            info.setText(infoStr);

            // voice key hint
            TextView voiceHint = (TextView) item.findViewById(R.id.voice_hint);
            voiceHint.setText(mActivity.getString(R.string.prompt_item_head)
                    + mActivity.getString(R.string.hotapp_top_hint_mid)
                    + apk.voiceKey + "\"");

            if (i < mTopApps.length - 1) {
                View line = item.findViewById(R.id.line);
                line.setVisibility(View.VISIBLE);
            }

            View downloadBtn = item.findViewById(R.id.download);
            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDownloadClick((TextView) v, item, apk);
                }
            });
            updateDownloadLabel((TextView) downloadBtn, item, apk);

            list.addView(item);
        }

        return main;
    }


    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public void ShowAPKNotification(final ApkStruct apk) {
        ntfm = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        ntf = new Notification(android.R.drawable.stat_sys_download, "", System.currentTimeMillis());
        ntf.contentView = new RemoteViews(mActivity.getPackageName(), R.layout.download_nitification_panel);
        ntf.contentView.setImageViewBitmap(R.id.app_icon, mIconCachePool.get(apk.id));
        ntf.contentView.setTextViewText(R.id.app_name, apk.name);
        ntf.contentView.setTextViewText(R.id.app_status, mActivity.getString(R.string.hotapp_downloading_hint));

        Intent ntfinteIntent = new Intent(Intent.ACTION_MAIN);
        ntfinteIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        ntfinteIntent.setComponent(new ComponentName(mActivity.getPackageName(), mActivity.getPackageName() + "." + mActivity.getLocalClassName()));
        ntfinteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent ntfpendingIntent = PendingIntent.getActivity(mActivity, 0, ntfinteIntent, 0);
        ntf.contentIntent = ntfpendingIntent;
        ntfm.notify(apk.id, ntf);
    }


}
