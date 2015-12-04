package com.example.chandra.getconnected.utility;

import com.example.chandra.getconnected.users.User;
import com.google.gson.Gson;


/**
 * Created by chandra on 12/1/2015.
 */
public class SharedPreferenceUtility {

    static public class Helper {

        public static String convertToString(User object) {
            return new Gson().toJson(object);
        }

        public static User convertToObject(String input) {
            return new Gson().fromJson(input, User.class);
        }
    }


}
