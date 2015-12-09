package com.example.chandra.getconnected.notifications;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
    Toolbar toolbar;

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

    public interface IParseNotificationQueryAdapter {
        void doApprovePhotosForTheAlbum(String photoId, String albumId);
        void deleteNotification(String notificationId);
    }

    @Override
    public View getItemView(final ParseObject album, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.gallerylistrow, null);
            toolbar = (Toolbar) v.findViewById(R.id.toolbar);
            if (album.getParseObject(ParseConstants.NOTIFICATIONS_ALBUM) != null) {
                toolbar.inflateMenu(R.menu.notifications_card_pending_menu);
            }else{
                toolbar.inflateMenu(R.menu.notifications_card_approval_menu);
            }
        }
        super.getItemView(album, v, parent);

        CardView cardView = (CardView) v.findViewById(R.id.card_view);
        Toolbar tool = (Toolbar) cardView.getChildAt(0);
        tool.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.takeaction) {
                    ((IParseNotificationQueryAdapter) getContext()).doApprovePhotosForTheAlbum
                            (album.getParseObject(ParseConstants.NOTIFICATIONS_PHOTOS).getObjectId(), album.getParseObject(ParseConstants.NOTIFICATIONS_ALBUM).getObjectId());
                }
                if (id == R.id.deletenotification) {
                    ((IParseNotificationQueryAdapter) getContext()).deleteNotification(album.getObjectId());
                }
                return true;
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
        title.setText(album.getString(ParseConstants.NOTIFICATIONS_MESSAGE));

        return v;

    }
}
