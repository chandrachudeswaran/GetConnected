package com.example.chandra.getconnected;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.messages.ChatMessage;
import com.example.chandra.getconnected.messages.ChatMessageAdapter;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.example.chandra.getconnected.utility.ParsingUtility;
import com.example.chandra.getconnected.utility.PhotoUtility;
import com.example.chandra.getconnected.utility.SharedPreferenceHelper;
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

public class Chatting extends AppCompatActivity implements ChatMessageAdapter.IChatMessageAdapter {
    private Toolbar mToolbar;
    EditText message_edit;
    ListView userlistview;
    JSONObject conversationHistory;
    String other_person_id;
    ArrayList<ChatMessage> chatMessageArrayList;
    ChatMessageAdapter adapter;
    ParseObject to_be_Update_messageIdObject;
    String to_be_Update_messageId;
    String chat_message;
    String currentMessageId;
    ParseUser other_person_object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        message_edit = (EditText) findViewById(R.id.message);
        setSupportActionBar(mToolbar);
        userlistview = (ListView) findViewById(R.id.userlistview);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        chatMessageArrayList = new ArrayList<>();
        //Set the username on screen

        setTitle(getIntent().getExtras().getString("OTHER_PERSON"));

        try {
            if (!getIntent().getExtras().getString("CHAT").equals("empty")) {
                //Getting message history
                conversationHistory = new JSONObject(getIntent().getExtras().getString("CHAT"));
                //Get Message ID
                currentMessageId = getIntent().getExtras().getString(ParseConstants.OBJECT_ID);
                //other than current user
                getOtherPersonId();
                //List to be displayed
                chatMessageArrayList = ParsingUtility.loadChatMessages(conversationHistory);
                //display in list
                displayChat();
                //To be updated message
                getSecondMessageId();

            } else {
                other_person_id = getIntent().getExtras().getString("OTHER_PERSON_ID");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getOtherPersonId() {
        try {
            JSONArray array = conversationHistory.getJSONArray("MessageRoot");
            JSONObject obj = array.getJSONObject(0);
            if (obj.getString("senderid").equals(ParseUser.getCurrentUser().getObjectId())) {
                other_person_id = obj.getString("receiverid");
            } else {
                other_person_id = obj.getString("senderid");
            }
            ActivityUtility.Helper.writeErrorLog("other" + "" + other_person_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayChat() {
        adapter = new ChatMessageAdapter(Chatting.this, R.layout.chatmessagelistrow, chatMessageArrayList);
        userlistview.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        message_edit.setText("");
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void getSecondMessageId() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
        query.whereEqualTo(ParseConstants.MESSAGES_IDENTIFIER, other_person_id + "," + ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    to_be_Update_messageIdObject = object;
                    to_be_Update_messageId = to_be_Update_messageIdObject.getObjectId();
                    ActivityUtility.Helper.writeErrorLog(to_be_Update_messageId);
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });

    }


    public void sendText(View view) {
        // Text Message sending
        if (message_edit.getText().length() == 0) {
            ActivityUtility.Helper.makeToast(Chatting.this, "Enter message");
        } else {

            String text = message_edit.getText().toString();
            chat_message = text;
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(text);
            chatMessage.setPosition(GetConnectedConstants.RIGHT);
            chatMessage.setImage(false);
            chatMessage.setTime(ActivityUtility.Helper.getTime(System.currentTimeMillis()));
            chatMessageArrayList.add(chatMessage);
            displayChat();
            //After displaying update Parse.com
            if (conversationHistory != null) {
                getPersonOtherObjectForNotification(other_person_id, text);


            } else {
                getOtherPersonId(other_person_id, text, false);

            }
        }
    }

    public void getPersonOtherObjectForNotification(String objectId, final String text) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseConstants.OBJECT_ID, objectId);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    other_person_object = object;
                    callMessagingEventPushNotification();
                    updateConversationHistory(conversationHistory, currentMessageId, text, ParseUser.getCurrentUser().getObjectId(), other_person_id, false, true);
                }
            }
        });
    }

    public void getOtherPersonId(String id1, final String text, final boolean image) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseConstants.OBJECT_ID, id1);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    other_person_object = object;
                    other_person_id = other_person_object.getObjectId();
                    callMessagingEventPushNotification();
                    createNewJsonMessage(ParseUser.getCurrentUser().getObjectId(), other_person_id, text, image, true);

                }
            }
        });
    }

    public void callMessagingEventPushNotification() {
        ActivityUtility.Helper.callPushNotification(other_person_object, "You received a message from " + ParseUser.getCurrentUser().getString(GetConnectedConstants.USER_FIRST_NAME), GetConnectedConstants.EVENT_MESSAGING);
    }


    public void createNewJsonMessage(String senderid, String receiverid, final String text, final boolean image, final boolean first_time) {


        try {
            final JSONObject message1 = new JSONObject();

            message1.put(GetConnectedConstants.JSON_SENDER_LABEL, senderid);
            message1.put(GetConnectedConstants.JSON_RECEIVER_LABEL, receiverid);
            message1.put(GetConnectedConstants.JSON_TIME, ActivityUtility.Helper.getTime(System.currentTimeMillis()));
            if (!first_time) {
                message1.put(GetConnectedConstants.JSON_STATUS, GetConnectedConstants.CHAT_STATUS_UNREAD);
            }
            if (!image) {
                message1.put(GetConnectedConstants.JSON_MESSAGE_CONTENT, text);
                message1.put(GetConnectedConstants.JSON_IMAGE, "empty");
            } else {
                message1.put(GetConnectedConstants.JSON_IMAGE, text);
                message1.put(GetConnectedConstants.JSON_MESSAGE_CONTENT, "");
            }

            JSONArray messages = new JSONArray();
            messages.put(message1);

            final JSONObject root = new JSONObject();
            root.put("MessageRoot", messages);

            final ParseObject obj = new ParseObject(ParseConstants.MESSAGES_TABLE);
            obj.put(ParseConstants.MESSAGES_SENDER, ParseUser.getCurrentUser());
            obj.put(ParseConstants.MESSAGES_RECEIVER, other_person_object);
            obj.put(ParseConstants.MESSAGES_MESSAGES, root);

            if (first_time) {
                obj.put(ParseConstants.MESSAGES_INBOX, senderid);
                obj.put(ParseConstants.MESSAGES_IDENTIFIER, senderid + "," + receiverid);
            } else {
                obj.put(ParseConstants.MESSAGES_INBOX, receiverid);
                obj.put(ParseConstants.MESSAGES_IDENTIFIER, receiverid + "," + senderid);
            }
            obj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        if (first_time) {
                            currentMessageId = obj.getObjectId();
                            createNewJsonMessage(ParseUser.getCurrentUser().getObjectId(), other_person_id, text, image, false);
                        } else {
                            conversationHistory = root;
                            getOtherPersonId();
                            getSecondMessageId();
                        }


                    } else {
                        ActivityUtility.Helper.writeErrorLog(e.toString());
                    }
                }
            });
        } catch (JSONException e) {
            ActivityUtility.Helper.writeErrorLog(e.toString());
            e.printStackTrace();
        }
    }


    public void updateConversationHistory(final JSONObject messageHistory, String messageid, final String text, final String senderid, final String receiverid, final boolean image, final boolean first_time) {


        try {
            JSONArray array = messageHistory.getJSONArray("MessageRoot");
            final JSONObject message = new JSONObject();
            message.put(GetConnectedConstants.JSON_SENDER_LABEL, senderid);
            message.put(GetConnectedConstants.JSON_RECEIVER_LABEL, receiverid);
            message.put(GetConnectedConstants.JSON_TIME, ActivityUtility.Helper.getTime(System.currentTimeMillis()));
            if (!first_time) {
                message.put(GetConnectedConstants.JSON_STATUS, GetConnectedConstants.CHAT_STATUS_UNREAD);
            }
            if (!image) {

                message.put(GetConnectedConstants.JSON_IMAGE, "empty");
                message.put(GetConnectedConstants.JSON_MESSAGE_CONTENT, text);

            } else {
                message.put(GetConnectedConstants.JSON_IMAGE, text);
                message.put(GetConnectedConstants.JSON_MESSAGE_CONTENT, "");
            }
            array.put(message);
            messageHistory.put("MessageRoot", array);
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
            query.whereEqualTo(ParseConstants.OBJECT_ID, messageid);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        object.put(ParseConstants.MESSAGES_MESSAGES, messageHistory);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    if (first_time) {
                                        updateConversationHistory(to_be_Update_messageIdObject.getJSONObject(ParseConstants.MESSAGES_MESSAGES),
                                                to_be_Update_messageId, text, senderid, receiverid, image, false);
                                    }
                                } else {
                                    ActivityUtility.Helper.writeErrorLog(e.toString());
                                }
                            }
                        });
                    } else {
                        ActivityUtility.Helper.writeErrorLog(e.toString());
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            ActivityUtility.Helper.writeErrorLog(e.toString());
        }


    }


    public void sendImage(View view) {
        Intent intent = new Intent(Chatting.this, ChatMessageImage.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    String uploadImaageId = data.getExtras().getString(ParseConstants.OBJECT_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessage(uploadImaageId);
                    SharedPreferenceHelper helper = new SharedPreferenceHelper();
                    String imageString = helper.loadFromSharedPreference(Chatting.this, uploadImaageId);

                    chatMessage.setBitmap(PhotoUtility.convertStringToImage(imageString));
                    chatMessage.setPosition(GetConnectedConstants.RIGHT);
                    chatMessage.setTime(ActivityUtility.Helper.getTime(System.currentTimeMillis()));
                    chatMessage.setImage(true);
                    chatMessage.setIsNew(false);
                    chatMessage.setImageid(uploadImaageId);
                    chatMessageArrayList.add(chatMessage);
                    adapter = new ChatMessageAdapter(Chatting.this, R.layout.chatmessagelistrow, chatMessageArrayList);
                    userlistview.setAdapter(adapter);
                    adapter.setNotifyOnChange(true);
                    message_edit.setText("");
                    if (conversationHistory != null) {
                        callMessagingEventPushNotification();
                        updateConversationHistory(conversationHistory, currentMessageId, uploadImaageId, ParseUser.getCurrentUser().getObjectId(), other_person_id, true, true);
                    } else {
                        getOtherPersonId(other_person_id, uploadImaageId, true);
                    }
                }
        }
    }


    @Override
    public void deleteMessage(ChatMessage chatMessage, int position) {

        displayAlertForDeleteMessage(chatMessage, position);

    }

    @Override
    public void updateReadMessage(int position) {
        updateReadStatusForConversationHistory(position);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void displayAlertForDeleteMessage(final ChatMessage chatMessage, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Chatting.this);
        builder.setTitle("Delete Message")
                .setCancelable(false)
                .setMessage("Are you sure you want to delete this Message?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter = new ChatMessageAdapter(Chatting.this, R.layout.chatmessagelistrow, chatMessageArrayList);
                        userlistview.setAdapter(adapter);
                        chatMessageArrayList.remove(chatMessage);
                        adapter.setNotifyOnChange(true);
                        deleteFromParse(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }


    public void deleteFromParse(int position) {

        try {
            JSONArray list = new JSONArray();
            JSONArray jsonArray = (conversationHistory.getJSONArray("MessageRoot"));
            int len = jsonArray.length();
            if (jsonArray != null) {
                for (int i = 0; i < len; i++) {
                    if (i != position) {
                        list.put(jsonArray.get(i));
                    }
                }
            }
            conversationHistory = new JSONObject();
            conversationHistory.put("MessageRoot", list);
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
            query.whereEqualTo(ParseConstants.OBJECT_ID, currentMessageId);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        object.put(ParseConstants.MESSAGES_MESSAGES, conversationHistory);
                        object.saveInBackground();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void updateReadStatusForConversationHistory(final int position) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
        query.whereEqualTo(ParseConstants.MESSAGES_IDENTIFIER, ParseUser.getCurrentUser().getObjectId() + "," + other_person_id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    try {
                        JSONObject root = object.getJSONObject(ParseConstants.MESSAGES_MESSAGES);
                        JSONArray jsonArray = (root.getJSONArray("MessageRoot"));
                        JSONObject obj = jsonArray.getJSONObject(position);
                        obj.put(GetConnectedConstants.JSON_STATUS, GetConnectedConstants.CHAT_STATUS_READ);
                        jsonArray.put(position, obj);
                        root = new JSONObject();
                        root.put("MessageRoot", jsonArray);
                        object.put(ParseConstants.MESSAGES_MESSAGES, root);
                        object.saveInBackground();

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


    }
}
