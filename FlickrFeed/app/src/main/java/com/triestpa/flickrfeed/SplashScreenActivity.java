package com.triestpa.flickrfeed;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

//Show a splash screen while loading the data for the first time
public class SplashScreenActivity extends ActionBarActivity {
    private final String TAG = SplashScreenActivity.class.getSimpleName();
    Button mRetryButton;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mRetryButton = (Button) findViewById(R.id.retry_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_indictor);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
                mRetryButton.setVisibility(View.GONE);
            }
        });

        loadData();
    }

    public void loadData() {
        mProgressBar.setVisibility(View.VISIBLE);
        String[] url = new String[1];
        url[0] = PhotoManager.requestURL;
        new XMLFeedParseTask().execute(url);
    }

    public void endLoading() {
        Intent intent = new Intent(this, PhotoFeedActivity.class);
        startActivity(intent);
        finish();
    }

    public void errorFallback(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        mRetryButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    //Aysnc task to download and parse feed from Flickr
    class XMLFeedParseTask extends AsyncTask<String, Integer, ArrayList<Photo>> {
        private final String TAG = XMLFeedParseTask.class.getSimpleName();

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
            } catch (IOException e) {
                errorFallback(e.getMessage());
                Log.e(TAG, e.getMessage());
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
                PhotoManager.addPhotos(photos);
                endLoading();
            }
        }

        @Override
        protected void onProgressUpdate(Integer[] values) {
            super.onProgressUpdate(values);
        }
    }
}
