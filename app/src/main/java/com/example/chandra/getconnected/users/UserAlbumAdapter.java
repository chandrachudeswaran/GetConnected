package com.example.chandra.getconnected.users;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.ShowAlbum;
import com.example.chandra.getconnected.albums.Album;
import com.example.chandra.getconnected.albums.Photo;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;

import java.util.ArrayList;

/**
 * Created by chandra on 11/29/2015.
 */
public class UserAlbumAdapter extends BaseAdapter {

    Context context;
    int resource;
    ArrayList<Album> list;


    public UserAlbumAdapter(Context context, int resource, ArrayList<Album> list) {

        this.context = context;
        this.resource = resource;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }

        LinearLayout layout =(LinearLayout)convertView.findViewById(R.id.parent);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowAlbum.class);
                intent.putExtra(ParseConstants.ALBUM_TABLE, list.get(position).getObjectId());
                intent.putExtra(GetConnectedConstants.REMOVE_PHOTOS_OPTION,true);
                context.startActivity(intent);
            }
        });
        TextView title = (TextView) convertView.findViewById(R.id.album_title);
        title.setText(list.get(position).getTitle());

        ImageView album_image = (ImageView) convertView.findViewById(R.id.imageGrid);
        album_image.setImageBitmap(list.get(position).getAlbum_image());
        return convertView;
    }
}
