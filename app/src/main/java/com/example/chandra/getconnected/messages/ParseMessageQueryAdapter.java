package com.example.chandra.getconnected.messages;

import android.content.Context;

import com.example.chandra.getconnected.constants.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandra on 12/3/2015.
 */
public class ParseMessageQueryAdapter extends ParseQueryAdapter<ParseObject> {


    public ParseMessageQueryAdapter(Context context, QueryFactory<ParseObject> queryFactory) {
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
}