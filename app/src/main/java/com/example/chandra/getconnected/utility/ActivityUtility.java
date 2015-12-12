package com.example.chandra.getconnected.utility;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chandra on 11/23/2015.
 */
public class ActivityUtility {


    static public class Helper {

        static public void writeDebugLog(String message) {
            Log.d("GetConnected", message);
        }

        static public void writeErrorLog(String message) {
            Log.e("GetConnected", message);
        }

        static public void makeToast(Context context, String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        static public void showNotificationLogin(CoordinatorLayout coordinatorLayout, String message) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

            snackbar.show();
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            textView.setGravity(Gravity.CENTER);
        }

        static public boolean isConnected(Context context) {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.isConnected()) {
                return true;
            }
            return false;
        }


        static public void showOfflineToastMessage(Context context) {
            ActivityUtility.Helper.makeToast(context, GetConnectedConstants.NO_INTERNET);
        }

        static public String getTime(long time) {
            Date date = new Date(time);
            Format format = new SimpleDateFormat("HH:mm");
            return format.format(date);
        }


        static public void callPushNotification(ParseUser receiverUser, String message, String event) {

            if (event.equals(GetConnectedConstants.EVENT_ALBUM_SHARE)) {
                if (receiverUser.getBoolean(GetConnectedConstants.USER_RECEIVE_PUSH)) {
                    Map<String, String> lNotificationMap = new HashMap<>();
                    lNotificationMap.put("toUser", receiverUser.getObjectId());
                    lNotificationMap.put("fromUser", ParseUser.getCurrentUser().getString(GetConnectedConstants.USER_FIRST_NAME));
                    lNotificationMap.put("message", message);
                    lNotificationMap.put("type", GetConnectedConstants.ALBUM_SHARE);
                    ParseCloud.callFunctionInBackground("notifyPush", lNotificationMap, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if (e == null) {
                            } else e.printStackTrace();
                        }
                    });
                }
            }

            if(event.equals(GetConnectedConstants.EVENT_PHOTO_ADDED)){
                if (receiverUser.getBoolean(GetConnectedConstants.USER_RECEIVE_PUSH)) {
                    Map<String, String> lNotificationMap = new HashMap<>();
                    lNotificationMap.put("toUser", receiverUser.getObjectId());
                    lNotificationMap.put("fromUser", ParseUser.getCurrentUser().getString(GetConnectedConstants.USER_FIRST_NAME));
                    lNotificationMap.put("message", message);
                    lNotificationMap.put("type", GetConnectedConstants.PHOTO_ADDED);
                    ParseCloud.callFunctionInBackground("notifyPushForPhoto", lNotificationMap, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if (e == null) {
                            } else e.printStackTrace();
                        }
                    });
                }
            }

            if(event.equals(GetConnectedConstants.EVENT_MESSAGING)){
                if (receiverUser.getBoolean(GetConnectedConstants.USER_RECEIVE_PUSH)) {
                    Map<String, String> lNotificationMap = new HashMap<>();
                    lNotificationMap.put("toUser", receiverUser.getObjectId());
                    lNotificationMap.put("fromUser", ParseUser.getCurrentUser().getString(GetConnectedConstants.USER_FIRST_NAME));
                    lNotificationMap.put("message", message);
                    lNotificationMap.put("type", GetConnectedConstants.MESSAGING);
                    ParseCloud.callFunctionInBackground("notifyPushForMessage", lNotificationMap, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if (e == null) {
                            } else e.printStackTrace();
                        }
                    });
                }
            }

            if(event.equals(GetConnectedConstants.EVENT_SIGNUP)){
                if (receiverUser.getBoolean(GetConnectedConstants.USER_RECEIVE_PUSH)) {
                    Map<String, String> lNotificationMap = new HashMap<>();
                    lNotificationMap.put("toUser", receiverUser.getObjectId());
                    lNotificationMap.put("fromUser", ParseUser.getCurrentUser().getString(GetConnectedConstants.USER_FIRST_NAME));
                    lNotificationMap.put("message", message);
                    lNotificationMap.put("type", GetConnectedConstants.SIGNUP);
                    ParseCloud.callFunctionInBackground("notifyPushForMessage", lNotificationMap, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if (e == null) {
                            } else e.printStackTrace();
                        }
                    });
                }
            }
        }
    }
}