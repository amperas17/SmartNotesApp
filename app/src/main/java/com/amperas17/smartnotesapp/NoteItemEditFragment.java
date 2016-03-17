package com.amperas17.smartnotesapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.ExFilePickerParcelObject;

/**
 * Provides note editing functions
 */
public class NoteItemEditFragment extends Fragment {
    final String LOG_TAG = "myLogs";
    public  static final String INITIAL_NOTE_TAG = "initNote";
    public  static final String IS_EDITING_TAG = "isEditing";
    private static final int SINGLE_IMAGE_PICKER_RESULT = 0;


    RelativeLayout mRelativeLayout;
    EditText mEtTitle,mEtContent;
    Spinner mSpinner;
    ImageButton mIbImage;

    Boolean mIsNoteEditing;
    Note mNote,mInitialNote;

    ArrayAdapter<String> spinnerAdapter;

    ImageDownloader mDownloader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_edit, container, false);
        setHasOptionsMenu(true);

        mNote = new Note();
        mInitialNote = new Note();

        mDownloader = new ImageDownloader(getActivity());

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
                chooseImageInFileDialog();
                return false;
            }
        });

        return view;
    }

    private void chooseImageInFileDialog() {
        Intent intent = new Intent(getActivity().getApplicationContext(),
                ru.bartwell.exfilepicker.ExFilePickerActivity.class);
        intent.putExtra(ExFilePicker.SET_ONLY_ONE_ITEM, true);
        intent.putExtra(ExFilePicker.SET_CHOICE_TYPE, ExFilePicker.CHOICE_TYPE_FILES);
        intent.putExtra(ExFilePicker.SET_FILTER_LISTED, new String[] {"jpg","jpeg","png"});
        startActivityForResult(intent, SINGLE_IMAGE_PICKER_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SINGLE_IMAGE_PICKER_RESULT) {
            if (data != null) {
                ExFilePickerParcelObject object = data
                        .getParcelableExtra(ExFilePickerParcelObject.class.getCanonicalName());
                try {
                    String filePath = object.path + object.names.get(0);
                    Log.d(LOG_TAG, "file " + filePath);

                    mNote.mImagePath = filePath;

                    mDownloader.setImage(mNote.mImagePath, mIbImage, ImageDownloader.imageSize.FULL);

                } catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
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

        mDownloader.setImage(mNote.mImagePath, mIbImage, ImageDownloader.imageSize.FULL);

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
                cv.put(NoteDBContract.NoteTable.COLUMN_IMAGE_PATH,mNote.mImagePath);
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

                    //I think it is a crutch or hardcode, but it work properly(go back to listFragment)
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
                    mDownloader.setImage(mInitialNote.mImagePath, mIbImage, ImageDownloader.imageSize.FULL);

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
