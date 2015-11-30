package com.example.chandra.getconnected.albums;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chandra.getconnected.AlbumActivity;
import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.ShowAlbum;
import com.example.chandra.getconnected.albums.Album;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.users.User;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by chandra on 11/25/2015.
 */
public class AlbumAdapter extends ArrayAdapter<Album> {

    Context context;
    int resource;
    ArrayList<Album> list;
    ParseObject album;
    String id;
    CharSequence[] users;
    ArrayList<String> userList;
    HashSet<String> usersHashset;
    List<ParseUser> finalList;
    ArrayList<User> userDetails;
    ArrayList<Integer> selectedUserListToShare;
    Toolbar toolbar;
    HashMap<Integer, Integer> track;
    ArrayList<User> userFinalDetails;

    public AlbumAdapter(Context context, int resource, ArrayList<Album> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        track = new HashMap<>();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);

            toolbar = (Toolbar) convertView.findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.card_menu);

        }


        CardView cardView = (CardView) convertView.findViewById(R.id.card_view);
        Toolbar tool = (Toolbar) cardView.getChildAt(0);
        tool.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.sharealbum) {
                    queryforAlbum(list.get(position).getObjectId(), false, false);
                }

                if (id == R.id.editalbum) {
                    ((IAlbumAdapter) context).callEditAlbumIntent(list.get(position).getObjectId());
                }
                if (id == R.id.sharingstatus) {
                    queryforAlbum(list.get(position).getObjectId(), false, true);
                }
                return true;
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IAlbumAdapter) context).callAddPhotosToAlbum(list.get(position).getObjectId());

            }
        });
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                displayAlert(list.get(position).getObjectId(), position);
                return true;
            }
        });
        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(list.get(position).getTitle());
        ImageView gallery = (ImageView) convertView.findViewById(R.id.album);
        gallery.setImageBitmap(list.get(position).getAlbum_image());
        TextView access = (TextView) convertView.findViewById(R.id.access);
        if (list.get(position).isPublic) {
            access.setText("Public Album");
        } else {
            access.setText("Private Album");
        }

        return convertView;
    }


    public void displayAlert(final String objectId, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Album")
                .setCancelable(false)
                .setMessage("Are you sure you want to delete this album?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        id = objectId;
                        list.remove(position);
                        notifyDataSetChanged();
                        queryforAlbum(objectId, true, false);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }

    public void queryforAlbum(String objectId, final boolean delete, final boolean sharing_status) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, objectId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    album = object;
                    if (delete) {
                        callDeleteAllPhotos(album);
                    } else {
                        queryUsersForShareAlbum(sharing_status);
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
                        album = object;
                        object.delete();
                        object.saveInBackground();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
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
                    deleteAlbum(id);
                }
            }
        });
    }

    public void deleteAllPhotos(ParseObject album) {
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
                            deleteAlbum(id);
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

    public interface IAlbumAdapter {
        void callEditAlbumIntent(String objectId);

        void callAddPhotosToAlbum(String objectId);

        void callNotificationSharingStatus();
    }


    public void queryUsersForShareAlbum(final boolean sharing_status) {
        userList = new ArrayList<>();
        usersHashset = new HashSet<>();
        userDetails = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo(ParseConstants.OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
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
                        if(usersHashset.size()!=0) {
                            removeAlreadySharedUsers();
                        }
                    }
                } else {
                    displayAlert();
                }
            }
        });
    }

    public void displaySharingStatus(boolean sharedPresent) {

        if (sharedPresent) {
            users = new CharSequence[userDetails.size()];
            selectedUserListToShare = new ArrayList<>();
            for (int i = 0; i < userDetails.size(); i++) {
                users[i] = userDetails.get(i).toString();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Sharing Status")
                    .setCancelable(true)
                    .setItems(users, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.create().show();
        } else {
            ((IAlbumAdapter) context).callNotificationSharingStatus();
        }
    }

    public void removeAlreadySharedUsers() {
        userFinalDetails = new ArrayList<>();

        for(User user:userDetails){
            if(!usersHashset.contains(user.getObjectId())){
                userFinalDetails.add(user);
            }
        }

        userDetails = userFinalDetails;

        displayAlert();
    }


    public void displayAlert() {
        users = new CharSequence[userDetails.size()];
        selectedUserListToShare = new ArrayList<>();
        for (int i = 0; i < userDetails.size(); i++) {
            users[i] = userDetails.get(i).toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    public void shareAlbum(ArrayList<ParseUser> usersListDetails) {
        for (ParseUser sharedUser : usersListDetails) {
            ParseObject sharedAlbum = new ParseObject(ParseConstants.SHARED_ALBUM_TABLE);
            sharedAlbum.put(ParseConstants.SHARED_ALBUM_POINTER, album);
            sharedAlbum.put(ParseConstants.SHARED_ALBUM_USER, sharedUser);
            sharedAlbum.put(ParseConstants.SHARED_ALBUM_OWNER, ParseUser.getCurrentUser());
            sharedAlbum.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ActivityUtility.Helper.makeToast(context, "Album Shared");
                    } else {
                        ActivityUtility.Helper.writeErrorLog(e.toString());
                    }
                }
            });


        }
    }
}
