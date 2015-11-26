package com.example.chandra.getconnected;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowGallery extends Fragment implements TextView.OnClickListener {

    OnCreateAlbum onCreateAlbum;

    public ShowGallery() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_gallery, container, false);
        TextView create = (TextView) view.findViewById(R.id.createAlbum);
        create.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onCreateAlbum = (OnCreateAlbum) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    @Override
    public void onClick(View v) {
        onCreateAlbum.createAlbum();
    }


    public interface OnCreateAlbum {
        public void createAlbum();
    }
}
