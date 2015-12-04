package com.example.chandra.getconnected.users;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 * Created by chandra on 12/2/2015.
 */
public class ParseUserQueryAdapter extends ParseQueryAdapter<ParseUser> {


    public ParseUserQueryAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseUser>() {
            public ParseQuery create() {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo(GetConnectedConstants.USER_LISTED, true);
                query.whereNotEqualTo(ParseConstants.OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
                return query;
            }
        });
    }

    @Override
    public View getItemView(final ParseUser user, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.user_listrow, null);

        }
        super.getItemView(user, v, parent);

        CardView cardView = (CardView) v.findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 ((IParseUserQueryAdapter) getContext()).callProfileViewForUser(user.getObjectId());
            }
        });
        ParseImageView profile = (ParseImageView) v.findViewById(R.id.profile_pic);

        if (user.getString(GetConnectedConstants.USER_IMAGE_FACEBOOK) != null) {
            Picasso.with(getContext()).load(user.getString(GetConnectedConstants.USER_IMAGE_FACEBOOK)).into(profile);
        }else{
            ParseFile imageFile = user.getParseFile(GetConnectedConstants.USER_PICTURE);
            if (imageFile != null) {
                profile.setParseFile(imageFile);
                profile.loadInBackground();
            }
        }
        TextView firstname = (TextView) v.findViewById(R.id.firstname);
        firstname.setText(user.getString(GetConnectedConstants.USER_FIRST_NAME));

        TextView lastname = (TextView) v.findViewById(R.id.lastname);
        lastname.setText(user.getString(GetConnectedConstants.USER_LAST_NAME));
        return v;
    }




    public interface IParseUserQueryAdapter{
        public void callProfileViewForUser(String objectId);
    }
}