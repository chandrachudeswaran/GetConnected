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

import com.example.chandra.getconnected.constants.GetConnectedConstants;
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

public class Home extends AppCompatActivity implements ShowGallery.OnCreateAlbum,ShowUsers.OnCreateUsers,ShowMessages.OnCreateMessages,ShowNotifications.OnCreateNotifications{
    private Toolbar mToolbar;
    private CoordinatorLayout coordinatorLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ParseUser user;


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
        adapter.addFragment(new ShowGallery(), "Gallery");
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

    public void doCreateAlbum(){
        Intent intent = new Intent(Home.this,AlbumActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }
}
