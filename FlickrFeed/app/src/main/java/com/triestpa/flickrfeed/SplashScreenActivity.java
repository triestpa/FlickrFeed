package com.triestpa.flickrfeed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SplashScreenActivity extends ActionBarActivity {
    private final String TAG = SplashScreenActivity.class.getSimpleName();
    private final String requestURL = "http://api.flickr.com/services/feeds/photos_public.gne?tags=boston";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        String[] url = new String[1];
        url[0] = requestURL;
        new XMLFeedParseTask().execute(url);
    }

    public void endLoading() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Aysnc task to download and parse feed from Flickr
    class XMLFeedParseTask extends AsyncTask<String, Integer, ArrayList<Photo>> {
        private final String TAG = XMLFeedParseTask.class.getSimpleName();

        XmlPullParser mParser;

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
                Log.e(TAG, e3.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Photo> photos) {
            super.onPostExecute(photos);
            if (photos == null || photos.isEmpty()) {
                //show error message
            }
            else {
                endLoading();
            }
        }

        @Override
        protected void onProgressUpdate(Integer[] values) {
            super.onProgressUpdate(values);
        }
    }
}
