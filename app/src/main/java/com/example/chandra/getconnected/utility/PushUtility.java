package com.example.chandra.getconnected.utility;

import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.parse.ParsePushBroadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chandra on 12/11/2015.
 */
public class PushUtility extends ParsePushBroadcastReceiver {


    JSONObject fData;

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);

        fData = getDataFromIntent(intent);
        String lActivityType = null;
        try {
            lActivityType = fData.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intentPush;

        if (lActivityType != null) {
            switch (lActivityType) {
                case GetConnectedConstants.ALBUM_SHARE:
                    intentPush = new Intent(GetConnectedConstants.SHOW_HOME);
                    intentPush.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentPush.putExtra(GetConnectedConstants.ALBUM_SHARE, GetConnectedConstants.ALBUM_SHARE);
                    context.startActivity(intentPush);
                    break;

                case GetConnectedConstants.PHOTO_ADDED:
                    intentPush = new Intent(GetConnectedConstants.SHOW_HOME);
                    intentPush.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentPush.putExtra(GetConnectedConstants.PHOTO_ADDED, GetConnectedConstants.PHOTO_ADDED);
                    context.startActivity(intentPush);
                    break;

                case GetConnectedConstants.MESSAGING:
                    intentPush = new Intent(GetConnectedConstants.SHOW_HOME);
                    intentPush.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentPush.putExtra(GetConnectedConstants.MESSAGING, GetConnectedConstants.MESSAGING);
                    context.startActivity(intentPush);
                    break;

                case GetConnectedConstants.SIGNUP:
                    intentPush = new Intent(GetConnectedConstants.SHOW_HOME);
                    intentPush.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentPush.putExtra(GetConnectedConstants.SIGNUP, GetConnectedConstants.SIGNUP);
                    context.startActivity(intentPush);
                    break;
            }
        }
    }

    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(GetConnectedConstants.PARSE_DATA));
        } catch (JSONException e) {
        }
        return data;
    }
}
