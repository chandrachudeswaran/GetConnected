package com.example.chandra.getconnected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddPhotos extends AppCompatActivity {

    EditText title;
    ImageView imageView;
    Bitmap picture;
    ParseObject album;
    ParseFile imageParseFile;
    ProgressDialog dialog;
    String album_id;
    boolean addingByOwner;
    ParseObject photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);

        title = (EditText) findViewById(R.id.title);
        imageView = (ImageView) findViewById(R.id.photo);
        if (getIntent().getExtras() != null) {
            album_id = getIntent().getExtras().getString(ParseConstants.ALBUM_TABLE);
            addingByOwner = getIntent().getExtras().getBoolean(GetConnectedConstants.PHOTOS_ADDING_BY_OWNER);
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo("objectId", album_id);
        query.include(ParseConstants.ALBUM_FIELD_OWNER);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    album = object;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_photos, menu);
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

    public void uploadImage(View v) {
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
                    imageView.setImageBitmap(picture);
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

    public void onSubmit(View view) {

        if (title.getText().length() == 0) {
            ActivityUtility.Helper.makeToast(AddPhotos.this, GetConnectedConstants.TITLE_REQUIRED);
        } else if (picture == null) {
            ActivityUtility.Helper.makeToast(AddPhotos.this, "Please choose a photo to add");
        } else {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] image = stream.toByteArray();
            imageParseFile = new ParseFile("thumbnail.png", image);
            new Upload().execute(imageParseFile);
        }
    }

    private class Upload extends AsyncTask<ParseFile, Void, Integer> {
        @Override
        protected Integer doInBackground(final ParseFile... params) {
            params[0].saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        photo = new ParseObject(ParseConstants.PHOTO_TABLE);
                        photo.put(ParseConstants.PHOTO_ALBUM, album);
                        photo.put(ParseConstants.PHOTO_CAPTION, title.getText().toString());
                        photo.put(ParseConstants.PHOTO_FIELD_FILE, imageParseFile);
                        if (addingByOwner) {
                            photo.put(ParseConstants.PHOTO_MODERATED_BY_OWNER, true);
                        } else {
                            photo.put(ParseConstants.PHOTO_MODERATED_BY_OWNER, false);
                        }
                        new DoUpload().execute(photo);
                    }
                    if (e != null) {
                        ActivityUtility.Helper.writeErrorLog(e.toString());
                    }

                }
            });
            return 0;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(AddPhotos.this);
            dialog.setMessage("Uploading Picture");
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
                        if (addingByOwner) {
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            ParseObject notifications = new ParseObject(ParseConstants.NOTIFICATIONS_TABLE);
                            notifications.put(ParseConstants.NOTIFICATIONS_FROMUSER, ParseUser.getCurrentUser());
                            notifications.put(ParseConstants.NOTIFICATIONS_ALBUM, album);
                            notifications.put(ParseConstants.NOTIFICATIONS_TOUSER,album.getParseUser(ParseConstants.ALBUM_FIELD_OWNER));
                            notifications.put(ParseConstants.NOTIFICATIONS_PHOTOS,photo);
                            notifications.put(ParseConstants.NOTIFICATIONS_MESSAGE,
                                            ParseUser.getCurrentUser().getString(GetConnectedConstants.USER_FIRST_NAME)+ " added photo to the album "+ album.getString(ParseConstants.ALBUM_FIELD_TITLE));
                            notifications.saveInBackground();
                            Intent intent = new Intent();
                            intent.putExtra("AddedbyInvitee", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
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

    @Override
    public void onBackPressed() {
        finish();
    }

}
