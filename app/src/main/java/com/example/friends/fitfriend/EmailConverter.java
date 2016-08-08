package com.example.friends.fitfriend;

/**
 * Created by thang on 08.08.2016.
 */
public class EmailConverter {
    private String email;

    public EmailConverter(String a){
        this.email = a.replaceAll("[^a-zA-Z0-9]", "");
    }
    public String getEmail(){
        return email;
    }
}
