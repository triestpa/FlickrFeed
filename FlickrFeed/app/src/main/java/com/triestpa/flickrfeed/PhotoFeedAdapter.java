package com.triestpa.flickrfeed;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.triestpa.flickrfeed.PhotoContent.Photo;

import java.util.ArrayList;

/* ListAdapter to render the photo list */
public class PhotoFeedAdapter extends ArrayAdapter<Photo> {
    private final String TAG = PhotoFeedAdapter.class.getSimpleName();
    private PhotoFeedActivity mContext;
    private int mWidth, mHeight;

    private int infoPanePosition = -1;

    public PhotoFeedAdapter(PhotoFeedActivity a, int layoutId,
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
        TextView imageTitle;
        TextView imageAuthor;
        TextView timeCreated;
        ImageView expandButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Photo photo = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_photo_cell, parent, false);;
            viewHolder.photoImage = (ImageView) convertView.findViewById(R.id.photo_image);
            viewHolder.photoImage.setMinimumWidth(mWidth);
            viewHolder.photoImage.setMinimumHeight(mHeight/2);

            viewHolder.infoPane = (RelativeLayout) convertView.findViewById(R.id.info_pane);
            viewHolder.imageTitle = (TextView) convertView.findViewById(R.id.image_title);
            viewHolder.imageAuthor = (TextView) convertView.findViewById(R.id.image_author);
            viewHolder.timeCreated = (TextView) convertView.findViewById(R.id.image_date);

            viewHolder.expandButton = (ImageView) convertView.findViewById(R.id.expand_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Launch the PhotoViewActivity when expand button is clicked
        viewHolder.expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, ""+photo.getId());
                Intent intent = new Intent(mContext, PhotoViewActivity.class);
                intent.putExtra(PhotoViewActivity.ARG_ITEM_ID, photo.getId());
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        mContext, viewHolder.photoImage, PhotoViewActivity.EXTRA_IMAGE);
                ActivityCompat.startActivity(mContext, intent, options.toBundle());
            }
        });

        //Load image, and size to fill 1/2 of screen height
        Picasso.with(mContext).load(photo.getPhotoURL()).resize(mWidth, mHeight / 2).centerCrop().into(viewHolder.photoImage);

        viewHolder.imageTitle.setText(photo.getTitle());
        viewHolder.imageAuthor.setText(photo.getAuthor());

        String dateString = DateUtils.getRelativeDateTimeString(mContext, photo.getDateTaken().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0).toString();
        viewHolder.timeCreated.setText(dateString);


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
