package com.example.chandra.getconnected.constants;

/**
 * Created by chandra on 11/24/2015.
 */
public class ParseConstants {

    public static final String OBJECT_ID = "objectId";

    //Album Fields
    public static final String ALBUM_TABLE = "Album";
    public static final String ALBUM_FIELD_TITLE = "Title";
    public static final String ALBUM_FIELD_ISPUBLIC = "ispublic";
    public static final String ALBUM_FIELD_OWNER = "createdbyUser";

    //Photo Fields
    public static final String PHOTO_TABLE = "Photo";
    public static final String PHOTO_ALBUM = "Album";
    public static final String PHOTO_FIELD_FILE = "Photo";
    public static final String PHOTO_CAPTION = "PhotoCaption";

    //Album Shared Fields

    public static final String SHARED_ALBUM_TABLE = "Albumshared";
    public static final String SHARED_ALBUM_POINTER = "album";
    public static final String SHARED_ALBUM_USER = "sharedwithuser";
    public static final String SHARED_ALBUM_OWNER = "albumowner";
}
