package com.amperas17.smartnotesapp.db;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * has static fields for work with ContentProvider and database.
 */
public final class NoteDBContract {

    public static final String AUTHORITY = "com.amperas17.providers.smartNotes";
    public static final String CONTENT_AUTHORITY ="content://" + AUTHORITY;

    public static final Uri AUTHORITY_URI = Uri.parse(CONTENT_AUTHORITY);

    public static final String PATH_NOTE_TABLE = "notePath";

    public static final Uri NOTE_TABLE_URI =
            AUTHORITY_URI.buildUpon().appendPath(PATH_NOTE_TABLE).build();

    private NoteDBContract(){}

}
