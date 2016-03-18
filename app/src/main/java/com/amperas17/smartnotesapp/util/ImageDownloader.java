package com.amperas17.smartnotesapp.util;

import android.content.Context;
import android.widget.ImageView;

import com.amperas17.smartnotesapp.R;
import com.squareup.picasso.Picasso;
import java.io.File;

/**
 * Set images to ImageView using Picasso
 */
public class ImageDownloader {
    public enum imageSize{SMALL_ICON,FULL}
    private Context mContext;

    public ImageDownloader(Context context){
        mContext = context;
    }

    public void setImage(String filePath,ImageView imageButton,imageSize size){
        if (filePath != null && !filePath.equals("")) {

            File file = new File(filePath);

            switch (size){
                case SMALL_ICON:
                    Picasso.with(mContext)
                            .load(file)
                            .placeholder(R.drawable.ic_simple_note)
                            .error(R.drawable.ic_simple_note)
                            .resizeDimen(R.dimen.list_item_image_size, R.dimen.list_item_image_size)
                            .centerInside()
                            .into(imageButton);
                    break;
                case FULL:
                    Picasso.with(mContext)
                            .load(file)
                            .placeholder(R.drawable.ic_simple_note)
                            .error(R.drawable.ic_simple_note)
                            .into(imageButton);
                    break;
                default:
                    break;
            }

        } else {
            //There could be different images
            switch (size) {
                case SMALL_ICON:
                    imageButton.setImageResource(R.drawable.ic_simple_note);
                    break;
                case FULL:
                    imageButton.setImageResource(R.drawable.ic_simple_note);
                    break;
                default:
                    break;
            }
        }
    }
}
