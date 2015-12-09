package com.example.chandra.getconnected.albums;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.chandra.getconnected.utility.ActivityUtility;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.PhotoUtility;
import com.parse.FindCallback;
import com.parse.GetCallback;
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
    ArrayList<Photo> list;
    ImageList imageList;
    boolean approve;
    String photoid;
    ParseObject photo;

    public GetPhotos(Context context, ParseObject album, ImageList imageList, boolean approve,String photoId) {
        this.context = context;
        this.album = album;
        this.imageList = imageList;
        list = new ArrayList<>();
        this.approve=approve;
        this.photoid=photoId;

        displayDialog();
        if (!approve) {
            photo_exists();
        } else {
            queryPhotoObjectForApproval();
        }

    }

    public void showPendingPhotos() {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.NOTIFICATIONS_TABLE);
        query.whereEqualTo(ParseConstants.NOTIFICATIONS_ALBUM,album);
        query.whereEqualTo(ParseConstants.NOTIFICATIONS_PHOTOS,photo);
        query.include(ParseConstants.NOTIFICATIONS_PHOTOS);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    final Photo photo = new Photo();
                    ParseObject ph = object.getParseObject(ParseConstants.NOTIFICATIONS_PHOTOS);
                    photo.setObjectId(ph.getObjectId());
                    photo.setTitle(ph.getString(ParseConstants.PHOTO_CAPTION));
                    file = ph.getParseFile(ParseConstants.PHOTO_FIELD_FILE);
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            photo.setImage(PhotoUtility.decodeSampledBitmap(data));
                            list.add(photo);
                            setImage(true);


                        }
                    });
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });

    }

    public void displayDialog() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Downloading photos for  " + album.getString(ParseConstants.ALBUM_FIELD_TITLE));
        dialog.setCancelable(false);
        dialog.show();
    }

    public void photo_exists() {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.PHOTO_TABLE);
        query.whereEqualTo(ParseConstants.PHOTO_ALBUM, album);
        query.whereEqualTo(ParseConstants.PHOTO_MODERATED_BY_OWNER, true);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    query();
                } else {
                    setImage(false);
                }
            }
        });
    }

    public void query() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.PHOTO_TABLE);
        query.whereEqualTo(ParseConstants.PHOTO_ALBUM, album);
        query.whereEqualTo(ParseConstants.PHOTO_MODERATED_BY_OWNER, true);
        query.findInBackground(new FindCallback<ParseObject>() {
                                   @Override
                                   public void done(List<ParseObject> objects, ParseException e) {
                                       if (e == null) {
                                           for (ParseObject parseObject : objects) {
                                               final Photo photo = new Photo();
                                               photo.setObjectId(parseObject.getObjectId());
                                               photo.setTitle(parseObject.getString(ParseConstants.PHOTO_CAPTION));
                                               file = parseObject.getParseFile(ParseConstants.PHOTO_FIELD_FILE);
                                               file.getDataInBackground(new GetDataCallback() {
                                                   @Override
                                                   public void done(byte[] data, ParseException e) {
                                                       photo.setImage(PhotoUtility.decodeSampledBitmap(data));
                                                       list.add(photo);
                                                       setImage(false);


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

    public void setImage(boolean approve) {
        dialog.dismiss();
        imageList.sendImages(list,approve);

    }


    public interface ImageList {
        void sendImages(ArrayList<Photo> images,boolean approve);
    }

    public void queryPhotoObjectForApproval(){
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.PHOTO_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID,photoid);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    photo=object;
                    showPendingPhotos();
                }else{
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }

}
