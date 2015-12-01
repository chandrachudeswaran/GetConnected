package com.example.chandra.getconnected;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chandra.getconnected.albums.Album;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.users.UserAlbumAdapter;
import com.example.chandra.getconnected.utility.PhotoUtility;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileView extends AppCompatActivity {
    ImageView profile;
    TextView first_name;
    TextView last_name;
    TextView gender;
    private Toolbar mToolbar;
    ParseFile profile_picture;
    GridView gridView;
    ParseUser profile_view_user;
    ParseFile album_picture;
    ArrayList<Album> albumsList;
    UserAlbumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        profile = (ImageView) findViewById(R.id.profile);
        first_name = (TextView) findViewById(R.id.first_name);
        last_name = (TextView) findViewById(R.id.last_name);
        gender = (TextView) findViewById(R.id.gender);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        gridView = (GridView) findViewById(R.id.gridview);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Profile View");
        queryProfileForUser(getIntent().getExtras().getString(ParseConstants.OBJECT_ID));
    }


    public void queryProfileForUser(String objectId) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseConstants.OBJECT_ID, objectId);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    profile_view_user = object;
                    first_name.setText(object.getString(GetConnectedConstants.USER_FIRST_NAME));
                    last_name.setText(object.getString(GetConnectedConstants.USER_LAST_NAME));
                    gender.setText(object.getString(GetConnectedConstants.USER_GENDER));
                    if (object.getString(GetConnectedConstants.USER_IMAGE_FACEBOOK) != null) {
                        Picasso.with(ProfileView.this).load(object.getString(GetConnectedConstants.USER_IMAGE_FACEBOOK)).into(profile);
                        queryPublicAlbumsForUser(profile_view_user);
                    } else {
                        profile_picture = object.getParseFile(GetConnectedConstants.USER_PICTURE);
                        if (profile_picture != null) {
                            profile_picture.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    profile.setImageBitmap(PhotoUtility.decodeSampledBitmap(data));
                                    queryPublicAlbumsForUser(profile_view_user);
                                }
                            });
                        } else {
                            profile.setImageBitmap(BitmapFactory.decodeResource(ProfileView.this.getResources(),
                                    R.drawable.no_image));
                            queryPublicAlbumsForUser(profile_view_user);
                        }
                    }
                }
            }
        });
    }

    public void queryPublicAlbumsForUser(ParseUser profile_view_user) {
        albumsList = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.ALBUM_FIELD_OWNER, profile_view_user);
        query.whereEqualTo(ParseConstants.ALBUM_FIELD_ISPUBLIC, true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject obj : objects) {
                        final Album album = new Album();
                        album.setTitle(obj.getString(ParseConstants.ALBUM_FIELD_TITLE));
                        album.setObjectId(obj.getObjectId());
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.PHOTO_TABLE);
                        query.whereEqualTo(ParseConstants.PHOTO_ALBUM, obj);
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    album_picture = object.getParseFile(ParseConstants.PHOTO_FIELD_FILE);
                                    album_picture.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            album.setAlbum_image(BitmapFactory.decodeByteArray(data, 0, data.length));
                                            albumsList.add(album);
                                            adapter = new UserAlbumAdapter(ProfileView.this, R.layout.grid_photos, albumsList);
                                            gridView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                } else {
                                    album.setAlbum_image(BitmapFactory.decodeResource(ProfileView.this.getResources(),
                                            R.drawable.no_image));
                                    albumsList.add(album);
                                    adapter = new UserAlbumAdapter(ProfileView.this, R.layout.grid_photos, albumsList);
                                    gridView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
