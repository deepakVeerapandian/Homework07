package com.example.homework07;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chats {
    public ArrayList<ChatObject> chatList;

    public Chats(ArrayList<ChatObject> chatList) {
        this.chatList = chatList;
    }
//
//    public Chats(String sender, String tripId, String messagedId, String message, Date sentTime ) {
//        this.sender = sender;
//        this.tripId = tripId;
//        this.messagedId = messagedId;
//        this.message = message;
//        this.sentTime = sentTime;
//    }

    public Chats(Map<String,Object>hashMap)
    {
//        this.sender=(String)hashMap.get("sender");
//        this.tripId=(String)hashMap.get("tripId");
//        this.messagedId=(String)hashMap.get("messagedId");
//        this.message=(String)hashMap.get("message");
//        this.sentTime=(Date) hashMap.get("sentTime");
        this.chatList = (ArrayList<ChatObject>) hashMap.get("chatList");
    }
    public Map<String,ArrayList<ChatObject>> ToHashMap()
    {
        HashMap<String,ArrayList<ChatObject>> hashChats = new HashMap<String, ArrayList<ChatObject>>();
//        hashChats.put("sender",this.sender);
//        hashChats.put("message",this.message);
//        hashChats.put("tripId",this.tripId);
//        hashChats.put("messagedId",this.messagedId);
//        hashChats.put("sentTime",this.sentTime);
        hashChats.put("chatList",this.chatList);
        return hashChats;
    }
}
