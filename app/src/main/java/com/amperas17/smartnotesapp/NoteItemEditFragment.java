package com.amperas17.smartnotesapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 *
 */
public class NoteItemEditFragment extends Fragment {
    final String LOG_TAG = "myLogs";
    public  static final String INITIAL_NOTE_TAG = "initNote";
    public  static final String IS_EDITING_TAG = "isEditing";

    RelativeLayout mRelativeLayout;
    EditText mEtTitle,mEtContent;
    Spinner mSpinner;
    ImageButton mIbImage;

    Boolean mIsNoteEditing;
    Note mNote,mInitialNote;

    ArrayAdapter<String> spinnerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_edit, container, false);
        setHasOptionsMenu(true);

        mNote = new Note();
        mInitialNote = new Note();

        mEtTitle = (EditText)view.findViewById(R.id.et_note_edit_title);
        mEtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.mTitle = s.toString();
                //Log.d(LOG_TAG,s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEtContent = (EditText)view.findViewById(R.id.et_note_edit_content);
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.mContent = s.toString();
                //Log.d(LOG_TAG,s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spinnerAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, NoteDBContract.NoteTable.PRIORITIES);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner = (Spinner) view.findViewById(R.id.spinner_note_edit_rank);
        mSpinner.setAdapter(spinnerAdapter
        );
        mSpinner.setSelection(NoteDBContract.NoteTable.NO_PRIORITY);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mNote.mRank = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        mSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                removeEditTextFocus(mEtTitle);
                return false;
            }
        });

        mRelativeLayout = (RelativeLayout)view.findViewById(R.id.rl_note_edit);
        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditTextFocus(mEtTitle);
            }
        });

        mIbImage = (ImageButton)view.findViewById(R.id.ib_note_edit_image);
        mIbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditTextFocus(mEtTitle);
            }
        });
        mIbImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState==null) {

            Bundle arguments = getArguments();
            if (arguments != null) {
                mIsNoteEditing = true;
                Log.d(LOG_TAG, "EditFrag:onActivityCreated " + arguments.getParcelable(Note.NOTE_TAG));
                mNote = arguments.getParcelable(Note.NOTE_TAG);
                try {
                    mInitialNote = mNote.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(LOG_TAG, "EditFrag:onActivityCreated arguments = null");
                mIsNoteEditing = false;
            }
        } else {
            mIsNoteEditing = savedInstanceState.getBoolean(IS_EDITING_TAG);
            mNote = savedInstanceState.getParcelable(Note.NOTE_TAG);
            mInitialNote = savedInstanceState.getParcelable(INITIAL_NOTE_TAG);
        }

        mEtTitle.setText(mNote.mTitle);
        mEtContent.setText(mNote.mContent);
        mSpinner.setSelection(mNote.mRank);

        if (mNote.mImagePath != null) {
            Picasso.with(getActivity())
                    .load(mNote.mImagePath)
                    .placeholder(R.drawable.ic_simple_note)
                    .error(R.drawable.ic_simple_note)
                    .centerInside()
                    .into(mIbImage);
        } else {
            mIbImage.setImageResource(R.drawable.ic_simple_note);
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Log.d(LOG_TAG,"onSaveInstanceState: "+mNote.toString()+" /---/ "+mInitialNote.toString());
        outState.putBoolean(INITIAL_NOTE_TAG, mIsNoteEditing);
        outState.putParcelable(Note.NOTE_TAG, mNote);
        outState.putParcelable(INITIAL_NOTE_TAG, mInitialNote);

        super.onSaveInstanceState(outState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.undo_button, menu);
        inflater.inflate(R.menu.save_button, menu);
        inflater.inflate(R.menu.delete_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btSaveMenuItem:
                ContentValues cv = new ContentValues();
                cv.clear();

                cv.put(NoteDBContract.NoteTable.COLUMN_TITLE, mNote.mTitle);
                cv.put(NoteDBContract.NoteTable.COLUMN_CONTENT, mNote.mContent);
                cv.put(NoteDBContract.NoteTable.COLUMN_RANK, mNote.mRank);
                cv.put(NoteDBContract.NoteTable.COLUMN_CREATED,System.currentTimeMillis());
                if (mIsNoteEditing){
                    Uri uri = ContentUris.withAppendedId(NoteDBContract.NoteTable.TABLE_URI,mNote.mId);
                    getActivity().getContentResolver().update(uri, cv, null, null);
                } else {
                    getActivity().getContentResolver().insert(NoteDBContract.NoteTable.TABLE_URI, cv);
                }

                getActivity().onBackPressed();
                return true;
            case R.id.btDeleteMenuItem:
                if (mIsNoteEditing) {
                    Uri uri = ContentUris.withAppendedId(NoteDBContract.NoteTable.TABLE_URI, mNote.mId);
                    getActivity().getContentResolver().delete(uri, null, null);

                    //I think it is a crutch or hardcode, but it work properly
                    while (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                } else{
                    getActivity().onBackPressed();
                }

                return true;

            case R.id.btUndoMenuItem:
                //Log.d(LOG_TAG,"Undo: "+mNote.toString()+" /---/ "+mInitialNote.toString());
                //Log.d(LOG_TAG,"Undo: "+mNote.equals(mInitialNote));
                if (!mNote.equals(mInitialNote)){
                    mEtTitle.setText(mInitialNote.mTitle);
                    mEtContent.setText(mInitialNote.mContent);
                    mSpinner.setSelection(mInitialNote.mRank);
                    try {
                        mNote = mInitialNote.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void removeEditTextFocus(EditText editText){
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
