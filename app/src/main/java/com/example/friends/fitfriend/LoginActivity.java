package com.example.friends.fitfriend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    EditText email;
    EditText password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseChild child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        child = new FirebaseChild();
        child.checkUser();
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String loginname = intent.getStringExtra("Email");
        String loginpw = intent.getStringExtra("Password");

        if(intent != null){
            email.setText(loginname);
            password.setText(loginpw);
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("ONCREATE SAYS", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("ONCREATE SAYS", "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };
        //FACEBOOK
        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        FacebookData data = new FacebookData();
                        Bundle bFacebookData = data.getFacebookData(object);
                        System.out.println(bFacebookData.getString("email") + " WO HOOOOOOOOOOOOOOO");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        /*
                        for(User user: child.checkUser()){
                            System.out.println(bFacebookData.getString("email") + " WO HOOOOOOOOOOOOOOO " + user.email);
                            if(bFacebookData.getString("email").equals(user.email)){
                                handleFacebookAccessToken(loginResult.getAccessToken());
                            }else{
                                LoginManager.getInstance().logOut();
                                Toast.makeText(LoginActivity.this, "Login to link with facebook", Toast.LENGTH_SHORT).show();
                            }
                        }
                        */
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        //TODO: Link facebook and ordinary acc.
        if(token != null){
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("YESYES", "signInWithCredential:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.w("NONO", "signInWithCredential", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

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
    }

    public void createAccountClick(View v){
        Intent createAccountIntent = new Intent(this, RegisterActivity.class);
        startActivity(createAccountIntent);
        LoginActivity.this.finish();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent accountIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(accountIntent);
            Toast.makeText(LoginActivity.this, "YOU ARE LOGGED IN.", Toast.LENGTH_SHORT).show();
            LoginActivity.this.finish();
        }
    }
}