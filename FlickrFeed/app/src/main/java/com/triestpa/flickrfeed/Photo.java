package com.triestpa.flickrfeed;

public class Photo {
    private String title;
    private String author;
    private String shareLink;
    private String published;
    private String dateTaken;
    private String photoLink;

    public Photo(String title, String author, String shareLink, String published, String dateTaken, String photoLink) {
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

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
}
