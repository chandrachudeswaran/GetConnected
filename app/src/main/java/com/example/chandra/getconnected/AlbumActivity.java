package com.example.chandra.getconnected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    EditText album_title;
    RadioGroup privacy;
    boolean isPublic = true;
    String title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        album_title = (EditText) findViewById(R.id.album);
        privacy = (RadioGroup) findViewById(R.id.accessgroup);


    }


    public void createAlbum(View view) {

        if (doValidation()) {

            final ParseObject album = new ParseObject(ParseConstants.ALBUM_TABLE);
            title = album_title.getText().toString();
            album.put(ParseConstants.ALBUM_FIELD_TITLE, album_title.getText().toString());
            album.put(ParseConstants.ALBUM_FIELD_OWNER, ParseUser.getCurrentUser());
            int checkedId = privacy.getCheckedRadioButtonId();
            if (checkedId == R.id.privatealbum) {
                isPublic = false;
            }
            album.put(ParseConstants.ALBUM_FIELD_ISPUBLIC, isPublic);
            album.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ActivityUtility.Helper.makeToast(AlbumActivity.this, album_title.getText().toString() + "  " + "album Created");
                        Intent intent = new Intent(AlbumActivity.this, CreatePhotoWithAlbum.class);
                        intent.putExtra(ParseConstants.ALBUM_TABLE, album.getObjectId());
                        intent.putExtra(GetConnectedConstants.EXISTING_ALBUM,false);
                        startActivity(intent);
                        finish();
                    } else {
                        ActivityUtility.Helper.writeErrorLog(e.toString());
                    }
                }
            });
        }
    }


    public boolean doValidation() {

        if (album_title.getText().length() == 0) {
            ActivityUtility.Helper.makeToast(AlbumActivity.this, GetConnectedConstants.TITLE_REQUIRED);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
