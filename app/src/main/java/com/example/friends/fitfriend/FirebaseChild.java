package com.example.friends.fitfriend;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.FitWindowsFrameLayout;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thang on 09.08.2016.
 */
public class FirebaseChild extends AppCompatActivity{
    private DatabaseReference mDatabase;
    List<User> userList = new ArrayList<User>();
    public FirebaseChild(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void checkUser(){
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("There are " + dataSnapshot.getChildrenCount() + " users");
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    userList.add(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FirebaseChild.class say", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(userListener);
    }
    public List getUsers(){
        return userList;
    }

}
