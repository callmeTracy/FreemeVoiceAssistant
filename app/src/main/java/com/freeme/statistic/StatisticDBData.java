
package com.freeme.statistic;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class StatisticDBData {
    public final static String ID = "_id";
    public final static String OPTION_ID = "op";
    public final static String OPTION_NUM = "n";
    public final static String OPTION_TIMES = "s";
    public final static String OPTION_TIMES_EXIT = "e";
    public final static String VERSION_CODE = "vc";
    public final static String VERSION_NAME = "vn";

    public static final String DEFAULT_SORT_ORDER = "_id asc";
    public static final String AUTHORITY = "com.freeme.voiceassistant.statistic";

    public static final int ITEM = 1;
    public static final int ITEM_ID = 2;

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.freeme.voiceasistant.statistic";
    public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.com.freeme.voiceassistant.statistic";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/item");

    public static class StatisticInfo {
        public String optionId;
        public int optionNum;
        public long optionTimes;
        public long optionTimesExit;
        public int versionCode;
        public String versionName;
    }


    public static void insertStatistic(Context context, StatisticInfo info) {
        if (context == null) {
            return;
        }

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null || info == null) {
            return;
        }


        ContentValues values = new ContentValues();
        values.put(OPTION_ID, info.optionId);
        values.put(OPTION_NUM, info.optionNum);
        values.put(OPTION_TIMES, info.optionTimes);
        values.put(OPTION_TIMES_EXIT, info.optionTimesExit);
        values.put(VERSION_CODE, info.versionCode);
        values.put(VERSION_NAME, info.versionName);
        resolver.insert(CONTENT_URI, values);
    }
}
