package com.example.chandra.getconnected.com.example.chandra.getconnected.albums;

import com.parse.ParseUser;

/**
 * Created by chandra on 11/25/2015.
 */
public class Album {

    String title;
    boolean isPublic;
    String userid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Album(){
        
    }
}
