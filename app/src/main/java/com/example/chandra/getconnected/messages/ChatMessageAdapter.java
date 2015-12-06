package com.example.chandra.getconnected.messages;

import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chandra.getconnected.R;
import com.example.chandra.getconnected.constants.GetConnectedConstants;
import com.example.chandra.getconnected.constants.ParseConstants;
import com.example.chandra.getconnected.utility.ActivityUtility;
import com.example.chandra.getconnected.utility.PhotoUtility;
import com.example.chandra.getconnected.utility.SharedPreferenceHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandra on 12/4/2015.
 */
public class ChatMessageAdapter extends ArrayAdapter {

    Context context;
    int resource;
    ArrayList<ChatMessage> objects;

    public ChatMessageAdapter(Context context, int resource, ArrayList<ChatMessage> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }


        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.parent);
        View v = layout.getChildAt(0);
        layout.removeView(v);





        if (objects.get(position).isImage()) {

            CardView card = new CardView(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            card.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout parentLayout = new LinearLayout(getContext());
            parentLayout.setLayoutParams(params);
            parentLayout.setOrientation(LinearLayout.VERTICAL);

            TextView time = new TextView(getContext());
            params.setMargins(7,7,7,7);
            params.gravity=Gravity.CENTER;
            time.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Small);
            time.setLayoutParams(params);
            time.setText(objects.get(position).getTime());


            final ParseImageView imageview1 = new ParseImageView(getContext());
            if (objects.get(position).getPosition().equals(GetConnectedConstants.LEFT)) {
                card.setBackgroundColor(Color.parseColor("#ffffff"));
                params.gravity = Gravity.LEFT;
            } else {
                params.gravity = Gravity.RIGHT;
                card.setBackgroundColor(Color.parseColor("#64B5F6"));
            }
            imageview1.setLayoutParams(params);
            imageview1.getLayoutParams().height = 500;
            imageview1.getLayoutParams().width = 450;
            imageview1.setLayoutParams(params);
            card.setLayoutParams(params);
            if (objects.get(position).isNew()) {
                SharedPreferenceHelper helper = new SharedPreferenceHelper();
                String imageString = helper.loadFromSharedPreference(getContext(), objects.get(position).getImageid());
                imageview1.setImageBitmap(PhotoUtility.convertStringToImage(imageString));

            } else {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.MESSAGES_PHOTO_TABLE);
                query.whereEqualTo(ParseConstants.OBJECT_ID, objects.get(position).getMessage());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            imageview1.setParseFile(object.getParseFile(ParseConstants.MESSAGES_PHOTO_PHOTO));
                            imageview1.loadInBackground();
                        } else {
                            ActivityUtility.Helper.writeErrorLog(e.toString());
                        }
                    }
                });

                parentLayout.addView(imageview1);
                parentLayout.addView(time);
                card.addView(parentLayout);
                layout.addView(card);
            }
        } else {

            CardView card = new CardView(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            card.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout parentLayout = new LinearLayout(getContext());
            parentLayout.setLayoutParams(params);
            parentLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView text = new TextView(getContext());
            text.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Large);
            params.setMargins(7, 7, 7, 7);
            text.setLayoutParams(params);
            text.setText(objects.get(position).getMessage());

            TextView time = new TextView(getContext());
            params.setMargins(7,7,7,7);
            params.gravity=Gravity.BOTTOM;
            time.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Small);
            time.setLayoutParams(params);
            time.setText(objects.get(position).getTime());



            if (objects.get(position).getPosition().equals(GetConnectedConstants.LEFT)) {

                card.setBackgroundColor(Color.parseColor("#ffffff"));
                params.gravity = Gravity.LEFT;
                card.setLayoutParams(params);
            } else {
                text.setText(objects.get(position).getMessage());
                params.gravity = Gravity.RIGHT;
                card.setBackgroundColor(Color.parseColor("#64B5F6"));
                card.setLayoutParams(params);
            }
            parentLayout.addView(text);
            parentLayout.addView(time);
            card.addView(parentLayout);
            layout.addView(card);
        }
        return convertView;
    }

}
