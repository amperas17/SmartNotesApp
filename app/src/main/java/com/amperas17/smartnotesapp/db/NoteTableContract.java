package com.amperas17.smartnotesapp.db;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Вова on 18.03.2016.
 */
public final class NoteTableContract implements BaseColumns {

    public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/" +
                    NoteDBContract.NOTE_TABLE_URI + "/" +
                    NoteDBContract.PATH_NOTE_TABLE;
    public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/" +
                    NoteDBContract.NOTE_TABLE_URI + "/" +
                    NoteDBContract.PATH_NOTE_TABLE;

    public static final String TABLE_NAME = "noteTable";

    public static final String COLUMN_TITLE = "noteTitle";
    public static final String COLUMN_CONTENT = "noteContent";
    public static final String COLUMN_CREATED = "noteCreated";
    public static final String COLUMN_RANK = "noteRank";
    public static final String COLUMN_IMAGE_PATH = "noteImagePath";
    public static final String COLUMN_LATITUDE = "noteLatitude";
    public static final String COLUMN_LONGITUDE = "noteLongitude";

    public static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_CONTENT + " TEXT NOT NULL, " +
            COLUMN_CREATED + " INTEGER, " +
            COLUMN_RANK + " INTEGER, " +
            COLUMN_IMAGE_PATH + " TEXT, " +
            COLUMN_LATITUDE + " REAL, " +
            COLUMN_LONGITUDE + " REAL);";

    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String[] PRIORITIES = {"no (white)","low (green)",
            "medium (yellow)", "high (red)"};
    public static final int NO_PRIORITY = 0;
    public static final int LOW_PRIORITY = 1;
    public static final int MEDIUM_PRIORITY = 2;
    public static final int HIGH_PRIORITY = 3;


    public static Uri buildCategoryUri(long id){
        return ContentUris.withAppendedId(NoteDBContract.NOTE_TABLE_URI, id);
    }
}
