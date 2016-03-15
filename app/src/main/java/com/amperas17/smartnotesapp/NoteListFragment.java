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
import android.widget.TextView;

/**
 *
 */
public class NoteListFragment extends ListFragment implements LoaderManager.LoaderCallbacks{

    final static Integer LOADER_ID = 1;
    final String LOG_TAG = "myLogs";

    public enum noteFragType{SHOW,EDIT}

    NoteAdapter mNoteAdapter;
    ListView mListView;

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
                openNoteFragment(noteFragType.EDIT,null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mListView = getListView();
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TextView tvID = (TextView)v.findViewById(R.id.tv_list_item_note_id);

        Bundle bundle = new Bundle();
        bundle.putInt(NoteDBContract.NoteTable._ID,Integer.parseInt(tvID.getText().toString()));
        //getListView().setBackgroundColor(R.color.transparent);

        openNoteFragment(noteFragType.SHOW,bundle);
    }

    public void openNoteFragment(noteFragType fragmentType,Bundle bundle){
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
            fragment.setArguments(bundle);
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
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                NoteDBContract.NoteTable.TABLE_URI, null,
                null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mNoteAdapter = new NoteAdapter(getActivity(),(Cursor) data,0);
        mListView.setAdapter(mNoteAdapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mNoteAdapter.swapCursor(null);
    }
}
