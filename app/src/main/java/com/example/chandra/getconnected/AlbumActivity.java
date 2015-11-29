package com.example.chandra.getconnected;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AlbumActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    EditText album_title;
    RadioGroup privacy;
    boolean isPublic = true;
    String title;
    String album_id;
    Button save_update_button;
    ParseObject album;
    RadioButton public_button;
    RadioButton private_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);


        album_title = (EditText) findViewById(R.id.album);
        privacy = (RadioGroup) findViewById(R.id.accessgroup);
        save_update_button = (Button) findViewById(R.id.save);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        public_button=(RadioButton)findViewById(R.id.publicalbum);
        private_button=(RadioButton)findViewById(R.id.privatealbum);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Create Album");

        if (getIntent().getExtras()!= null) {
            album_id = getIntent().getExtras().getString(ParseConstants.OBJECT_ID);
            save_update_button.setText("Update Album");
            setTitle("Edit Album");
            queryForAlbum();
        }

    }


    public void createAlbum(View view) {

        if(album_id==null) {

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
                            Intent intent = new Intent();
                            intent.putExtra(ParseConstants.ALBUM_TABLE,album.getObjectId());
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            ActivityUtility.Helper.writeErrorLog(e.toString());
                        }
                    }
                });
            }
        }
        else{

            if(doValidation()){
                updateAlbum();
            }
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


    public void queryForAlbum() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, album_id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    album = object;
                    album_title.setText(album.getString(ParseConstants.ALBUM_FIELD_TITLE));
                    if (album.getBoolean(ParseConstants.ALBUM_FIELD_ISPUBLIC)) {
                        public_button.setChecked(true);
                    } else {
                        private_button.setChecked(true);
                    }
                }
            }
        });
    }

    public void updateAlbum(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, album_id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    int checkedId = privacy.getCheckedRadioButtonId();
                    if (checkedId == R.id.privatealbum) {
                        isPublic = false;
                    }else{
                        isPublic=true;
                    }
                    object.put(ParseConstants.ALBUM_FIELD_ISPUBLIC,isPublic);
                    object.put(ParseConstants.ALBUM_FIELD_TITLE, album_title.getText().toString());
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Intent intent = new Intent();
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        }
                    });

                }
            }
        });
    }
}
