package com.amperas17.smartnotesapp.fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.Toast;

import com.amperas17.smartnotesapp.dao.Note;
import com.amperas17.smartnotesapp.db.NoteDBContract;
import com.amperas17.smartnotesapp.R;
import com.amperas17.smartnotesapp.db.NoteTableContract;
import com.amperas17.smartnotesapp.util.ImageDownloader;
import com.amperas17.smartnotesapp.util.NoteDeleter;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.ExFilePickerParcelObject;

/**
 * Provides note editing functions for single note.
 */
public class NoteItemEditFragment extends Fragment {
    public  static final String INITIAL_NOTE_TAG = "initNote";
    public  static final String IS_EDITING_TAG = "isEditing";
    private static final int SINGLE_IMAGE_PICKER_RESULT = 0;

    public static final int EDIT_FRAGMENT_REQUEST_CODE = 12;


    RelativeLayout mRelativeLayout;
    EditText mEtTitle,mEtContent;
    Spinner mSpinner;
    ImageButton mIbImage;

    Boolean mIsNoteEditing;
    Note mNote,mInitialNote;

    ArrayAdapter<String> spinnerAdapter;

    ImageDownloader mDownloader;
    NoteDeleter mDeleter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_edit, container, false);
        setHasOptionsMenu(true);

        mNote = new Note();
        mInitialNote = new Note();

        mDownloader = new ImageDownloader(getActivity());
        mDeleter = new NoteDeleter(this);

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
                android.R.layout.simple_spinner_item, NoteTableContract.PRIORITIES);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner = (Spinner) view.findViewById(R.id.spinner_note_edit_rank);
        mSpinner.setAdapter(spinnerAdapter
        );
        mSpinner.setSelection(NoteTableContract.NO_PRIORITY);
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

    private void setImageToImageView(Intent data){
        if (data != null) {
            ExFilePickerParcelObject object = data
                    .getParcelableExtra(ExFilePickerParcelObject.class.getCanonicalName());
            try {
                String filePath = object.path + object.names.get(0);
                mNote.mImagePath = filePath;

                mDownloader.setImage(mNote.mImagePath, mIbImage, ImageDownloader.imageSize.FULL);

            } catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState==null) {

            Bundle arguments = getArguments();
            if (arguments != null) {
                mIsNoteEditing = true;
                mNote = arguments.getParcelable(Note.NOTE_TAG);
                try {
                    mInitialNote = mNote.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

            } else {
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
                saveNote();
                return true;
            case R.id.btDeleteMenuItem:
                mDeleter.openDeleteDialog(mNote,EDIT_FRAGMENT_REQUEST_CODE);
                return true;
            case R.id.btUndoMenuItem:
                undoChanges();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote(){
        removeEditTextFocus(mEtTitle);
        if (!TextUtils.isEmpty(mNote.mTitle) && !TextUtils.isEmpty(mNote.mContent)) {
            ContentValues cv = new ContentValues();
            cv.clear();

            cv.put(NoteTableContract.COLUMN_TITLE, mNote.mTitle);
            cv.put(NoteTableContract.COLUMN_CONTENT, mNote.mContent);
            cv.put(NoteTableContract.COLUMN_RANK, mNote.mRank);
            cv.put(NoteTableContract.COLUMN_IMAGE_PATH, mNote.mImagePath);
            cv.put(NoteTableContract.COLUMN_CREATED, System.currentTimeMillis());
            if (mIsNoteEditing) {
                Uri uri = ContentUris.withAppendedId(NoteDBContract.NOTE_TABLE_URI, mNote.mId);
                getActivity().getContentResolver().update(uri, cv, null, null);
            } else {
                getActivity().getContentResolver().insert(NoteDBContract.NOTE_TABLE_URI, cv);
            }
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getActivity(),
                    "Text fields can`t be empty!",Toast.LENGTH_LONG).show();
        }
    }

    private void undoChanges(){
        removeEditTextFocus(mEtTitle);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SINGLE_IMAGE_PICKER_RESULT:
                setImageToImageView(data);
                break;
            case EDIT_FRAGMENT_REQUEST_CODE:
                deleteNote(data);
                break;
            default:
                break;
        }

    }

    private void deleteNote(Intent data){
        removeEditTextFocus(mEtTitle);
        if (mIsNoteEditing) {
            int id = data.getIntExtra(NoteTableContract._ID, -1);
            if (id != -1) {
                mDeleter.deleteNote(id);

                //I think it is a crutch or hardcode, but it work properly(go back to listFragment)
                while (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        } else{
            getActivity().onBackPressed();
        }
    }

    public void removeEditTextFocus(EditText editText){
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
