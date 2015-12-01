package com.example.chandra.getconnected;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.chandra.getconnected.albums.AlbumAdapter;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.users.User;
import com.example.chandra.getconnected.users.UserAdapter;
import com.example.chandra.getconnected.utility.PhotoUtility;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowUsers extends Fragment {

    ParseUser user;
    Context context;
    OnCreateUsers onCreateUsers;
    Bitmap image;
    ArrayList<User> usersList;
    ParseFile profile_pic_file;
    UserAdapter adapter;
    ListView listview;

    public ShowUsers() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        user = ParseUser.getCurrentUser();
        queryForListedUsers();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        getActivity().getMenuInflater().inflate(R.menu.menu_home, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout) {
            if (user.get("authData") != null) {
                if (user.get("authData").toString().contains("twitter")) {
                    ParseUser.logOut();
                }
                if (user.get("authData").toString().contains("facebook")) {
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    if (accessToken != null) {
                        LoginManager.getInstance().logOut();
                    }
                    ParseUser.logOut();
                    onCreateUsers.doFinish();

                } else {

                    ParseUser.logOut();
                    onCreateUsers.doFinish();
                }
            } else {
                ParseUser.logOut();
                onCreateUsers.doFinish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_users, container, false);
        listview = (ListView) view.findViewById(R.id.userlistview);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.context = activity;
            onCreateUsers = (OnCreateUsers) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    public interface OnCreateUsers {
        public void doFinish();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new UserAdapter(context, R.layout.user_listrow, usersList);
        listview.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
    }


    public void queryForListedUsers() {
        usersList = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(GetConnectedConstants.USER_LISTED, true);
        query.whereNotEqualTo(ParseConstants.OBJECT_ID, user.getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseUser users : objects) {
                        final User user = new User();
                        user.setObjectId(users.getObjectId());
                        user.setFirstname(users.getString(GetConnectedConstants.USER_FIRST_NAME));
                        user.setLastname(users.getString(GetConnectedConstants.USER_LAST_NAME));
                        if(users.getString(GetConnectedConstants.USER_IMAGE_FACEBOOK) != null){
                            user.setProfile_pic_facebook(users.getString(GetConnectedConstants.USER_IMAGE_FACEBOOK));
                            usersList.add(user);
                            adapter = new UserAdapter(context, R.layout.user_listrow, usersList);
                            listview.setAdapter(adapter);
                            adapter.setNotifyOnChange(true);
                        }

                        else {
                            profile_pic_file = users.getParseFile(GetConnectedConstants.USER_PICTURE);


                            if (profile_pic_file != null) {
                                profile_pic_file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        user.setProfile_pic(PhotoUtility.decodeSampledBitmap(data));
                                        usersList.add(user);
                                        adapter = new UserAdapter(context, R.layout.user_listrow, usersList);
                                        listview.setAdapter(adapter);
                                        adapter.setNotifyOnChange(true);
                                    }
                                });
                            } else {
                                user.setProfile_pic(BitmapFactory.decodeResource(context.getResources(),
                                        R.drawable.no_image));
                                usersList.add(user);
                                adapter = new UserAdapter(context, R.layout.user_listrow, usersList);
                                listview.setAdapter(adapter);
                                adapter.setNotifyOnChange(true);
                            }
                        }
                    }
                }
            }
        });
    }
}
