package com.amperas17.smartnotesapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class NoteProvider extends ContentProvider {

    private static final int NOTE = 100;
    private static final int NOTE_ID = 101;

    final String LOG_TAG = "myLogs";

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private NoteDBHelper mOpenHelper;

    public NoteProvider() {}

    @Override
    public boolean onCreate() {
        //Log.d(LOG_TAG, "provider:onCreate");
        mOpenHelper = new NoteDBHelper(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher(){
        String authority = NoteDBContract.AUTHORITY;

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(authority, NoteDBContract.PATH_NOTE_TABLE, NOTE);
        matcher.addURI(authority, NoteDBContract.PATH_NOTE_TABLE + "/#", NOTE_ID);

        return matcher;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor cursor;
        switch(sUriMatcher.match(uri)) {
            case NOTE:
                cursor = db.query(
                        NoteDBContract.NoteTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        NoteDBContract.NoteTable.COLUMN_CREATED + " DESC"
                );
                break;
            case NOTE_ID:
                long _id = ContentUris.parseId(uri);
                cursor = db.query(
                        NoteDBContract.NoteTable.TABLE_NAME,
                        projection,
                        NoteDBContract.NoteTable._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("[provider:query]Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;
        //Log.d(LOG_TAG,"[provider:insert]");

        switch(sUriMatcher.match(uri)){
            case NOTE:

            _id = db.insert(NoteDBContract.NoteTable.TABLE_NAME, null, values);
            Log.d(LOG_TAG,"Insert - id= "+_id);

            if(_id > 0){
                returnUri =  NoteDBContract.NoteTable.buildCategoryUri(_id);
            } else{
                throw new UnsupportedOperationException("[provider:insert]Unable to insert rows into: " + uri);
            }
            break;

            default:
                throw new UnsupportedOperationException("[provider:insert]Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }



    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch(sUriMatcher.match(uri)){
            case NOTE:
                rows = db.update(NoteDBContract.NoteTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case NOTE_ID:
                long _id = ContentUris.parseId(uri);
                rows = db.update(NoteDBContract.NoteTable.TABLE_NAME,
                        values,
                        NoteDBContract.NoteTable._ID + " = ?",
                        new String[]{String.valueOf(_id)});
                break;
            default:
                throw new UnsupportedOperationException("[update]Unknown uri: " + uri);
        }

        if(rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch(sUriMatcher.match(uri)) {
            case NOTE:
                rows = db.delete(NoteDBContract.NoteTable.TABLE_NAME, selection, selectionArgs);
                Log.d(LOG_TAG, "deleted " + rows + " Note ");
                break;
            case NOTE_ID:
                long _id = ContentUris.parseId(uri);
                rows = db.delete(NoteDBContract.NoteTable.TABLE_NAME,
                        NoteDBContract.NoteTable._ID + " = ?",
                        new String[]{String.valueOf(_id)});
                Log.d(LOG_TAG, "deleted " + rows + " Note ");
                break;

            default:
                throw new UnsupportedOperationException("[provider:delete]Unknown uri: " + uri);
        }

        if(rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }


    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)){
            case NOTE:
                return NoteDBContract.NoteTable.CONTENT_TYPE;
            case NOTE_ID:
                return NoteDBContract.NoteTable.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("[provider:getType]Unknown uri: " + uri);
        }
    }
}
