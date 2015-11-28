package com.example.chandra.getconnected.albums;

import android.graphics.Bitmap;

/**
 * Created by chandra on 11/28/2015.
 */
public class Photo {

    String title;
    Bitmap image;
    String objectId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Photo(){

    }
}
