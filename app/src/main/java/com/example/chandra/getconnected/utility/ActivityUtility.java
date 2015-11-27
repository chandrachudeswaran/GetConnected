package com.example.chandra.getconnected.utility;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

        static public void makeToast(Context context,String message){
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }

        static public void showNotificationLogin(CoordinatorLayout coordinatorLayout,String message) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

            snackbar.show();
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            textView.setGravity(Gravity.CENTER);
        }


    }
}