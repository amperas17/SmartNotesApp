package com.amperas17.smartnotesapp.fragment;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amperas17.smartnotesapp.activity.MapsActivity;
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
    public static final String INITIAL_NOTE_TAG = "initNote";
    public static final String IS_EDITING_TAG = "isEditing";
    public static final String IS_COORDINATES_SET_TAG = "mIsCoordinatesSet";

    public static final String COORDINATES_IS_NOT_SET_STRING = "not set";


    private static final int SINGLE_IMAGE_PICKER_REQUEST_CODE = 0;

    public static final int DELETE_DIALOG_REQUEST_CODE = 1;

    public static final int LOCATION_LISTENER_TIME_DELAY = 1*1000;
    public static final int LOCATION_LISTENER_DISTANCE_INCREMENT = 10;

    public static final String MAPS_REQUEST_ACTION_TAG = "mapsAction";
    public static final int MAPS_REQUEST_CODE = 2;
    public static final int MAPS_ACTION_GET_COORDINATES = 100;


    RelativeLayout mRelativeLayout;
    EditText mEtTitle, mEtContent;
    Spinner mSpinner;
    ImageButton mIbImage;
    TextView mTvLatitude,mTvLongitude;
    Button mBtSetCoordinates;

    boolean mIsNoteEditing;
    Note mNote, mInitialNote;

    ArrayAdapter<String> spinnerAdapter;

    ImageDownloader mDownloader;
    NoteDeleter mDeleter;
    LocationManager mLocationManager;

    boolean mIsCoordinatesSet;
    double mCurrentLatitude;
    double mCurrentLongitude;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNote = new Note();
        mInitialNote = new Note();

        mDownloader = new ImageDownloader(getActivity());
        mDeleter = new NoteDeleter(this);

        mCurrentLatitude = NoteTableContract.WRONG_UNSET_COORDINATE;
        mCurrentLongitude = NoteTableContract.WRONG_UNSET_COORDINATE;
        mIsCoordinatesSet = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_edit, container, false);
        setHasOptionsMenu(true);

        mEtTitle = (EditText) view.findViewById(R.id.et_note_edit_title);
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
        mEtContent = (EditText) view.findViewById(R.id.et_note_edit_content);
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

        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.rl_note_edit);
        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditTextFocus(mEtTitle);
            }
        });

        mIbImage = (ImageButton) view.findViewById(R.id.ib_note_edit_image);
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

        mLocationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        mTvLatitude = (TextView)view.findViewById(R.id.tv_note_edit_latitude);
        mTvLongitude = (TextView)view.findViewById(R.id.tv_note_edit_longitude);

        mBtSetCoordinates = (Button)view.findViewById(R.id.bt_note_edit_set_coordinates);
        mBtSetCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra(MAPS_REQUEST_ACTION_TAG,MAPS_ACTION_GET_COORDINATES);
                startActivityForResult(intent,MAPS_REQUEST_CODE);
            }
        });

        return view;
    }

    private void chooseImageInFileDialog() {
        Intent intent = new Intent(getActivity().getApplicationContext(),
                ru.bartwell.exfilepicker.ExFilePickerActivity.class);
        intent.putExtra(ExFilePicker.SET_ONLY_ONE_ITEM, true);
        intent.putExtra(ExFilePicker.SET_CHOICE_TYPE, ExFilePicker.CHOICE_TYPE_FILES);
        intent.putExtra(ExFilePicker.SET_FILTER_LISTED, new String[]{"jpg", "jpeg", "png"});
        startActivityForResult(intent, SINGLE_IMAGE_PICKER_REQUEST_CODE);
    }

    private void setImageToImageView(Intent data) {
        if (data != null) {
            ExFilePickerParcelObject object = data
                    .getParcelableExtra(ExFilePickerParcelObject.class.getCanonicalName());
            try {
                String filePath = object.path + object.names.get(0);
                mNote.mImagePath = filePath;

                mDownloader.setImage(mNote.mImagePath, mIbImage, ImageDownloader.imageSize.FULL);

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState == null) {

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
            mIsCoordinatesSet = savedInstanceState.getBoolean(IS_COORDINATES_SET_TAG);

            mNote = savedInstanceState.getParcelable(Note.NOTE_TAG);
            mInitialNote = savedInstanceState.getParcelable(INITIAL_NOTE_TAG);
        }

        mEtTitle.setText(mNote.mTitle);
        mEtContent.setText(mNote.mContent);
        mSpinner.setSelection(mNote.mRank);

        mDownloader.setImage(mNote.mImagePath, mIbImage, ImageDownloader.imageSize.FULL);

        if (mIsNoteEditing || mIsCoordinatesSet) {
            if (mNote.mLatitude!=NoteTableContract.WRONG_UNSET_COORDINATE) {
                mTvLatitude.setText(String.valueOf(mNote.mLatitude));
                mTvLongitude.setText(String.valueOf(mNote.mLongitude));
            } else {
                mTvLatitude.setText(String.valueOf(COORDINATES_IS_NOT_SET_STRING));
                mTvLongitude.setText(String.valueOf(COORDINATES_IS_NOT_SET_STRING));
            }
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_EDITING_TAG, mIsNoteEditing);
        outState.putBoolean(IS_COORDINATES_SET_TAG, mIsCoordinatesSet);
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
                mDeleter.openDeleteDialog(mNote, DELETE_DIALOG_REQUEST_CODE);
                return true;
            case R.id.btUndoMenuItem:
                undoChanges();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        removeEditTextFocus(mEtTitle);
        if (!TextUtils.isEmpty(mNote.mTitle) && !TextUtils.isEmpty(mNote.mContent)) {
            if (!mIsNoteEditing && !mIsCoordinatesSet){
                mNote.mLatitude = mCurrentLatitude;
                mNote.mLongitude = mCurrentLongitude;
            }
            ContentValues cv = new ContentValues();
            cv.clear();

            cv.put(NoteTableContract.COLUMN_TITLE, mNote.mTitle);
            cv.put(NoteTableContract.COLUMN_CONTENT, mNote.mContent);
            cv.put(NoteTableContract.COLUMN_RANK, mNote.mRank);
            cv.put(NoteTableContract.COLUMN_IMAGE_PATH, mNote.mImagePath);
            cv.put(NoteTableContract.COLUMN_CREATED, System.currentTimeMillis());
            cv.put(NoteTableContract.COLUMN_LATITUDE,mNote.mLatitude);
            cv.put(NoteTableContract.COLUMN_LONGITUDE,mNote.mLongitude);

            if (mIsNoteEditing) {
                Uri uri = ContentUris.withAppendedId(NoteDBContract.NOTE_TABLE_URI, mNote.mId);
                getActivity().getContentResolver().update(uri, cv, null, null);
            } else {
                getActivity().getContentResolver().insert(NoteDBContract.NOTE_TABLE_URI, cv);
            }
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getActivity(),
                    "Text fields can`t be empty!", Toast.LENGTH_LONG).show();
        }
    }

    private void undoChanges() {
        removeEditTextFocus(mEtTitle);
        if (!mNote.equals(mInitialNote)) {
            mEtTitle.setText(mInitialNote.mTitle);
            mEtContent.setText(mInitialNote.mContent);

            mSpinner.setSelection(mInitialNote.mRank);

            mDownloader.setImage(mInitialNote.mImagePath,
                    mIbImage, ImageDownloader.imageSize.FULL);

            if (mIsNoteEditing) {
                if (mInitialNote.mLatitude!=NoteTableContract.WRONG_UNSET_COORDINATE) {
                    mTvLatitude.setText(String.valueOf(mInitialNote.mLatitude));
                    mTvLongitude.setText(String.valueOf(mInitialNote.mLongitude));
                } else {
                    mTvLatitude.setText(COORDINATES_IS_NOT_SET_STRING);
                    mTvLongitude.setText(COORDINATES_IS_NOT_SET_STRING);
                }

            } else {
                mIsCoordinatesSet = false;
                mTvLatitude.setText(COORDINATES_IS_NOT_SET_STRING);
                mTvLongitude.setText(COORDINATES_IS_NOT_SET_STRING);
            }

            try {
                mNote = mInitialNote.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SINGLE_IMAGE_PICKER_REQUEST_CODE:
                setImageToImageView(data);
                break;
            case DELETE_DIALOG_REQUEST_CODE:
                deleteNote(data);
                break;
            case MAPS_REQUEST_CODE:
                if (resultCode == getActivity().RESULT_OK) {
                    updateCoordinates(data);
                } else {
                    Toast.makeText(getActivity(), "Coordinates was not set!", Toast.LENGTH_LONG).show();
                }
            default:
                break;
        }

    }

    private void deleteNote(Intent data) {
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
        } else {
            getActivity().onBackPressed();
        }
    }

    private void updateCoordinates(Intent data){
        double latitude,longitude;
        latitude = data.getDoubleExtra(Note.LATITUDE_TAG,NoteTableContract.WRONG_UNSET_COORDINATE);
        longitude = data.getDoubleExtra(Note.LONGITUDE_TAG,NoteTableContract.WRONG_UNSET_COORDINATE);

        if (latitude!=NoteTableContract.WRONG_UNSET_COORDINATE &&
                longitude!=NoteTableContract.WRONG_UNSET_COORDINATE) {
            mNote.mLatitude = latitude;
            mNote.mLongitude = longitude;
            mIsCoordinatesSet = true;

            mTvLatitude.setText(String.valueOf(latitude));
            mTvLongitude.setText(String.valueOf(longitude));
        }
    }

    public void removeEditTextFocus(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_LISTENER_TIME_DELAY,
                    LOCATION_LISTENER_DISTANCE_INCREMENT,
                    locationListener);
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_LISTENER_TIME_DELAY,
                    LOCATION_LISTENER_DISTANCE_INCREMENT,
                    locationListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            setLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (isCoordinatesProvidersEnabled()) {
                    setLocation(mLocationManager.getLastKnownLocation(provider));
                }
            }
        }

        private void setLocation(Location location) {
            if (location != null){
                mCurrentLatitude = location.getLatitude();
                mCurrentLongitude = location.getLongitude();

                if (!mIsNoteEditing && !mIsCoordinatesSet){
                    mTvLatitude.setText(String.valueOf(mCurrentLatitude));
                    mTvLongitude.setText(String.valueOf(mCurrentLongitude));
                }
            }
        }

        private boolean isCoordinatesProvidersEnabled() {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                return false;
            } else {
                return true;
            }
        }

    };



}
