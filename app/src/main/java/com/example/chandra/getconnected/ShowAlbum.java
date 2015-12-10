package com.example.chandra.getconnected;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import com.example.chandra.getconnected.albums.GetPhotos;
import com.example.chandra.getconnected.albums.ImageListImpl;
import com.example.chandra.getconnected.albums.PhotoAdapter;
import com.example.chandra.getconnected.albums.PhotosImpl;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class ShowAlbum extends AppCompatActivity implements PhotoAdapter.IPhotoAdapter {
    private Toolbar mToolbar;
    TextView title;
    TextView hint;
    GridView grid;
    PhotosImpl photosImpl;
    String album_id;
    boolean remove_photos = false;
    boolean addingByOwner;
    String photoId;
    boolean approve = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_album);
        setTitle(GetConnectedConstants.ALBUM_VIEW);
        title = (TextView) findViewById(R.id.albumtitle);
        hint = (TextView) findViewById(R.id.hint);
        grid = (GridView) findViewById(R.id.gridview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent().getExtras() != null) {
            album_id = getIntent().getExtras().getString(ParseConstants.ALBUM_TABLE);

            //Public albums visited by other users should not have add photos option
            remove_photos = getIntent().getExtras().getBoolean(GetConnectedConstants.REMOVE_PHOTOS_OPTION);

            addingByOwner = getIntent().getExtras().getBoolean(GetConnectedConstants.PHOTOS_ADDING_BY_OWNER);

            if (getIntent().getExtras().getBoolean("Approve")) {
                photoId = getIntent().getExtras().getString(ParseConstants.NOTIFICATIONS_PHOTOS);
                approve = true;
            }

        }

        photosImpl = new PhotosImpl();
        photosImpl.queryForPhotos(album_id, title, ShowAlbum.this,
                new ImageListImpl(hint, grid, R.layout.grid_photos, ShowAlbum.this,remove_photos),approve,photoId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!remove_photos) {
            getMenuInflater().inflate(R.menu.menu_show_album, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.addphotos) {
            Intent intent = new Intent(ShowAlbum.this, AddPhotos.class);
            intent.putExtra(ParseConstants.ALBUM_TABLE, album_id);
            intent.putExtra(GetConnectedConstants.PHOTOS_ADDING_BY_OWNER, addingByOwner);
            startActivityForResult(intent, 100);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    if (data.getExtras() != null) {
                        ActivityUtility.Helper.makeToast(getApplicationContext(), "Added Photos will be available after owner's approval");
                    }
                    photosImpl.queryForPhotos(album_id, title, ShowAlbum.this,
                            new ImageListImpl(hint, grid, R.layout.grid_photos, ShowAlbum.this,remove_photos),approve,photoId);
                }

        }
    }

    @Override
    public void onBackPressed() {
        onFinish();
    }

    public void onFinish(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finishApproval() {
        onFinish();
    }
}
