package com.example.chandra.getconnected.albums;

import android.graphics.Bitmap;

import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by chandra on 11/25/2015.
 */
public class Album {

    String title;
    boolean isPublic;
    String userid;
    Bitmap album_image;
    String objectId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Bitmap getAlbum_image() {
        return album_image;
    }

    public void setAlbum_image(Bitmap album_image) {
        this.album_image = album_image;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Album() {

    }
}
