package com.example.friends.fitfriend;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by thang on 08.08.2016.
 */
@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public String birthday;
    public String gender;
    public String userUID; //Not show anywhere this.



    public User() {
    }

    public User(String name, String email, String birthday, String gender, String userUID) {
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.userUID = userUID;
    }

}