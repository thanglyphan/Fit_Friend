package com.example.friends.fitfriend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public void logoutClick(View view){
        FirebaseAuth.getInstance().signOut();
        Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(toLogin);
        Toast.makeText(MainActivity.this, "YOU ARE LOGGED OUT.", Toast.LENGTH_SHORT).show();
        MainActivity.this.finish();
    }

}
