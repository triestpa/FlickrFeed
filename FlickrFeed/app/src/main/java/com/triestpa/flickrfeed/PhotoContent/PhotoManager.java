package com.triestpa.flickrfeed.PhotoContent;

import com.triestpa.flickrfeed.PhotoContent.Photo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


// Singleton class to serve photo data to the rest of the app. NOT INTENDED TO BE THREADSAFE.
public class PhotoManager {
    public static final String requestURL = "http://api.flickr.com/services/feeds/photos_public.gne?tags=boston";

    private static ArrayList<Photo> mPhotos;

    //HashMap to store photos to find quickly based on the ID
    private static HashMap<Integer, Photo> LinkMap;

    protected PhotoManager() {
    }

    public static ArrayList<Photo> getPhotos() {
        if (mPhotos == null) {
            mPhotos = new ArrayList<Photo>();
        }
        return mPhotos;
    }

    public static void addPhotos(ArrayList<Photo> photos) {
        for (Photo photo : photos) {
            addPhoto(photo);
        }
    }

    // Add city to the instance of the photo list, assigning it a random ID and placing in the hashmap
    public static void addPhoto(Photo photo) {
        if (LinkMap == null) {
            LinkMap = new HashMap<Integer,Photo>();
        }

        if (mPhotos == null) {
            mPhotos = new ArrayList<Photo>();
        }

        Random rand = new Random();
        int randMax = 100;
        //Dynamically set the max size of the id based on how many photos we're dealing with
        if (mPhotos.size() > 20) {
            randMax = mPhotos.size() * 5;
        }

        //Generate a random number to act as the photo id
        int id = rand.nextInt(randMax + 1);

        //make sure the id is not already assigned to another photo
        while (LinkMap.get(id) != null) {
            id = rand.nextInt(randMax);
        }

        photo.setId(id);
        LinkMap.put(id, photo);
        mPhotos.add(photo);
    }

    public static Photo getPhotoLink(int id) {
        return LinkMap.get(id);
    }

    public static void clearPhotos() {
        LinkMap.clear();
        mPhotos.clear();
    }
}

