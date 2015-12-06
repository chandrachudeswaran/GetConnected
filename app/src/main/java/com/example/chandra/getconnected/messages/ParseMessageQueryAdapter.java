package com.example.chandra.getconnected.messages;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandra on 12/3/2015.
 */
public class ParseMessageQueryAdapter extends ParseQueryAdapter<ParseObject> {

    String name;

    public ParseMessageQueryAdapter(Context context) {
        super(context, new ParseMessageQueryAdapter.QueryFactory<ParseObject>() {

            public ParseQuery create() {
                ParseQuery<ParseObject> sender_query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
                sender_query.whereEqualTo(ParseConstants.MESSAGES_SENDER, ParseUser.getCurrentUser());

                ParseQuery<ParseObject> receiver_query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
                receiver_query.whereEqualTo(ParseConstants.MESSAGES_RECEIVER, ParseUser.getCurrentUser());

                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                queries.add(sender_query);
                queries.add(receiver_query);

                ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
                mainQuery.include(ParseConstants.MESSAGES_RECEIVER);
                mainQuery.include(ParseConstants.MESSAGES_SENDER);
                return mainQuery;
            }
        });
    }

    @Override
    public View getItemView(final ParseObject messages, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.messageslistrow, null);

        }
        super.getItemView(messages, v, parent);

        ParseImageView profile_pic = (ParseImageView) v.findViewById(R.id.profie_pic);

        final TextView message = (TextView) v.findViewById(R.id.message);
        if (messages.getParseUser(ParseConstants.MESSAGES_SENDER).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            message.setText(messages.getParseUser(ParseConstants.MESSAGES_RECEIVER).getString(GetConnectedConstants.USER_FIRST_NAME));
            profile_pic.setParseFile(messages.getParseUser(ParseConstants.MESSAGES_RECEIVER).getParseFile(GetConnectedConstants.USER_PICTURE));
            profile_pic.loadInBackground();

        } else {
            message.setText(messages.getParseUser(ParseConstants.MESSAGES_SENDER).getString(GetConnectedConstants.USER_FIRST_NAME));
            profile_pic.setParseFile(messages.getParseUser(ParseConstants.MESSAGES_SENDER).getParseFile(GetConnectedConstants.USER_PICTURE));
            profile_pic.loadInBackground();
        }

        LinearLayout layout = (LinearLayout) v.findViewById(R.id.parent);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtility.Helper.writeErrorLog("onclick");
                        ((IParseMessageQueryAdapter) getContext()).
                        showMessages(messages.getJSONObject(ParseConstants.MESSAGES_MESSAGES), message.getText().toString(), messages.getObjectId());
            }
        });


        return v;
    }


    public interface IParseMessageQueryAdapter {
        void showMessages(JSONObject message, String person_name, String objectId);
    }
}