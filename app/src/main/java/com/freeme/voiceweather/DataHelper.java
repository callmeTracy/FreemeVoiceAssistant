package com.freeme.voiceweather;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.freeme.voiceassistant.R;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

public class DataHelper {
    public static final String SORT_ORDER_BY_CITY_CODE = "city_code ASC";
    public static final String SORT_ORDER_SORT_NUMBER = "sort ASC";
    public static final String SORT_ORDER_WEATHER_DATE = "date ASC";
    public static final String DATA = Environment.getDataDirectory().getPath();
    public static final String PACKAGE = "com.freeme.voiceassistant";
    public static final String DATABASE = "databases";
    public static final String DATABASE_NAME = "city.db";
    public static final String SEPARATOR = "/";
    public static final String DATABASE_PATH = DATA + DATA + SEPARATOR + PACKAGE + SEPARATOR + DATABASE + SEPARATOR;
    public ContentResolver mContentResolver;
    public Context mContext;

    public DataHelper(Context paramContext) {
        this.mContext = paramContext;
        this.mContentResolver = paramContext.getContentResolver();
    }

    public void closeCursor(Cursor paramCursor) {
        if (paramCursor != null)
            paramCursor.close();
    }

    public Cursor getCursorWidthCondition(Uri paramUri) {
        return getCursorWidthCondition(paramUri, null, null, null);
    }

    public Cursor getCursorWidthCondition(Uri paramUri, String[] paramArrayOfString) {
        return getCursorWidthCondition(paramUri, paramArrayOfString, null, null);
    }

    public Cursor getCursorWidthCondition(Uri paramUri, String[] paramArrayOfString, String paramString) {
        return getCursorWidthCondition(paramUri, paramArrayOfString, paramString, null);
    }

    public Cursor getCursorWidthCondition(Uri paramUri, String[] paramArrayOfString, String paramString1,
                                          String paramString2) {
        Cursor localCursor1 = null;
        try {
            Cursor localCursor2 = this.mContentResolver.query(paramUri,
                    paramArrayOfString, paramString1, null, paramString2);
            localCursor1 = localCursor2;
            return localCursor1;
        } catch (Exception localException) {
            while (true) {
                localException.printStackTrace();
            }
        }
    }

    public boolean isCursorEmpty(Cursor paramCursor) {
        if ((paramCursor == null) || (paramCursor.getCount() <= 0))
            return true;
        return false;
    }

    /**
     * use the city.db of raw ,create the city database
     */
    synchronized public static int init(Context context) {

        String outFileName = DATABASE_PATH + DATABASE_NAME;
        File dir = new File(outFileName);
        if (dir.exists())
            return 1;
        dir = new File(DATABASE_PATH);
        if (!dir.exists())
            dir.mkdir();
        InputStream input = null;
        OutputStream output = null;
        input = context.getResources().openRawResource(R.raw.city);
        try {
            output = new FileOutputStream(outFileName);
            byte[] buffer = new byte[2048];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                output.flush();
                output.close();
            } catch (IOException e) {
            }
            try {
                input.close();
            } catch (IOException e) {
            }
        }
        return 1;
    }

}
