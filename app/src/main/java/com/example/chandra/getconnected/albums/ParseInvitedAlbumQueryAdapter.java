package com.example.chandra.getconnected.albums;

import android.content.Context;
import android.support.v7.widget.CardView;
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
 * Created by chandra on 12/8/2015.
 */
public class ParseInvitedAlbumQueryAdapter extends ParseQueryAdapter<ParseObject> {

    public ParseInvitedAlbumQueryAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery(ParseConstants.SHARED_ALBUM_TABLE);
                query.whereEqualTo(ParseConstants.SHARED_ALBUM_USER, ParseUser.getCurrentUser());
                query.include(ParseConstants.SHARED_ALBUM_POINTER).include(ParseConstants.ALBUM_FIELD_OWNER);
                return query;
            }
        });
    }

    public interface IParseInvitedAlbumQueryAdapter {
         void addPhotosToInvitedAlbum(String objectId);
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
                ((IParseInvitedAlbumQueryAdapter)getContext()).addPhotosToInvitedAlbum(album.getParseObject(ParseConstants.SHARED_ALBUM_POINTER).getObjectId());
            }
        });

        ParseImageView albumImage = (ParseImageView) v.findViewById(R.id.album);
        ParseFile imageFile = album.getParseObject(ParseConstants.SHARED_ALBUM_POINTER).getParseFile(ParseConstants.ALBUM_FIEELD_COVER);
        if (imageFile != null) {
            albumImage.setParseFile(imageFile);
            albumImage.loadInBackground();
        }
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(album.getParseObject(ParseConstants.SHARED_ALBUM_POINTER).getString(ParseConstants.ALBUM_FIELD_TITLE));

        TextView access = (TextView) v.findViewById(R.id.access);
        access.setText("Sharedby: "+ album.getParseObject(ParseConstants.SHARED_ALBUM_POINTER).getParseUser
                (ParseConstants.ALBUM_FIELD_OWNER).getString(GetConnectedConstants.USER_FIRST_NAME));

        return v;
    }

}
