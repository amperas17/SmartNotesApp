package com.amperas17.smartnotesapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 *
 */
public class NoteItemShowFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_item_show, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if (bundle!=null){
            Toast.makeText(getActivity(),bundle.toString(),Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(),"null",Toast.LENGTH_LONG).show();
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
}
