package com.example.chandra.getconnected.utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chandra on 12/1/2015.
 */
public class ParsingUtility {

    public static class FacebookProfilePicture {

        public static String getProfilePicFromFacebook(JSONObject input) throws JSONException {


            ActivityUtility.Helper.writeErrorLog(input.toString());
            JSONObject object  = input.getJSONObject("data");
            return object.getString("url");
        }
    }
}
