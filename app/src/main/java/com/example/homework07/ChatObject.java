package com.example.homework07;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ChatObject {
    public String sender, tripId, messagedId, message, sentTime,isImageSent;
//    int isImageSent;
//    public Date sentTime;

    public ChatObject(String sender, String tripId, String messagedId, String message, String  isImageSent, String sentTime) {
        this.sender = sender;
        this.tripId = tripId;
        this.messagedId = messagedId;
        this.message = message;
        this.sentTime = sentTime;
        this.isImageSent = isImageSent;
    }

    public ChatObject(Map<String,String> hashMap)  {
        this.sender=(String)hashMap.get("sender");
        this.tripId=(String)hashMap.get("tripId");
        this.messagedId=(String)hashMap.get("messagedId");
        this.message=(String)hashMap.get("message");
        this.isImageSent= (String) hashMap.get("isImageSent");
        this.sentTime=(String)hashMap.get("sentTime");

    }
}
