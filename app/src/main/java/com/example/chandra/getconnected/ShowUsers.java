package com.example.chandra.getconnected;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowUsers extends Fragment {

    ParseUser user;
    Context context;
    OnCreateUsers onCreateUsers;

    public ShowUsers() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_users, container, false);
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
}