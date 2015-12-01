package com.example.chandra.getconnected;

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

import com.example.chandra.getconnected.albums.AlbumAdapter;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.users.UserAdapter;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.facebook.FacebookSdk;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class Home extends AppCompatActivity implements ShowGallery.OnCreateAlbum, ShowUsers.OnCreateUsers,
        ShowMessages.OnCreateMessages, ShowNotifications.OnCreateNotifications, AlbumAdapter.IAlbumAdapter, UserAdapter.IUserAdapter {
    private Toolbar mToolbar;
    private CoordinatorLayout coordinatorLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ParseUser user;
    ShowGallery gallery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        gallery = new ShowGallery();
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


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ShowUsers(), "Users");
        adapter.addFragment(new ShowMessages(), "Messages");
        adapter.addFragment(new ShowNotifications(), "Notifications");
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
                    gallery.queryAllAlbumForUser();
                }
                break;
            case 500:
                if (resultCode == RESULT_OK) {
                    gallery.queryAllAlbumForUser();
                }
            case 1000:
                if (resultCode == RESULT_OK) {
                    gallery.queryAllAlbumForUser();
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
    public void callEditAlbumIntent(String ObjectId) {
        Intent intent = new Intent(Home.this, AlbumActivity.class);
        intent.putExtra(ParseConstants.OBJECT_ID, ObjectId);
        startActivityForResult(intent, 200);
    }


    @Override
    public void callProfileView(String objectId) {
        Intent intent = new Intent(Home.this, ProfileView.class);
        intent.putExtra(ParseConstants.OBJECT_ID, objectId);
        startActivity(intent);
    }

    @Override
    public void callAddPhotosToAlbum(String objectId) {
        Intent intent = new Intent(Home.this, ShowAlbum.class);
        intent.putExtra(ParseConstants.ALBUM_TABLE, objectId);
        startActivityForResult(intent, 1000);
    }

    @Override
    public void callNotificationSharingStatus() {
        ActivityUtility.Helper.showNotificationLogin(coordinatorLayout, "This Album is not shared with anybody");
    }

    @Override
    public void callEmptyNotificationSharingAlbum() {
        ActivityUtility.Helper.showNotificationLogin(coordinatorLayout,"Album is shared with all the public users");
    }
}
