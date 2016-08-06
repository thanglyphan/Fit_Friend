package com.example.friends.fitfriend;

/**
 * Created by thang on 06.08.2016.
 */
public class CheckRequired {

    private String Password;
    private String Email;
    private String PasswordConfirm;

    public CheckRequired(String c, String e, String f){
        this.Email = c;
        this.Password = e;
        this.PasswordConfirm = f;
    }

    public boolean checkRequiredFields(){
        if(Email != "" && Password != "" && PasswordConfirm != ""){
            return true;
        }else{
            return false;
        }
    }

    public boolean checkPwConfirmed(){
        return Password.equals(PasswordConfirm);
    }

    public String recquired(){
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            return "email";
        }else if(Password.length() < 6){
            return "password";
        }else if(!Password.equals(PasswordConfirm)){
            return "pwconfirm";
        }else{
            return "";
        }
    }
}