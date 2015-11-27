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

import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.ShowAlbum;
import com.example.chandra.getconnected.albums.Album;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;

import java.util.ArrayList;

/**
 * Created by chandra on 11/25/2015.
 */
public class AlbumAdapter extends ArrayAdapter<Album> implements CardView.OnLongClickListener {

    Context context;
    int resource;
    ArrayList<Album> list;


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

        }
        Toolbar toolbar = (Toolbar) convertView.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.card_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.sharealbum) {

                }
                return true;
            }
        });
        CardView cardView = (CardView) convertView.findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowAlbum.class);
                intent.putExtra(ParseConstants.ALBUM_TABLE,list.get(position).getObjectId());
                context.startActivity(intent);
            }
        });
        cardView.setOnLongClickListener(this);
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




    @Override
    public boolean onLongClick(View v) {
        displayAlert();
        return true;
    }


    public void displayAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Album")
                .setCancelable(false)
                .setMessage("Are you sure you want to delete this album?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }
}
