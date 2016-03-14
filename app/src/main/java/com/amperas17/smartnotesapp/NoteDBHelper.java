package com.amperas17.smartnotesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * creates note database
 */
public class NoteDBHelper extends SQLiteOpenHelper {

    final String LOG_TAG = "myLogs";


    public NoteDBHelper(Context context) {
        super(context, NoteDBContract.DATABASE_NAME, null, NoteDBContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Helper:onCreate");
        db.execSQL(NoteDBContract.NoteTable.CREATE_TABLE);

        /*
        ContentValues cv = new ContentValues();

        for (int i = 1; i <= 7; i++) {
            cv.clear();
            cv.put(NoteDBContract.NoteTable.COLUMN_TITLE, "title " + i);
            cv.put(NoteDBContract.NoteTable.COLUMN_CONTENT,"content "+i);
            cv.put(NoteDBContract.NoteTable.COLUMN_CREATED,i);
            cv.put(NoteDBContract.NoteTable.COLUMN_RANK,i);
            cv.put(NoteDBContract.NoteTable.COLUMN_IMAGE_PATH,"path "+i);
            cv.put(NoteDBContract.NoteTable.COLUMN_LATITUDE,"lat "+i);
            cv.put(NoteDBContract.NoteTable.COLUMN_LONGITUDE,"long "+i);

            db.insert(NoteDBContract.NoteTable.TABLE_NAME,null,cv);
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            db.execSQL(NoteDBContract.NoteTable.DELETE_TABLE);
            onCreate(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            db.execSQL(NoteDBContract.NoteTable.DELETE_TABLE);
            onCreate(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
