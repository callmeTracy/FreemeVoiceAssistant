package com.freeme.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.freeme.voiceassistant.R;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

public class Util {

    private static final float DISABLE_ALPHA = 0.4f;
    public static final String APP_ID = "20160217000012251";
    public static final String APP_KEY = "vsyirugRiJ9UCWfyZ_Rv";
    public static final String AND = "&";
    private static final String DOWNLOAD_FOLDER_NAME = "/hotApp/";
    private static final String TAG = "[Freeme]VA.Util";
    public static final int DAYS_IN_A_WEEK = 7;
    public static final int STORAGE_OK = 0;
    public static final int UNAVALIABLE = -1;
    public static final int SPACE_NOT_ENOUGH = -2;
    public static final String APIKEY = "9a4ef4cfa178c25132978775318ba5ba";

    /**
     * @param context
     * @param intent  Strip the activity
     */
    public static void launcherIntent(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    /**
     * Check the SIM card is ready
     *
     * @param context
     * @return
     */
    public static boolean isSimCardExist(Context context) {
        try {
            TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            return TelephonyManager.SIM_STATE_READY == mgr
                    .getSimState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setEnabled(View v, boolean enabled) {
        v.setEnabled(enabled);

        if (enabled) {
            v.setAlpha(1f);
        } else {
            v.setAlpha(DISABLE_ALPHA);
        }
    }

    public static int getIcon(int icon, String imgSize) {
        int resId = -1;
        switch (icon) {
            case 0:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_0;
                } else {
                    resId = R.drawable.a_0;
                }
                break;
            case 1:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_1;
                } else {
                    resId = R.drawable.a_1;
                }
                break;
            case 2:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_2;
                } else {
                    resId = R.drawable.a_2;
                }
                break;
            case 3:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 4:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_4;
                } else {
                    resId = R.drawable.a_4;
                }
                break;
            case 5:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_4;
                } else {
                    resId = R.drawable.a_4;
                }
                break;
            case 6:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_6;
                } else {
                    resId = R.drawable.a_6;
                }
                break;
            case 7:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 8:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_8;
                } else {
                    resId = R.drawable.a_8;
                }
                break;
            case 9:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 10:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 11:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 12:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 13:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 14:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_8;
                } else {
                    resId = R.drawable.a_8;
                }
                break;
            case 15:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_7;
                } else {
                    resId = R.drawable.a_7;
                }
                break;
            case 16:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_7;
                } else {
                    resId = R.drawable.a_7;
                }
                break;
            case 17:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_7;
                } else {
                    resId = R.drawable.a_7;
                }
                break;
            case 18:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_9;
                } else {
                    resId = R.drawable.a_9;
                }
                break;
            case 19:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 20:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_5;
                } else {
                    resId = R.drawable.a_5;
                }
                break;
            case 21:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 22:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 23:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 24:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 25:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_3;
                } else {
                    resId = R.drawable.a_3;
                }
                break;
            case 26:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_7;
                } else {
                    resId = R.drawable.a_7;
                }
                break;
            case 27:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_7;
                } else {
                    resId = R.drawable.a_7;
                }
                break;
            case 28:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_7;
                } else {
                    resId = R.drawable.a_7;
                }
                break;
            case 29:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_5;
                } else {
                    resId = R.drawable.a_5;
                }
                break;
            case 30:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_5;
                } else {
                    resId = R.drawable.a_5;
                }
                break;
            case 31:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_5;
                } else {
                    resId = R.drawable.a_5;
                }
                break;
            case -1:
                if (imgSize.equals("big")) {
                    resId = R.drawable.b_10;
                } else {
                    resId = R.drawable.a_10;
                }
                break;
            default:
                break;
        }
        return resId;
    }

    public static ComponentName getAppComponentName(Context context, String app_name) {
        ComponentName component = null;
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = pm.queryIntentActivities(intent, 0);
        if(context.getString(R.string.app_xiaokang).equals(app_name)){
            app_name=context.getString(R.string.app_zhjkyun);
        }else if(context.getString(R.string.app_xiaoyun).equals(app_name)||context.getString(R.string.app_znyun).equals(app_name)){
            if(checkPackage(context,"com.cn21.ecloud")){
                component = new ComponentName(
                        "com.cn21.ecloud", "com.cn21.ecloud.activity.MainPageActivity");
                return component;
            }else{
                return null;
            }

        }
        for (ResolveInfo resovleInfo : appList) {
            String label = resovleInfo.loadLabel(pm).toString();
            if (app_name.equalsIgnoreCase(label)) {
                component = new ComponentName(
                        resovleInfo.activityInfo.packageName,
                        resovleInfo.activityInfo.name);
                break;
            }
        }

        return component;
    }

    public static boolean checkPackage(Context context,String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }


    public static String returnAnswer(Context context, String question) {

        if (question.equals(context.getText(R.string.ques_hello))||question.equals(context.getText(R.string.ques_hello_a))) {
            return context.getText(R.string.answ_hello).toString();
        } else if (question.contains(context.getText(R.string.ques_marry)) && question.startsWith((String) context.getText(R.string.ques_marry), 0)) {
            return context.getText(R.string.answ_marry).toString();
        } else if (question.contains(context.getText(R.string.ques_name)) && question.startsWith((String) context.getText(R.string.ques_name), 0)) {
            return context.getText(R.string.answ_name).toString();
        } else if (question.contains(context.getText(R.string.ques_school)) && question.startsWith((String) context.getText(R.string.ques_school), 0)) {
            return context.getText(R.string.answ_school).toString();
        } else if (question.contains(context.getText(R.string.ques_old)) && question.startsWith((String) context.getText(R.string.ques_old), 0)) {
            return context.getText(R.string.answ_old).toString();
        } else if (question.contains(context.getText(R.string.ques_home)) && question.startsWith((String) context.getText(R.string.ques_home), 0)) {
            return context.getText(R.string.answ_home).toString();
        } else if (question.contains(context.getText(R.string.ques_weight)) && question.startsWith((String) context.getText(R.string.ques_weight), 0)) {
            return context.getText(R.string.answ_weight).toString();
        } else if (question.contains(context.getText(R.string.ques_father)) && question.startsWith((String) context.getText(R.string.ques_father), 0)) {
            return context.getText(R.string.answ_father).toString();
        } else if (question.contains(context.getText(R.string.ques_silly)) && question.startsWith((String) context.getText(R.string.ques_silly), 0)) {
            return context.getText(R.string.answ_silly).toString();
        } else if (question.contains(context.getText(R.string.ques_redbag)) && question.startsWith((String) context.getText(R.string.ques_redbag), 0)) {
            return context.getText(R.string.answ_redbag).toString();
        } else if (question.contains(context.getText(R.string.ques_hold)) && question.startsWith((String) context.getText(R.string.ques_hold), 0)) {
            return context.getText(R.string.answ_hold).toString();
        } else if (question.contains(context.getText(R.string.ques_drunk)) && question.startsWith((String) context.getText(R.string.ques_drunk), 0)) {
            return context.getText(R.string.answ_drunk).toString();
        } else if (question.contains(context.getText(R.string.ques_good)) && question.startsWith((String) context.getText(R.string.ques_good), 0)) {
            return context.getText(R.string.answ_good).toString();
        } else if (question.contains(context.getText(R.string.ques_haha)) && question.startsWith((String) context.getText(R.string.ques_haha), 0)) {
            return context.getText(R.string.answ_haha).toString();
        } else if (question.contains(context.getText(R.string.ques_easy)) && question.startsWith((String) context.getText(R.string.ques_easy), 0)) {
            return context.getText(R.string.answ_easy).toString();
        } else if (question.contains(context.getText(R.string.ques_whoareyou)) && question.startsWith((String) context.getText(R.string.ques_whoareyou), 0)) {
            return context.getText(R.string.answ_whoareyou).toString();
        } else if (question.contains(context.getText(R.string.ques_work)) && question.startsWith((String) context.getText(R.string.ques_work), 0)) {
            return context.getText(R.string.answ_work).toString();
        } else if (question.contains(context.getText(R.string.ques_friend)) && question.startsWith((String) context.getText(R.string.ques_friend), 0)) {
            return context.getText(R.string.answ_friend).toString();
        } else if (question.contains(context.getText(R.string.ques_eat)) && question.startsWith((String) context.getText(R.string.ques_eat), 0)) {
            return context.getText(R.string.answ_eat).toString();
        } else if (question.contains(context.getText(R.string.ques_girl)) || question.contains(context.getText(R.string.ques_boy))) {
            return context.getText(R.string.answ_boyorgirl).toString();
        } else if (question.contains(context.getText(R.string.ques_bring)) && question.startsWith((String) context.getText(R.string.ques_bring), 0)) {
            return context.getText(R.string.answ_bring).toString();
        } else if (question.contains(context.getText(R.string.ques_lovely)) && question.startsWith((String) context.getText(R.string.ques_lovely), 0)) {
            return context.getText(R.string.answ_lovely).toString();
        } else if (question.contains(context.getText(R.string.ques_hi)) && question.startsWith((String) context.getText(R.string.ques_hi), 0)) {
            return context.getText(R.string.answ_hi).toString();
        }
        return "";
    }


    public static List<String> getAppNames(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = pm.queryIntentActivities(intent, 0);
        List<String> appnames = new ArrayList<String>();
        for (ResolveInfo resovleInfo : appList) {
            String label = resovleInfo.loadLabel(pm).toString();
            appnames.add(label);
        }

        return appnames;
    }

    public static int returntarget(Context mContext, String targetstr) {

        int target = 0;
        if (mContext.getString(R.string.zh).equals(targetstr) || "1".equals(targetstr)) {
            target = 0;
        } else if (mContext.getString(R.string.en).equals(targetstr) || mContext.getString(R.string.enw).equals(targetstr) || "0".equals(targetstr)) {
            target = 1;
        } else if (mContext.getString(R.string.yue).equals(targetstr) || "141".equals(targetstr)) {
            target = 141;
        } else if (mContext.getString(R.string.wyw).equals(targetstr) || "110".equals(targetstr)) {
            target = 110;
        } else if (mContext.getString(R.string.jp).equals(targetstr) || mContext.getString(R.string.jpw).equals(targetstr) || "3".equals(targetstr)) {
            target = 3;
        } else if (mContext.getString(R.string.kor).equals(targetstr) || mContext.getString(R.string.korw).equals(targetstr) || "4".equals(targetstr)) {
            target = 4;
        } else if (mContext.getString(R.string.fra).equals(targetstr) || mContext.getString(R.string.fraw).equals(targetstr) || "10".equals(targetstr)) {
            target = 10;
        } else if (mContext.getString(R.string.th).equals(targetstr) || "6".equals(targetstr)) {
            target = 6;
        } else if (mContext.getString(R.string.ara).equals(targetstr) || "13".equals(targetstr)) {
            target = 13;
        } else if (mContext.getString(R.string.ru).equals(targetstr) || mContext.getString(R.string.ruw).equals(targetstr) || "11".equals(targetstr)) {
            target = 11;
        } else if (mContext.getString(R.string.pt).equals(targetstr) || "34".equals(targetstr)) {
            target = 34;
        } else if (mContext.getString(R.string.de).equals(targetstr) || "9".equals(targetstr)) {
            target = 9;
        } else if (mContext.getString(R.string.it).equals(targetstr) || "47".equals(targetstr)) {
            target = 47;
        } else if (mContext.getString(R.string.el).equals(targetstr) || "44".equals(targetstr)) {
            target = 44;
        } else if (mContext.getString(R.string.nl).equals(targetstr) || "24".equals(targetstr)) {
            target = 24;
        } else if (mContext.getString(R.string.pl).equals(targetstr) || "19".equals(targetstr)) {
            target = 19;
        } else if (mContext.getString(R.string.bul).equals(targetstr) || "16".equals(targetstr)) {
            target = 16;
        } else if (mContext.getString(R.string.est).equals(targetstr) || "14".equals(targetstr)) {
            target = 14;
        } else if (mContext.getString(R.string.dan).equals(targetstr) || "21".equals(targetstr)) {
            target = 21;
        } else if (mContext.getString(R.string.fin).equals(targetstr) || "23".equals(targetstr)) {
            target = 23;
        } else if (mContext.getString(R.string.cs).equals(targetstr) || "26".equals(targetstr)) {
            target = 26;
        } else if (mContext.getString(R.string.rom).equals(targetstr) || "30".equals(targetstr)) {
            target = 30;
        } else if (mContext.getString(R.string.slo).equals(targetstr) || "39".equals(targetstr)) {
            target = 39;
        } else if (mContext.getString(R.string.swe).equals(targetstr) || "35".equals(targetstr)) {
            target = 35;
        } else if (mContext.getString(R.string.hu).equals(targetstr) || "45".equals(targetstr)) {
            target = 45;
        } else if (mContext.getString(R.string.spa).equals(targetstr)) {
            target = 65;
        }
        return target;
    }


    public static ArrayList<String> getSongName(Context mContext) {
        ArrayList<String> songs = new ArrayList<String>();
        String log = "";
        final String[] selectSong = {MediaStore.Audio.Media.TITLE};
        final int COL_NAME = 0;

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selectSong, null, null, null);

            if (cursor == null) {
                Log.i("heqianqian", "Error: query song returns null");
                return null;
            } else if (cursor.getCount() <= 0) {
                if (cursor != null && !cursor.isClosed())
                    cursor.close();
                return null;
            }

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    String SongName = cursor.getString(COL_NAME);
                    if (!TextUtils.isEmpty(SongName)) {
                        if (searchList(songs, SongName)) {
                            Log.i("heqianqian", "duplicate song: " + SongName);
                        } else {
                            songs.add(SongName);
                            log += SongName + ";";
                        }
                    }
                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return songs;
    }


    public static ArrayList<String> getArtistName(Context mContext) {
        ArrayList<String> artist = new ArrayList<String>();
        String log = "";
        final String[] selectArtist = {MediaStore.Audio.AudioColumns.ARTIST};
        final int COL_NAME = 0;

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selectArtist, null, null, null);

            if (cursor == null) {
                Log.i("heqianqian", "Error: query artist returns null");
                return null;
            } else if (cursor.getCount() <= 0) {
                if (cursor != null && !cursor.isClosed())
                    cursor.close();
                return null;
            }

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    String ArtistName = cursor.getString(COL_NAME);
                    if (!TextUtils.isEmpty(ArtistName)) {
                        if (searchList(artist, ArtistName)) {
                        } else {
                            artist.add(ArtistName);
                            log += ArtistName + ";";
                        }
                    }
                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return artist;
    }


    /**
     * @param context
     * @return
     */
    public static ArrayList<String> getContactsNameList(Context mContext) {
        ArrayList<String> contacts = new ArrayList<String>();
        String log = "";

        final String[] selectContact = {ContactsContract.Contacts.DISPLAY_NAME};
        final int COL_NAME = 0;

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, selectContact, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

            if (cursor == null) {
                Log.i("heqianqian", "Error: query contact returns null");
                return null;
            } else if (cursor.getCount() <= 0) {
                if (cursor != null && !cursor.isClosed())
                    cursor.close();
                return null;
            }

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    String contactName = cursor.getString(COL_NAME);
                    if (!TextUtils.isEmpty(contactName)) {
                        if (searchList(contacts, contactName)) {
                            Log.i("heqianqian", "duplicate contact: " + contactName);
                        } else {
                            contacts.add(contactName);
                            log += contactName + ";";
                        }
                    }
                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return contacts;
    }


    public static boolean searchList(ArrayList list, String subStr) {
        if (list == null || list.size() < 1 || subStr == null) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            if (subStr.equals(list.get(i))) {
                return true;
            }
        }

        return false;
    }


    private static String md5(String string) {
        // TODO Auto-generated method stub
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();
            messageDigest.update(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString().toLowerCase(Locale.getDefault());
    }


    public static String getUrl(String content, String from, String to) {
        StringBuffer sb = new StringBuffer(
                "http://api.fanyi.baidu.com/api/trans/vip/translate?");
        try {
            sb.append("q=" + URLEncoder.encode(content, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sb.append(AND);
        sb.append("from=" + from);
        sb.append(AND);
        sb.append("to=" + to);
        sb.append(AND);
        sb.append("appid=" + APP_ID);
        sb.append(AND);
        String salt = (System.currentTimeMillis() / 1000) + "";
        sb.append("salt=" + salt);
        sb.append(AND);

        String sign = md5(APP_ID + content + salt + APP_KEY);
        sb.append("sign=" + sign);
        return sb.toString();

    }

    public static boolean isNetworkAvailable(Context context) {
        boolean result = false;
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    result = true;
                }
            }
        }

        return result;
    }


    public static String getHotappDownloadPath() {
        String filePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getPath() + DOWNLOAD_FOLDER_NAME;

        return filePath;
    }

    public static int getStorageSpaceStatus(long size) {
        int result = UNAVALIABLE;
        String state = Environment.getExternalStorageState();

        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            result = UNAVALIABLE;
        } else {
            String path = Environment.getExternalStorageDirectory().getPath();
            try {
                StatFs stat = new StatFs(path);
                if (stat.getAvailableBlocks() * (long) stat.getBlockSize() <= size) {
                    result = SPACE_NOT_ENOUGH;
                } else {
                    result = STORAGE_OK;
                }
            } catch (Exception e) {
                Log.i(TAG, "Fail to access external storage", e);
            }
        }

        return result;
    }

    //person result
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("apikey", APIKEY);
            connection.setDoOutput(true);
            connection.getOutputStream().write(httpArg.getBytes("UTF-8"));
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                Log.i("heqianqian", "strRead=======");
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("heqianqian", "onerror=======");
        }
        return result;
    }

}
