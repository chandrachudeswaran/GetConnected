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
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.messages.ParseMessageQueryAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity implements ShowGallery.OnCreateAlbum, ShowUsers.OnCreateUsers,
        ShowMessages.OnCreateMessages, ShowNotifications.OnCreateNotifications,
        ParseUserQueryAdapter.IParseUserQueryAdapter, ParseAlbumQueryAdapter.IParseAlbumQueryAdapter, ParseMessageQueryAdapter.IParseMessageQueryAdapter {
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
    ParseObject conversationObject;
    JSONObject messageHistory;
    JSONObject r;
    int item_retrieved;
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


    }

    public void createAlbum(MenuItem item) {
        doCreateAlbum();
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
        Intent intent = new Intent(Home.this, ShowAlbum.class);
        intent.putExtra(ParseConstants.ALBUM_TABLE, objectId);
        startActivityForResult(intent, 1000);
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

    public void getConversationHistory(){
        messageHistory =null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
        query.whereEqualTo(ParseConstants.MESSAGES_IDENTIFIER,ParseUser.getCurrentUser().getObjectId()+","+receiverObject.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    messageHistory=object.getJSONObject(ParseConstants.MESSAGES_MESSAGES);
                }else{
                    messageHistory=null;
                }
                Intent intent = new Intent(Home.this,Chatting.class);
                if(messageHistory!=null) {
                    intent.putExtra("CHAT", messageHistory.toString());
                    intent.putExtra(ParseConstants.OBJECT_ID,object.getObjectId());
                }else{
                    intent.putExtra("CHAT","empty");
                    intent.putExtra(ParseConstants.OBJECT_ID,"empty");
                    intent.putExtra("OTHER_PERSON_ID",userList.get(item_retrieved).getObjectId());
                }
                intent.putExtra("OTHER_PERSON",userList.get(item_retrieved).getFirstname());
                //TO DO Check for Result Activity









                startActivity(intent);
            }
        });
    }

    public void retrieveConversationHistory() {
        messageHistory =null;
        ParseQuery<ParseObject> sender_query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
        sender_query.whereEqualTo(ParseConstants.MESSAGES_SENDER, ParseUser.getCurrentUser());
        sender_query.whereEqualTo(ParseConstants.MESSAGES_RECEIVER, receiverObject);

        ParseQuery<ParseObject> receiver_query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
        receiver_query.whereEqualTo(ParseConstants.MESSAGES_RECEIVER, ParseUser.getCurrentUser());
        receiver_query.whereEqualTo(ParseConstants.MESSAGES_SENDER, receiverObject);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(sender_query);
        queries.add(receiver_query);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.include(ParseConstants.MESSAGES_RECEIVER);
        mainQuery.include(ParseConstants.MESSAGES_SENDER);
        mainQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    conversationObject = object;
                    messageHistory = conversationObject.getJSONObject(ParseConstants.MESSAGES_MESSAGES);
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }

                Intent intent = new Intent(Home.this, Chat.class);
                intent.putExtra("OTHER_PERSON", userList.get(item_retrieved).getFirstname());
                if(messageHistory!=null) {
                    intent.putExtra(ParseConstants.OBJECT_ID, conversationObject.getObjectId());
                    ActivityUtility.Helper.writeErrorLog("not "+   messageHistory.toString());
                    intent.putExtra("CHAT", messageHistory.toString());
                }else{
                    ActivityUtility.Helper.writeErrorLog("null ");
                    intent.putExtra("CHAT", "empty");
                    intent.putExtra("RECEIVERID",userList.get(item_retrieved).getObjectId());
                }
                startActivityForResult(intent, 2000);
            }
        });


    }

    public void updateConversationHistory(JSONObject messageHistory,String text,String senderid,String receiverid){


        try {
            r = messageHistory;
            ActivityUtility.Helper.writeErrorLog(r.toString());
            JSONArray array = r.getJSONArray("MessageRoot");

            JSONObject message = new JSONObject();
            message.put(GetConnectedConstants.JSON_SENDER_LABEL,senderid);
            message.put(GetConnectedConstants.JSON_RECEIVER_LABEL, receiverid);
            message.put(GetConnectedConstants.JSON_IMAGE, "");
            message.put(GetConnectedConstants.JSON_MESSAGE_CONTENT, text);

            ActivityUtility.Helper.writeErrorLog(message.toString());

            array.put(message);

            ActivityUtility.Helper.writeErrorLog(array.toString());
            r.put("MessageRoot", array);
            ActivityUtility.Helper.writeErrorLog(r.toString());

            ActivityUtility.Helper.writeErrorLog(r.toString());

            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
            query.whereEqualTo(ParseConstants.OBJECT_ID, conversationObject.getObjectId());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        object.put(ParseConstants.MESSAGES_MESSAGES, r);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ActivityUtility.Helper.makeToast(Home.this, "Message updated");
                                }
                            }
                        });
                    }
                }
            });



        } catch (JSONException e) {
            e.printStackTrace();
            ActivityUtility.Helper.writeErrorLog(e.toString());
        }

    }


}
