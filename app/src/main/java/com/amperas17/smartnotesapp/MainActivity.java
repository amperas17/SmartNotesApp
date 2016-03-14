package com.amperas17.smartnotesapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    final static Integer LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_note_list_container, new NoteListFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        //Log.d(LOG_TAG, "" + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            while (getSupportFragmentManager().getBackStackEntryCount()>0) {
                getSupportFragmentManager().popBackStackImmediate();
                //Log.d(LOG_TAG, "" + getSupportFragmentManager().getBackStackEntryCount());
            }
        }
    }
}
