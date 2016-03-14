package com.amperas17.smartnotesapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Map;

/**
 *
 */
public class NoteItemEditFragment extends Fragment {

    Spinner mSpinner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_edit, container, false);
        setHasOptionsMenu(true);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, NoteDBContract.NoteTable.PRIORITIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner = (Spinner) view.findViewById(R.id.spinner_note_edit_rank);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(NoteDBContract.NoteTable.NO_PRIORUTY);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(getActivity(), "Position = " + position,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btSaveMenuItem:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
