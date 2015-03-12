package com.triestpa.flickrfeed;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class XMLFeedParseTask extends AsyncTask<String, Integer, Integer> {
    private final String TAG = XMLFeedParseTask.class.getSimpleName();

    XmlPullParser mParser;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Integer doInBackground(String[] params) {
        HttpClient httpclient = new DefaultHttpClient();

        try {
            HttpGet request = new HttpGet();
            URI website = new URI(params[0]);
            request.setURI(website);
            HttpResponse response = httpclient.execute(request);
            InputStream inputStream = response.getEntity().getContent();
            ArrayList<Photo> photos = parseXML(inputStream);
        }
        catch (URISyntaxException e1) {
            Log.e(TAG, e1.getMessage());
            return null;
        }
        catch (ClientProtocolException e2) {
            Log.e(TAG, e2.getMessage());
            return null;
        }
        catch (IOException e3) {
            Log.e(TAG, e3.getMessage());
            return null;
        }

        return null;
    }

    private ArrayList<Photo> parseXML(InputStream inputStream) {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            mParser = pullParserFactory.newPullParser();

            mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            mParser.setInput(inputStream, null);

            ArrayList<Photo> photos = null;
            int eventType = mParser.getEventType();
            Photo thisPhoto = null;

            while (eventType != XmlPullParser.END_DOCUMENT){
                String name = null;
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        photos = new ArrayList();
                        break;
                    case XmlPullParser.START_TAG:
                        name = mParser.getName();
                        if (name == "entry"){
                            thisPhoto = new Photo();
                        } else if (thisPhoto != null){
                            if (name == "title"){
                                thisPhoto.setTitle(mParser.nextText());
                            } else if (name == "author"){
                                thisPhoto.setAuthor(getAuthor());
                            } else if (name == "link"){
                                thisPhoto.setPhotoLink(getURL());
                            } else if (name == "published"){
                                thisPhoto.setPublished(getDate());
                            } else if (name == "flickr:date_taken"){
                                thisPhoto.setDateTaken(getDate());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = mParser.getName();
                        if (name.equalsIgnoreCase("entry") && thisPhoto != null){
                            Log.d(TAG, thisPhoto.getAuthor());
                            photos.add(thisPhoto);
                        }
                }
                eventType = mParser.next();
            }

            Log .d(TAG, "" + photos.size());
            //doing something with photos
            return null;
        }
        catch (XmlPullParserException e1) {
            Log.e(TAG, e1.getMessage());
            return null;
        }
        catch (IOException e2) {
            Log.e(TAG, e2.getMessage());
            return null;
        }
    }

    private Calendar getDate() throws XmlPullParserException, IOException {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US);
            cal.setTime(sdf.parse(mParser.nextText()));
        }
        catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private String getAuthor() throws IOException, XmlPullParserException {
        if (mParser.getName() == "name"){
            return mParser.nextText();
        }
        return null;
    }

    private String getURL() throws IOException, XmlPullParserException {
        String linkData = mParser.nextText();
        int urlStart = linkData.indexOf("href=\"");
        int urlEnd = linkData.indexOf("\"", urlStart);

        return linkData.substring(urlStart, urlEnd);
    }

    @Override
    protected void onPostExecute(Integer o) {
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Integer[] values) {
        super.onProgressUpdate(values);
    }
}
