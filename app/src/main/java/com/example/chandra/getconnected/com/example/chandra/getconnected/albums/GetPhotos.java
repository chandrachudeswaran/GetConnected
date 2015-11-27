package com.example.chandra.getconnected.com.example.chandra.getconnected.albums;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.chandra.getconnected.ActivityUtility;
import com.example.chandra.getconnected.CreatePhotoWithAlbum;
import com.example.chandra.getconnected.ParseConstants;
import com.example.chandra.getconnected.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandra on 11/25/2015.
 */
public class GetPhotos {

    ProgressDialog dialog;
    Context context;
    ParseObject album;
    ParseFile file;
    ArrayList<Bitmap> list;
    ImageList imageList;

    public GetPhotos(Context context, ParseObject album, ImageList imageList, boolean existing) {
        this.context = context;
        this.album = album;
        this.imageList = imageList;
        list = new ArrayList<>();


        if (existing) {
            displayDialog();
            query();
        }
        else{
            imageList.sendImages(list);
        }

    }

    public void displayDialog() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Downloading photos for  " + album.getString(ParseConstants.ALBUM_FIELD_TITLE));
        dialog.setCancelable(false);
        dialog.show();
    }

    public void query() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.PHOTO_TABLE);
        query.whereEqualTo(ParseConstants.PHOTO_ALBUM, album);
        query.findInBackground(new FindCallback<ParseObject>() {
                                   @Override
                                   public void done(List<ParseObject> objects, ParseException e) {
                                       if (e == null) {
                                           for (ParseObject parseObject : objects) {
                                               file = parseObject.getParseFile(ParseConstants.PHOTO_FIELD_FILE);
                                               file.getDataInBackground(new GetDataCallback() {
                                                   @Override
                                                   public void done(byte[] data, ParseException e) {
                                                       list.add(BitmapFactory.decodeByteArray(data, 0, data.length));
                                                       setImage();


                                                   }
                                               });
                                           }

                                       } else {
                                           ActivityUtility.Helper.writeErrorLog("Photos null");
                                       }
                                   }
                               }

        );

    }

    public void setImage() {
        dialog.dismiss();
        imageList.sendImages(list);

    }

    public interface ImageList {
        public void sendImages(ArrayList<Bitmap> images);
    }


}
