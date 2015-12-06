package com.example.chandra.getconnected.utility;

import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.messages.ChatMessage;
import com.example.chandra.getconnected.messages.ChatMessageAdapter;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by chandra on 12/1/2015.
 */
public class ParsingUtility {

    public static class FacebookProfilePicture {

        public static String getProfilePicFromFacebook(JSONObject input) throws JSONException {

            JSONObject object = input.getJSONObject("data");
            return object.getString("url");
        }
    }

    public static ArrayList<ChatMessage> loadChatMessages(JSONObject message) {

        ArrayList<ChatMessage> chatMessageArrayList = new ArrayList<>();
        try {
            JSONArray array = message.getJSONArray("MessageRoot");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setTime(obj.getString("time"));
                chatMessage.setIsNew(false);
                if (obj.getString("imageid").equals("empty")) {
                    chatMessage.setMessage(obj.getString("messagetext"));
                    chatMessage.setImage(false);
                } else {
                    chatMessage.setMessage(obj.getString("imageid"));
                    chatMessage.setImage(true);
                }
                if (obj.getString("senderid").equals(ParseUser.getCurrentUser().getObjectId())) {
                    chatMessage.setPosition(GetConnectedConstants.RIGHT);

                } else {
                    chatMessage.setPosition(GetConnectedConstants.LEFT);

                }

                chatMessageArrayList.add(chatMessage);
            }
            return chatMessageArrayList;
        } catch (JSONException e) {
            ActivityUtility.Helper.writeErrorLog(e.toString());
        }

        return chatMessageArrayList;
    }
}
