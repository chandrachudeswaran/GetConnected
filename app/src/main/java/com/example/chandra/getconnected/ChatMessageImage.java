package com.example.chandra.getconnected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.example.chandra.getconnected.utility.PhotoUtility;
import com.example.chandra.getconnected.utility.SharedPreferenceHelper;
import com.example.chandra.getconnected.utility.SharedPreferenceUtility;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ChatMessageImage extends AppCompatActivity {

    ParseImageView image;
    Bitmap picture;
    ProgressDialog dialog;
    ParseObject messageObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message_image);
        image = (ParseImageView) findViewById(R.id.image);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        opengallery();
    }

    public void doCancel(View v) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void onSend(View v) {
        doSave();
    }

    public void doSave() {
        if (picture != null) {
            dialog = new ProgressDialog(ChatMessageImage.this);
            dialog.setMessage("Sending Image");
            dialog.setCancelable(false);
            dialog.show();


            final ParseObject photoMessage = new ParseObject(ParseConstants.MESSAGES_PHOTO_TABLE);
            final ParseFile image = PhotoUtility.getParseFileFromBitmap(picture);
            image.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        photoMessage.put(ParseConstants.MESSAGES_PHOTO_PHOTO, image);
                        photoMessage.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    String imageString = PhotoUtility.convertImageToString(picture);
                                    SharedPreferenceHelper helper = new SharedPreferenceHelper();
                                    helper.saveInSharedPreference(ChatMessageImage.this, photoMessage.getObjectId(), imageString);
                                    dialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra(ParseConstants.OBJECT_ID, photoMessage.getObjectId());
                                    setResult(RESULT_OK, intent);
                                    finish();

                                } else {
                                    ActivityUtility.Helper.writeErrorLog(e.toString());
                                }
                            }
                        });
                    }
                }
            });


        }


    }


    public void opengallery() {
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
                    image.setImageBitmap(picture);
                } else {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
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


}
