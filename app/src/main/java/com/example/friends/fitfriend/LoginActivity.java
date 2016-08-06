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
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    EditText email;
    EditText password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Facebook login button
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Profile profile = Profile.getCurrentProfile();
            nextActivity(profile);
        }
        @Override
        public void onCancel() {        }
        @Override
        public void onError(FacebookException e) {      }
    };

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
                // ...
            }
        };
    }

    private void loginFacebook(){
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                nextActivity(profile);
                Toast.makeText(getApplicationContext(), "Logget inn!", Toast.LENGTH_SHORT).show();    }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        };
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, callback);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);

        loginFirebase(); //Method for logging in with firebase auth.
        loginFacebook(); //Method for logging in with facebook auth.
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Facebook login
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);
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
        //Facebook login
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
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

    private void nextActivity(Profile profile){
        if(profile != null){
            //Change TestActivity to MainActivity?, ViewprofileActivity? when importing database.
            Intent main = new Intent(LoginActivity.this, TestActivity.class);
            main.putExtra("name", profile.getFirstName());
            main.putExtra("surname", profile.getLastName());
            main.putExtra("imageUrl", profile.getProfilePictureUri(200,200).toString());
            startActivity(main);
            LoginActivity.this.finish();
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