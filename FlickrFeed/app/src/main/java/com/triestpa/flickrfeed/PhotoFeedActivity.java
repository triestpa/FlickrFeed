package com.triestpa.flickrfeed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.triestpa.flickrfeed.PhotoContent.FlickrFeedParser;
import com.triestpa.flickrfeed.PhotoContent.Photo;
import com.triestpa.flickrfeed.PhotoContent.PhotoManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

//Display an interactive list of photos
public class PhotoFeedActivity extends ActionBarActivity {
    private final String TAG = PhotoFeedActivity.class.getSimpleName();
    ArrayList<Photo> mPhotos;
    PhotoFeedAdapter mAdapter;
    RelativeLayout currentInfoPane;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        //Initialize the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("  Beautiful Boston");
        getSupportActionBar().setIcon(R.drawable.app_icon_small);

        //Initialize the Swipe Refresh Layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.photo_list_swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark, R.color.primary_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPhotos();
            }
        });

        //Initialize the ListView
        ListView photoListView = (ListView) findViewById(R.id.photo_list_view);
        mPhotos = PhotoManager.getPhotos();
        mAdapter = new PhotoFeedAdapter(this, R.layout.adapter_photo_cell, mPhotos);
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

    //Slide the info pane down
    public void animateInfoPaneOut(RelativeLayout infoPane) {
        infoPane.animate().translationY(infoPane.getHeight());
    }

    //Slide the info pane up
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
            mSwipeRefreshLayout.setRefreshing(true);
            refreshPhotos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Call the async task to redownload the feed
    private void refreshPhotos() {
        String[] url = new String[1];
        url[0] = PhotoManager.requestURL;
        new RefreshFeedParseTask().execute(url);
    }

    public void errorFallback(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //Aysnc task to refresh feed from Flickr
    class RefreshFeedParseTask extends AsyncTask<String, Integer, ArrayList<Photo>> {
        private final String TAG = RefreshFeedParseTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<Photo> doInBackground(String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(params[0]).build();
                Response response = client.newCall(request).execute();

                InputStreamReader xmlData = (InputStreamReader) response.body().charStream();

                FlickrFeedParser feedParser = new FlickrFeedParser();
                return feedParser.parseXML(xmlData);
            } catch (IOException e3) {
                errorFallback(e3.getMessage());
                Log.e(TAG, e3.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Photo> photos) {
            super.onPostExecute(photos);
            if (photos == null || photos.isEmpty()) {
                errorFallback("Error Loading Feed");
                Log.e(TAG, "Error Loading Feed");
            }
            else {
                PhotoManager.clearPhotos();
                PhotoManager.addPhotos(photos);
                mPhotos = PhotoManager.getPhotos();
                mAdapter.notifyDataSetChanged();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onProgressUpdate(Integer[] values) {
            super.onProgressUpdate(values);
        }
    }
}
