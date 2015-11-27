package com.example.chandra.getconnected.com.example.chandra.getconnected.albums;


import android.content.Context;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.example.chandra.getconnected.R;


import java.util.ArrayList;


/**
 * Created by chandra on 11/25/2015.
 */
public class PhotoAdapter extends BaseAdapter {

    Context context;
    int resource;
    ArrayList<Bitmap> list;


    public PhotoAdapter(Context context, int resource, ArrayList<Bitmap> list) {

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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageGrid);
        imageView.setImageBitmap(list.get(position));
        return convertView;
    }


}
