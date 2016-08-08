package com.example.friends.fitfriend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    EditText email;
    EditText password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private String emailValue;

    //Facebook login button
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Profile profile = Profile.getCurrentProfile();
            Log.d("THANGGGGGGGGGGG first", emailValue);
            nextActivity(profile, "", null);
        }
        @Override
        public void onCancel() {        }
        @Override
        public void onError(FacebookException e) {      }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //Facebook login
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile, "Lol", "");
    }

    private void loginFirebase(){
        Intent intent = getIntent();
        String loginname = intent.getStringExtra("Email");
        String loginpw = intent.getStringExtra("Password");

        if(intent != null){
            email.setText(loginname);
            password.setText(loginpw);
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("SIGNED IN BRO", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("SIGNED OUT BRO", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void loginFacebook(){
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                // Application code
                                try {
                                    Profile profile = Profile.getCurrentProfile();
                                    emailValue = object.getString("email");
                                    String birthday = object.getString("birthday"); // 01/31/1980 format
                                    nextActivity(profile, emailValue, birthday);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginFirebase(); //Method for logging in with firebase auth.
        loginFacebook(); //Method for logging in with facebook auth.
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public void onStart() {
        super.onStart();
        //Firebase login shit.
        mAuth.addAuthStateListener(mAuthListener);
    }
    protected void onStop() {
        super.onStop();
        //Firebase login shit.
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);

    }

    public void loginClick(View v){
        //Method for loginbtn with firebase.
        String emailAddress = email.getText().toString();
        String passwordLol = password.getText().toString();

        if(emailAddress.isEmpty() && passwordLol.isEmpty()){
            emailAddress = "JaneDoe";
            passwordLol = "JaneDoe";
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(emailAddress, passwordLol).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent accountIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(accountIntent);
                            Toast.makeText(LoginActivity.this, "YOU ARE LOGGED IN.", Toast.LENGTH_SHORT).show();
                            LoginActivity.this.finish();
                        } else{
                            Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //Finish signing in.
    }

    private void nextActivity(Profile profile, String email, String birthday){
        if(profile != null){
            //Change TestActivity to MainActivity?, ViewprofileActivity? when importing database.
            boolean found = false;
            //Log.d("LOGINNNNNNNNNNNNNNNNNNN", "" + email.toString());
            EmailConverter emailCon = new EmailConverter(email);
            String a = emailCon.getEmail();


            Intent main = new Intent(LoginActivity.this, TestActivity.class);
            main.putExtra("name", profile.getFirstName());
            main.putExtra("surname", profile.getLastName());
            main.putExtra("imageUrl", profile.getProfilePictureUri(200,200).toString());
            startActivity(main);
            LoginActivity.this.finish();


            //TODO: SEARCH FOR USER HERE.

            //Check database if email is registered.
            /*
            if(found){
                Intent main = new Intent(LoginActivity.this, TestActivity.class);
                main.putExtra("name", profile.getFirstName());
                main.putExtra("surname", profile.getLastName());
                main.putExtra("imageUrl", profile.getProfilePictureUri(200,200).toString());
                startActivity(main);
                LoginActivity.this.finish();
            }else{
                LoginManager.getInstance().logOut();
                Toast.makeText(this, "You need to make an account", Toast.LENGTH_SHORT).show();
            }
            */
        }
    }

    public void createAccountClick(View v){
        Intent createAccountIntent = new Intent(this, RegisterActivity.class);
        startActivity(createAccountIntent);
        LoginActivity.this.finish();
    }

    /*
    public void loginButton(View v) {
        //if login successful go to activity main
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        super.finish();
    }
    */
}