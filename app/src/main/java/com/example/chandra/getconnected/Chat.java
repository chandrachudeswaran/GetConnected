package com.example.chandra.getconnected;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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


public class Chat extends AppCompatActivity {
    private Toolbar mToolbar;
    JSONObject message;
    ArrayList<ChatMessage> chatMessageArrayList;
    ChatMessageAdapter adapter;
    EditText message_edit;
    ListView userlistview;
    String id;
    JSONObject r;
    String receiverglobal_id;
    ParseObject receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatMessageArrayList = new ArrayList<>();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        message_edit = (EditText) findViewById(R.id.message);
        setSupportActionBar(mToolbar);
        userlistview = (ListView) findViewById(R.id.userlistview);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getIntent().getExtras().getString("OTHER_PERSON"));


        if (getIntent().getExtras().getString(ParseConstants.OBJECT_ID) != null) {
            id = getIntent().getExtras().getString(ParseConstants.OBJECT_ID);
        } else {
            id = getIntent().getExtras().getString("RECEIVERID");
        }
        ActivityUtility.Helper.writeErrorLog("id" + "  " + id);
        try {
            if (!getIntent().getExtras().getString("CHAT").equals("empty")) {
                message = new JSONObject(getIntent().getExtras().getString("CHAT"));
                getReceiverGlobalId();
                chatMessageArrayList = ParsingUtility.loadChatMessages(message);
                displayChat();
            } else {

            }

        } catch (JSONException e) {
            ActivityUtility.Helper.writeErrorLog(e.toString());
        }


    }

    public void displayChat() {
        adapter = new ChatMessageAdapter(Chat.this, R.layout.chatmessagelistrow, chatMessageArrayList);
        userlistview.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void getReceiverGlobalId() {
        try {
            JSONArray array = message.getJSONArray("MessageRoot");
            JSONObject obj = array.getJSONObject(0);
            if (obj.getString("senderid").equals(ParseUser.getCurrentUser().getObjectId())) {
                receiverglobal_id = obj.getString("receiverid");
            } else {
                receiverglobal_id = obj.getString("senderid");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void sendText(View view) {

        if (message_edit.getText().length() == 0) {
            ActivityUtility.Helper.makeToast(Chat.this, "Enter message");
        } else {

            String text = message_edit.getText().toString();
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(text);
            chatMessage.setPosition(GetConnectedConstants.RIGHT);
            chatMessage.setImage(false);
            chatMessage.setTime(ActivityUtility.Helper.getTime(System.currentTimeMillis()));
            chatMessageArrayList.add(chatMessage);
            adapter = new ChatMessageAdapter(Chat.this, R.layout.chatmessagelistrow, chatMessageArrayList);
            userlistview.setAdapter(adapter);
            adapter.setNotifyOnChange(true);
            message_edit.setText("");

            if (message != null) {
                ActivityUtility.Helper.writeErrorLog("not nnull in chat");
                updateConversationHistory(message, text, ParseUser.getCurrentUser().getObjectId(), receiverglobal_id, false);
            } else {
                ActivityUtility.Helper.writeErrorLog("nnull in chat");
                queryUserObject(id, text, false);

            }
        }
    }

    public void queryUserObject(String id1, final String text, final boolean image) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseConstants.OBJECT_ID, id1);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    receiver = object;
                    createNewJsonMessage(ParseUser.getCurrentUser().getObjectId(), id, text, image);

                }
            }
        });
    }


    public void updateConversationHistory(JSONObject messageHistory, String text, String senderid, String receiverid, boolean image) {
        ActivityUtility.Helper.writeErrorLog("update");
        try {
            r = messageHistory;
            JSONArray array = r.getJSONArray("MessageRoot");
            JSONObject message = new JSONObject();
            message.put(GetConnectedConstants.JSON_SENDER_LABEL, senderid);
            message.put(GetConnectedConstants.JSON_RECEIVER_LABEL, receiverid);
            message.put(GetConnectedConstants.JSON_TIME,ActivityUtility.Helper.getTime(System.currentTimeMillis()));
            if (!image) {

                message.put(GetConnectedConstants.JSON_IMAGE, "empty");
                message.put(GetConnectedConstants.JSON_MESSAGE_CONTENT, text);

            } else {
                message.put(GetConnectedConstants.JSON_IMAGE, text);
                message.put(GetConnectedConstants.JSON_MESSAGE_CONTENT, "");
            }
            array.put(message);
            r.put("MessageRoot", array);
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_TABLE);
            query.whereEqualTo(ParseConstants.OBJECT_ID, id);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        object.put(ParseConstants.MESSAGES_MESSAGES, r);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

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
        Intent intent = new Intent(Chat.this, ChatMessageImage.class);
        if (message != null) {
            intent.putExtra(ParseConstants.OBJECT_ID, id);
        }
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    String id1 = data.getExtras().getString(ParseConstants.OBJECT_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessage(id1);
                    SharedPreferenceHelper helper = new SharedPreferenceHelper();
                    String imageString = helper.loadFromSharedPreference(Chat.this, id1);
                    chatMessage.setBitmap(PhotoUtility.convertStringToImage(imageString));
                    chatMessage.setPosition(GetConnectedConstants.RIGHT);
                    chatMessage.setTime(ActivityUtility.Helper.getTime(System.currentTimeMillis()));
                    chatMessage.setImage(true);
                    chatMessage.setIsNew(true);
                    chatMessage.setImageid(id1);
                    chatMessageArrayList.add(chatMessage);
                    adapter = new ChatMessageAdapter(Chat.this, R.layout.chatmessagelistrow, chatMessageArrayList);
                    userlistview.setAdapter(adapter);
                    adapter.setNotifyOnChange(true);
                    message_edit.setText("");
                    if (message != null) {
                        updateConversationHistory(message, id1, ParseUser.getCurrentUser().getObjectId(), receiverglobal_id, true);
                    } else {
                        queryUserObject(id, id1, true);
                    }
                }
                if (resultCode == RESULT_CANCELED) {

                }
        }
    }


    public void createNewJsonMessage(String senderid, String receiverid, final String text, final boolean image) {


        try {
            JSONObject message = new JSONObject();

            message.put(GetConnectedConstants.JSON_SENDER_LABEL, senderid);
            message.put(GetConnectedConstants.JSON_RECEIVER_LABEL, receiverid);
            message.put(GetConnectedConstants.JSON_TIME, ActivityUtility.Helper.getTime(System.currentTimeMillis()));
            if (!image) {
                message.put(GetConnectedConstants.JSON_MESSAGE_CONTENT, text);
                message.put(GetConnectedConstants.JSON_IMAGE, "empty");
            } else {
                message.put(GetConnectedConstants.JSON_IMAGE, text);
                message.put(GetConnectedConstants.JSON_MESSAGE_CONTENT, "");
            }

            JSONArray messages = new JSONArray();
            messages.put(message);

            JSONObject root = new JSONObject();
            root.put("MessageRoot", messages);

            final ParseObject obj = new ParseObject(ParseConstants.MESSAGES_TABLE);
            obj.put(ParseConstants.MESSAGES_SENDER, ParseUser.getCurrentUser());
            obj.put(ParseConstants.MESSAGES_RECEIVER, receiver);
            obj.put(ParseConstants.MESSAGES_MESSAGES, root);
            obj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        if (image) {
                            updatePhotoMessageObject(text, obj);
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

    public void updatePhotoMessageObject(String id, final ParseObject obj) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_PHOTO_TABLE);
        query.whereEqualTo(ParseConstants.OBJECT_ID, id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.put(ParseConstants.MESSAGES_PHOTO_MESSAGES, obj);
                    object.saveInBackground();
                } else {
                    ActivityUtility.Helper.writeErrorLog(e.toString());
                }
            }
        });

    }


}
