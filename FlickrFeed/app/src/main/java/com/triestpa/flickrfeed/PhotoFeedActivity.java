package com.triestpa.flickrfeed;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public class PhotoFeedActivity extends ActionBarActivity {
    private final String TAG = PhotoFeedActivity.class.getSimpleName();
    ArrayList<Photo> mPhotos;
    PhotoListAdapter mAdapter;
    RelativeLayout currentInfoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Beautiful Boston");

        ListView photoListView = (ListView) findViewById(R.id.photo_list_view);
        mPhotos = PhotoManager.getPhotos();
        mAdapter = new PhotoListAdapter(this, R.layout.adapter_photo_cell, mPhotos);
        photoListView.setAdapter(mAdapter);

        //Toggle the info pane on image tap
        photoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout infoPane = (RelativeLayout) view.findViewById(R.id.info_pane);
                int infoPanePosition = mAdapter.getInfoPanePosition();

                //If the info pane is at the current position, close it
                if (infoPanePosition == position) {
                    animateInfoPaneOut(currentInfoPane);
                    currentInfoPane = null;
                    mAdapter.setInfoPanePosition(-1);
                }
                //Else open the info pane at the selected picture
                else {
                    //If there is another info pane open, close it
                    if (infoPanePosition != -1 && currentInfoPane != null) {
                        animateInfoPaneOut(currentInfoPane);
                    }

                    mAdapter.setInfoPanePosition(position);
                    animateInfoPaneIn(infoPane);
                    currentInfoPane = infoPane;
                }
            }
        });
    }

    public void animateInfoPaneOut(RelativeLayout infoPane) {
        infoPane.animate().translationY(infoPane.getHeight());
    }

    public void animateInfoPaneIn(RelativeLayout infoPane) {
        infoPane.setVisibility(View.VISIBLE);

        infoPane.setTranslationY(infoPane.getHeight());
        infoPane.animate().translationY(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
