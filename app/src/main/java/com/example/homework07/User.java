package com.example.homework07;

import java.util.HashMap;
import java.util.Map;

public class User {
    int userId;
    String userName;
    String password;
    String firstName;
    String lastName;
    String imgUrl;

    public User(int userId, String userName, String password, String firstName, String lastName, String imgUrl, String gender) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imgUrl = imgUrl;
        this.gender = gender;
    }

    String gender;

    public User(Map<String,Object> hashMap)
    {
        this.userId=(int)(long) hashMap.get("userId");
        this.userName=(String)hashMap.get("userName");
        this.password=(String)hashMap.get("password");
        this.firstName=(String)hashMap.get("firstName");
        this.lastName=(String)hashMap.get("lastName");
        this.imgUrl=(String)hashMap.get("imgUrl");
        this.gender=(String)hashMap.get("gender");
    }

    public Map toHashMap()
    {
        Map<String,Object> hashing = new HashMap<String, Object>();
        hashing.put("userId",this.userId);
        hashing.put("userName",this.userName);
        hashing.put("password",this.password);
        hashing.put("firstName",this.firstName);
        hashing.put("lastName",this.lastName);
        hashing.put("imgUrl",this.imgUrl);
        hashing.put("gender",this.gender);
        return hashing;

    }
}
