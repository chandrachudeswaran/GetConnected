package com.example.chandra.getconnected.users;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.ShowAlbum;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * Created by chandra on 12/2/2015.
 */
public class ParseUserAlbumQueryAdapter extends ParseQueryAdapter<ParseObject> {

    public ParseUserAlbumQueryAdapter(Context context, final String owner_id) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.ALBUM_TABLE);
                query.whereEqualTo(ParseConstants.ALBUM_FIELD_OWNER_ID, owner_id);
                query.whereEqualTo(ParseConstants.ALBUM_FIELD_ISPUBLIC, true);
                return query;
            }
        });
    }


    @Override
    public View getItemView(final ParseObject album, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.grid_photos, null);
        }
        super.getItemView(album, convertView, parent);

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.parent);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowAlbum.class);
                intent.putExtra(ParseConstants.ALBUM_TABLE, album.getObjectId());
                intent.putExtra(GetConnectedConstants.REMOVE_PHOTOS_OPTION, true);
                getContext().startActivity(intent);
            }
        });

        TextView title = (TextView) convertView.findViewById(R.id.album_title);
        title.setText(album.getString(ParseConstants.ALBUM_FIELD_TITLE));

        ParseImageView album_image = (ParseImageView) convertView.findViewById(R.id.imageGrid);
        ParseFile file = album.getParseFile(ParseConstants.ALBUM_FIEELD_COVER);
        album_image.setParseFile(file);
        album_image.loadInBackground();

        return convertView;
    }
}