package com.example.chandra.getconnected;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.PhotoUtility;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class PhotoView extends AppCompatActivity {

    TextView title;
    ImageView image;
    ParseFile imageParseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setStatusBarColor(Color.BLACK);
        }

        title = (TextView) findViewById(R.id.phototitle);
        image = (ImageView) findViewById(R.id.image);

        getPhoto(getIntent().getExtras().getString(ParseConstants.OBJECT_ID));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getPhoto(final String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.PHOTO_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, objectId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    title.setText(object.getString(ParseConstants.PHOTO_CAPTION));
                    imageParseFile = object.getParseFile(ParseConstants.PHOTO_FIELD_FILE);
                    imageParseFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            image.setImageBitmap(PhotoUtility.decodeSampledBitmap(data));

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
