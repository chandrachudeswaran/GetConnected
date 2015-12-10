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
import android.widget.ListView;
import android.widget.TextView;

import com.example.chandra.getconnected.albums.Album;
import com.example.chandra.getconnected.albums.ParseAlbumQueryAdapter;
import com.example.chandra.getconnected.albums.ParseInvitedAlbumQueryAdapter;
import com.example.chandra.getconnected.albums.ParsePublicAlbumQueryAdapter;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowGallery extends Fragment {

    OnCreateAlbum onCreateAlbum;
    Context context;
    ListView listView;
    ParseUser user;
    TextView owner;

    public ShowGallery() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        user = ParseUser.getCurrentUser();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_home_add_gallery, menu);
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
                    onCreateAlbum.doFinish();

                } else {

                    ParseUser.logOut();
                    onCreateAlbum.doFinish();
                }
            } else {
                ParseUser.logOut();
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put(ParseConstants.INSTALLATION_USERID, "");
                installation.saveInBackground();
                onCreateAlbum.doFinish();
            }
        }
        if (id == R.id.showpublic) {
            queryAllPublicAlbum();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_gallery, container, false);
        listView = (ListView) view.findViewById(R.id.gallerylistview);
        owner = (TextView) view.findViewById(R.id.owner);
        queryAllAlbum(false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.context = activity;
            onCreateAlbum = (OnCreateAlbum) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }


    public interface OnCreateAlbum {
        void doFinish();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void queryAllAlbum(boolean condition) {
        ParseAlbumQueryAdapter adapter = new ParseAlbumQueryAdapter(context);
        owner.setText(GetConnectedConstants.OWNED_ALBUM_LABEL);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (condition) {
            adapter.loadObjects();
        }
    }


    public void queryInvitedAlbum() {
        ParseInvitedAlbumQueryAdapter parseInvitedAlbumQueryAdapter = new ParseInvitedAlbumQueryAdapter(context);
        owner.setText(GetConnectedConstants.INVITED_ALBUM_LABEL);
        listView.setAdapter(parseInvitedAlbumQueryAdapter);
        parseInvitedAlbumQueryAdapter.notifyDataSetChanged();
    }


    public void queryAllPublicAlbum() {
        ParsePublicAlbumQueryAdapter parsePublicAlbumQueryAdapter = new ParsePublicAlbumQueryAdapter(context);
        owner.setText(GetConnectedConstants.PUBLIC_ALBUMS);
        listView.setAdapter(parsePublicAlbumQueryAdapter);
        parsePublicAlbumQueryAdapter.notifyDataSetChanged();
    }
}
