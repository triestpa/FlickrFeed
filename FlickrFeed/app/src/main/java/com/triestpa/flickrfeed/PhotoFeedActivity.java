package com.triestpa.flickrfeed;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;


public class PhotoFeedActivity extends ActionBarActivity {
    private final String TAG = PhotoFeedActivity.class.getSimpleName();
    ArrayList<Photo> mPhotos;
    PhotoListAdapter mAdapter;
    LinearLayout currentInfoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        Log.d(TAG, "Show List");

        ListView photoListView = (ListView) findViewById(R.id.photo_list_view);
        mPhotos = PhotoManager.getPhotos();
        mAdapter = new PhotoListAdapter(this, R.layout.adapter_photo_cell, mPhotos);
        photoListView.setAdapter(mAdapter);
        photoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout infoPane = (LinearLayout) view.findViewById(R.id.info_pane);
                int infoPanePosition = mAdapter.getInfoPanePosition();
                if (infoPanePosition == position) {
                    infoPane.setVisibility(View.GONE);
                    currentInfoPane = null;
                    mAdapter.setInfoPanePosition(-1);
                }
                if (infoPanePosition != -1 && currentInfoPane != null) {
                    currentInfoPane.setVisibility(View.GONE);
                }
                mAdapter.setInfoPanePosition(position);
                infoPane.setVisibility(View.VISIBLE);
                currentInfoPane = infoPane;
            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
