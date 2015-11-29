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
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandra on 11/25/2015.
 */
public class AlbumAdapter extends ArrayAdapter<Album> {

    Context context;
    int resource;
    ArrayList<Album> list;
    ParseObject album;
    String id;


    public AlbumAdapter(Context context, int resource, ArrayList<Album> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
            Toolbar toolbar = (Toolbar) convertView.findViewById(R.id.toolbar);
            if (toolbar != null) {

                toolbar.inflateMenu(R.menu.card_menu);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.sharealbum) {

                        }

                        if (id == R.id.editalbum) {

                            ((IAlbumAdapter)context).callEditAlbumIntent(list.get(position).getObjectId());

                        }
                        return true;
                    }
                });
            }

        }

        CardView cardView = (CardView) convertView.findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowAlbum.class);
                intent.putExtra(ParseConstants.ALBUM_TABLE, list.get(position).getObjectId());
                context.startActivity(intent);
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
                        queryforAlbum(objectId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }

    public void queryforAlbum(String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, objectId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    album = object;
                    callDeleteAllPhotos(album);
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
        photoexists(album);
    }

    public void photoexists(final ParseObject album) {
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

    public interface IAlbumAdapter{
        public void callEditAlbumIntent(String objectId);
    }
}
