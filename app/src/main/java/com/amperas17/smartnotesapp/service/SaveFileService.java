package com.amperas17.smartnotesapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.amperas17.smartnotesapp.receiver.SaveFileResultReceiver;
import com.amperas17.smartnotesapp.dao.Note;
import com.amperas17.smartnotesapp.util.FileSaver;

/**
 * Save file to device using FileSaver.
 */

public class SaveFileService extends IntentService {

    public static final String FILE_PATH_TAG = "filePath";
    public static final String FILE_TYPE_TAG = "fileType";

    public SaveFileService() {
        super("SaveFileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            ResultReceiver receiver = intent.getParcelableExtra(SaveFileResultReceiver.TAG);

            String filePath = intent.getStringExtra(FILE_PATH_TAG);
            Note note = intent.getParcelableExtra(Note.NOTE_TAG);
            FileSaver.fileType fileType = (FileSaver.fileType)intent.getSerializableExtra(FILE_TYPE_TAG);

            FileSaver fileSaver = new FileSaver(this);
            int result = fileSaver.saveNoteFile(filePath, note, fileType);

            receiver.send(result, new Bundle());
        }
    }



}
