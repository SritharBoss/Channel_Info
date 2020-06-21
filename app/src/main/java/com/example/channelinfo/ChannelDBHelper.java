package com.example.channelinfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.channelinfo.ItemContract.*;

public class ChannelDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="groceryList.db";
    private static final int DB_VERSION=1;
    private static final String onCreateQuery="CREATE TABLE " +
            ChannelEntry.TABLE_NAME + " (" +
            ChannelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ChannelEntry.COLUMN_CHANNEL_NAME + " TEXT NOT NULL, " +
            ChannelEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
            ChannelEntry.COLUMN_IMG_URL + " TEXT NOT NULL, " +
            ChannelEntry.COLUMN_PRICE + " DOUBLE NOT NULL, " +
            ChannelEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
            ChannelEntry.COLUMN_HD + " TEXT NOT NULL, " +
            ChannelEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");";

    private static final String onUpgradeQuery="DROP TABLE IF EXISTS "+ChannelEntry.TABLE_NAME;

    public ChannelDBHelper(@Nullable Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(onCreateQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(onUpgradeQuery);
        onCreate(db);
    }
}
