
package com.freeme.statistic;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class StatisticContentProvider extends ContentProvider {
    private static final String DB_NAME = "Statistic.db";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "StatisticTable";

    private static final UriMatcher uriMatcher;

    private DBHelper mDbHelper;
    private ContentResolver mResolver;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(StatisticDBData.AUTHORITY, "item", StatisticDBData.ITEM);
        uriMatcher.addURI(StatisticDBData.AUTHORITY, "item/#", StatisticDBData.ITEM_ID);
    }

    private static final HashMap<String, String> articleProjectionMap;

    static {
        articleProjectionMap = new HashMap<String, String>();
        articleProjectionMap.put(StatisticDBData.ID, StatisticDBData.ID);
        articleProjectionMap.put(StatisticDBData.OPTION_ID, StatisticDBData.OPTION_ID);
        articleProjectionMap.put(StatisticDBData.OPTION_NUM, StatisticDBData.OPTION_NUM);
        articleProjectionMap.put(StatisticDBData.OPTION_TIMES, StatisticDBData.OPTION_TIMES);
        articleProjectionMap.put(StatisticDBData.OPTION_TIMES_EXIT, StatisticDBData.OPTION_TIMES_EXIT);
        articleProjectionMap.put(StatisticDBData.VERSION_CODE, StatisticDBData.VERSION_CODE);
        articleProjectionMap.put(StatisticDBData.VERSION_NAME, StatisticDBData.VERSION_NAME);
    }

    private static final String DB_CREATE = "create table " + DB_TABLE + " ("
            + StatisticDBData.ID + " integer primary key autoincrement, "
            + StatisticDBData.OPTION_ID + " text, "
            + StatisticDBData.OPTION_NUM + " text, "
            + StatisticDBData.OPTION_TIMES + " text, "
            + StatisticDBData.OPTION_TIMES_EXIT + " text, "
            + StatisticDBData.VERSION_CODE + " text, "
            + StatisticDBData.VERSION_NAME + " text " + ");";

    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;
        String sql = "DELETE FROM " + DB_TABLE + ";";
        db.execSQL(sql);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case StatisticDBData.ITEM:
                return StatisticDBData.CONTENT_TYPE;

            case StatisticDBData.ITEM_ID:
                return StatisticDBData.CONTENT_TYPE_ITEM;

            default:
                throw new IllegalArgumentException("Error Uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != StatisticDBData.ITEM) {
            throw new IllegalArgumentException("Error Uri: " + uri);
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(DB_TABLE, StatisticDBData.ID, values);
        if (id < 0) {
            throw new SQLiteException("Unable to insert " + values + " for " + uri);
        }

        Uri newUri = ContentUris.withAppendedId(uri, id);
        mResolver.notifyChange(newUri, null);

        return newUri;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mResolver = context.getContentResolver();
        mDbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case StatisticDBData.ITEM: {
                sqlBuilder.setTables(DB_TABLE);
                sqlBuilder.setProjectionMap(articleProjectionMap);
                break;
            }

            case StatisticDBData.ITEM_ID: {
                String id = uri.getPathSegments().get(1);
                sqlBuilder.setTables(DB_TABLE);
                sqlBuilder.setProjectionMap(articleProjectionMap);
                sqlBuilder.appendWhere(StatisticDBData.ID + "=" + id);
                break;
            }

            default:
                throw new IllegalArgumentException("Error Uri: " + uri);
        }

        Cursor cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null,
                TextUtils.isEmpty(sortOrder) ? StatisticDBData.DEFAULT_SORT_ORDER : sortOrder, null);
        cursor.setNotificationUri(mResolver, uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case StatisticDBData.ITEM: {
                count = db.update(DB_TABLE, values, selection, selectionArgs);
                break;
            }

            case StatisticDBData.ITEM_ID: {
                String id = uri.getPathSegments().get(1);
                count = db.update(DB_TABLE, values, StatisticDBData.ID
                        + "="
                        + id
                        + (!TextUtils.isEmpty(selection) ? " and (" + selection
                        + ')' : ""), selectionArgs);
                break;
            }

            default:
                throw new IllegalArgumentException("Error Uri: " + uri);
        }

        mResolver.notifyChange(uri, null);

        return count;
    }

}
