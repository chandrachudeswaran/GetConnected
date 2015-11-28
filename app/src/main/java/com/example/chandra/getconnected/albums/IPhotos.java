package com.example.chandra.getconnected.albums;

import android.content.Context;
import android.view.View;

/**
 * Created by chandra on 11/27/2015.
 */
public interface IPhotos {

    public void queryForPhotos(String objectId,View title,Context context,ImageListImpl imageList);
}
