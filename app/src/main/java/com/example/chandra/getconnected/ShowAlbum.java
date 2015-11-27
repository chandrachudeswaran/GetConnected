package com.example.chandra.getconnected;

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
    ParseObject album;
    TextView title;
    TextView hint;
    GridView grid;

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
        queryForAlbumPhotos(getIntent().getExtras().getString(ParseConstants.ALBUM_TABLE));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void queryForAlbumPhotos(String objectId) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo("objectId", objectId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    album = object;
                    title.setText(object.getString(ParseConstants.ALBUM_FIELD_TITLE));
                }
                new GetPhotos(ShowAlbum.this,album, new ImageListImpl(hint,grid,R.layout.grid_photos,ShowAlbum.this), true);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
