package com.amperas17.smartnotesapp;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 *
 */
public class NoteItemEditFragment extends Fragment {
    final String LOG_TAG = "myLogs";

    EditText mEtTitle,mEtContent;
    Spinner mSpinner;

    int mRank;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_edit, container, false);
        setHasOptionsMenu(true);

        mEtTitle = (EditText)view.findViewById(R.id.et_note_edit_title);
        mEtContent = (EditText)view.findViewById(R.id.et_note_edit_content);

        mRank = NoteDBContract.NoteTable.NO_PRIORITY;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, NoteDBContract.NoteTable.PRIORITIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner = (Spinner) view.findViewById(R.id.spinner_note_edit_rank);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(NoteDBContract.NoteTable.NO_PRIORITY);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mRank = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        Bundle bundle = getArguments();
        if (bundle!=null){
            Log.d(LOG_TAG, "" + getArguments().toString());

        } else {
            Log.d(LOG_TAG, "null");
        }


        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btSaveMenuItem:

                ContentValues cv = new ContentValues();
                cv.put(NoteDBContract.NoteTable.COLUMN_TITLE, mEtTitle.getText().toString());
                cv.put(NoteDBContract.NoteTable.COLUMN_CONTENT,mEtContent.getText().toString());
                cv.put(NoteDBContract.NoteTable.COLUMN_RANK,mRank);

                getActivity().getContentResolver().insert(NoteDBContract.NoteTable.TABLE_URI,cv);

                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
