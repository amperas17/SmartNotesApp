package com.amperas17.smartnotesapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for Note list items
 */
public class NoteAdapter extends CursorAdapter{
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
            Note note = new Note(cursor);
            holder.tvId.setText(note.mId.toString());
            holder.tvId.setTag(note);

            ImageDownloader downloader = new ImageDownloader(context);
            downloader.setImage(note.mImagePath,holder.ivImage, ImageDownloader.imageSize.SMALL_ICON);

            holder.tvTitle.setText(note.mTitle);

            setIcon(note.mRank,holder.ivPriority);

        }
    }

    private void setIcon(int rank,ImageView imageView){
        switch (rank){
            case NoteDBContract.NoteTable.NO_PRIORITY:
                imageView.setImageResource(R.drawable.ic_white_pin);
                break;
            case NoteDBContract.NoteTable.LOW_PRIORITY:
                imageView.setImageResource(R.drawable.ic_green_pin);
                break;
            case NoteDBContract.NoteTable.MEDIUM_PRIORITY:
                imageView.setImageResource(R.drawable.ic_yellow_pin);
                break;
            case NoteDBContract.NoteTable.HIGH_PRIORITY:
                imageView.setImageResource(R.drawable.ic_red_pin);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_white_pin);
        }
    }

    public static class ViewHolder {
        public TextView tvId;
        public ImageView ivImage;
        public TextView tvTitle;
        public ImageView ivPriority;

    }
}
