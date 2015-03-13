package com.triestpa.flickrfeed;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/* ListAdapter to render the photo list */
public class PhotoListAdapter extends ArrayAdapter<Photo> {
    private Context mContext;
    private int mWidth, mHeight;

    private int infoPanePosition = -1;

    public PhotoListAdapter(Context a, int layoutId,
                            ArrayList<Photo> data) {
        super(a, layoutId, data);
        mContext = a;

        //Get Screen width and height
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        mWidth = display.getWidth();
        mHeight = display.getHeight();
    }

    private class ViewHolder {
        ImageView photoImage;
        RelativeLayout infoPane;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Photo photo = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_photo_cell, parent, false);;
            viewHolder.photoImage = (ImageView) convertView.findViewById(R.id.photo_image);
            viewHolder.photoImage.setMinimumWidth(mWidth);
            viewHolder.photoImage.setMinimumHeight(mHeight/2);

            viewHolder.infoPane = (RelativeLayout) convertView.findViewById(R.id.info_pane);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Load image, and size to fill 1/2 of screen height
        Picasso.with(mContext).load(photo.getPhotoURL()).resize(mWidth, mHeight / 2).centerCrop().into(viewHolder.photoImage);

        //Ensure that only one detail pane is showing at once
        if (infoPanePosition == position) {
            viewHolder.infoPane.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.infoPane.setVisibility(View.GONE);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public int getInfoPanePosition() {
        return infoPanePosition;
    }

    public void setInfoPanePosition(int infoPanePosition) {
        this.infoPanePosition = infoPanePosition;
    }
}
