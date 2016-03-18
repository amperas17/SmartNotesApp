package com.amperas17.smartnotesapp.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.amperas17.smartnotesapp.db.NoteDBContract;
import com.amperas17.smartnotesapp.db.NoteDBHelper;

public class NoteProvider extends ContentProvider {

    private static final int NOTE = 100;
    private static final int NOTE_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private NoteDBHelper mOpenHelper;

    public NoteProvider() {}

    @Override
    public boolean onCreate() {
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
                        NoteTableContract.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        NoteTableContract.COLUMN_CREATED + " DESC"
                );
                break;
            case NOTE_ID:
                long _id = ContentUris.parseId(uri);
                cursor = db.query(
                        NoteTableContract.TABLE_NAME,
                        projection,
                        NoteTableContract._ID + " = ?",
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

        switch(sUriMatcher.match(uri)){
            case NOTE:

            _id = db.insert(NoteTableContract.TABLE_NAME, null, values);

            if(_id > 0){
                returnUri =  NoteTableContract.buildCategoryUri(_id);
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
                rows = db.update(NoteTableContract.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case NOTE_ID:
                long _id = ContentUris.parseId(uri);
                rows = db.update(NoteTableContract.TABLE_NAME,
                        values,
                        NoteTableContract._ID + " = ?",
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
                rows = db.delete(NoteTableContract.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                long _id = ContentUris.parseId(uri);
                rows = db.delete(NoteTableContract.TABLE_NAME,
                        NoteTableContract._ID + " = ?",
                        new String[]{String.valueOf(_id)});
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
                return NoteTableContract.CONTENT_TYPE;
            case NOTE_ID:
                return NoteTableContract.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("[provider:getType]Unknown uri: " + uri);
        }
    }
}
