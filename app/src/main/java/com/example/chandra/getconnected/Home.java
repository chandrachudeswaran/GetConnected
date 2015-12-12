package com.example.chandra.getconnected;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.chandra.getconnected.albums.ParseAlbumQueryAdapter;
import com.example.chandra.getconnected.albums.ParseInvitedAlbumQueryAdapter;
import com.example.chandra.getconnected.albums.ParsePublicAlbumQueryAdapter;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.messages.ParseMessageQueryAdapter;
import com.example.chandra.getconnected.notifications.ParseNotificationQueryAdapter;
import com.example.chandra.getconnected.users.ParseUserQueryAdapter;
import com.example.chandra.getconnected.users.User;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity implements ShowGallery.OnCreateAlbum, ShowUsers.OnCreateUsers,
        ShowMessages.OnCreateMessages, ShowNotifications.OnCreateNotifications,
        ParseUserQueryAdapter.IParseUserQueryAdapter, ParseAlbumQueryAdapter.IParseAlbumQueryAdapter,
        ParseMessageQueryAdapter.IParseMessageQueryAdapter,
        ParseInvitedAlbumQueryAdapter.IParseInvitedAlbumQueryAdapter,
        ParseNotificationQueryAdapter.IParseNotificationQueryAdapter, ParsePublicAlbumQueryAdapter.IParsePublicAlbum {

    private Toolbar mToolbar;
    private CoordinatorLayout coordinatorLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ParseUser user;
    ShowGallery gallery;
    ShowMessages messages;
    ShowUsers usersShow;
    ShowNotifications notifications;
    ArrayList<User> userList;
    CharSequence[] users;
    ParseObject receiverObject;
    JSONObject messageHistory;
    JSONObject r;
    int item_retrieved;
    //True to display owned albums
    boolean toggleAlbums = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        gallery = new ShowGallery();
        messages = new ShowMessages();
        usersShow = new ShowUsers();
        notifications = new ShowNotifications();
        user = ParseUser.getCurrentUser();

        if (user.getString(GetConnectedConstants.USER_FIRST_NAME) == null) {
            ActivityUtility.Helper.showNotificationLogin(coordinatorLayout, GetConnectedConstants.NEW_USER_MESSAGE + "  " + user.getUsername());
        } else {
            ActivityUtility.Helper.showNotificationLogin(coordinatorLayout, GetConnectedConstants.NEW_USER_MESSAGE + "  " + user.getString(GetConnectedConstants.USER_FIRST_NAME));
        }


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        if (getIntent().getExtras() != null) {

            if(getIntent().getExtras().getString(GetConnectedConstants.ALBUM_SHARE)!=null) {
                viewPager.setCurrentItem(2);
            }
            if(getIntent().getExtras().getString(GetConnectedConstants.PHOTO_ADDED)!=null){
                viewPager.setCurrentItem(2);
            }
            if(getIntent().getExtras().getString(GetConnectedConstants.MESSAGING)!=null){
                viewPager.setCurrentItem(1);
            }
        }
    }

    public void createAlbum(MenuItem item) {
        doCreateAlbum();
    }

    public void toggleAlbums(MenuItem item) {
        toggleAlbums = !toggleAlbums;
        doToggleAlbums();
    }

    public void composeNewMessage(MenuItem item) {
        doComposeMessageForUser();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(usersShow, "Users");
        adapter.addFragment(messages, "Messages");
        adapter.addFragment(notifications, "Notifications");
        adapter.addFragment(gallery, "Gallery");
        viewPager.setAdapter(adapter);

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.users);
        tabLayout.getTabAt(1).setIcon(R.drawable.messages);
        tabLayout.getTabAt(2).setIcon(R.drawable.notifications);
        tabLayout.getTabAt(3).setIcon(R.drawable.gallery);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }


    @Override
    public void doFinish() {
        finish();
    }

    public void doCreateAlbum() {
        Intent intent = new Intent(Home.this, AlbumActivity.class);
        startActivityForResult(intent, 100);
    }

    public void doComposeMessageForUser() {
        queryUsers();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    String id = data.getExtras().getString(ParseConstants.ALBUM_TABLE);
                    Intent intent = new Intent(Home.this, CreatePhotoWithAlbum.class);
                    intent.putExtra(ParseConstants.ALBUM_TABLE, id);
                    startActivityForResult(intent, 500);

                }
                break;
            case 200:
                if (resultCode == RESULT_OK) {
                    gallery.queryAllAlbum(true);
                }
                break;
            case 500:
                if (resultCode == RESULT_OK) {
                    gallery.queryAllAlbum(true);
                }
            case 1000:
                if (resultCode == RESULT_OK) {
                    gallery.queryAllAlbum(true);
                }
                break;

            case 2000:
                if (resultCode == RESULT_OK) {
                    //gallery.queryAllAlbum(true);
                }
                break;

            case 3000:
                if (resultCode == RESULT_OK) {
                    notifications.queryNotifications();
                }
                break;

            case 4000:
                if (resultCode == RESULT_OK) {
                    messages.queryConversations();
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void callProfileViewForUser(String objectId) {
        Intent intent = new Intent(Home.this, ProfileView.class);
        intent.putExtra(ParseConstants.OBJECT_ID, objectId);
        startActivity(intent);
    }

    @Override
    public void addPhotos(String objectId) {
        addPhotosToAllAlbum(objectId, true);
    }

    @Override
    public void updateAlbumView() {
        gallery.queryAllAlbum(true);
    }

    @Override
    public void callEditAlbum(String objectId) {
        Intent intent = new Intent(Home.this, AlbumActivity.class);
        intent.putExtra(ParseConstants.OBJECT_ID, objectId);
        startActivityForResult(intent, 200);
    }

    @Override
    public void donotificationSharingStatus() {
        ActivityUtility.Helper.showNotificationLogin(coordinatorLayout, "This Album is not shared with anybody");
    }

    @Override
    public void doEmptyNotoficationStatus() {
        ActivityUtility.Helper.showNotificationLogin(coordinatorLayout, "Album is shared with all the public users");
    }

    @Override
    public void addPhotosToInvitedAlbum(String objectId) {
        addPhotosToAllAlbum(objectId, false);
    }

    @Override
    public void doApprovePhotosForTheAlbum(String photoId, String albumId) {
        Intent intent = new Intent(Home.this, ShowAlbum.class);
        intent.putExtra(ParseConstants.ALBUM_TABLE, albumId);
        intent.putExtra(ParseConstants.NOTIFICATIONS_PHOTOS, photoId);
        intent.putExtra(GetConnectedConstants.REMOVE_PHOTOS_OPTION, true);
        intent.putExtra("Approve", true);
        startActivityForResult(intent, 3000);

    }

    @Override
    public void deleteNotification(String notificationId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.NOTIFICATIONS_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, notificationId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    try {
                        object.delete();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            notifications.queryNotifications();
                        }
                    });
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }

    @Override
    public void showMessages(JSONObject message, String other_person, String objectId) {
        Intent intent = new Intent(Home.this, Chatting.class);
        //History message
        intent.putExtra("CHAT", message.toString());
        //For displaying name in screen
        intent.putExtra("OTHER_PERSON", other_person);
        //Message row object id
        intent.putExtra(ParseConstants.OBJECT_ID, objectId);
        startActivity(intent);
    }

    @Override
    public void deleteEntireThread() {

    }

    @Override
    public void showInvitedAlbums() {
        //After sharing show the invited album. Replace the notification fragment with Gallery Fragment
        viewPager.setCurrentItem(3);
        gallery.queryInvitedAlbum();
    }

    @Override
    public void showAllPhotos(String objectId) {
        Intent intent = new Intent(Home.this, ShowAlbum.class);
        intent.putExtra(ParseConstants.ALBUM_TABLE, objectId);
        intent.putExtra(GetConnectedConstants.REMOVE_PHOTOS_OPTION, true);
        startActivity(intent);
    }

    public void addPhotosToAllAlbum(String objectId, boolean addingByOwner) {
        Intent intent = new Intent(Home.this, ShowAlbum.class);
        intent.putExtra(ParseConstants.ALBUM_TABLE, objectId);
        intent.putExtra(GetConnectedConstants.PHOTOS_ADDING_BY_OWNER, addingByOwner);
        startActivityForResult(intent, 1000);
    }

    public void queryUsers() {
        userList = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo(ParseConstants.OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo(GetConnectedConstants.USER_LISTED, true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseUser users : objects) {
                        User user = new User();
                        user.setFirstname(users.getString(GetConnectedConstants.USER_FIRST_NAME));
                        user.setLastname(users.getString(GetConnectedConstants.USER_LAST_NAME));
                        user.setObjectId(users.getObjectId());
                        userList.add(user);

                    }
                    displayDialog();
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }

    public void displayDialog() {
        users = new CharSequence[userList.size()];
        for (int i = 0; i < userList.size(); i++) {
            users[i] = userList.get(i).toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select user to message").
                setCancelable(true).
                setItems(users, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        item_retrieved = item;
                        //Get receiver id for sending message and query for ParseUser object
                        queryUserObject(userList.get(item).getObjectId());


                    }
                });
        builder.create().show();
    }


    public void queryUserObject(String id) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseConstants.OBJECT_ID, id);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    receiverObject = object;
                    //Pull the conversation history to whom to chat if available
                    getConversationHistory();
                }
            }
        });
    }

    public void getConversationHistory() {
        messageHistory = null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
        query.whereEqualTo(ParseConstants.MESSAGES_IDENTIFIER, ParseUser.getCurrentUser().getObjectId() + "," + receiverObject.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    messageHistory = object.getJSONObject(ParseConstants.MESSAGES_MESSAGES);
                } else {
                    messageHistory = null;
                }
                Intent intent = new Intent(Home.this, Chatting.class);
                if (messageHistory != null) {
                    intent.putExtra("CHAT", messageHistory.toString());
                    intent.putExtra(ParseConstants.OBJECT_ID, object.getObjectId());
                } else {
                    intent.putExtra("CHAT", "empty");
                    intent.putExtra(ParseConstants.OBJECT_ID, "empty");
                    intent.putExtra("OTHER_PERSON_ID", userList.get(item_retrieved).getObjectId());
                }
                intent.putExtra("OTHER_PERSON", userList.get(item_retrieved).getFirstname());
                //TO DO Check for Result Activity
                startActivityForResult(intent, 4000);
            }
        });
    }

    public void doToggleAlbums() {
        if (toggleAlbums) {
            gallery.queryAllAlbum(true);
        } else {
            gallery.queryInvitedAlbum();
        }
    }


}
