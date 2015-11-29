package com.example.chandra.getconnected;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ProfileView extends AppCompatActivity {
    ImageView profile;
    TextView first_name;
    TextView last_name;
    TextView gender;
    private Toolbar mToolbar;
    ParseFile image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        profile = (ImageView) findViewById(R.id.profile);
        first_name = (TextView) findViewById(R.id.first_name);
        last_name = (TextView) findViewById(R.id.last_name);
        gender = (TextView) findViewById(R.id.gender);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
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
                    first_name.setText(object.getString(GetConnectedConstants.USER_FIRST_NAME));
                    last_name.setText(object.getString(GetConnectedConstants.USER_LAST_NAME));
                    gender.setText(object.getString(GetConnectedConstants.USER_GENDER));
                    image = object.getParseFile(GetConnectedConstants.USER_PICTURE);
                    image.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            profile.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
