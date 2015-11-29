package com.example.chandra.getconnected.albums;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.chandra.getconnected.PhotoView;
import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import java.util.ArrayList;


/**
 * Created by chandra on 11/25/2015.
 */
public class PhotoAdapter extends BaseAdapter {

    Context context;
    int resource;
    ArrayList<Photo> list;


    public PhotoAdapter(Context context, int resource, ArrayList<Photo> list) {

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

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.parent);
        layout.removeView(convertView.findViewById(R.id.album_title));
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageGrid);
        imageView.setImageBitmap(list.get(position).getImage());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoView.class);
                intent.putExtra(ParseConstants.OBJECT_ID, list.get(position).getObjectId());
                context.startActivity(intent);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                displayAlertDialog(position, list.get(position).getObjectId());

                return true;
            }
        });
        return convertView;
    }


    public void displayAlertDialog(final int position, final String objectId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Photo")
                .setCancelable(false)
                .setMessage("Are you sure you want to delete this photo?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(position);
                        notifyDataSetChanged();
                        deletePhotoSelected(objectId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }

    public void deletePhotoSelected(String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.PHOTO_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, objectId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    try {
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

}
