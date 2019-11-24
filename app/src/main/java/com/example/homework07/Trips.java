package com.example.homework07;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Trips {

    String title;
    String latitude;
    String longitude;
    String coverPhoto;
    String createdBy;
    String imgUrl;
    ArrayList<String> members = new ArrayList<String>();

    public Trips(String title, String latitude, String longitude, String coverPhoto,ArrayList<String> members,String createdBy) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.coverPhoto = coverPhoto;
        this.members = members;
        this.createdBy=createdBy;

    }

    public Trips(String title, String createdBy, String coverPhoto,String imgUrl) {
        this.title = title;
        this.coverPhoto = coverPhoto;
        this.createdBy = createdBy;
        this.imgUrl=imgUrl;
    }

    public Trips(Map<String,Object>hashMap)
    {
        this.title=(String)hashMap.get("title");
        this.createdBy=(String)hashMap.get("admin");
        this.coverPhoto=(String)hashMap.get("coverPhoto");
        this.latitude=(String)hashMap.get("latitude");
        this.longitude=(String)hashMap.get("longitude");
        this.members= (ArrayList<String>) hashMap.get("members");
    }
    public Map<String,Object> tripsToHashMap()
    {
        HashMap<String,Object> hashTrips = new HashMap<String, Object>();
        hashTrips.put("title",this.title);
        hashTrips.put("latitude",this.latitude);
        hashTrips.put("longitude",this.longitude);
        hashTrips.put("coverPhoto",this.coverPhoto);
        hashTrips.put("members",this.members);
        hashTrips.put("admin",this.createdBy);
        return hashTrips;
    }


}
