package com.example.homework07;

import java.util.Date;

public class ChatObject {
    public String sender, tripId, messagedId, message;
    public Date sentTime;

    public ChatObject(String sender, String tripId, String messagedId, String message, Date sentTime) {
        this.sender = sender;
        this.tripId = tripId;
        this.messagedId = messagedId;
        this.message = message;
        this.sentTime = sentTime;
    }
}
