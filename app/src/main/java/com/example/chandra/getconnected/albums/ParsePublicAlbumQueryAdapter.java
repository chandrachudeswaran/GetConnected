package com.example.chandra.getconnected.albums;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * Created by chandra on 12/10/2015.
 */
public class ParsePublicAlbumQueryAdapter extends ParseQueryAdapter<ParseObject> {

    public ParsePublicAlbumQueryAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery(ParseConstants.ALBUM_TABLE);
                query.whereEqualTo(ParseConstants.ALBUM_FIELD_ISPUBLIC, true);
                query.whereNotEqualTo(ParseConstants.ALBUM_FIELD_OWNER, ParseUser.getCurrentUser());
                query.include(ParseConstants.ALBUM_FIELD_OWNER);
                return query;
            }
        });
    }


    public interface IParsePublicAlbum{
        void showAllPhotos(String objectId);
    }

    @Override
    public View getItemView(final ParseObject album, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.gallerylistrow, null);
        }
        super.getItemView(album, v, parent);

        CardView cardView = (CardView) v.findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IParsePublicAlbum)getContext()).showAllPhotos(album.getObjectId());
            }
        });
        ParseImageView albumImage = (ParseImageView) v.findViewById(R.id.album);
        ParseFile imageFile = album.getParseFile(ParseConstants.ALBUM_FIEELD_COVER);
        if (imageFile != null) {
            albumImage.setParseFile(imageFile);
            albumImage.loadInBackground();
        }

        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(album.getString(ParseConstants.ALBUM_FIELD_TITLE));
        TextView access = (TextView) v.findViewById(R.id.access);
        access.setText("Owner: "+album.getParseUser(ParseConstants.ALBUM_FIELD_OWNER).getString(GetConnectedConstants.USER_FIRST_NAME));

        return v;
    }
}
