package com.amperas17.smartnotesapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amperas17.smartnotesapp.dao.Note;
import com.amperas17.smartnotesapp.db.NoteDBContract;
import com.amperas17.smartnotesapp.R;
import com.amperas17.smartnotesapp.db.NoteTableContract;
import com.amperas17.smartnotesapp.util.ImageDownloader;

/**
 * Adapter for Note list items
 */
public class NoteAdapter extends CursorAdapter{
    public NoteAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.note_list_item, parent, false);

        ViewHolder holder = new ViewHolder();

        holder.tvId = (TextView)rootView.findViewById(R.id.tv_list_item_note_id);

        holder.ivImage = (ImageView)rootView.findViewById(R.id.iv_list_item_image);

        holder.tvTitle = (TextView)rootView.findViewById(R.id.tv_list_item_title);

        holder.ivPriority = (ImageView)rootView.findViewById(R.id.iv_list_item_priority);

        rootView.setTag(holder);

        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();
        if (holder != null){
            Note note = new Note(cursor);
            holder.tvId.setText(((Integer)note.mId).toString());
            holder.tvId.setTag(note);

            ImageDownloader downloader = new ImageDownloader(context);
            downloader.setImage(note.mImagePath,holder.ivImage, ImageDownloader.imageSize.SMALL_ICON);

            holder.tvTitle.setText(note.mTitle);

            setIcon(note.mRank,holder.ivPriority);

        }
    }

    private void setIcon(int rank,ImageView imageView){
        switch (rank){
            case NoteTableContract.NO_PRIORITY:
                imageView.setImageResource(R.drawable.ic_white_pin);
                break;
            case NoteTableContract.LOW_PRIORITY:
                imageView.setImageResource(R.drawable.ic_green_pin);
                break;
            case NoteTableContract.MEDIUM_PRIORITY:
                imageView.setImageResource(R.drawable.ic_yellow_pin);
                break;
            case NoteTableContract.HIGH_PRIORITY:
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
