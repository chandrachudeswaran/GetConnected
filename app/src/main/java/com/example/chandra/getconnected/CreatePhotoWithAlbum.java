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
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo("objectId", album_id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    album = object;
                    title.setText(object.getString(ParseConstants.ALBUM_FIELD_TITLE));
                }

                 new GetPhotos(CreatePhotoWithAlbum.this, album, CreatePhotoWithAlbum.this);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_photo_with_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addPhotos) {

            Intent intent = new Intent(CreatePhotoWithAlbum.this, AddPhotos.class);
            intent.putExtra(ParseConstants.ALBUM_TABLE, album_id);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendImages(ArrayList<Bitmap> images) {

        if (images == null) {
            message.setText("no");
        } else {
            message.setText("no");
            PhotoAdapter adapter = new PhotoAdapter(CreatePhotoWithAlbum.this, R.layout.grid_photos, images);
            grid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }


}
