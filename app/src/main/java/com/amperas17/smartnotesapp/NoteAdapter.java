package com.amperas17.smartnotesapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Вова on 12.03.2016.
 */
public class NoteAdapter extends CursorAdapter{
    Picasso mPicasso;
    public NoteAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.note_list_item, parent, false);

        ViewHolder holder = new ViewHolder();

        TextView tvId = (TextView)rootView.findViewById(R.id.tv_list_item_note_id);
        holder.tvId = tvId;

        ImageView ivImage = (ImageView)rootView.findViewById(R.id.iv_list_item_image);
        holder.ivImage = ivImage;

        TextView tvTitle = (TextView)rootView.findViewById(R.id.tv_list_item_title);
        holder.tvTitle = tvTitle;

        ImageView ivPriority = (ImageView)rootView.findViewById(R.id.iv_list_item_priority);
        holder.ivPriority = ivPriority;

        rootView.setTag(holder);

        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();
        if (holder != null){

            holder.tvId.setText(cursor.getString(cursor
                    .getColumnIndex(NoteDBContract.NoteTable._ID)).toString());

            String imagePath = cursor.getString(cursor
                    .getColumnIndex(NoteDBContract.NoteTable.COLUMN_IMAGE_PATH));
            if (imagePath != null){
                File file = new File(imagePath);

                mPicasso.with(context)
                        .load(file)
                        .resizeDimen(R.dimen.list_item_image_size, R.dimen.list_item_image_size)
                        .placeholder(R.drawable.ic_simple_note)
                        .error(R.drawable.ic_simple_note)
                        .centerInside()
                        .into(holder.ivImage);
            } else {
                holder.ivImage.setImageResource(R.drawable.ic_simple_note);
            }

            holder.tvTitle.setText(cursor.getString(cursor
                    .getColumnIndex(NoteDBContract.NoteTable.COLUMN_TITLE)));

            int rank = cursor.getInt(cursor
                    .getColumnIndex(NoteDBContract.NoteTable.COLUMN_RANK));
            switch (rank){
                case NoteDBContract.NoteTable.NO_PRIORITY:
                    holder.ivPriority.setImageResource(R.drawable.ic_white_pin);
                    break;
                case NoteDBContract.NoteTable.LOW_PRIORITY:
                    holder.ivPriority.setImageResource(R.drawable.ic_green_pin);
                    break;
                case NoteDBContract.NoteTable.MEDIUM_PRIORITY:
                    holder.ivPriority.setImageResource(R.drawable.ic_yellow_pin);
                    break;
                case NoteDBContract.NoteTable.HIGH_PRIORITY:
                    holder.ivPriority.setImageResource(R.drawable.ic_red_pin);
                    break;
                default:
                    holder.ivPriority.setImageResource(R.drawable.ic_white_pin);
            }

        }
    }

    public static class ViewHolder {
        public TextView tvId;
        public ImageView ivImage;
        public TextView tvTitle;
        public ImageView ivPriority;

    }
}
