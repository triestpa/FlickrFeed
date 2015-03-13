package com.triestpa.flickrfeed.PhotoContent;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

//Parse the XML feed into an array of Photo objects
public class FlickrFeedParser {
    private final String TAG = FlickrFeedParser.class.getSimpleName();

    XmlPullParser mParser;

    public ArrayList<Photo> parseXML(Reader xmlData) {
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
                                String type =  mParser.getAttributeValue(null, "type");
                               if (type.equals("text/html")) {
                                    thisPhoto.setShareLink(getURL());
                                }
                                else if (type.equals("image/jpeg")) {
                                   thisPhoto.setPhotoURL(getURL());
                               }
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

            return photos;
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

    //Read a date from the xml feed
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

    //Find the nested author tag
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

    //Read the url attribute
    private String getURL() throws IOException, XmlPullParserException {
        return mParser.getAttributeValue(null, "href");
    }

}
