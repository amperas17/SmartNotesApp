package com.amperas17.smartnotesapp.util;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.amperas17.smartnotesapp.dao.Note;
import com.amperas17.smartnotesapp.db.NoteDBContract;
import com.amperas17.smartnotesapp.db.NoteTableContract;
import com.amperas17.smartnotesapp.fragment.DeleteNoteDialogFragment;

/**
 * Provides methods for working with DeleteDialog
 */
public class NoteDeleter {
    Fragment mFragment;
    DeleteNoteDialogFragment mDeleteDialogFragment;


    public NoteDeleter(Fragment fragment){
        mFragment = fragment;
    }


    public void deleteNote(int noteId){
        Uri uri = ContentUris.withAppendedId(NoteDBContract.NOTE_TABLE_URI, noteId);
        mFragment.getActivity().getContentResolver().delete(uri, null, null);
    }

    public void openDeleteDialog(Note note,int requestCode){
        mDeleteDialogFragment = new DeleteNoteDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(NoteTableContract._ID,note.mId);
        bundle.putString(NoteTableContract.COLUMN_TITLE,note.mTitle);
        mDeleteDialogFragment.setArguments(bundle);
        mDeleteDialogFragment.setTargetFragment(mFragment, requestCode);
        mDeleteDialogFragment.show(mFragment.getActivity()
                .getSupportFragmentManager(), DeleteNoteDialogFragment.DELETE_FRAGMENT_TAG);
    }
}
