package com.example.chandra.getconnected.albums;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.constants.GetConnectedConstants;

import java.util.ArrayList;

/**
 * Created by chandra on 11/27/2015.
 */
public class ImageListImpl implements GetPhotos.ImageList {

    TextView hint;
    GridView grid;
    int resource;
    Context context;

    public ImageListImpl(View hint, View grid, int resource, Context context) {

        this.hint = (TextView) hint;
        this.grid = (GridView) grid;
        this.resource = resource;
        this.context = context;
    }

    @Override
    public void sendImages(ArrayList<Bitmap> images) {

        if (images.isEmpty()) {

            hint.setText(GetConnectedConstants.NO_PHOTOS);
        } else {
            hint.setText(" ");
            PhotoAdapter adapter = new PhotoAdapter(context,resource, images);
            grid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
