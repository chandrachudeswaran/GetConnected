package com.example.chandra.getconnected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.example.chandra.getconnected.utility.PhotoUtility;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
//Creating and Updating Album
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
    ProgressDialog dialog;
    ParseFile imageParseFile;
    ParseImageView coverPic;
    Bitmap picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);


        album_title = (EditText) findViewById(R.id.album);
        privacy = (RadioGroup) findViewById(R.id.accessgroup);
        save_update_button = (Button) findViewById(R.id.save);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        public_button = (RadioButton) findViewById(R.id.publicalbum);
        private_button = (RadioButton) findViewById(R.id.privatealbum);
        coverPic=(ParseImageView)findViewById(R.id.cover);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Create Album");

        if (getIntent().getExtras() != null) {
            album_id = getIntent().getExtras().getString(ParseConstants.OBJECT_ID);
            save_update_button.setText("Edit Album");
            setTitle("Save Album");
            queryForAlbum();
        }

    }

    public void createAlbum(View view) {

        if (album_id == null) {
            if (doValidation()) {
                album = new ParseObject(ParseConstants.ALBUM_TABLE);
                title = album_title.getText().toString();
                album.put(ParseConstants.ALBUM_FIELD_TITLE, title);
                album.put(ParseConstants.ALBUM_FIELD_OWNER, ParseUser.getCurrentUser());
                album.put(ParseConstants.ALBUM_FIELD_OWNER_ID, ParseUser.getCurrentUser().getObjectId());
                if(picture==null) {
                    imageParseFile = (PhotoUtility.getParseFileFromBitmap(BitmapFactory.decodeResource
                            (AlbumActivity.this.getResources(),
                                    R.drawable.no_image)));
                }else{
                    imageParseFile=PhotoUtility.getParseFileFromBitmap(picture);
                }
                new Upload().execute(imageParseFile);
            }
        } else {

            if (doValidation()) {
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
                    album.getParseFile(ParseConstants.ALBUM_FIEELD_COVER).getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                         coverPic.setImageBitmap(PhotoUtility.decodeSampledBitmap(data));
                        }
                    });

                }
            }
        });
    }

    public void updateAlbum() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, album_id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    int checkedId = privacy.getCheckedRadioButtonId();
                    if (checkedId == R.id.privatealbum) {
                        isPublic = false;
                    } else {
                        isPublic = true;
                    }
                    object.put(ParseConstants.ALBUM_FIELD_ISPUBLIC, isPublic);
                    object.put(ParseConstants.ALBUM_FIELD_TITLE, album_title.getText().toString());

                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    });

                }
            }
        });
    }

    public void uploadCoverPic(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GetConnectedConstants.SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case GetConnectedConstants.SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    getRealPathFromURI(selectedImage);
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    picture = BitmapFactory.decodeStream(imageStream);
                    coverPic.setImageBitmap(picture);
                }
        }
    }

    private void getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
    }


    private class Upload extends AsyncTask<ParseFile, Void, Integer> {
        @Override
        protected Integer doInBackground(final ParseFile... params) {
            params[0].saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        int checkedId = privacy.getCheckedRadioButtonId();
                        if (checkedId == R.id.privatealbum) {
                            isPublic = false;
                        }
                        album.put(ParseConstants.ALBUM_FIELD_ISPUBLIC, isPublic);
                        album.put(ParseConstants.ALBUM_FIEELD_COVER, imageParseFile);
                        new DoUpload().execute(album);
                    } else {
                        ActivityUtility.Helper.writeErrorLog(e.toString());
                    }

                }
            });
            return 0;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(AlbumActivity.this);
            dialog.setMessage("Creating Album");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);

        }
    }


    private class DoUpload extends AsyncTask<ParseObject, Void, Integer> {
        @Override
        protected Integer doInBackground(final ParseObject... params) {
            params[0].saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        dialog.dismiss();
                        ActivityUtility.Helper.makeToast(AlbumActivity.this, album_title.getText().toString() + "  " + "album Created");
                        Intent intent = new Intent();
                        intent.putExtra(ParseConstants.ALBUM_TABLE, album.getObjectId());
                        ActivityUtility.Helper.writeErrorLog(album.getObjectId());
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        ActivityUtility.Helper.writeErrorLog(e.toString());

                    }

                }
            });
            return 0;
        }


        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);

        }
    }
}
