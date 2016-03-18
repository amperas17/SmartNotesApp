package com.amperas17.smartnotesapp.dao;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.amperas17.smartnotesapp.db.NoteDBContract;
import com.amperas17.smartnotesapp.db.NoteTableContract;

import java.text.SimpleDateFormat;

/**
 * Provides work with Note objects.
 */
public class Note implements Parcelable,Cloneable {
    public  static final String NOTE_TAG = "note";
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

    public int mId;
    public String mTitle;
    public String mContent;
    public int mRank;
    public double mCreated;
    public String mImagePath;
    public double mLatitude;
    public double mLongitude;

    public Note(){
        mId = 0;
        mTitle = "";
        mContent = "";
        mRank = 0;
        mCreated = 0;
        mImagePath = "";
        mLatitude = 0;
        mLongitude = 0;
    }
    public Note(int id,String title,String content,int rank,double created,
                String imagePath,double latitude,double longitude){
        mId = id;
        mTitle = title;
        mContent = content;
        mRank = rank;
        mCreated = created;
        mImagePath = imagePath;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public Note(Parcel in){
        mId = in.readInt();
        mTitle = in.readString();
        mContent = in.readString();
        mRank = in.readInt();
        mCreated = in.readDouble();
        mImagePath = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    public Note(Cursor cursor){
        mId = cursor.getInt(cursor
                .getColumnIndex(NoteTableContract._ID));
        mTitle = cursor.getString(cursor
                .getColumnIndex(NoteTableContract.COLUMN_TITLE));
        mContent = cursor.getString(cursor
                .getColumnIndex(NoteTableContract.COLUMN_CONTENT));
        mRank = cursor.getInt(cursor
                .getColumnIndex(NoteTableContract.COLUMN_RANK));
        mCreated = cursor.getDouble(cursor
                .getColumnIndex(NoteTableContract.COLUMN_CREATED));
        mImagePath = cursor.getString(cursor
                .getColumnIndex(NoteTableContract.COLUMN_IMAGE_PATH));
        mLatitude = cursor.getDouble(cursor
                .getColumnIndex(NoteTableContract.COLUMN_LATITUDE));
        mLongitude = cursor.getDouble(cursor
                .getColumnIndex(NoteTableContract.COLUMN_LONGITUDE));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mContent);
        dest.writeInt(mRank);
        dest.writeDouble(mCreated);
        dest.writeString(mImagePath);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {

        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public String toString() {
        return mId+" "+mTitle+" "+mContent+" "+mRank+" "+
                mDateFormat.format(mCreated)+" "+mImagePath+" "+mLatitude+" "+mLongitude;
    }

    @Override
    public boolean equals(Object o) {
        Note note = (Note) o;
        if (note.mTitle.equals(this.mTitle) &&
                note.mContent.equals(this.mContent) &&
                note.mRank == this.mRank){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public Note clone() throws CloneNotSupportedException {
        return (Note) super.clone();
    }

    public String toHumanReadableString(){

        String result;
        result = "Title: " + mTitle + "\n" +
                "Content: " + mContent + "\n" +
                "Priority: " + NoteTableContract.PRIORITIES[mRank] + "\n" +
                "Created: " + mDateFormat.format(mCreated) + "\n" +
                "Latitude: " + mLatitude + "\n" +
                "Longitude: " + mLongitude + "\n";
        return result;
    }

}
