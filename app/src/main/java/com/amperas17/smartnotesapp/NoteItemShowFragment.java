package com.amperas17.smartnotesapp;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 *
 */
public class NoteItemShowFragment extends Fragment {
    final String LOG_TAG = "myLogs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_show, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if (bundle!=null){
            Log.d(LOG_TAG, "" + getArguments().toString());
            Uri uri = ContentUris.withAppendedId(NoteDBContract.NoteTable.TABLE_URI,
                    bundle.getInt(NoteDBContract.NoteTable._ID));
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            do {
                int i=0;
                while (i<cursor.getColumnCount()) {
                    Log.d(LOG_TAG, "" + cursor.getString(i));
                    i++;
                }
            }  while (cursor.moveToNext());
        } else {
            Log.d(LOG_TAG, "null");
        }
        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btEditMenuItem:
                Fragment fragment = new NoteItemEditFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack("editNote")
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                                R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fl_note_list_container, fragment)
                        .commit();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
