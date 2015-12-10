package com.example.chandra.getconnected;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.users.ParseUserQueryAdapter;
import com.example.chandra.getconnected.users.User;
import com.example.chandra.getconnected.utility.SharedPreferenceHelper;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.ArrayList;


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

    ListView listview;
    SharedPreferenceHelper sharedPreferenceHelper;
    boolean created = false;
    ParseUserQueryAdapter adapter;

    public ShowUsers() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        sharedPreferenceHelper = new SharedPreferenceHelper();
        user = ParseUser.getCurrentUser();
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
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put(ParseConstants.INSTALLATION_USERID, "");
                installation.saveInBackground();
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
        adapter = new ParseUserQueryAdapter(context);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
    }



}
