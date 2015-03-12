package com.triestpa.flickrfeed;

import java.util.Calendar;

public class Photo {
    private String title;
    private String author;
    private String shareLink;
    private Calendar published;
    private Calendar dateTaken;
    private String photoLink;

    public Photo() {
    }

    public Photo(String title, String author, String shareLink, Calendar published, Calendar dateTaken, String photoLink) {
        this.title = title;
        this.author = author;
        this.shareLink = shareLink;
        this.published = published;
        this.dateTaken = dateTaken;
        this.photoLink = photoLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public Calendar getPublished() {
        return published;
    }

    public void setPublished(Calendar published) {
        this.published = published;
    }

    public Calendar getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Calendar dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
}
