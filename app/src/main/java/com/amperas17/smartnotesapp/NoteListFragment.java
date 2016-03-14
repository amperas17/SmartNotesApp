package com.amperas17.smartnotesapp;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 */
public class NoteListFragment extends ListFragment implements LoaderManager.LoaderCallbacks{

    final static Integer LOADER_ID = 1;
    final String LOG_TAG = "myLogs";

    public enum noteFragType{SHOW,EDIT}

    NoteAdapter mNoteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        setHasOptionsMenu(true);

        Log.d(LOG_TAG, "NoteFrag:onCreateView");
        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_button, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btAddMenuItem:
                openNoteFragment(noteFragType.EDIT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d(AppContract.LOG_TAG, "NoteFrag[onActivityCreated]");
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TextView textView = (TextView)v.findViewById(R.id.tv_list_item_title);
        Toast.makeText(getActivity(),textView.getText().toString(),Toast.LENGTH_LONG).show();
        //getListView().setBackgroundColor(R.color.transparent);

        openNoteFragment(noteFragType.SHOW);
    }

    public void openNoteFragment(noteFragType fragmentType){
        final String SHOW_NOTE = "showNote";
        final String EDIT_NOTE = "editNote";

        Fragment fragment = null;
        String stackTag = "";

        switch (fragmentType){
            case SHOW:
                fragment = new NoteItemShowFragment();
                stackTag = SHOW_NOTE;
                break;
            case EDIT:
                fragment = new NoteItemEditFragment();
                stackTag = EDIT_NOTE;
                break;
        }

        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(stackTag)
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.fl_note_list_container, fragment)
                    .commit();
        }
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getActivity(), NoteDBContract.NOTE_TABLE_URI, null,
                null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mNoteAdapter = new NoteAdapter(getActivity(),(Cursor) data,0);
        getListView().setAdapter(mNoteAdapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mNoteAdapter.swapCursor(null);
    }
}
