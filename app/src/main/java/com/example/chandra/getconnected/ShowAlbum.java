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

public class ShowAlbum extends AppCompatActivity {
    private Toolbar mToolbar;
    TextView title;
    TextView hint;
    GridView grid;
    PhotosImpl photosImpl;
    String album_id;
    boolean remove_photos = false;

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

            remove_photos = getIntent().getExtras().getBoolean(GetConnectedConstants.REMOVE_PHOTOS_OPTION);

        }

        photosImpl = new PhotosImpl();


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
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        photosImpl.queryForPhotos(album_id, title, ShowAlbum.this,
                new ImageListImpl(hint, grid, R.layout.grid_photos, ShowAlbum.this));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
