package com.example.chandra.getconnected.albums;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.users.User;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by chandra on 12/2/2015.
 */
public class ParseAlbumQueryAdapter extends ParseQueryAdapter<ParseObject> {

    Toolbar toolbar;
    String id;
    ArrayList<String> userList;
    HashSet<String> usersHashset;
    List<ParseUser> finalList;
    ArrayList<User> userDetails;
    ArrayList<User> userFinalDetails;
    ArrayList<Integer> selectedUserListToShare;
    ParseObject album;
    CharSequence[] users;

    public interface IParseAlbumQueryAdapter {
        void addPhotos(String objectId);

        void updateAlbumView();

        void callEditAlbum(String objectId);
        void donotificationSharingStatus();
        void doEmptyNotoficationStatus();
    }

    public ParseAlbumQueryAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery(ParseConstants.ALBUM_TABLE);
                query.whereEqualTo(ParseConstants.ALBUM_FIELD_OWNER, ParseUser.getCurrentUser());
                return query;
            }
        });
    }

    @Override
        public View getItemView(final ParseObject album, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(getContext(), R.layout.gallerylistrow, null);
                toolbar = (Toolbar) v.findViewById(R.id.toolbar);
                toolbar.inflateMenu(R.menu.card_menu);
            }
            super.getItemView(album, v, parent);

        ParseImageView albumImage = (ParseImageView) v.findViewById(R.id.album);
        ParseFile imageFile = album.getParseFile(ParseConstants.ALBUM_FIEELD_COVER);
        if (imageFile != null) {
            albumImage.setParseFile(imageFile);
            albumImage.loadInBackground();
        }
        CardView cardView = (CardView) v.findViewById(R.id.card_view);
        Toolbar tool = (Toolbar) cardView.getChildAt(0);
        tool.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.sharealbum) {
                    queryAlbum(album.getObjectId(), false, false);
                }

                if (id == R.id.editalbum) {
                    ((IParseAlbumQueryAdapter) getContext()).callEditAlbum(album.getObjectId());
                }
                if (id == R.id.sharingstatus) {
                    queryAlbum(album.getObjectId(), false, true);
                }
                return true;
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IParseAlbumQueryAdapter) getContext()).addPhotos(album.getObjectId());

            }
        });
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                displayAlert(album.getObjectId());
                return true;
            }
        });


        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(album.getString(ParseConstants.ALBUM_FIELD_TITLE));
        TextView access = (TextView) v.findViewById(R.id.access);
        if (album.getBoolean(ParseConstants.ALBUM_FIELD_ISPUBLIC)) {
            access.setText("Public Album");
        } else {
            access.setText("Private Album");
        }

        return v;
    }


    public void displayAlert(final String objectId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Album")
                .setCancelable(false)
                .setMessage("Are you sure you want to delete this album?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        id = objectId;
                        queryAlbum(objectId, true, false);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }


    public void queryAlbum(String id, final boolean delete, final boolean sharing_status) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    album = object;
                    if (delete) {
                        callDeleteAllPhotos(object);
                    } else {
                        queryUsersForShareAlbum(sharing_status);
                    }
                }
            }
        });

    }

    public void callDeleteAllPhotos(ParseObject album) {
        photoExists(album);
    }

    public void photoExists(final ParseObject album) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.PHOTO_TABLE);
        query.whereEqualTo(ParseConstants.PHOTO_ALBUM, album);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    deleteAllPhotos(album);
                } else {
                    checkAlbumSharing(id);
                }
            }
        });
    }

    public void deleteAllPhotos(final ParseObject album) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.PHOTO_TABLE);
        query.whereEqualTo(ParseConstants.PHOTO_ALBUM, album);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        try {
                            object.delete();
                            object.saveInBackground();
                            checkAlbumSharing(id);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }

    public void deleteAlbum(String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, objectId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    try {
                        object.delete();
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                ((IParseAlbumQueryAdapter) getContext()).updateAlbumView();
                            }
                        });
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }

    public void checkAlbumSharing(final String id){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.SHARED_ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.SHARED_ALBUM_POINTER,album);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    deleteAlbumSharing(id);
                }else{
                    deleteAlbum(id);
                }
            }
        });
    }

    public void deleteAlbumSharing(final String id){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.SHARED_ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.SHARED_ALBUM_POINTER,album);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        try {
                            object.delete();
                            object.saveInBackground();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    deleteAlbum(id);
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }
    public void queryUsersForShareAlbum(final boolean sharing_status) {
        userList = new ArrayList<>();
        usersHashset = new HashSet<>();
        userDetails = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo(ParseConstants.OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo(GetConnectedConstants.USER_LISTED, true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    finalList = objects;
                    for (ParseUser user : objects) {
                        userList.add(user.getObjectId());
                        User details = new User();
                        details.setObjectId(user.getObjectId());
                        details.setFirstname(user.getString(GetConnectedConstants.USER_FIRST_NAME));
                        details.setLastname(user.getString(GetConnectedConstants.USER_LAST_NAME));
                        userDetails.add(details);
                    }
                    queryAlreadyShared(sharing_status);
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });
    }

    public void queryAlreadyShared(final boolean sharing_status) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.SHARED_ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.SHARED_ALBUM_POINTER, album);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> shared, ParseException e) {
                if (e == null) {
                    if (sharing_status) {
                        if (shared.isEmpty()) {
                            displaySharingStatus(false);
                        } else {
                            for (ParseObject shared_user : shared) {
                                usersHashset.add(shared_user.getParseUser(ParseConstants.SHARED_ALBUM_USER).getObjectId());
                            }
                            removeUnSharedUsers();
                        }
                    } else {

                        if (shared.isEmpty()) {
                            displayAlert();
                        }
                        for (ParseObject shared_user : shared) {
                            usersHashset.add(shared_user.getParseUser(ParseConstants.SHARED_ALBUM_USER).getObjectId());
                        }
                        if (usersHashset.size() != 0) {
                            removeAlreadySharedUsers();
                        }
                    }
                } else {
                    displayAlert();
                }
            }
        });
    }
    public void removeUnSharedUsers() {
        ArrayList<String> userTemp = userList;
        ArrayList<User> tempDetails = new ArrayList<>();


        for (int i = 0; i < userList.size(); i++) {
            if (usersHashset.contains(userList.get(i))) {
                tempDetails.add(userDetails.get(i));

            }
        }

        userDetails = tempDetails;

        if (userDetails.isEmpty()) {
            displaySharingStatus(false);
        } else {
            displaySharingStatus(true);
        }
    }

    public void displaySharingStatus(boolean sharedPresent) {

        if (sharedPresent) {
            users = new CharSequence[userDetails.size()];
            selectedUserListToShare = new ArrayList<>();
            for (int i = 0; i < userDetails.size(); i++) {
                users[i] = userDetails.get(i).toString();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Sharing Status")
                    .setCancelable(true)
                    .setItems(users, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.create().show();
        } else {
            ((IParseAlbumQueryAdapter) getContext()).donotificationSharingStatus();
        }
    }

    public void removeAlreadySharedUsers() {
        userFinalDetails = new ArrayList<>();

        for (User user : userDetails) {
            if (!usersHashset.contains(user.getObjectId())) {
                userFinalDetails.add(user);
            }
        }

        userDetails = userFinalDetails;

        displayAlert();
    }


    public void displayAlert() {
        if (userDetails.size() == 0) {
            ((IParseAlbumQueryAdapter) getContext()).doEmptyNotoficationStatus();
        } else {
            users = new CharSequence[userDetails.size()];
            selectedUserListToShare = new ArrayList<>();
            for (int i = 0; i < userDetails.size(); i++) {
                users[i] = userDetails.get(i).toString();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select Users to share the Album")
                    .setCancelable(true)
                    .setMultiChoiceItems(users, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                selectedUserListToShare.add(which);
                            } else {
                                selectedUserListToShare.remove(which);
                            }
                        }
                    }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<ParseUser> temp = new ArrayList<ParseUser>();
                    if (!selectedUserListToShare.isEmpty()) {
                        for (Integer integer : selectedUserListToShare) {
                            temp.add(finalList.get(integer));
                        }
                        shareAlbum(temp);
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        }
    }

    public void shareAlbum(ArrayList<ParseUser> usersListDetails) {
        for (final ParseUser sharedUser : usersListDetails) {
            ParseObject sharedAlbum = new ParseObject(ParseConstants.SHARED_ALBUM_TABLE);
            sharedAlbum.put(ParseConstants.SHARED_ALBUM_POINTER, album);
            sharedAlbum.put(ParseConstants.SHARED_ALBUM_USER, sharedUser);
            sharedAlbum.put(ParseConstants.SHARED_ALBUM_OWNER, ParseUser.getCurrentUser());
            sharedAlbum.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ActivityUtility.Helper.makeToast(getContext(), "Album Shared");
                        createNotificationOnAlbumSharing(sharedUser);
                    } else {
                        ActivityUtility.Helper.writeErrorLog(e.toString());
                    }
                }
            });


        }
    }

    public void createNotificationOnAlbumSharing(final ParseUser sendApprovalUser){
        ParseObject notification = new ParseObject(ParseConstants.NOTIFICATIONS_TABLE);
        notification.put(ParseConstants.NOTIFICATIONS_FROMUSER,ParseUser.getCurrentUser());
        notification.put(ParseConstants.NOTIFICATIONS_TOUSER, sendApprovalUser);
        notification.put(ParseConstants.NOTIFICATIONS_MESSAGE, ParseUser.getCurrentUser().getString(GetConnectedConstants.USER_FIRST_NAME)+ " "+ "shared an album with you");
        notification.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ActivityUtility.Helper.sendPushNotification(sendApprovalUser,ParseUser.getCurrentUser().getString(GetConnectedConstants.USER_FIRST_NAME) + " " + "shared an album with you");

            }
        });
    }



}

