package com.example.chandra.getconnected.users;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chandra on 11/29/2015.
 */
public class User  {

    String firstname;
    String lastname;
    String gender;
    String objectId;
    boolean listed_user;
    boolean receive_push;
    String username;
    Bitmap profile_pic;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isListed_user() {
        return listed_user;
    }

    public void setListed_user(boolean listed_user) {
        this.listed_user = listed_user;
    }

    public boolean isReceive_push() {
        return receive_push;
    }

    public void setReceive_push(boolean receive_push) {
        this.receive_push = receive_push;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(Bitmap profile_pic) {
        this.profile_pic = profile_pic;
    }

    public User(){

    }


}
