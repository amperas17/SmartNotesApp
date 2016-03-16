package com.amperas17.smartnotesapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by Вова on 16.03.2016.
 */
public final class FileSaver {

    public enum fileType{TXT_FILE,JSON_FILE}

    Context mContext;

    public FileSaver(Context context){
        mContext = context;
    }

    public void saveFile(String filePath,Note note,fileType type) {
        String result = "";
        switch (type){
            case TXT_FILE:
                filePath = filePath + ".txt";
                result = note.getHumanReadableString();
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




    /*
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public static  File getStorageDir(Context context, String folderName) {
        // Get the directory for the app's private documents directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), folderName);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.d(NoteDBContract.LOG_TAG, "Directory not created");
            }
        }
        return file;
    }


    public static boolean storeTextFile(Context context,Note note) {
        try {
            if (isExternalStorageWritable()) {
                File directory = getStorageDir(context, "Notes");

                String filePath = directory.getPath()+"/" + note.mTitle+".txt";

                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                writer.write(note.toString());
                writer.close();
                Gson gson = new Gson();
                String json = gson.toJson(note);
                Log.d(NoteDBContract.LOG_TAG,""+json);
            }

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }

        return true;
    }*/


}
