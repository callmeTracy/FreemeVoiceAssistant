package com.freeme.statistic;

import android.content.Context;

public class VoiceStatisticUtil {

    /* option id */
    public static final String OPTION_ENTER = "00130001";   //启动语音助手apk
    public static final String OPTION_EXIT = "00130002";   //退出语音助手apk

    public static final String OPTION_LONGPRESSMENU = "00130003";   //长按菜单键启动
    public static final String OPTION_OPENVOICEASSISTANT = "00130004";   //打开语音助手
    public static final String OPTION_OPENCALLMSG = "00130005";   //打开电短联
    public static final String OPTION_OPENMUSIC = "00130006";   //打开音乐

    public static final String OPTION_OPENWEATHER = "00130008";   //打开天气
    public static final String OPTION_OPENAPPLICATION = "00130009";   //打开应用
    public static final String OPTION_OPENSYSTEMSETTING = "00130010";   //打开系统设置
    public static final String OPTION_OPENTIMEDATE = "00130011";   //打开时间和日期
    public static final String OPTION_OPENTRANSLATE = "00130012";   //打开翻译

    public static final String OPTION_OPENKNOWLEDGE = "00130013";   //打开知识
    public static final String OPTION_OPENMAPGUIDE = "00130014";   //打开导航
    public static final String OPTION_OPENSEARCH = "00130015";   //打开搜索
    public static final String OPTION_OPENJOKE = "00130016";   //打开笑话


    public static void generateStatisticInfo(Context context, String optionId) {
        StatisticUtil.generateStatisticInfo(context, optionId);
    }

    public static void saveStatisticInfoToFileFromDB(Context context) {
        StatisticUtil.saveStatisticInfoToFileFromDB(context);
    }
}
