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

import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.notifications.ParseNotificationQueryAdapter;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.parse.ParseInstallation;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowNotifications extends Fragment {
    ParseUser user;
    Context context;
    OnCreateNotifications onCreateNotifications;
    ListView listView;
    ParseNotificationQueryAdapter adapter;

    public ShowNotifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_notifications, container, false);
        listView = (ListView) view.findViewById(R.id.notificationslistview);
        queryNotifications();
        return view;
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

    public void queryNotifications(){
        adapter= new ParseNotificationQueryAdapter(context);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
                    onCreateNotifications.doFinish();

                } else {

                    ParseUser.logOut();
                    onCreateNotifications.doFinish();
                }
            } else {
                ParseUser.logOut();
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put(ParseConstants.INSTALLATION_USERID, "");
                installation.saveInBackground();
                onCreateNotifications.doFinish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.context = activity;
            onCreateNotifications = (OnCreateNotifications) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    public interface OnCreateNotifications {
        public void doFinish();
    }

}
