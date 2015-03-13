package com.triestpa.flickrfeed;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.triestpa.flickrfeed.PhotoContent.Photo;
import com.triestpa.flickrfeed.PhotoContent.PhotoManager;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

//Full-screen zoomable view of the photo
public class PhotoViewActivity extends ActionBarActivity {
    public final static String ARG_ITEM_ID = "Photo ID";
    public static final String EXTRA_IMAGE = "DetailActivity:image";

    ImageViewTouch imageView;
    Photo mPhoto;

    boolean toolbarHidden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        //Initialize toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.photoview_toolbar);
        RelativeLayout screen = (RelativeLayout) findViewById(R.id.photo_screen);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imageView = (ImageViewTouch) findViewById(R.id.full_image);
        //Enable basic material activity transition animation
        ViewCompat.setTransitionName(imageView, EXTRA_IMAGE);

        imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        //Retrieve the photo to be loaded
        int id = getIntent().getIntExtra(ARG_ITEM_ID, 0);
        mPhoto = PhotoManager.getPhotoLink(id);
        String url = mPhoto.getPhotoURL();
        Picasso.with(this).load(url).into(target);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            shareUrl();
            return true;
        }
        else if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to share either text or URL.
    private void shareUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Check out this Photo!");
        share.putExtra(Intent.EXTRA_TEXT, mPhoto.getShareLink());
        startActivity(Intent.createChooser(share, "Share Photo"));
    }

    //Custom target to load downloaded bitmap into ZoomableImageView
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Toast.makeText(getApplicationContext(), "Problem Loading Photo", Toast.LENGTH_LONG);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

}
