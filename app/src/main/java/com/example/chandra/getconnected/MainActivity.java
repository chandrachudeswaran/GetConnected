package com.example.chandra.getconnected;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import android.support.design.widget.CoordinatorLayout;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.twitter.Twitter;
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
import java.io.InputStreamReader;
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
    TwitterLoginButton tweets;
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
                                new TwitterLogin().execute(GetConnectedConstants.TWITTER_API_CALL_USER + ParseTwitterUtils.getTwitter().getUserId());
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
        }
        else{
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
                    username = jsonObject.getString("name");
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
        parameters.putString("fields", "name,email");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    public void saveUserInParse() {
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(username);
        String[] names = username.split(" ");
        user.put(GetConnectedConstants.USER_FIRST_NAME, names[0]);
        user.put(GetConnectedConstants.USER_LAST_NAME, names[1]);
        user.setEmail(email);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    showHome();
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }

    public void doSignup(View v) {
        Intent intent = new Intent(MainActivity.this, Signup.class);
        startActivity(intent);
        finish();
    }

    private class TwitterLogin extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
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
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            showHome();
                        } else {
                            ActivityUtility.Helper.writeErrorLog(e.toString());
                        }
                    }
                });
                return result;
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void doLogin(View v) {

        if(ActivityUtility.Helper.isConnected(MainActivity.this)) {
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
        }else{
            ActivityUtility.Helper.showOfflineToastMessage(MainActivity.this);
        }
    }


    public void showHome() {
        Intent intent = new Intent(MainActivity.this, Home.class);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
