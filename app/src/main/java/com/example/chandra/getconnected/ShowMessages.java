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

import com.example.chandra.getconnected.messages.ParseMessageQueryAdapter;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowMessages extends Fragment {

    ParseUser user;
    Context context;
    OnCreateMessages onCreateMessages;
    ListView listview;
    ParseMessageQueryAdapter adapter;

    public ShowMessages() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_messages, container, false);
        listview = (ListView) view.findViewById(R.id.messageslistview);
        adapter = new ParseMessageQueryAdapter(context);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //adapter = new ParseMessageQueryAdapter(context);
       // listview.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        //adapter.loadObjects();
       // adapter.setAutoload(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        user = ParseUser.getCurrentUser();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_messages, menu);
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
                    onCreateMessages.doFinish();

                } else {

                    ParseUser.logOut();
                    onCreateMessages.doFinish();
                }
            } else {
                ParseUser.logOut();
                onCreateMessages.doFinish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.context = activity;
            onCreateMessages = (OnCreateMessages) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    public interface OnCreateMessages {
        void doFinish();
    }


}
