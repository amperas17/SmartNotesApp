package com.amperas17.smartnotesapp;

import android.content.Context;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Save files into device memory.
 */
public final class FileSaver {

    public enum fileType{TXT_FILE,JSON_FILE}

    Context mContext;

    public FileSaver(Context context){
        mContext = context;
    }

    public void saveNoteFile(String filePath, Note note, fileType type) {
        String result = "";
        switch (type){
            case TXT_FILE:
                filePath = filePath + ".txt";
                result = note.toHumanReadableString();
                break;
            case JSON_FILE:
                filePath = filePath + ".json";
                Gson gson = new Gson();
                result = gson.toJson(note);
                break;
            default:
                break;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);

            writer.write(result);
            writer.close();
            //Log.d(NoteDBContract.LOG_TAG, "" + result);
            Toast.makeText(mContext, "File was saved successfully", Toast.LENGTH_LONG);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "File did not found", Toast.LENGTH_LONG);
        } catch (IOException e) {
            Toast.makeText(mContext, "Error! File was not saved.", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
    }



}
