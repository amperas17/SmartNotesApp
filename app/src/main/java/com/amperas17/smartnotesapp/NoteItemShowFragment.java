package com.amperas17.smartnotesapp;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 *
 */
public class NoteItemShowFragment extends Fragment implements LoaderManager.LoaderCallbacks{
    final static Integer LOADER_ID = 2;
    final String LOG_TAG = "myLogs";

    TextView mTvTitle,mTvContent,mTvRank;
    ImageView mIvImage;

    String mImagePath;
    int mRank;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_show, container, false);
        setHasOptionsMenu(true);

        mTvTitle = (TextView)view.findViewById(R.id.tv_note_show_title);
        mTvContent = (TextView)view.findViewById(R.id.tv_note_show_content);
        mTvRank = (TextView)view.findViewById(R.id.tv_note_show_rank);

        mIvImage = (ImageView)view.findViewById(R.id.iv_note_show_image);

        Bundle arguments = getArguments();
        if (arguments!=null) {
            Log.d(LOG_TAG, "onCreateView" + getArguments().toString());
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, arguments, this);
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
                Bundle bundle = new Bundle();
                Note note = new Note(0,mTvTitle.getText().toString(),
                        mTvContent.getText().toString(),mRank,0,mImagePath,0,0);
                bundle.putParcelable(Note.NOTE,note);
                fragment.setArguments(bundle);
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

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        Uri uri = ContentUris.withAppendedId(NoteDBContract.NoteTable.TABLE_URI,
               args.getInt(NoteDBContract.NoteTable._ID));

        return new CursorLoader(getActivity(),uri, null,null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Cursor cursor = (Cursor) data;
        cursor.moveToFirst();

        mTvTitle.setText(cursor.getString(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_TITLE)));

        mTvContent.setText(cursor.getString(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_CONTENT)));

        int rank_number = cursor.getInt(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_RANK));
        mRank = rank_number;
        mTvRank.setText(NoteDBContract.NoteTable.PRIORITIES[rank_number]);

        String imagePath = cursor.getString(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_IMAGE_PATH));
        if (imagePath != null){
            mImagePath = imagePath;
            Picasso.with(getActivity())
                    .load(imagePath)
                    .placeholder(R.drawable.ic_simple_note)
                    .error(R.drawable.ic_simple_note)
                    .centerInside()
                    .into(mIvImage);
        } else {
            mIvImage.setImageResource(R.drawable.ic_simple_note);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
