package com.amperas17.smartnotesapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ImageButton;

import com.amperas17.smartnotesapp.R;
import com.amperas17.smartnotesapp.dao.Note;

import com.amperas17.smartnotesapp.db.NoteDBContract;
import com.amperas17.smartnotesapp.db.NoteTableContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

public class MapsActivity extends FragmentActivity
        implements LoaderManager.LoaderCallbacks{

    public static final int LOADER_ID = 0;

    public static final String MAPS_REQUEST_ACTION_TAG = "mapsAction";
    public static final int MAPS_ACTION_GET_COORDINATES = 100;
    public static final int MAPS_ACTION_SHOW_NOTES = 101;
    public static final int MAPS_ACTION_SHOW_SINGLE_NOTE = 102;

    public static final String IS_COORDINATES_SET_TAG = "isCoordinatesSet";
    public static final String CHOSEN_LATITUDE_TAG = "mChosenLatitude";
    public static final String CHOSEN_LONGITUDE_TAG = "mChosenLongitude";

    private GoogleMap mMap;
    ImageButton mIbSaveCoordinates,mIbShowNote;

    int mActionType;
    Note mResultNote,mRequestNote;
    Map<Marker,Note> mNoteHashMap;

    boolean isCoordinatesSet;
    double mChosenLatitude;
    double mChosenLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        mIbSaveCoordinates = (ImageButton)findViewById(R.id.ib_maps_save_coordinates);
        mIbSaveCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCoordinatesAndFinish();
            }
        });

        mIbShowNote = (ImageButton)findViewById(R.id.ib_maps_show_note);
        mIbShowNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteAndFinish();
            }
        });

        if (isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            Intent requestIntent = getIntent();
            mActionType = requestIntent.getIntExtra(MAPS_REQUEST_ACTION_TAG,-1);

            switch (mActionType){
                case MAPS_ACTION_GET_COORDINATES:
                    isCoordinatesSet = false;
                    break;
                case MAPS_ACTION_SHOW_NOTES:
                    mNoteHashMap = new HashMap<>();
                    break;
                case MAPS_ACTION_SHOW_SINGLE_NOTE:
                    mRequestNote = requestIntent.getParcelableExtra(Note.NOTE_TAG);
                    break;
                case -1:
                default:
                    break;
            }

        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isCoordinatesSet = savedInstanceState.getBoolean(IS_COORDINATES_SET_TAG);

        mChosenLatitude = savedInstanceState.getDouble(CHOSEN_LATITUDE_TAG);
        mChosenLongitude = savedInstanceState.getDouble(CHOSEN_LONGITUDE_TAG);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_COORDINATES_SET_TAG, isCoordinatesSet);

        outState.putDouble(CHOSEN_LATITUDE_TAG,mChosenLatitude);
        outState.putDouble(CHOSEN_LONGITUDE_TAG, mChosenLongitude);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();

                switch (mActionType){
                    case MAPS_ACTION_GET_COORDINATES:
                        initChoosePlace();
                        break;
                    case MAPS_ACTION_SHOW_NOTES:
                        initShowNotes();
                        break;
                    case MAPS_ACTION_SHOW_SINGLE_NOTE:
                        initShowSingleNote();
                        break;
                    default:
                        break;
                }

            } else {
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mActionType == MAPS_ACTION_SHOW_NOTES){
            getSupportLoaderManager().getLoader(LOADER_ID).stopLoading();
        }
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setMyLocationEnabled(true);
    }

    private void initChoosePlace() {

        if (isCoordinatesSet){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mChosenLatitude, mChosenLongitude))
                    .title("Chosen Place"));
            mIbSaveCoordinates.setVisibility(View.VISIBLE);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mIbSaveCoordinates.setVisibility(View.VISIBLE);

                mChosenLatitude = latLng.latitude;
                mChosenLongitude = latLng.longitude;
                isCoordinatesSet = true;

                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mChosenLatitude, mChosenLongitude))
                        .title("Chosen Place"));
            }

        });
    }

    private void initShowNotes(){
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mResultNote = mNoteHashMap.get(marker);
                mIbShowNote.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private void initShowSingleNote(){
        if (mRequestNote!=null) {
            if (mRequestNote.mLatitude!=NoteTableContract.WRONG_UNSET_COORDINATE) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mRequestNote.mLatitude, mRequestNote.mLongitude))
                        .title(mRequestNote.mTitle)
                        .anchor(0.25f, 1)
                        .icon(BitmapDescriptorFactory.fromResource(getPinIcon(mRequestNote.mRank)))
                );
            }
        }
    }


    private void saveCoordinatesAndFinish(){
        if (isCoordinatesSet) {
            Intent intent = new Intent();
            intent.putExtra(Note.LATITUDE_TAG, mChosenLatitude);
            intent.putExtra(Note.LONGITUDE_TAG, mChosenLongitude);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        onBackPressed();
    }

    private void showNoteAndFinish(){
        if (mResultNote!=null){
            Intent intent = new Intent();
            intent.putExtra(Note.NOTE_TAG, mResultNote);
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this,
                NoteDBContract.NOTE_TABLE_URI, null,
                null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Cursor cursor = (Cursor) data;
        while (cursor.moveToNext()){
            Note note = new Note(cursor);
            if (note.mLatitude!=NoteTableContract.WRONG_UNSET_COORDINATE) {
                createMarkers(note);
            }
        }
    }

    private void createMarkers(Note note){
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(note.mLatitude, note.mLongitude))
                .title(note.mTitle)
                .anchor(0.25f, 1)
                .icon(BitmapDescriptorFactory.fromResource(getPinIcon(note.mRank))
                ));
        mNoteHashMap.put(marker, note);
    }

    private int getPinIcon(int rank){
        switch (rank){
            case NoteTableContract.NO_PRIORITY:
                return R.drawable.ic_white_pin;

            case NoteTableContract.LOW_PRIORITY:
                return R.drawable.ic_green_pin;

            case NoteTableContract.MEDIUM_PRIORITY:
                return R.drawable.ic_yellow_pin;

            case NoteTableContract.HIGH_PRIORITY:
                return R.drawable.ic_red_pin;
            default:
                return R.drawable.ic_white_pin;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
