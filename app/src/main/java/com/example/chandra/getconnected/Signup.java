package com.example.chandra.getconnected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class Signup extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    RadioGroup gender_group;
    ImageView profile_picture;
    private Toolbar mToolbar;
    EditText first_name;
    EditText last_name;
    EditText email;
    EditText password;
    EditText confirm_password;
    boolean condition = false;
    private CoordinatorLayout coordinatorLayout;
    Bitmap picture;
    ParseFile imageParseFile;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        intialize();

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (checkedId == R.id.male) {
            profile_picture.setImageResource(R.drawable.male_profile);
        } else {
            profile_picture.setImageResource(R.drawable.female_profile);
        }
    }


    public void doSave(View v) {

        if (!doValidation()) {
            saveInParseSignup();
        }

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
                    profile_picture.setImageBitmap(picture);
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

    public boolean doValidation() {

        if (first_name.getText().length() == 0 || last_name.getText().length() == 0 || email.getText().length() == 0 || password.getText().length() == 0 || confirm_password.getText().length() == 0) {
            ActivityUtility.Helper.makeToast(this, GetConnectedConstants.MANDATORY_FIELDS_MISSING);
            return true;
        } else if (!password.getText().toString().equals(confirm_password.getText().toString())) {
            ActivityUtility.Helper.makeToast(this, GetConnectedConstants.PASSWORDS_DONT_MATCH);
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
                    ActivityUtility.Helper.makeToast(Signup.this, GetConnectedConstants.EMAIL_EXIST);
                    condition = true;
                } else {
                    condition = false;
                }
            }
        });
        return condition;
    }

    public void saveInParseSignup() {

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] image = stream.toByteArray();
        imageParseFile = new ParseFile("thumbnail.png", image);
        new Upload().execute(imageParseFile);

    }



    private class DoSignup extends AsyncTask<ParseUser ,Void,Integer >{
        @Override
        protected Integer doInBackground(final ParseUser... params) {
            params[0].signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ActivityUtility.Helper.showNotificationLogin(coordinatorLayout, GetConnectedConstants.NEW_USER_MESSAGE + " " + params[0].getString(GetConnectedConstants.USER_FIRST_NAME));
                        Intent intent = new Intent(Signup.this, Home.class);
                        startActivity(intent);
                    } else {
                        ActivityUtility.Helper.writeErrorLog(e.toString());
                        ActivityUtility.Helper.makeToast(Signup.this, "Signup failed");
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


    private class Upload extends AsyncTask<ParseFile ,Void,Integer >{
        @Override
        protected Integer doInBackground(final ParseFile... params) {
            params[0].saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        final ParseUser user = new ParseUser();
                        user.setUsername(email.getText().toString());
                        user.setEmail(email.getText().toString());
                        user.setPassword(password.getText().toString());
                        user.put(GetConnectedConstants.USER_FIRST_NAME, first_name.getText().toString());
                        user.put(GetConnectedConstants.USER_LAST_NAME, last_name.getText().toString());
                        user.put(GetConnectedConstants.USER_PICTURE, imageParseFile);
                        new DoSignup().execute(user);
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
            dialog = new ProgressDialog(Signup.this);
            dialog.setMessage("Signing Up");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);

        }
    }

    public void intialize(){
        try {
            Parse.enableLocalDatastore(this);
            Parse.initialize(this, GetConnectedConstants.PARSE_APPLICATION_ID, GetConnectedConstants.PARSE_CLIENT_KEY);
            ParseUser.enableRevocableSessionInBackground();
        } catch (Exception e) {
            ActivityUtility.Helper.writeErrorLog(e.toString());
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirmpassword);

        profile_picture = (ImageView) findViewById(R.id.profile_pic);
        gender_group = (RadioGroup) findViewById(R.id.genderGroup);
        gender_group.setOnCheckedChangeListener(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
    }

}
