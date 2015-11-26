package com.example.chandra.getconnected.com.example.chandra.getconnected.albums;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chandra.getconnected.R;

import java.util.ArrayList;

/**
 * Created by chandra on 11/25/2015.
 */
public class AlbumAdapter extends ArrayAdapter<Album> {

    Context context;
    int resource;
    ArrayList<Album> list;


    public AlbumAdapter(Context context, int resource, ArrayList<Album> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.list=objects;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);

        }

        TextView title = (TextView)convertView.findViewById(R.id.title);
        title.setText(list.get(position).getTitle());
        ImageView gallery = (ImageView)convertView.findViewById(R.id.album);
        gallery.setImageResource(R.drawable.galleryimage);
        return convertView;
    }
}
