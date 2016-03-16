package com.amperas17.smartnotesapp;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Вова on 15.03.2016.
 */
public class Note implements Parcelable,Cloneable {
    public  static final String NOTE_TAG = "note";

    public Integer mId;
    protected String mTitle;
    public String mContent;
    public Integer mRank;
    public Integer mCreated;
    public String mImagePath;
    public Double mLatitude;
    public Double mLongitude;

    public Note(){
        mId = null;
        mTitle = "";
        mContent = "";
        mRank = 0;
        mCreated = null;
        mImagePath = null;
        mLatitude = null;
        mLongitude = null;
    }
    public Note(int id,String title,String content,int rank,int created,
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
        mCreated = in.readInt();
        mImagePath = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    public Note(Cursor cursor){
        mId = cursor.getInt(cursor
                .getColumnIndex(NoteDBContract.NoteTable._ID));
        mTitle = cursor.getString(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_TITLE));
        mContent = cursor.getString(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_CONTENT));
        mRank = cursor.getInt(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_RANK));
        mCreated = cursor.getInt(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_CREATED));
        mImagePath = cursor.getString(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_IMAGE_PATH));
        mLatitude = cursor.getDouble(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_LATITUDE));
        mLongitude = cursor.getDouble(cursor
                .getColumnIndex(NoteDBContract.NoteTable.COLUMN_LONGITUDE));
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
        dest.writeInt(mCreated);
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
                mCreated+" "+mImagePath+" "+mLatitude+" "+mLongitude;
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

    public String getHumanReadableString(){
        String result;
        result = "Title: " + mTitle + "\n" +
                "Content: " + mContent + "\n" +
                "Priority: " + NoteDBContract.NoteTable.PRIORITIES[mRank] + "\n" +
                "Created: " + mCreated + "\n" +
                "Latitude: " + mLatitude + "\n" +
                "Longitude: " + mLongitude + "\n";
        return result;
    }

}
