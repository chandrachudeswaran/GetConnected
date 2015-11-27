package com.example.chandra.getconnected;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chandra.getconnected.com.example.chandra.getconnected.albums.GetPhotos;
import com.example.chandra.getconnected.com.example.chandra.getconnected.albums.PhotoAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class CreatePhotoWithAlbum extends AppCompatActivity implements GetPhotos.ImageList {
    private Toolbar mToolbar;
    ParseObject album;
    GridView grid;
    Bitmap picture;
    String album_id;
    TextView title;
    TextView message;
    boolean condition;

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
        condition = getIntent().getExtras().getBoolean(GetConnectedConstants.EXISTING_ALBUM);
        queryForPhotos(condition);

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

    @Override
    public void sendImages(ArrayList<Bitmap> images) {

        if (images.isEmpty()) {

            message.setText(GetConnectedConstants.NO_PHOTOS);
        } else {
            message.setText(" ");
            PhotoAdapter adapter = new PhotoAdapter(CreatePhotoWithAlbum.this, R.layout.grid_photos, images);
            grid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }





    public void queryForPhotos(final boolean existing) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo("objectId", album_id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    album = object;
                    title.setText(object.getString(ParseConstants.ALBUM_FIELD_TITLE));
                }

                new GetPhotos(CreatePhotoWithAlbum.this, album, CreatePhotoWithAlbum.this, existing);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                queryForPhotos(true);

            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
