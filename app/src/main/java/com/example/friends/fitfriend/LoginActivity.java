package com.example.friends.fitfriend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void loginButton(View v) {
        //if login successful go to activity main
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        super.finish();
    }
}