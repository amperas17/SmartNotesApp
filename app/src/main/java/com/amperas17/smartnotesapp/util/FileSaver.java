package com.amperas17.smartnotesapp.util;

import android.content.Context;
import com.amperas17.smartnotesapp.dao.Note;
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

    public static int SUCCESS_RESULT_CODE = 0;
    public static int ERROR_RESULT_CODE = -1;

    private Context mContext;

    public FileSaver(Context context){
        mContext = context;
    }

    public int saveNoteFile(String filePath, Note note, fileType type) {
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
            return SUCCESS_RESULT_CODE;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ERROR_RESULT_CODE;

        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_RESULT_CODE;
        }
    }



}
