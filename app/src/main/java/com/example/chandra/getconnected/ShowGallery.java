package com.example.chandra.getconnected;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.chandra.getconnected.albums.Album;
import com.example.chandra.getconnected.albums.AlbumAdapter;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowGallery extends Fragment {

    OnCreateAlbum onCreateAlbum;
    ArrayList<Album> albumList;
    Context context;
    List<ParseObject> parseAlbums;
    ListView listView;
    ParseFile imageParseFile;
    ParseUser user;
    AlbumAdapter adapter;

    public ShowGallery() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        user = ParseUser.getCurrentUser();
        queryAllAlbumForUser();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_home_add_gallery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout) {
            if (user.get("authData") != null) {
                if (user.get("authData").toString().contains("twitter")) {
                    ParseUser.logOut();
                }
                if (user.get("authData").toString().contains("facebook")) {
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    if (accessToken != null) {
                        LoginManager.getInstance().logOut();
                    }
                    ParseUser.logOut();
                    onCreateAlbum.doFinish();

                } else {

                    ParseUser.logOut();
                    onCreateAlbum.doFinish();
                }
            } else {
                ParseUser.logOut();
                onCreateAlbum.doFinish();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_gallery, container, false);
        listView = (ListView) view.findViewById(R.id.gallerylistview);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.context = activity;
            onCreateAlbum = (OnCreateAlbum) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }


    public interface OnCreateAlbum {
        void doFinish();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new AlbumAdapter(context, R.layout.gallerylistrow, albumList);
        listView.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
    }

    public void queryAllAlbumForUser() {
        albumList = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.ALBUM_FIELD_OWNER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    parseAlbums = objects;
                    for (final ParseObject obj : objects) {
                        final Album album = new Album();
                        album.setTitle(obj.getString(ParseConstants.ALBUM_FIELD_TITLE));
                        album.setIsPublic(obj.getBoolean(ParseConstants.ALBUM_FIELD_ISPUBLIC));
                        album.setObjectId(obj.getObjectId());
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.PHOTO_TABLE);
                        query.whereEqualTo(ParseConstants.PHOTO_ALBUM, obj);
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    imageParseFile = object.getParseFile(ParseConstants.PHOTO_FIELD_FILE);
                                    imageParseFile.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            album.setAlbum_image(BitmapFactory.decodeByteArray(data, 0, data.length));
                                            albumList.add(album);
                                            adapter = new AlbumAdapter(context, R.layout.gallerylistrow, albumList);
                                            listView.setAdapter(adapter);
                                            adapter.setNotifyOnChange(true);

                                        }
                                    });
                                } else {
                                    album.setAlbum_image(BitmapFactory.decodeResource(context.getResources(),
                                            R.drawable.no_image));
                                    albumList.add(album);
                                    adapter = new AlbumAdapter(context, R.layout.gallerylistrow, albumList);
                                    listView.setAdapter(adapter);
                                    adapter.setNotifyOnChange(true);
                                }

                            }
                        });

                    }

                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }


}
