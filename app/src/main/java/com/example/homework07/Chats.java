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


    public Chats(Map<String,Object>hashMap)
    {
        this.chatList = (ArrayList<ChatObject>) hashMap.get("chatList");
    }

    public Map<String,ArrayList<ChatObject>> ToHashMap()
    {
        HashMap<String,ArrayList<ChatObject>> hashChats = new HashMap<String, ArrayList<ChatObject>>();
        hashChats.put("chatList",this.chatList);

        return hashChats;
    }
}
