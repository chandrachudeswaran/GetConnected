package com.example.chandra.getconnected;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.example.chandra.getconnected.utility.ParsingUtility;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import android.support.design.widget.CoordinatorLayout;
import android.widget.ImageView;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.twitter.Twitter;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

import com.twitter.sdk.android.core.TwitterAuthConfig;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private CoordinatorLayout coordinatorLayout;
    EditText email_edit;
    EditText password_edit;
    String username;
    String email;
    String first_name;
    String last_name;
    String gender;
    ArrayList<Integer> privacy_settings;
    TwitterLoginButton tweets;
    String id;
    ProgressDialog dialog;
    boolean user_listed = false;
    boolean receive_push = false;
    String url_facebook;
    public static final List<String> facebook_Permissions = new ArrayList<String>() {{
        add(GetConnectedConstants.FACEBOOK_PERMISSION_PUBLIC);
        add(GetConnectedConstants.FACEBOOK_PERMISSION_FRIENDS);
        add(GetConnectedConstants.FACEBOOK_PERMISSION_EMAIL);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityUtility.Helper.isConnected(MainActivity.this)) {
            try {
                Parse.enableLocalDatastore(this);
                Parse.initialize(this, GetConnectedConstants.PARSE_APPLICATION_ID, GetConnectedConstants.PARSE_CLIENT_KEY);
                ParseFacebookUtils.initialize(this);
                FacebookSdk.sdkInitialize(getApplicationContext());
                ParseTwitterUtils.initialize(GetConnectedConstants.TWITTER_CONSUMER_KEY, GetConnectedConstants.TWITTER_SECRET_KEY);
                TwitterAuthConfig authConfig = new TwitterAuthConfig(GetConnectedConstants.TWITTER_CONSUMER_KEY, GetConnectedConstants.TWITTER_SECRET_KEY);
                Fabric.with(this, new TwitterCore(authConfig));
                ParseInstallation.getCurrentInstallation().saveInBackground();

            } catch (Exception e) {
            }


            setContentView(R.layout.activity_main);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            email_edit = (EditText) findViewById(R.id.username);
            password_edit = (EditText) findViewById(R.id.password);
            tweets = (TwitterLoginButton) findViewById(R.id.submit_twittert);

            ParseUser user = ParseUser.getCurrentUser();
            if (user != null) {
                showHome();
            }

            tweets.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    ParseTwitterUtils.logIn(MainActivity.this, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user == null) {
                                ActivityUtility.Helper.writeErrorLog(e.toString());
                            } else if (user.isNew()) {
                                new TwitterLogin().execute(GetConnectedConstants.TWITTER_API_CALL_USER + ParseTwitterUtils.getTwitter().getUserId());
                            } else {
                                showHome();
                            }
                        }
                    });
                }

                @Override
                public void failure(TwitterException e) {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            });

        } else {
            ActivityUtility.Helper.showOfflineToastMessage(MainActivity.this);
        }
    }

    public void doFacebookLogin(View v) {
        if (ActivityUtility.Helper.isConnected(MainActivity.this)) {
            ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, facebook_Permissions, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (parseUser == null) {
                        ActivityUtility.Helper.writeErrorLog(e.toString());

                    } else if (parseUser.isNew()) {
                        getUserDetailsFromFB();
                    } else {
                        showHome();
                    }

                }
            });
        } else {
            ActivityUtility.Helper.showOfflineToastMessage(MainActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    if (ParseUser.getCurrentUser() != null) {
                        finish();
                    }
                }
                break;
            default:
                tweets.onActivityResult(requestCode, resultCode, data);
                ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    private void getUserDetailsFromFB() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                try {
                    ActivityUtility.Helper.writeErrorLog("json" + jsonObject.toString());
                    username = jsonObject.getString("email");
                    first_name = jsonObject.getString("first_name");
                    last_name = jsonObject.getString("last_name");
                    gender = jsonObject.getString("gender");
                    id = jsonObject.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    email = jsonObject.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                saveUserInParse();
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,email,link,gender");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    public void saveUserInParse() {

        getProfilePictureFromFacebook();

    }

    public void doSignup(View v) {
        Intent intent = new Intent(MainActivity.this, Signup.class);
        startActivity(intent);
        finish();
    }

    private class TwitterLogin extends AsyncTask<String, Void, ParseUser> {

        @Override
        protected ParseUser doInBackground(String... params) {
            HttpUriRequest request = new HttpGet(params[0]);
            Twitter twitter1 = ParseTwitterUtils.getTwitter();
            twitter1.signRequest(request);
            HttpClient client = new DefaultHttpClient();
            try {
                HttpResponse response = client.execute(request);
                BufferedReader input = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                String result = input.readLine();
                JSONObject JsonResponse = new JSONObject(result);
                ParseUser user = ParseUser.getCurrentUser();
                user.setUsername(JsonResponse.getString("name"));
                String[] array = JsonResponse.getString("name").split(" ");
                user.put(GetConnectedConstants.USER_FIRST_NAME, array[0]);
                if (array.length > 1) {
                    user.put(GetConnectedConstants.USER_LAST_NAME, array[1]);
                }else{
                    user.put(GetConnectedConstants.USER_LAST_NAME,"");
                }
                user.put(GetConnectedConstants.USER_IMAGE_FACEBOOK, JsonResponse.getString("profile_image_url"));


                return user;
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ParseUser user) {
            displayUserSettingsForTwitterLogin(user);
        }
    }

    public void doLogin(View v) {

        if (ActivityUtility.Helper.isConnected(MainActivity.this)) {
            if (email_edit.getText().length() == 0 || password_edit.getText().length() == 0) {
                ActivityUtility.Helper.makeToast(MainActivity.this, GetConnectedConstants.MANDATORY_FIELDS_MISSING);
                return;
            } else {
                ParseUser.logInInBackground(email_edit.getText().toString(), password_edit.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (e == null) {
                            showHome();
                        } else {
                            ActivityUtility.Helper.makeToast(MainActivity.this, "Login Failed");
                            ActivityUtility.Helper.writeErrorLog(e.toString());
                        }
                    }
                });
            }
        } else {
            ActivityUtility.Helper.showOfflineToastMessage(MainActivity.this);
        }
    }


    public void showHome() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.INSTALLATION_USERID, ParseUser.getCurrentUser().getObjectId());
        installation.saveInBackground();
        Intent intent = new Intent(MainActivity.this, Home.class);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public void getProfilePictureFromFacebook() {

        new TaskUtil().execute("https://graph.facebook.com/" + id + "/picture?width=250&height=250&redirect=false");

    }


    private class TaskUtil extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                BufferedReader reader = null;
                String line = "";
                StringBuilder sb = new StringBuilder();
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return ParsingUtility.FacebookProfilePicture.getProfilePicFromFacebook(new JSONObject(sb.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String url) {
            url_facebook = url;
            ParseUser user = ParseUser.getCurrentUser();
            user.setUsername(username);
            user.put(GetConnectedConstants.USER_FIRST_NAME, first_name);
            user.put(GetConnectedConstants.USER_LAST_NAME, last_name);
            user.put(GetConnectedConstants.USER_IMAGE_FACEBOOK, url_facebook);
            if (gender.equalsIgnoreCase("male")) {
                gender = GetConnectedConstants.MALE;
            } else {
                gender = GetConnectedConstants.FEMALE;
            }
            user.put(GetConnectedConstants.USER_GENDER, gender);
            user.setEmail(email);
            displayUserPreferenceSettings(user);


        }

    }

    public void displayUserSettingsForTwitterLogin(final ParseUser user) {
        privacy_settings = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("User Profile and Settings")
                .setCancelable(false)
                .setMultiChoiceItems(GetConnectedConstants.TWITTER_PRIVACY_SETTINGS, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            privacy_settings.add(which);
                        } else {
                            privacy_settings.remove(which);
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                populatePrivacySettingsForTwitter(user);
            }
        });
        builder.create().show();
    }

    public void displayUserPreferenceSettings(final ParseUser user) {

        privacy_settings = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("User Preference Settings")
                .setCancelable(false)
                .setMultiChoiceItems(GetConnectedConstants.PRIVACY_SETTINGS, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            privacy_settings.add(which);
                        } else {
                            privacy_settings.remove(which);
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                populatePrivacySettings(user);
            }
        });
        builder.create().show();
    }

    public void populatePrivacySettings(ParseUser user) {

        for (Integer i : privacy_settings) {

            if (i == 0) {
                user_listed = true;
            }
            if (i == 1) {
                receive_push = true;
            }
        }
        user.put(GetConnectedConstants.USER_LISTED, user_listed);
        user.put(GetConnectedConstants.USER_RECEIVE_PUSH, receive_push);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    sendNotificationToOtherUsers();

                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });

    }

    public void populatePrivacySettingsForTwitter(ParseUser user) {

        for (Integer i : privacy_settings) {
            if (i == 0) {
                gender = GetConnectedConstants.MALE;
            }
            if (i == 1) {
                user_listed = true;
            }
            if (i == 2) {
                receive_push = true;
            }
        }
        if (gender != null) {
            user.put(GetConnectedConstants.USER_GENDER, gender);
        } else {
            user.put(GetConnectedConstants.USER_GENDER, GetConnectedConstants.FEMALE);
        }
        user.put(GetConnectedConstants.USER_LISTED, user_listed);
        user.put(GetConnectedConstants.USER_RECEIVE_PUSH, receive_push);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    sendNotificationToOtherUsers();

                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });

    }

    public void sendNotificationToOtherUsers() {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo(ParseConstants.OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseUser user : objects) {
                        ActivityUtility.Helper.callPushNotification(user, ParseUser.getCurrentUser().getString(GetConnectedConstants.USER_FIRST_NAME) + " " + "has joined GetConnected", GetConnectedConstants.EVENT_MESSAGING);
                    }

                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });

        showHome();
    }


}
