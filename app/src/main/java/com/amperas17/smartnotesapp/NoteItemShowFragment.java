package com.amperas17.smartnotesapp;

import android.content.ContentUris;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
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
import java.text.SimpleDateFormat;

/**
 *  Show note data to user.
 */
public class NoteItemShowFragment extends Fragment implements LoaderManager.LoaderCallbacks{
    final static Integer LOADER_ID = 2;
    final static Integer DELETED_NOTE_ID = -1;

    final String EDIT_NOTE_TRANSACTION_TAG = "editNote";

    final String LOG_TAG = "myLogs";

    TextView mTvTitle,mTvContent,mTvRank,mTvCreated;
    ImageView mIvImage;

    Note mNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_show, container, false);
        setHasOptionsMenu(true);

        mNote = new Note();
        mTvTitle = (TextView)view.findViewById(R.id.tv_note_show_title);
        mTvContent = (TextView)view.findViewById(R.id.tv_note_show_content);
        mTvRank = (TextView)view.findViewById(R.id.tv_note_show_rank);
        mTvCreated = (TextView)view.findViewById(R.id.tv_note_show_created);

        mIvImage = (ImageView)view.findViewById(R.id.iv_note_show_image);

        Log.d(LOG_TAG, "ShowFrag:onCreateView" );

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments!=null) {
            Log.d(LOG_TAG, "ShowFrag:onCreateView" + getArguments().toString());
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, arguments, this);
        }
        super.onActivityCreated(savedInstanceState);
    }



    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_button, menu);
        inflater.inflate(R.menu.delete_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btEditMenuItem:

                if (mNote.mId!=-1) {
                    Fragment fragment = new NoteItemEditFragment();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Note.NOTE_TAG, mNote);
                    fragment.setArguments(bundle);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(EDIT_NOTE_TRANSACTION_TAG)
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                                    R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.fl_note_list_container, fragment)
                            .commit();
                } else {
                    Toast.makeText(getActivity(), "Note was deleted!", Toast.LENGTH_SHORT).show();
                }
            return true;

            case R.id.btDeleteMenuItem:
                if (mNote.mId!=-1) {
                    Uri uri = ContentUris.withAppendedId(NoteDBContract.NoteTable.TABLE_URI, mNote.mId);
                    getActivity().getSupportLoaderManager().destroyLoader(LOADER_ID);
                    getActivity().getContentResolver().delete(uri, null, null);
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getActivity(), "Note was deleted!", Toast.LENGTH_SHORT).show();
                }
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
        try {
            mNote = new Note(cursor);
        } catch (CursorIndexOutOfBoundsException ex){
            mNote = new Note(DELETED_NOTE_ID,"Deleted","Note was deleted",
                    NoteDBContract.NoteTable.NO_PRIORITY,0,null,0,0);
        }
        mTvTitle.setText(mNote.mTitle);

        mTvContent.setText(mNote.mContent);

        mTvRank.setText(NoteDBContract.NoteTable.PRIORITIES[mNote.mRank]);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM.yy");
        mTvCreated.setText(sdf.format(mNote.mCreated));

        ImageDownloader downloader = new ImageDownloader(getActivity());
        downloader.setImage(mNote.mImagePath,mIvImage, ImageDownloader.imageSize.FULL);

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
