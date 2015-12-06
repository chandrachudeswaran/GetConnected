package com.example.chandra.getconnected.messages;

import android.graphics.Bitmap;

/**
 * Created by chandra on 12/3/2015.
 */
public class ChatMessage {

    String position;
    String message;
    boolean image;
    Bitmap bitmap;
    boolean isNew;
    String imageid;
    String time;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "position='" + position + '\'' +
                ", message='" + message + '\'' +
                ", image=" + image +
                '}';
    }
}
