package com.example.chandra.getconnected.constants;

/**
 * Created by chandra on 11/24/2015.
 */
public class ParseConstants {

    public static final String OBJECT_ID = "objectId";

    public static final String MESSAGE_ID = "messageId";

    //Album Fields
    public static final String ALBUM_TABLE = "Album";
    public static final String ALBUM_FIELD_TITLE = "Title";
    public static final String ALBUM_FIELD_ISPUBLIC = "ispublic";
    public static final String ALBUM_FIELD_OWNER = "createdbyUser";
    public static final String ALBUM_FIELD_OWNER_ID = "createdbyuserid";
    public static final String ALBUM_FIEELD_COVER = "albumphoto";

    //Photo Fields
    public static final String PHOTO_TABLE = "Photo";
    public static final String PHOTO_ALBUM = "Album";
    public static final String PHOTO_FIELD_FILE = "Photo";
    public static final String PHOTO_CAPTION = "PhotoCaption";
    public static final String PHOTO_MODERATED_BY_OWNER = "ModeratedByowner";

    //Album Shared Fields

    public static final String SHARED_ALBUM_TABLE = "Albumshared";
    public static final String SHARED_ALBUM_POINTER = "album";
    public static final String SHARED_ALBUM_USER = "sharedwithuser";
    public static final String SHARED_ALBUM_OWNER = "albumowner";

    //Message Fields

    public static final String MESSAGES_TABLE = "Messages";
    public static final String MESSAGES_SENDER = "sender";
    public static final String MESSAGES_RECEIVER = "receiver";
    public static final String MESSAGES_MESSAGES = "messages";
    public static final String MESSAGES_INBOX = "userinbox";
    public static final String MESSAGES_IDENTIFIER = "identifier";

    //MessagesPhoto Fields

    public static final String MESSAGES_PHOTO_TABLE = "MessagesPhoto";
    public static final String MESSAGES_PHOTO_PHOTO = "photo";
    public static final String MESSAGES_PHOTO_MESSAGES = "messages";
    public static final String MESSAGES_PHOTO_INBOX = "userinbox";


    //Notification Fields

    public static final String NOTIFICATIONS_TABLE = "Notifications";
    public static final String NOTIFICATIONS_FROMUSER = "FromUser";
    public static final String NOTIFICATIONS_TOUSER = "ToUser";
    public static final String NOTIFICATIONS_ALBUM = "Album";
    public static final String NOTIFICATIONS_PHOTOS = "Photo";
    public static final String NOTIFICATIONS_MESSAGE = "Message";

    //Installation Fields

    public static final String INSTALLATION_USERID = "userid";
}
