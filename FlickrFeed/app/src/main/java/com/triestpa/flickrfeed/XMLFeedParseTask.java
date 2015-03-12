package com.triestpa.flickrfeed;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class XMLFeedParseTask extends AsyncTask<String, Integer, ArrayList<Photo>> {
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
            ArrayList<Photo> photos = parseXML(xmlData);
        }
        catch (IOException e3) {
            Log.e(TAG, e3.getMessage());
            return null;
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Photo> photos) {
        super.onPostExecute(photos);
    }

    @Override
    protected void onProgressUpdate(Integer[] values) {
        super.onProgressUpdate(values);
    }

    private ArrayList<Photo> parseXML(Reader xmlData) {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            mParser = pullParserFactory.newPullParser();

            mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            mParser.setInput(xmlData);

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

                        if (name.equals("entry")){
                            thisPhoto = new Photo();
                        } else if (thisPhoto != null){
                            if (name.equals("title")){
                                thisPhoto.setTitle(mParser.nextText());
                            } else if (name.equals("author")){
                                thisPhoto.setAuthor(getAuthor());
                            } else if (name.equals("link")){
                                thisPhoto.setPhotoLink(getURL());
                            } else if (name.equals("published")){
                                thisPhoto.setPublished(getDate());
                            } else if (name.equals("flickr:date_taken")){
                                thisPhoto.setDateTaken(getDate());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = mParser.getName();
                        if (name.equalsIgnoreCase("entry") && thisPhoto != null){
                            photos.add(thisPhoto);
                        }
                }
                eventType = mParser.next();
            }

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

            Calendar calendar = GregorianCalendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            calendar.setTime(sdf.parse(mParser.nextText().substring(0,20)));
            return calendar;

        }
        catch (ParseException e1) {
            Log.e(TAG, e1.getMessage());
            return null;
        }
        catch (IndexOutOfBoundsException e2) {
            Log.e(TAG, e2.getMessage());
            return null;
        }
    }

    private String getAuthor() throws IOException, XmlPullParserException {
            int eventType = mParser.next();
            String eventName = mParser.getName();

            if (eventType == XmlPullParser.START_TAG) {
                    if (eventName.equals("name")) {
                       return mParser.nextText();
                    }
            }
            else if (eventType == XmlPullParser.END_TAG && eventName.equals("author")) {
                return "No Author";
            }

            //Call the method recursively until the author has been found or the author tag is ended
            return getAuthor();
    }

    private String getURL() throws IOException, XmlPullParserException {
        return mParser.getAttributeValue(null, "href");
    }

}
