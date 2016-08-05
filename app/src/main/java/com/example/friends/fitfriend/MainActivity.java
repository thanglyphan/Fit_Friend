package com.example.friends.fitfriend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void viewPButton(View v) {
        Intent intent = new Intent(this, ViewprofileActivity.class);
        startActivity(intent);

    }
    public void fListButton(View v) {
        Intent intent = new Intent(this, FriendlistActivity.class);
        startActivity(intent);

    }
    public void swipeButton(View v) {
        Intent intent = new Intent(this, SwipeActivity.class);
        startActivity(intent);

    }

}
