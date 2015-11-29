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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class CreatePhotoWithAlbum extends AppCompatActivity {
    private Toolbar mToolbar;
    ParseObject album;
    GridView grid;
    String album_id;
    TextView title;
    TextView message;
    boolean condition;
    PhotosImpl photosImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_photo_with_album);

        grid = (GridView) findViewById(R.id.gridview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        title = (TextView) findViewById(R.id.albumtitle);
        message = (TextView) findViewById(R.id.hint);
        album_id = getIntent().getExtras().getString(ParseConstants.ALBUM_TABLE);

        photosImpl = new PhotosImpl();
        queryForPhotos();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_photo_with_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addPhotos) {

            Intent intent = new Intent(CreatePhotoWithAlbum.this, AddPhotos.class);
            intent.putExtra(ParseConstants.ALBUM_TABLE, album_id);
            startActivityForResult(intent, 100);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void queryForPhotos() {
        photosImpl.queryForPhotos(album_id,title,CreatePhotoWithAlbum.this,new ImageListImpl(message,grid,R.layout.grid_photos,CreatePhotoWithAlbum.this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                queryForPhotos();

            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }
}
