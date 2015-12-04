package com.example.chandra.getconnected;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComposeMessage extends AppCompatActivity {

    ParseObject receiverObject;
    EditText message;
    ParseObject conversationObject;
    JSONObject messageHistory;
    JSONObject r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        message = (EditText) findViewById(R.id.message);

        queryUserObject(getIntent().getExtras().getString(ParseConstants.OBJECT_ID));
    }


    public void sendMessage(View v) {

        if (message.getText().length() == 0) {
            ActivityUtility.Helper.makeToast(ComposeMessage.this, "Cannnot send a empty message");
        } else {
            retrieveConversationHistory();

        }
    }


    public void queryUserObject(String id) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseConstants.OBJECT_ID, id);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    receiverObject = object;
                }
            }
        });
    }

    public void retrieveConversationHistory() {
        ParseQuery<ParseObject> sender_query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
        sender_query.whereEqualTo(ParseConstants.MESSAGES_SENDER, ParseUser.getCurrentUser());
        sender_query.whereEqualTo(ParseConstants.MESSAGES_RECEIVER, receiverObject);

        ParseQuery<ParseObject> receiver_query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
        receiver_query.whereEqualTo(ParseConstants.MESSAGES_RECEIVER, ParseUser.getCurrentUser());
        receiver_query.whereEqualTo(ParseConstants.MESSAGES_SENDER, receiverObject);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(sender_query);
        queries.add(receiver_query);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.include(ParseConstants.MESSAGES_RECEIVER);
        mainQuery.include(ParseConstants.MESSAGES_SENDER);
        mainQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    conversationObject = object;
                    messageHistory= conversationObject.getJSONObject(ParseConstants.MESSAGES_MESSAGES);
                    updateConversationHistory(messageHistory,message.getText().toString(),ParseUser.getCurrentUser().getObjectId(),receiverObject.getObjectId());
                }
                else{
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                    createNewJsonMessage(ParseUser.getCurrentUser().getObjectId(),
                            receiverObject.getObjectId(), message.getText().toString());
                }
            }
        });


    }

    public void updateConversationHistory(JSONObject messageHistory,String text,String senderid,String receiverid){


        try {
             r = messageHistory;
            ActivityUtility.Helper.writeErrorLog(r.toString());
            JSONArray array = r.getJSONArray("MessageRoot");

            JSONObject message = new JSONObject();
            message.put(GetConnectedConstants.JSON_SENDER_LABEL,senderid);
            message.put(GetConnectedConstants.JSON_RECEIVER_LABEL, receiverid);
            message.put(GetConnectedConstants.JSON_MESSAGE_CONTENT,text);

            ActivityUtility.Helper.writeErrorLog(message.toString());

            array.put(message);

            ActivityUtility.Helper.writeErrorLog(array.toString());
            r.put("MessageRoot", array);
            ActivityUtility.Helper.writeErrorLog(r.toString());

            ActivityUtility.Helper.writeErrorLog(r.toString());

            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
            query.whereEqualTo(ParseConstants.OBJECT_ID, conversationObject.getObjectId());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        object.put(ParseConstants.MESSAGES_MESSAGES, r);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    ActivityUtility.Helper.makeToast(ComposeMessage.this,"Message updated");
                                }
                            }
                        });
                    }
                }
            });



        } catch (JSONException e) {
            e.printStackTrace();
            ActivityUtility.Helper.writeErrorLog(e.toString());
        }

    }

    public void createNewJsonMessage(String senderid, String receiverid, String text) {


        try {
            JSONObject message = new JSONObject();

            message.put(GetConnectedConstants.JSON_SENDER_LABEL, senderid);
            message.put(GetConnectedConstants.JSON_RECEIVER_LABEL, receiverid);
            message.put(GetConnectedConstants.JSON_MESSAGE_CONTENT, text);



            JSONArray messages = new JSONArray();
            messages.put(message);



            JSONObject root = new JSONObject();
            root.put("MessageRoot", messages);



            ParseObject obj = new ParseObject(ParseConstants.MESSAGES_TABLE);

            obj.put(ParseConstants.MESSAGES_SENDER, ParseUser.getCurrentUser());
            obj.put(ParseConstants.MESSAGES_RECEIVER, receiverObject);
            obj.put(ParseConstants.MESSAGES_MESSAGES, root);
            obj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ActivityUtility.Helper.makeToast(ComposeMessage.this, "Saved");
                    }
                    else{
                        ActivityUtility.Helper.writeErrorLog(e.toString());
                    }
                }
            });
        } catch (JSONException e) {
            ActivityUtility.Helper.writeErrorLog(e.toString());
            e.printStackTrace();
        }
    }
}
