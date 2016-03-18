package com.amperas17.smartnotesapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.amperas17.smartnotesapp.db.NoteTableContract;

/**
 * Provides Yes/No dialog before note deleting
 */
public class DeleteNoteDialogFragment extends DialogFragment {

    public static final String DELETE_FRAGMENT_TAG = "deleteNoteTag";
    public static final int RESULT_CODE_YES = 2;

    private int mId;
    private String mTitle;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mId = getArguments().getInt(NoteTableContract._ID);
        mTitle = getArguments().getString(NoteTableContract.COLUMN_TITLE);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Are you sure you want to delete \"" + mTitle + "\" note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(NoteTableContract._ID, mId);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_CODE_YES, intent);
                    }
                })
                .setNegativeButton("No", null);

        return dialog.create();
    }



}
