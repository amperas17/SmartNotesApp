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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 *
 */
public class NoteItemEditFragment extends Fragment {
    final String LOG_TAG = "myLogs";

    EditText mEtTitle,mEtContent;
    Spinner mSpinner;
    ImageButton mIbImage;

    Boolean mIsEditing;

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

        mIbImage = (ImageButton)view.findViewById(R.id.ib_note_edit_image);

        Bundle arguments = getArguments();
        if (arguments!=null){
            mIsEditing = true;
            Log.d(LOG_TAG, "EditFrag:onCreateView " + arguments.getParcelable(Note.NOTE));
            Note note = arguments.getParcelable(Note.NOTE);
            mEtTitle.setText(note.mTitle);
            mEtContent.setText(note.mContent);
            mSpinner.setSelection(note.mRank);

            if (note.mImagePath!=null){
                Picasso.with(getActivity())
                        .load(note.mImagePath)
                        .placeholder(R.drawable.ic_simple_note)
                        .error(R.drawable.ic_simple_note)
                        .centerInside()
                        .into(mIbImage);
            } else {
                mIbImage.setImageResource(R.drawable.ic_simple_note);
            }

        } else {
            mIsEditing = false;
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
