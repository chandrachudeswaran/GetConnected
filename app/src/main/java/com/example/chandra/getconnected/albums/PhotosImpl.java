package com.example.chandra.getconnected.albums;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by chandra on 11/27/2015.
 */
public class PhotosImpl implements IPhotos {

    ParseObject album;
    TextView titleView;
    Context context_activity;
    ImageListImpl list;

    @Override
    public void queryForPhotos(String objectId, View title, Context context, ImageListImpl imageList,final boolean approve,final String photoId) {
        titleView = (TextView) title;
        this.context_activity = context;
        this.list=imageList;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo("objectId", objectId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    album = object;
                    titleView.setText(object.getString(ParseConstants.ALBUM_FIELD_TITLE));
                }

                new GetPhotos(context_activity, album, list,approve,photoId);
            }
        });
    }
}
