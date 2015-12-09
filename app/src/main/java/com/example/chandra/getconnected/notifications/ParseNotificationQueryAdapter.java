package com.example.chandra.getconnected.notifications;

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
import com.squareup.picasso.Picasso;

/**
 * Created by chandra on 12/8/2015.
 */
public class ParseNotificationQueryAdapter extends ParseQueryAdapter<ParseObject> {

    public ParseNotificationQueryAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery(ParseConstants.NOTIFICATIONS_TABLE);
                query.whereEqualTo(ParseConstants.NOTIFICATIONS_TOUSER, ParseUser.getCurrentUser());
                query.include(ParseConstants.NOTIFICATIONS_ALBUM);
                query.include(ParseConstants.NOTIFICATIONS_FROMUSER);
                return query;
            }
        });
    }

    public interface IParseNotificationQueryAdapter{
        void doApprovePhotosForTheAlbum(String photoId,String albumId);
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
                ((IParseNotificationQueryAdapter)getContext()).doApprovePhotosForTheAlbum
                        (album.getParseObject(ParseConstants.NOTIFICATIONS_PHOTOS).getObjectId(), album.getParseObject(ParseConstants.NOTIFICATIONS_ALBUM).getObjectId());
            }
        });
        ParseImageView albumImage = (ParseImageView) v.findViewById(R.id.album);
        if (album.getParseUser(ParseConstants.NOTIFICATIONS_FROMUSER).getString(GetConnectedConstants.USER_IMAGE_FACEBOOK) != null) {
            Picasso.with(getContext()).load(album.getParseUser(ParseConstants.NOTIFICATIONS_FROMUSER).getString(GetConnectedConstants.USER_IMAGE_FACEBOOK)).into(albumImage);
        } else {
            ParseFile imageFile = album.getParseUser(ParseConstants.NOTIFICATIONS_FROMUSER).getParseFile(GetConnectedConstants.USER_PICTURE);
            if (imageFile != null) {
                albumImage.setParseFile(imageFile);
                albumImage.loadInBackground();
            }
        }

        TextView title = (TextView) v.findViewById(R.id.title);
        title.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
        String text = album.getParseUser(ParseConstants.NOTIFICATIONS_FROMUSER).getString(GetConnectedConstants.USER_FIRST_NAME) + " " + "added photos to album- " +" "+
                album.getParseObject(ParseConstants.NOTIFICATIONS_ALBUM).getString(ParseConstants.ALBUM_FIELD_TITLE);
        title.setText(text);

        return v;

    }
}
