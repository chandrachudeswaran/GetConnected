package com.example.chandra.getconnected;

import android.app.Notification;
import android.app.ProgressDialog;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class EditProfile extends AppCompatActivity {

    EditText first_name;
    EditText email;
    EditText last_name;
    RadioButton male;
    RadioButton female;
    Switch list_user_switch;
    Switch receive_push_notification;
    ParseUser user;
    ParseImageView profile;
    private Toolbar mToolbar;
    boolean image_changed = false;
    Bitmap picture;
    boolean condition = false;
    ParseFile imageParseFile;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Edit Profile");
        first_name = (EditText) findViewById(R.id.first_name);
        email = (EditText) findViewById(R.id.email);
        last_name = (EditText) findViewById(R.id.last_name);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        list_user_switch = (Switch) findViewById(R.id.list_user_switch);
        receive_push_notification = (Switch) findViewById(R.id.receive_push_notification);
        profile = (ParseImageView) findViewById(R.id.profile);
        user = ParseUser.getCurrentUser();

        if (user.getString(GetConnectedConstants.USER_IMAGE_FACEBOOK) != null) {
            Picasso.with(EditProfile.this).load(user.getString(GetConnectedConstants.USER_IMAGE_FACEBOOK)).into(profile);
        } else {
            ParseFile imageFile = user.getParseFile(GetConnectedConstants.USER_PICTURE);
            if (imageFile != null) {
                profile.setParseFile(imageFile);
                profile.loadInBackground();
            }
        }

        first_name.setText(user.getString(GetConnectedConstants.USER_FIRST_NAME));
        last_name.setText(user.getString(GetConnectedConstants.USER_LAST_NAME));
        email.setText(user.getEmail());
        if (user.getString(GetConnectedConstants.USER_GENDER).equals(GetConnectedConstants.MALE)) {
            male.setChecked(true);
        } else {
            female.setChecked(true);
        }

        if (user.getBoolean(GetConnectedConstants.USER_LISTED)) {
            list_user_switch.setChecked(true);
            list_user_switch.setText("Listed User");
        } else {
            list_user_switch.setChecked(false);
            list_user_switch.setText("Not Listed User");
        }

        if (user.getBoolean(GetConnectedConstants.USER_RECEIVE_PUSH)) {
            receive_push_notification.setChecked(true);
            receive_push_notification.setText("Can receive Push Messages");
        } else {
            receive_push_notification.setChecked(false);
            receive_push_notification.setText("Cannot receive Push Messages");
        }


        list_user_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    list_user_switch.setChecked(true);
                    list_user_switch.setText("Listed User");
                } else {
                    list_user_switch.setChecked(false);
                    list_user_switch.setText("Not Listed User");
                }
            }
        });


        receive_push_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    receive_push_notification.setChecked(true);
                    receive_push_notification.setText("Can receive Push Messages");
                } else {
                    receive_push_notification.setChecked(false);
                    receive_push_notification.setText("Cannot receive Push Messages");
                }
            }
        });

    }


    public void changeImage(View v) {
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
                    profile.setImageBitmap(picture);
                    image_changed = true;
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

    public void doEditProfile(View v) {
        if (!doValidation()) {
            editProfileInParse();
        }
    }


    public boolean doValidation() {

        if (first_name.getText().length() == 0 || last_name.getText().length() == 0 || email.getText().length() == 0) {
            ActivityUtility.Helper.makeToast(this, GetConnectedConstants.MANDATORY_FIELDS_MISSING);
            return true;
        } else {
            return checkEmailExists();
        }
    }

    public boolean checkEmailExists() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(GetConnectedConstants.USER_EMAIL, email);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    ActivityUtility.Helper.makeToast(EditProfile.this, GetConnectedConstants.EMAIL_EXIST);
                    condition = true;
                } else {
                    condition = false;
                }
            }
        });
        return condition;
    }

    public void editProfileInParse() {
        displayProgress();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseConstants.OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser object, ParseException e) {
                if (e == null) {
                    object.put(GetConnectedConstants.USER_FIRST_NAME, first_name.getText().toString());
                    object.put(GetConnectedConstants.USER_LAST_NAME, last_name.getText().toString());
                    object.put(GetConnectedConstants.USER_EMAIL, email.getText().toString());
                    if (list_user_switch.isChecked()) {
                        object.put(GetConnectedConstants.USER_LISTED, true);
                    } else {
                        object.put(GetConnectedConstants.USER_LISTED, false);
                    }

                    if (receive_push_notification.isChecked()) {
                        object.put(GetConnectedConstants.USER_RECEIVE_PUSH, true);
                    } else {
                        object.put(GetConnectedConstants.USER_RECEIVE_PUSH, false);
                    }
                    if (image_changed) {
                        object.put(GetConnectedConstants.USER_IMAGE_FACEBOOK, JSONObject.NULL);
                        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.PNG, 0, stream);
                        byte[] image = stream.toByteArray();
                        imageParseFile = new ParseFile("thumbnail.png", image);
                        imageParseFile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    object.put(GetConnectedConstants.USER_PICTURE, imageParseFile);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                dialog.dismiss();
                                                finish();
                                            } else {
                                                ActivityUtility.Helper.writeErrorLog(e.toString());
                                            }
                                        }
                                    });
                                } else {
                                    ActivityUtility.Helper.writeErrorLog(e.toString());
                                }
                            }
                        });
                    }else{
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    dialog.dismiss();
                                    finish();
                                }
                            }
                        });
                    }
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }


    public void displayProgress() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Editing Profile");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
