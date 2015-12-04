package com.example.chandra.getconnected;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.GridView;
import android.widget.TextView;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.users.ParseUserAlbumQueryAdapter;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.example.chandra.getconnected.utility.SharedPreferenceHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ProfileView extends AppCompatActivity {
    ParseImageView profile;
    TextView first_name;
    TextView last_name;
    TextView gender;
    private Toolbar mToolbar;
    GridView gridView;
    ParseUserAlbumQueryAdapter adapter;
    SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        profile = (ParseImageView) findViewById(R.id.profile);
        first_name = (TextView) findViewById(R.id.first_name);
        last_name = (TextView) findViewById(R.id.last_name);
        gender = (TextView) findViewById(R.id.gender);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        gridView = (GridView) findViewById(R.id.gridview);
        sharedPreferenceHelper = new SharedPreferenceHelper();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Profile View");
        queryForProfile(getIntent().getExtras().getString(ParseConstants.OBJECT_ID));
    }



    public void queryForProfile(final String objectId) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseConstants.OBJECT_ID, objectId);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    ParseFile imageFile = object.getParseFile(GetConnectedConstants.USER_PICTURE);
                    if (imageFile != null) {
                        profile.setParseFile(imageFile);
                        profile.loadInBackground();
                    }
                    first_name.setText(object.getString(GetConnectedConstants.USER_FIRST_NAME));
                    last_name.setText(object.getString(GetConnectedConstants.USER_LAST_NAME));
                    gender.setText(object.getString(GetConnectedConstants.USER_GENDER));
                    queryPublicAlbums(objectId);
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }

    public void queryPublicAlbums(String objectId) {
        adapter = new ParseUserAlbumQueryAdapter(ProfileView.this, objectId);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    @Override
    public void onBackPressed() {
        finish();
    }
}
