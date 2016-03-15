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

import com.squareup.picasso.Picasso;

/**
 *
 */
public class NoteItemShowFragment extends Fragment implements LoaderManager.LoaderCallbacks{
    final static Integer LOADER_ID = 2;
    final String EDIT_NOTE_Transaction_TAG = "editNote";

    final String LOG_TAG = "myLogs";

    TextView mTvTitle,mTvContent,mTvRank;
    ImageView mIvImage;

    Note mNote;
    String mImagePath;
    int mRank;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_show, container, false);
        setHasOptionsMenu(true);

        mNote = new Note();
        mTvTitle = (TextView)view.findViewById(R.id.tv_note_show_title);
        mTvContent = (TextView)view.findViewById(R.id.tv_note_show_content);
        mTvRank = (TextView)view.findViewById(R.id.tv_note_show_rank);

        mIvImage = (ImageView)view.findViewById(R.id.iv_note_show_image);

        Bundle arguments = getArguments();
        if (arguments!=null) {
            //Log.d(LOG_TAG, "onCreateView" + getArguments().toString());
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
                bundle.putParcelable(Note.NOTE,mNote);
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(EDIT_NOTE_Transaction_TAG)
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
        mNote = new Note(cursor);

        mTvTitle.setText(mNote.mTitle);

        mTvContent.setText(mNote.mContent);

        mTvRank.setText(NoteDBContract.NoteTable.PRIORITIES[mNote.mRank]);

        String imagePath = mNote.mImagePath;
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
