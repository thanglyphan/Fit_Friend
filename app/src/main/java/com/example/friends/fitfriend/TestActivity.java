package com.example.friends.fitfriend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import junit.framework.Test;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Bundle inBundle = getIntent().getExtras();
        String name = inBundle.get("name").toString();
        String surname = inBundle.get("surname").toString();
        String imageUrl = inBundle.get("imageUrl").toString();

        TextView nameView = (TextView)findViewById(R.id.nameAndSurname);
        nameView.setText("" + name + " " + surname);

        new DownloadImage((ImageView)findViewById(R.id.profileImage)).execute(imageUrl);
    }

    public void logout(View view){
        LoginManager.getInstance().logOut();
        Intent login = new Intent(TestActivity.this, LoginActivity.class);
        startActivity(login);
        TestActivity.this.finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("Back is pressed TEST", "HELLOOOOOOOOO");
        TestActivity.this.finish();
    }
}
