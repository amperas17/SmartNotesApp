package com.amperas17.smartnotesapp;


import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.ExFilePickerParcelObject;

/**
 * Show list of notes, provides interaction with note items.
 */
public class NoteListFragment extends ListFragment implements LoaderManager.LoaderCallbacks{

    final static Integer LOADER_ID = 1;
    final String LOG_TAG = "myLogs";

    public enum noteFragType{SHOW,EDIT}

    final String EDIT_NOTE_TRANSACTION_TAG = "editNote";
    final String SHOW_NOTE_TRANSACTION_TAG = "showNote";

    public static final String [] CONTEXT_MENU_ACTIONS = {"Delete","Edit","Save file"};

    public static final int CONTEXT_ACTION_DELETE = 0;
    public static final int CONTEXT_ACTION_EDIT = 1;
    public static final int CONTEXT_ACTION_SAVE_FILE = 2;

    private static final int SINGLE_DIRECTORY_PICKER_RESULT = 0;



    NoteAdapter mNoteAdapter;
    ListView mListView;
    Note mSavingNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        setHasOptionsMenu(true);

        mSavingNote = new Note();

        //Log.d(LOG_TAG, "NoteFrag:onCreateView");
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==android.R.id.list){
            //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Choose action:");
            String[] menuItems = CONTEXT_MENU_ACTIONS;
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        Note note = (Note)info.targetView.findViewById(R.id.tv_list_item_note_id).getTag();

        switch (menuItemIndex){
            case CONTEXT_ACTION_DELETE:
                Uri uri = ContentUris.withAppendedId(NoteDBContract.NoteTable.TABLE_URI, note.mId);
                getActivity().getContentResolver().delete(uri,null,null);
                break;

            case CONTEXT_ACTION_EDIT:
                Bundle bundle = new Bundle();
                bundle.putParcelable(Note.NOTE_TAG,note);
                openNoteFragment(noteFragType.EDIT,bundle);
                break;

            case CONTEXT_ACTION_SAVE_FILE:
                try {
                    mSavingNote = note.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                chooseDirectoryInFileDialog();
                break;
            default:
                break;
        }
        //Log.d(LOG_TAG, "ContextListMenu: Tag = " + note);
        return true;
    }

    private void chooseDirectoryInFileDialog() {
       Intent intent = new Intent(getActivity().getApplicationContext(),
               ru.bartwell.exfilepicker.ExFilePickerActivity.class);
       intent.putExtra(ExFilePicker.SET_ONLY_ONE_ITEM, true);
       intent.putExtra(ExFilePicker.SET_CHOICE_TYPE, ExFilePicker.CHOICE_TYPE_DIRECTORIES);
       startActivityForResult(intent, SINGLE_DIRECTORY_PICKER_RESULT);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SINGLE_DIRECTORY_PICKER_RESULT) {
            if (data != null) {
                ExFilePickerParcelObject object = data
                        .getParcelableExtra(ExFilePickerParcelObject.class.getCanonicalName());
                try {
                    String directoryPath = object.path + object.names.get(0);
                    String filePath = directoryPath +'/' + mSavingNote.mTitle;

                    FileSaver fileSaver = new FileSaver(getActivity());
                    fileSaver.saveNoteFile(filePath, mSavingNote, FileSaver.fileType.TXT_FILE);
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
        mListView = getListView();
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });
        registerForContextMenu(mListView);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TextView tvID = (TextView)v.findViewById(R.id.tv_list_item_note_id);
        Log.d(LOG_TAG,"onListItemClick"+tvID.getTag().toString());

        Bundle bundle = new Bundle();
        bundle.putInt(NoteDBContract.NoteTable._ID,Integer.parseInt(tvID.getText().toString()));

        openNoteFragment(noteFragType.SHOW,bundle);
    }

    public void openNoteFragment(noteFragType fragmentType,Bundle bundle){
        Fragment fragment;
        String transactionTag;

        switch (fragmentType){
            case SHOW:
                fragment = new NoteItemShowFragment();
                transactionTag = SHOW_NOTE_TRANSACTION_TAG;
                break;
            case EDIT:
                fragment = new NoteItemEditFragment();
                transactionTag = EDIT_NOTE_TRANSACTION_TAG;
                break;
            default:
                fragment = null;
                transactionTag = "";
        }

        if (fragment != null) {
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(transactionTag)
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
